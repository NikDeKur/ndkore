/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 * Copyright (c) 2024-present "Nik De Kur"
 */

@file:Suppress("NOTHING_TO_INLINE")

package dev.nikdekur.ndkore.service.manager

import dev.nikdekur.ndkore.service.Definition
import dev.nikdekur.ndkore.service.Qualifier
import dev.nikdekur.ndkore.service.Service
import dev.nikdekur.ndkore.service.ServiceNotFoundException
import org.koin.ext.getFullName
import kotlin.reflect.KClass

/**
 * # Runtime Services Manager
 *
 * [ServicesManager] implementation using a simple Map to store services.
 *
 * Use this implementation if you don't want to use Koin for dependency injection.
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
 *     // Initialize the ServicesManager with the constructor default map
 *     val serviceManager: ServicesManager = RuntimeServicesManager()
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
 * @property servicesMap Map of services to store and manage
 */
public open class RuntimeServicesManager(
    override val builder: ServicesManagerBuilder<*>,
    public val servicesMap: MutableMap<String, Service>
) : AbstractServicesManager() {


    public open fun classId(clazz: KClass<*>): String {
        // Not great, that we have to use Koin's internal function to get the full name
        return clazz.getFullName()
    }


    public open fun definitionId(serviceClass: KClass<*>, qualifier: Qualifier): String {
        val classId = classId(serviceClass)
        val qualifierId = qualifier.value
        return "$classId:$qualifierId"
    }

    public inline fun definitionId(definition: Definition<*>): String =
        definitionId(definition.service::class, definition.qualifier)


    override suspend fun registerService(definition: Definition<*>) {
        super.registerService(definition)

        val service = definition.service as Service
        val qualifier = definition.qualifier
        val bindTo = definition.bindTo

        bindTo.forEach { clazz ->
            val id = definitionId(clazz, qualifier)
            servicesMap.put(id, service)
        }

        val id = definitionId(definition)
        servicesMap[id] = service
    }

    override fun <C : Any> getServiceOrNull(serviceClass: KClass<out C>, qualifier: Qualifier): C? {
        val id = definitionId(serviceClass, qualifier)
        val service = servicesMap[id] ?: return null

        @Suppress("UNCHECKED_CAST", "kotlin:S6531")
        return service as C
    }

    override fun <C : Any> getService(serviceClass: KClass<out C>, qualifier: Qualifier): C {
        return getServiceOrNull(serviceClass, qualifier) ?: throw ServiceNotFoundException(serviceClass, qualifier)
    }
}


public open class RuntimeServicesManagerBuilder : ServicesManagerBuilder<RuntimeServicesManager>() {
    public var actualServicesMap: MutableMap<String, Service>? = null

    public fun servicesMap(map: MutableMap<String, Service>) {
        actualServicesMap = map
    }

    override fun build(): RuntimeServicesManager {
        val map = actualServicesMap ?: mutableMapOf()
        return RuntimeServicesManager(this, map)
    }
}

public inline fun RuntimeServicesManager(builder: RuntimeServicesManagerBuilder.() -> Unit): ServicesManager {
    val b = RuntimeServicesManagerBuilder().apply(builder)
    return b.build()
}