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

    override fun <S : Service<*>> registerService(service: S, vararg bindTo: KClass<out S>) {
        super.registerService(service, *bindTo)
        // Koin doesn't allow passing class instead of a reified type, so here we see
        // rewrote access to koin internals to make it work and cast magic
        context.loadModule {
            reg<Any>(service, *bindTo)
        }
    }


    @Suppress("UNCHECKED_CAST")
    inline fun <I : Any> Module.reg(service: Service<*>, vararg bindTo: KClass<*>) {
        val moduleClass = service::class as KClass<I>
        val service = service as I
        val definition = single(clazz = moduleClass) { service }
        bindTo.forEach {
            definition bind it as KClass<I>
        }
    }

    override fun <S : Service<*>> getServiceOrNull(serviceClass: KClass<out S>): S? {
        return context.get().getOrNull(serviceClass)
    }

    override fun <S : Service<*>> getService(serviceClass: KClass<out S>): S {
        return try {
            context.get().get(serviceClass)
        } catch (e: NoDefinitionFoundException) {
            throw ServiceNotFoundException(serviceClass)
        }
    }
}