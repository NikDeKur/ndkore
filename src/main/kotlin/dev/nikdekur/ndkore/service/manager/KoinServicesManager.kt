/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 * Copyright (c) 2024-present "Nik De Kur"
 */

@file:Suppress("NOTHING_TO_INLINE")

package dev.nikdekur.ndkore.service.manager

import dev.nikdekur.ndkore.ext.loadModule
import dev.nikdekur.ndkore.ext.single
import dev.nikdekur.ndkore.service.Service
import dev.nikdekur.ndkore.service.ServiceNotFoundException
import org.koin.core.context.KoinContext
import org.koin.core.error.NoDefinitionFoundException
import org.koin.core.module.Module
import org.koin.dsl.bind
import kotlin.reflect.KClass

/**
 * # Koin Services Manager
 *
 * [ServicesManager] implementation using a Koin Dependency Injection framework.
 *
 * Use Koin to store and manage services instances.
 *
 * ### Example Usage:
 * ```
 * // Define a simple service interface
 * interface MyService {
 *     fun doSomething()
 * }
 *
 * // Implementation of the service
 * class MyServiceImpl(override val servicesManager: ServicesManager<MyApp>) : Service<MyApp>, MyService {
 *     override fun doSomething() {
 *         println("Doing something!")
 *     }
 *
 *     override fun onLoad() {
 *         println("Service loaded!")
 *     }
 *
 *     override fun onUnload() {
 *         println("Service unloaded!")
 *     }
 * }
 *
 * // Define an application context
 * class MyApp(val name: String)
 *
 * fun main() {
 *     // Start Koin for dependency injection
 *     startKoin {
 *         // No modules to load initially
 *     }
 *
 *     // Initialize the ServicesManager with the application context
 *     val myApp = MyApp("My Application")
 *     val serviceManager: ServicesManager<MyApp> = KoinServicesManager(GlobalContext, myApp)
 *
 *     // Register the service with the service manager
 *     val myService = MyServiceImpl()
 *     serviceManager.registerService(myService, MyService::class)
 *
 *     // Load all registered services
 *     serviceManager.loadAll()
 *
 *     // Retrieve and use the service
 *     val service: MyService = serviceManager.getService(MyService::class)
 *     service.doSomething()
 *
 *     // Inject the service lazily
 *     val injectedService by serviceManager.inject<MyService>()
 *     injectedService.doSomething()
 *
 *     // Unload all registered services
 *     serviceManager.unloadAll()
 * }
 * ```
 *
 * @property context The Koin context.
 */
class KoinServicesManager(
    val context: KoinContext
) : AbstractServicesManager() {


    override fun <C : Any, S : C> registerService(service: S, vararg bindTo: Class<out C>) {
        super.registerService(service, *bindTo)
        context.loadModule {
            reg<Any>(service as Service, *bindTo)
        }
    }


    @Suppress("UNCHECKED_CAST")
    inline fun <I : Any> Module.reg(service: Service, vararg bindTo: Class<*>) {
        val moduleClass = service::class as KClass<I>
        val service = service as I
        val definition = single(clazz = moduleClass) { service }
        bindTo.forEach {
            definition bind it.kotlin as KClass<I>
        }
    }

    override fun <C : Any> getServiceOrNull(serviceClass: Class<out C>): C? {
        return context.get().getOrNull(serviceClass.kotlin)
    }

    override fun <C : Any> getService(serviceClass: Class<out C>): C {
        return try {
            context.get().get(serviceClass.kotlin)
        } catch (e: NoDefinitionFoundException) {
            throw ServiceNotFoundException(serviceClass)
        }
    }
}