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
     * A collection of all registered services.
     *
     * This property provides access to an iterable collection of all services that have been registered
     * with the service manager.
     */
    val services: Collection<Service<*>>

    /**
     * Registers a service with the manager.
     *
     * This method registers a service and binds it to one or more classes, allowing for dependency injection.
     * Injecting a service will always return the same instance.
     *
     * @param service The service to be registered.
     * @param bindTo One or more classes to which the service should be bound.
     */
    fun <S : Service<*>> registerService(service: S, vararg bindTo: KClass<out S>)

    /**
     * Retrieves a service by its class, or returns null if it is not found.
     *
     * This method attempts to locate a service by its class.
     * It will return the service instance if found,
     * or null if no service matching the class or its superclasses is registered.
     *
     * @param S The type of the service to retrieve.
     * @param serviceClass The KClass of the service to retrieve.
     * @return The service instance, or null if not found.
     */
    fun <S : Service<*>> getServiceOrNull(serviceClass: KClass<out S>): S?

    /**
     * Retrieves a service by its class.
     *
     * This method attempts to locate a service by its class.
     * It will return the service instance if found,
     * or throw a [ServiceNotFoundException] if no service matching the class or its superclasses is registered.
     *
     * @param S The type of the service to retrieve.
     * @param serviceClass The KClass of the service to retrieve.
     * @return The service instance.
     * @throws ServiceNotFoundException If the service is not found.
     */
    fun <S : Service<*>> getService(serviceClass: KClass<out S>): S

    /**
     * Loads all registered services.
     *
     * This method should be called after all services have been registered.
     * It will invoke the [Service.onLoad]
     * method on all registered services to initialize them.
     */
    fun loadAll()

    /**
     * Unloads all registered services.
     *
     * This method will invoke the [Service.onUnload] method on all registered services, allowing them to perform
     * any necessary cleanup before being unloaded.
     */
    fun unloadAll()

    /**
     * Reloads all registered services.
     *
     * This method is equivalent to calling [unloadAll] followed by [loadAll].
     * It allows for reinitializing
     * services without restarting the entire application.
     */
    fun reloadAll() {
        unloadAll()
        loadAll()
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
