/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 * Copyright (c) 2024-present "Nik De Kur"
 */

package dev.nikdekur.ndkore.service

import dev.nikdekur.ndkore.di.Qualifier
import dev.nikdekur.ndkore.service.manager.ServicesManager
import kotlin.properties.ReadOnlyProperty

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
public interface ServicesComponent {

    /**
     * The manager that manages this service.
     */
    public val manager: ServicesManager
}

/**
 * Retrieves a service by its class, or returns null if it is not found.
 *
 * This extension function simplifies the retrieval of services by using reified generics to infer the service class.
 *
 * @param C The type of the service to retrieve.
 * @return The service instance, or null if not found.
 */
public inline fun <reified C : Service> ServicesComponent.getOrNull(
    qualifier: Qualifier = Qualifier.Empty
): C? = manager.getServiceOrNull(C::class, qualifier)

/**
 * Retrieves a service by its class.
 *
 * This extension function simplifies the retrieval of services by using reified generics to infer the service class.
 * It will return the service instance if found, or throw a [dev.nikdekur.ndkore.di.DependentServiceNotFoundException] if not found.
 *
 * @param C The type of the service to retrieve.
 * @return The service instance.
 * @throws dev.nikdekur.ndkore.di.DependentServiceNotFoundException If the service is not found.
 */
public inline fun <reified C : Service> ServicesComponent.get(
    qualifier: Qualifier = Qualifier.Empty
): C = manager.getService(C::class, qualifier)

/**
 * Return a property delegate that provides a service instance.
 *
 * Service instance every time will be retrieved from the manager.
 *
 * @param C The type of the service to inject.
 * @return A property delegate that provides the service instance, which might return null if no service found.
 */
public inline fun <reified C : Service> ServicesComponent.injectOrNull(
    qualifier: Qualifier = Qualifier.Empty
): ReadOnlyProperty<Any?, C?> =
    ReadOnlyProperty { _, _ -> getOrNull(qualifier) }

/**
 * Return a property delegate that provides a service instance.
 *
 * Service instance every time will be retrieved from the manager.
 *
 * @param C The type of the service to inject.
 * @return A property delegate that provides the service instance.
 * @throws dev.nikdekur.ndkore.di.DependentServiceNotFoundException If the service is not found.
 */
public inline fun <reified C : Service> ServicesComponent.inject(
    qualifier: Qualifier = Qualifier.Empty
): ReadOnlyProperty<Any?, C> = ReadOnlyProperty { _, _ -> get<C>(qualifier) }


