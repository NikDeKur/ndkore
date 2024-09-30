/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 * Copyright (c) 2024-present "Nik De Kur"
 */

package dev.nikdekur.ndkore.service.manager

import dev.nikdekur.ndkore.service.Service
import dev.nikdekur.ndkore.service.ServiceNotFoundException
import dev.nikdekur.ndkore.service.ServicesManager
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
    public val servicesMap: MutableMap<String, Service> = LinkedHashMap()
) : AbstractServicesManager() {

    public open fun classId(clazz: KClass<*>): String = clazz.toString()

    override fun <C : Any, S : C> registerService(service: S, vararg bindTo: KClass<out C>) {
        super.registerService(service, *bindTo)

        service as Service

        bindTo.forEach { clazz ->
            val id = classId(clazz)
            servicesMap[id] = service
        }

        val id = classId(service::class)
        servicesMap[id] = service
    }

    override fun <C : Any> getServiceOrNull(serviceClass: KClass<out C>): C? {
        val id = classId(serviceClass)
        val service = servicesMap[id] ?: return null

        @Suppress("UNCHECKED_CAST", "kotlin:S6531")
        return service as C
    }

    override fun <C : Any> getService(serviceClass: KClass<out C>): C {
        return getServiceOrNull(serviceClass) ?: throw ServiceNotFoundException(serviceClass)
    }
}