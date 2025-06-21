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
import dev.nikdekur.ndkore.service.Definition
import dev.nikdekur.ndkore.service.Qualifier
import dev.nikdekur.ndkore.service.Service
import dev.nikdekur.ndkore.service.ServiceNotFoundException
import org.koin.core.context.KoinContext
import org.koin.core.error.NoDefinitionFoundException
import org.koin.core.module.Module
import org.koin.core.qualifier.StringQualifier
import org.koin.core.qualifier.qualifier
import org.koin.dsl.binds
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
 * class MyServiceImpl(override val servicesManager: ServicesManager) : AbstractService(), MyService {
 *     override fun doSomething() {
 *         println("Doing something!")
 *     }
 *
 *     override fun onEnable() {
 *         println("Service loaded!")
 *     }
 *
 *     override fun onDisable() {
 *         println("Service unloaded!")
 *     }
 * }
 *
 * fun main() {
 *     // Start Koin for dependency injection
 *     startKoin {
 *         // No modules to load initially
 *     }
 *
 *     // Initialize the ServicesManager with the application context
 *     val serviceManager: ServicesManager = KoinServicesManager(GlobalContext)
 *
 *     // Register the service with the service manager
 *     val myService = MyServiceImpl(serviceManager)
 *     serviceManager.registerService(myService, MyService::class)
 *
 *     // Enable all registered services
 *     serviceManager.enable()
 *
 *     // Retrieve and use the service
 *     val service: MyService = serviceManager.getService(MyService::class)
 *     service.doSomething()
 *
 *     // Inject the service lazily
 *     val injectedService by serviceManager.inject<MyService>()
 *     injectedService.doSomething()
 *
 *     // Disable all registered services
 *     serviceManager.disable()
 * }
 * ```
 *
 * @property context The Koin context.
 */
public class KoinServicesManager(
    override val builder: ServicesManagerBuilder<*>,
    public val context: KoinContext
) : AbstractServicesManager() {

    override suspend fun registerService(definition: Definition<*>) {
        super.registerService(definition)
        context.loadModule {
            reg<Any>(definition.service as Service, definition.qualifier, definition.bindTo)
        }
    }

    @Suppress("UNCHECKED_CAST")
    public inline fun <I : Any> Module.reg(service: Service, qualifier: Qualifier, bindTo: Iterable<KClass<*>>) {
        val moduleClass = service::class as KClass<I>
        val service = service as I

        val qualifier = qualifier.toKoinQualifier()
        val definition = single(clazz = moduleClass, qualifier = qualifier) { service }
        definition binds bindTo.toList().toTypedArray()
    }

    override fun <C : Any> getServiceOrNull(serviceClass: KClass<out C>, qualifier: Qualifier): C? {
        return context.get().getOrNull(serviceClass, qualifier = qualifier.toKoinQualifier())
    }


    override fun <C : Any> getService(serviceClass: KClass<out C>, qualifier: Qualifier): C {
        return try {
            context.get().get(serviceClass, qualifier = qualifier.toKoinQualifier())
        } catch (e: NoDefinitionFoundException) {
            throw ServiceNotFoundException(serviceClass, qualifier)
        }
    }
}

public inline fun Qualifier.toKoinQualifier(): StringQualifier? = if (value.isEmpty()) null else qualifier(value)


public open class KoinServicesManagerBuilder : ServicesManagerBuilder<KoinServicesManager>() {

    public var actualContext: KoinContext? = null

    public fun context(context: KoinContext) {
        this.actualContext = context
    }

    override fun build(): KoinServicesManager {
        val context = actualContext
        requireNotNull(context) { "Koin context must be set!" }

        return KoinServicesManager(this, context)
    }
}


public inline fun KoinServicesManager(builder: KoinServicesManagerBuilder.() -> Unit): ServicesManager {
    val b = KoinServicesManagerBuilder().apply(builder)
    return b.build()
}