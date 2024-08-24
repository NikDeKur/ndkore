/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 * Copyright (c) 2024-present "Nik De Kur"
 */

@file:Suppress("NOTHING_TO_INLINE")

package dev.nikdekur.ndkore.service

import dev.nikdekur.ndkore.ext.error
import org.slf4j.LoggerFactory

val logger = LoggerFactory.getLogger(Service::class.java)

/**
 * A service that can be registered to the [ServicesManager].
 *
 * Service is a module that can be enabled and disabled.
 *
 * Service have to be able to do a reload infinitely.
 *
 * @param A The type of the application.
 * @see ServicesManager
 */
interface Service<A : Any> : ServicesComponent {

    /**
     * Dependencies that this service has.
     *
     * [ServicesManager] will enable all dependencies in the order
     * to satisfy all dependencies of all services.
     */
    val dependencies: Dependencies
        get() = Dependencies.none()

    /**
     * Function that will be called when the service is enabling.
     *
     * May throw an exception which will be caught in [doEnable]
     *
     * @see doEnable
     */
    fun onEnable() {
        // Do nothing by default
    }

    /**
     * Function that will be called when the service is disabling.
     *
     * May throw an exception which will be caught in [doDisable]
     *
     * @see doDisable
     */
    fun onDisable() {
        // Do nothing by default
    }

    /**
     * Enables the service.
     *
     * Calls [onEnable] function and catches all exceptions.
     */
    fun doEnable() {
        try {
            onEnable()
        } catch (e: Exception) {
            logger.error(e) {
                "Failed to enable service: ${this::class.simpleName}"
            }
        }
    }

    /**
     * Disables the service.
     *
     * Calls [onDisable] function and catches all exceptions.
     */
    fun doDisable() {
        try {
            onDisable()
        } catch (e: Exception) {
            logger.error(e) {
                "Failed to disable service: ${this::class.simpleName}"
            }
        }
    }

}

