/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 * Copyright (c) 2024-present "Nik De Kur"
 */

@file:Suppress("NOTHING_TO_INLINE")

package dev.nikdekur.ndkore.service

/**
 * A service that can be registered to the [ServicesManager].
 *
 * Service is a module that can be loaded and unloaded.
 *
 * Service have to be able to do a reloading infinite times.
 *
 * @param A The type of the application.
 * @see ServicesManager
 */
interface Service<A> {

    /**
     * The manager that manages this service.
     */
    val manager: ServicesManager<A>

    /**
     * The application instance.
     *
     * This property provides access to the application-specific data or context that services may depend on.
     */
    val app
        get() = manager.app

    /**
     * Load's service.
     *
     * This method should be called after service has been registered.
     */
    fun onLoad() {
        // Do nothing by default
    }

    /**
     * Unloads service.
     */
    fun onUnload() {
        // Do nothing by default
    }

    /**
     * Dependencies that this service has.
     *
     * [ServicesManager] will load all dependencies in the order to satisfy all dependencies of all services.
     */
    val dependencies: Dependencies
        get() = Dependencies.none()
}

