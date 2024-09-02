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
 * @see ServicesManager
 */
abstract class Service : ServicesComponent {

    open val logger = LoggerFactory.getLogger(javaClass)
    open var state: State = State.Disabled

    /**
     * Dependencies that this service has.
     *
     * [ServicesManager] will enable all dependencies in the order
     * to satisfy all dependencies of all services.
     */
    open val dependencies: Dependencies
        get() = Dependencies.none()

    /**
     * Function that will be called when the service is enabling.
     *
     * May throw an exception which will be caught in [doEnable]
     *
     * @see doEnable
     */
    open fun onEnable() {
        // Do nothing by default
    }

    /**
     * Function that will be called when the service is disabling.
     *
     * May throw an exception which will be caught in [doDisable]
     *
     * @see doDisable
     */
    open fun onDisable() {
        // Do nothing by default
    }

    /**
     * Enables the service.
     *
     * Calls [onEnable] function and catches all exceptions.
     */
    open fun doEnable() {
        state = State.Enabling
        try {
            onEnable()
            state = State.Enabled
        } catch (e: Exception) {
            logger.error(e) { "Failed to enable service: ${this::class.simpleName}" }
            state = State.ErrorEnabling(e)
        }
    }

    /**
     * Disables the service.
     *
     * Calls [onDisable] function and catches all exceptions.
     */
    open fun doDisable() {
        state = State.Disabling
        try {
            onDisable()
            state = State.Disabled
        } catch (e: Exception) {
            logger.error(e) { "Failed to disable service: ${this::class.simpleName}" }
            state = State.ErrorDisabling(e)
        }
    }

    /**
     * Reloads the service.
     *
     * Calls [doDisable] and [doEnable] functions in sequence.
     */
    open fun doReload() {
        doDisable()
        doEnable()
    }


    sealed interface State {
        object Enabling : State
        data class ErrorEnabling(val e: Exception) : State
        object Enabled : State

        object Disabling : State
        data class ErrorDisabling(val e: Exception) : State
        object Disabled : State

        fun isErrored() = this is ErrorEnabling || this is ErrorDisabling
    }

}

