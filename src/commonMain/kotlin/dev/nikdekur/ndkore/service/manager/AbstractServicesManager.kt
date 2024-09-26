/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 * Copyright (c) 2024-present "Nik De Kur"
 */

package dev.nikdekur.ndkore.service.manager

import dev.nikdekur.ndkore.service.*
import io.github.oshai.kotlinlogging.KLogger
import io.github.oshai.kotlinlogging.KotlinLogging
import kotlin.reflect.KClass

public abstract class AbstractServicesManager : ServicesManager {

    public val logger: KLogger = KotlinLogging.logger {}

    override var state: ServicesManager.State = ServicesManager.State.DISABLED

    public val servicesCollection: MutableSet<Service> = LinkedHashSet<Service>()

    override val services: Collection<Service>
        get() = sortModules()

    override fun <C : Any, S : C> registerService(service: S, vararg bindTo: KClass<out C>) {
        if (service !is Service)
            throw ClassIsNotServiceException(service::class)

        // If services manager is enabled and bind override existing services,
        // then disable existing services and enable the new one
        if (state == ServicesManager.State.ENABLED) {
            getServiceInternal(service::class)?.doDisable()
            bindTo.forEach { getServiceInternal(it)?.doDisable() }
            service.doEnable()
        }

        servicesCollection.add(service)
    }

    public fun getServiceInternal(serviceClass: KClass<*>): Service? {
        val service = getServiceOrNull(serviceClass) ?: return null
        return service as? Service ?: throw ClassIsNotServiceException(serviceClass)
    }



    override fun enable() {
        check(state == ServicesManager.State.DISABLED) {
            "Services Manager is not disabled!"
        }

        state = ServicesManager.State.ENABLING

        val sorted = sortModules()
        logger.info { "Load order: ${sorted.joinToString { it::class.simpleName ?: "UnknownClassName" }}" }
        sorted.forEach {
            try {
                it.doEnable()
            } catch (e: Exception) {
                logger.error(e) { "Error while loading module '$it'!" }
            }
        }

        state = ServicesManager.State.ENABLED
    }

    override fun disable() {
        check(state == ServicesManager.State.ENABLED) {
            "Services Manager is not enabled!"
        }

        state = ServicesManager.State.DISABLING

        // Unload in reverse order, because of dependencies
        sortModules().reversed().forEach {
            try {
                it.doDisable()
            } catch (e: Exception) {
                logger.error(e) { "Error while unloading module '$it'!" }
            }
        }

        state = ServicesManager.State.DISABLED
    }


    public fun reload(service: Service) {
        service.onDisable()
        service.onEnable()
    }


    public fun sortModules(): Collection<Service> {
        val firstModules = mutableListOf<Service>()
        val lastModules = mutableListOf<Service>()
        val middleModules = mutableListOf<Service>()
        val sortedServices = LinkedHashSet<Service>()
        val addedModules = HashSet<KClass<out Service>>()

        // Group modules into first, last, and middle categories
        servicesCollection.forEach { service ->
            when {
                service.dependencies.first -> firstModules.add(service)
                service.dependencies.last -> lastModules.add(service)
                else -> middleModules.add(service)
            }
        }

        // Set for tracking modules currently in the process of loading
        val currentlyProcessing = HashSet<Service>()

        // Helper function to add a module considering its dependencies
        fun addModule(service: Service) {
            if (sortedServices.contains(service)) return

            if (currentlyProcessing.contains(service)) {
                // If the module is already in the process of loading, a cycle is detected
                throw CircularDependencyException(service)
            }

            // Add module to the current process
            currentlyProcessing.add(service)

            // Load modules that the current one depends on
            service.dependencies.after.forEach { afterModule ->
                val afterService = getServiceInternal(afterModule) ?: throw ServiceNotFoundException(afterModule)
                addModule(afterService)
            }

            // Add the current module
            sortedServices.add(service)
            addedModules.add(service::class)

            // Load modules that should be loaded before the current one
            service.dependencies.before.forEach { beforeModule ->
                val beforeService = getServiceInternal(beforeModule)
                if (beforeService != null && !sortedServices.contains(beforeService)) {
                    // Remove the current module so it can be re-added after its dependencies
                    sortedServices.remove(service)
                    addModule(beforeService)
                    sortedServices.add(service)
                }
            }

            // Remove the module from the current process after its loading is finished
            currentlyProcessing.remove(service)
        }

        // Process modules in the correct order
        firstModules.forEach(::addModule)
        middleModules.forEach(::addModule)
        lastModules.forEach(::addModule)

        return sortedServices
    }
}