/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 * Copyright (c) 2024-present "Nik De Kur"
 */

package dev.nikdekur.ndkore.service

import org.checkerframework.checker.units.qual.A
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
 *
 * @param A The type representing the application context or environment that services may need to access.
 */
interface ServicesManager {

    /**
     * The current state of the service manager.
     *
     * @see State
     */
    val state: State

    /**
     * A collection of all registered services.
     *
     * This property provides access to an iterable collection of all services that have been registered
     * with the service manager.
     *
     * Guaranteed to return services in the order of their dependencies.
     */
    val services: Collection<Service<*>>

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
    fun <S : Service<*>> registerService(service: S, vararg bindTo: KClass<out S>)

    /**
     * Retrieves a service by its class, or returns null if it is not found.
     *
     * This method attempts to locate a service by its class
     * or class it is binded to.
     * It will return the service instance if found,
     * or null if no service is found.
     *
     * @param S The type of the service to retrieve.
     * @param serviceClass The KClass of the service to retrieve.
     * @return The service instance, or null if not found.
     */
    fun <S : Service<*>> getServiceOrNull(serviceClass: KClass<out S>): S?

    /**
     * Retrieves a service by its class.
     *
     * This method attempts to locate a service by its class
     * or class it is binded to.
     *
     * @param S The type of the service to retrieve.
     * @param serviceClass The KClass of the service to retrieve.
     * @return The service instance.
     * @throws ServiceNotFoundException If the service is not found.
     */
    fun <S : Service<*>> getService(serviceClass: KClass<out S>): S

    /**
     * Enable service manager.
     *
     * This method will enable all registered services ([Service.doEnable])
     * and will enable every service in the correct order to satisfy all dependencies.
     */
    fun enable()

    /**
     * Disable service manager.
     *
     * This method will disable all registered services ([Service.doDisable])
     * and will disable every service in the correct order to satisfy all dependencies.
     */
    fun disable()


    /**
     * Reload service manager.
     *
     * This method call is equivalent to calling [disable] and [enable] in sequence.
     *
     * @see enable
     * @see disable
     */
    fun reload() {
        disable()
        enable()
    }


    enum class State {
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
 * @param S The type of the service to retrieve.
 * @return The service instance, or null if not found.
 */
inline fun <reified S : Service<out Any>> ServicesManager.getServiceOrNull() = getServiceOrNull<S>(S::class)

/**
 * Retrieves a service by its class.
 *
 * This extension function simplifies the retrieval of services by using reified generics to infer the service class.
 * It will return the service instance if found, or throw a [ServiceNotFoundException] if not found.
 *
 * @param S The type of the service to retrieve.
 * @return The service instance.
 * @throws ServiceNotFoundException If the service is not found.
 */
inline fun <reified S : Service<*>> ServicesManager.getService() = getService(S::class)

/**
 * Lazily injects a service by its class.
 *
 * This extension function allows for lazy initialization of a service, which will be injected when first accessed.
 * It uses reified generics to infer the service class.
 *
 * @param S The type of the service to inject.
 * @return A lazy delegate that provides the service instance.
 * @throws ServiceNotFoundException If the service is not found.
 */
inline fun <reified S : Service<*>> ServicesManager.inject() = lazy { getService<S>() }
