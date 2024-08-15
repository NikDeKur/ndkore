/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 * Copyright (c) 2024-present "Nik De Kur"
 */

@file:Suppress("NOTHING_TO_INLINE")

package dev.nikdekur.ndkore.service

import dev.nikdekur.ndkore.ext.loadModule
import dev.nikdekur.ndkore.ext.single
import org.koin.core.context.KoinContext
import org.koin.core.error.NoDefinitionFoundException
import org.koin.core.module.Module
import org.koin.dsl.bind
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.util.LinkedList
import kotlin.reflect.KClass

class KoinServicesManager<A>(
    val context: KoinContext,
    override val app: A
) : ServicesManager<A> {

    val logger: Logger = LoggerFactory.getLogger(javaClass)

    override val services = LinkedList<Service<A>>()
    val states = HashMap<String, ServiceState>()

    override fun registerService(service: Service<A>, vararg bindTo: KClass<*>) {


        // Koin doesn't allow passing class instead of a reified type, so here we see
        // rewrote access to koin internals to make it work and cast magic
        context.loadModule {
            reg<Any>(service, *bindTo)
        }

        services.add(service)
    }

    @Suppress("UNCHECKED_CAST")
    inline fun <I : Any> Module.reg(service: Service<A>, vararg bindTo: KClass<*>) {
        val moduleClass = service::class as KClass<I>
        val service = service as I
        val definition = single(clazz = moduleClass) { service }
        bindTo.forEach {
            require(it.isInstance(service)) {
                "Module '${service::class.qualifiedName}' does not implement '${it.qualifiedName}'!"
            }

            definition bind it as KClass<I>
        }
    }


    override fun <T : Any> getService(serviceClass: KClass<T>): T {
        return try {
            context.get().get(serviceClass)
        } catch (e: NoDefinitionFoundException) {
            throw ServiceNotFoundException(serviceClass)
        }
    }

    override fun <T : Any> getServiceOrNull(serviceClass: KClass<T>): T? {
        return context.get().getOrNull(serviceClass)
    }

    @Suppress("UNCHECKED_CAST")
    inline fun <T : Any> getServiceInternal(clazz: KClass<T>) = getServiceOrNull(clazz) as? Service<A>

    override fun loadAll() {
        val sorted = sortModules()
        logger.info("Load order: ${sorted.joinToString { it.javaClass.simpleName }}")
        sorted.forEach {
            try {
                it.onLoad()
            } catch (e: Exception) {
                logger.error("Error while loading module '$it'!", e)
            }
        }
    }

    override fun unloadAll() {
        // Unload in reverse order, because of dependencies
        sortModules().reversed().forEach {
            try {
                it.onUnload()
            } catch (e: Exception) {
                logger.error("Error while unloading module '$it'!", e)
            }
        }
    }


    private fun sortModules(): Collection<Service<A>> {
        val firstModules = LinkedList<Service<A>>()
        val lastModules = LinkedList<Service<A>>()
        val middleModules = LinkedList<Service<A>>()
        val sortedServices = LinkedHashSet<Service<A>>()
        val addedModules = HashSet<KClass<out Service<A>>>()

        // Group modules into first, last, and middle categories
        services.forEach { service ->
            when {
                service.dependencies.first -> firstModules.add(service)
                service.dependencies.last -> lastModules.add(service)
                else -> middleModules.add(service)
            }
        }

        // Set for tracking modules currently in the process of loading
        val currentlyProcessing = HashSet<Service<A>>()

        // Helper function to add a module considering its dependencies
        fun addModule(service: Service<A>) {
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