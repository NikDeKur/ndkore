/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 * Copyright (c) 2024-present "Nik De Kur"
 */

package dev.nikdekur.ndkore.service.manager

import dev.nikdekur.ndkore.di.Definition
import dev.nikdekur.ndkore.di.DependencyNotFoundException
import dev.nikdekur.ndkore.di.Qualifier
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
     * @param definition The service definition to register.
     */
    public suspend fun registerService(definition: ServiceDefinition)

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
    public fun <C : Service> getServiceOrNull(
        serviceClass: KClass<out C>,
        qualifier: Qualifier = Qualifier.Empty
    ): C?

    /**
     * Retrieves a service by its class.
     *
     * This method attempts to locate a service by its class
     * or class it is binded to.
     *
     * @param C The type of the service to retrieve.
     * @param serviceClass The KClass of the service to retrieve.
     * @return The service instance.
     * @throws DependencyNotFoundException If the service is not found.
     */
    public fun <C : Service> getService(
        serviceClass: KClass<out C>,
        qualifier: Qualifier = Qualifier.Empty
    ): C

    /**
     * Enable service manager.
     *
     * This method will enable all registered services ([Service.enable])
     * and will enable every service in the correct order to satisfy all dependencies.
     */
    public suspend fun enable()

    /**
     * Disable service manager.
     *
     * This method will disable all registered services ([Service.disable])
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

public typealias ServiceDefinition = Definition<out Service>

public enum class OnServiceOperation {
    ENABLE, DISABLE, RELOAD
}

public data class OnErrorContext(
    val manager: DIServicesManager,
    val service: Service,
    val operation: OnServiceOperation,
    val exception: Throwable
)

public suspend inline fun ServicesManager.registerService(
    service: Service,
    qualifier: Qualifier = Qualifier.Empty
) {
    val definition = Definition(service, qualifier)
    registerService(definition)
}

public suspend inline fun <S : Service> ServicesManager.registerService(
    definition: Definition<S>,
    qualifier: Qualifier = Qualifier.Empty
) {
    val definition = Definition(definition.obj, qualifier, definition.bindTo)
    registerService(definition)
}
