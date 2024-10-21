/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 * Copyright (c) 2024-present "Nik De Kur"
 */

package dev.nikdekur.ndkore.service.manager

import dev.nikdekur.ndkore.service.Service
import dev.nikdekur.ndkore.service.ServicesComponent
import kotlin.reflect.KClass

/**
 * # Services Manager
 *
 * Interface for managing the lifecycle and registration of services within an application.
 *
 * Services are components that provide functionality to an application.
 * They can be registered with a service manager and injected into other components.
 *
 * Services provide an easy way to split the application into smaller, more manageable parts.
 * Which also have load and unload lifecycle methods.
 */
public interface ServicesManager : ServicesComponent {

    override val manager: ServicesManager
        get() = this

    /**
     * The current state of the service manager.
     *
     * @see State
     */
    public val state: State

    /**
     * A collection of all registered services.
     *
     * This property provides access to an iterable collection of all services that have been registered
     * with the service manager.
     *
     * Guaranteed to return services in the order of their dependencies.
     */
    public val services: Collection<Service>

    /**
     * Registers a service with the manager.
     *
     * This method registers a service and binds it to one or more classes, allowing for dependency injection.
     * Injecting a service will always return the same instance.
     *
     * If manager is enabled, the service will be enabled immediately.
     * If some service is already registered with the same class, it will be replaced
     * and the old service will be disabled (if manager is enabled).
     *
     * @param service The service to be registered.
     * @param bindTo One or more classes to which the service should be bound.
     */
    public suspend fun <C : Any, S> registerService(service: S, vararg bindTo: KClass<out C>) where S : C

    /**
     * Retrieves a service by its class, or returns null if it is not found.
     *
     * This method attempts to locate a service by its class
     * or class it is binded to.
     * It will return the service instance if found,
     * or null if no service is found.
     *
     * @param C The type of the service to retrieve.
     * @param serviceClass The KClass of the service to retrieve.
     * @return The service instance, or null if not found.
     */
    public fun <C : Any> getServiceOrNull(serviceClass: KClass<out C>): C?

    /**
     * Retrieves a service by its class.
     *
     * This method attempts to locate a service by its class
     * or class it is binded to.
     *
     * @param C The type of the service to retrieve.
     * @param serviceClass The KClass of the service to retrieve.
     * @return The service instance.
     * @throws ServiceNotFoundException If the service is not found.
     */
    public fun <C : Any> getService(serviceClass: KClass<out C>): C

    /**
     * Enable service manager.
     *
     * This method will enable all registered services ([AbstractService.doEnable])
     * and will enable every service in the correct order to satisfy all dependencies.
     */
    public suspend fun enable()

    /**
     * Disable service manager.
     *
     * This method will disable all registered services ([AbstractService.doDisable])
     * and will disable every service in the correct order to satisfy all dependencies.
     */
    public suspend fun disable()


    /**
     * Reload service manager.
     *
     * This method call is equivalent to calling [disable] and [enable] in sequence.
     *
     * @see enable
     * @see disable
     */
    public suspend fun reload() {
        disable()
        enable()
    }

    /**
     * # Services Manager's State
     *
     * Represent the state of a Service Manager
     */
    public enum class State {
        ENABLING,
        ENABLED,
        DISABLING,
        DISABLED
    }
}

/**
 * Retrieves a service by its class, or returns null if it is not found.
 *
 * This extension function simplifies the retrieval of services by using reified generics to infer the service class.
 *
 * @param C The type of the service to retrieve.
 * @return The service instance, or null if not found.
 */
public inline fun <reified C : Any> ServicesManager.getServiceOrNull() = getServiceOrNull(C::class)

/**
 * Retrieves a service by its class.
 *
 * This extension function simplifies the retrieval of services by using reified generics to infer the service class.
 * It will return the service instance if found, or throw a [ServiceNotFoundException] if not found.
 *
 * @param C The type of the service to retrieve.
 * @return The service instance.
 * @throws ServiceNotFoundException If the service is not found.
 */
public inline fun <reified C : Any> ServicesManager.getService() = getService(C::class)
