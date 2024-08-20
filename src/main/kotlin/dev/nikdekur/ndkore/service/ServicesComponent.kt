/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 * Copyright (c) 2024-present "Nik De Kur"
 */

package dev.nikdekur.ndkore.service

/**
 * # Services Component
 *
 * Represents a component that has access to a [ServicesManager].
 *
 * This interface is shortcut for accessing components from a [ServicesManager].
 *
 * ### Example Usage:
 * ```kotlin
 * class MyComponent(override val manager: ServicesManager) : ServicesComponent {
 *    // We assume AnotherService is a service class and is registered in the manager.
 *    val anotherService by inject<AnotherService>()
 * }
 * ```
 *
 * @see ServicesManager
 */
interface ServicesComponent {

    /**
     * The manager that manages this service.
     */
    val manager: ServicesManager
}

/**
 * Retrieves a service by its class, or returns null if it is not found.
 *
 * This extension function simplifies the retrieval of services by using reified generics to infer the service class.
 *
 * @param S The type of the service to retrieve.
 * @return The service instance, or null if not found.
 */
inline fun <reified S : Service<*>> ServicesComponent.getOrNull() = manager.getServiceOrNull(S::class)

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
inline fun <reified S : Service<*>> ServicesComponent.get() = manager.getService(S::class)

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
inline fun <reified S : Service<*>> ServicesComponent.inject() = lazy { get<S>() }

