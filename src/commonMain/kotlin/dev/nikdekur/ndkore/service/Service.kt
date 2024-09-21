/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 * Copyright (c) 2024-present "Nik De Kur"
 */


package dev.nikdekur.ndkore.service

import io.github.oshai.kotlinlogging.KotlinLogging


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

    open val logger = KotlinLogging.logger { }
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
        data object Enabling : State
        data class ErrorEnabling(val e: Exception) : State
        data object Enabled : State

        data object Disabling : State
        data class ErrorDisabling(val e: Exception) : State
        data object Disabled : State

        fun isErrored() = this is ErrorEnabling || this is ErrorDisabling
    }

}

