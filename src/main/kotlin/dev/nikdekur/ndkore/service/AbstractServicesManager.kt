/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 * Copyright (c) 2024-present "Nik De Kur"
 */

@file:Suppress("NOTHING_TO_INLINE")

package dev.nikdekur.ndkore.service

import org.slf4j.LoggerFactory
import java.util.LinkedList
import kotlin.reflect.KClass

abstract class AbstractServicesManager : ServicesManager {

    val logger = LoggerFactory.getLogger(javaClass)

    override var state = ServicesManager.State.DISABLED

    val servicesCollection = LinkedHashSet<Service<*>>()

    override val services
        get() = sortModules()

    override fun <S : Service<*>> registerService(service: S, vararg bindTo: KClass<out S>) {
        // If services manager is enabled and bind override existing services,
        // then disable existing services and enable the new one
        if (state == ServicesManager.State.ENABLED) {
            getServiceOrNull(service::class)?.doDisable()
            bindTo.forEach { getServiceOrNull(it)?.doDisable() }

            service.doEnable()
        }

        servicesCollection.add(service)
    }


    override fun enable() {
        check(state == ServicesManager.State.DISABLED) {
            "Services Manager is not disabled!"
        }

        state = ServicesManager.State.ENABLING

        val sorted = sortModules()
        logger.info("Load order: ${sorted.joinToString { it.javaClass.simpleName }}")
        sorted.forEach {
            try {
                it.onEnable()
            } catch (e: Exception) {
                logger.error("Error while loading module '$it'!", e)
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
                it.onDisable()
            } catch (e: Exception) {
                logger.error("Error while unloading module '$it'!", e)
            }
        }

        state = ServicesManager.State.DISABLED
    }


    fun sortModules(): Collection<Service<*>> {
        val firstModules = LinkedList<Service<*>>()
        val lastModules = LinkedList<Service<*>>()
        val middleModules = LinkedList<Service<*>>()
        val sortedServices = LinkedHashSet<Service<*>>()
        val addedModules = HashSet<KClass<out Service<*>>>()

        // Group modules into first, last, and middle categories
        servicesCollection.forEach { service ->
            when {
                service.dependencies.first -> firstModules.add(service)
                service.dependencies.last -> lastModules.add(service)
                else -> middleModules.add(service)
            }
        }

        // Set for tracking modules currently in the process of loading
        val currentlyProcessing = HashSet<Service<*>>()

        // Helper function to add a module considering its dependencies
        fun addModule(service: Service<*>) {
            if (sortedServices.contains(service)) return

            if (currentlyProcessing.contains(service)) {
                // If the module is already in the process of loading, a cycle is detected
                throw CircularDependencyException(service)
            }

            // Add module to the current process
            currentlyProcessing.add(service)

            // Load modules that the current one depends on
            service.dependencies.after.forEach { afterModule ->
                val afterService = getServiceOrNull(afterModule) ?: throw ServiceNotFoundException(afterModule)
                @Suppress("UNCHECKED_CAST")
                addModule(afterService)
            }

            // Add the current module
            sortedServices.add(service)
            addedModules.add(service::class)

            // Load modules that should be loaded before the current one
            service.dependencies.before.forEach { beforeModule ->
                val beforeService = getServiceOrNull(beforeModule)
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