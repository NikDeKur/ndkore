/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 * Copyright (c) 2024-present "Nik De Kur"
 */


package dev.nikdekur.ndkore.service

import dev.nikdekur.ndkore.service.Service.State
import dev.nikdekur.ndkore.service.manager.OnServiceOperation
import io.github.oshai.kotlinlogging.KLogger
import io.github.oshai.kotlinlogging.KotlinLogging


/**
 * # AbstractService
 *
 * Abstract implementation of the [Service] interface.
 *
 * This class automatically handles the state of the service and provides a logger.
 *
 * @see Service
 */
public abstract class AbstractService : Service {

    public open val logger: KLogger = KotlinLogging.logger { }
    public override var state: State = State.Disabled


    public override val dependencies: Dependencies
        get() = Dependencies.none()

    /**
     * Function that will be called when the service is enabling.
     *
     * May throw an exception which will be caught in [enable]
     *
     * @see enable
     */
    protected open suspend fun onEnable() {
        // Do nothing by default
    }

    /**
     * Function that will be called when the service is disabling.
     *
     * May throw an exception which will be caught in [disable]
     *
     * @see disable
     */
    protected open suspend fun onDisable() {
        // Do nothing by default
    }

    /**
     * Function that will be called when the service is reloading.
     *
     * May throw an exception which will be caught in [reload]
     *
     * By default calls [onDisable]
     *
     * @see reload
     */
    protected open suspend fun onReload() {
        disable()
        enable()
    }

    public override suspend fun enable() {
        state = State.Enabling
        try {
            onEnable()
            state = State.Enabled
        } catch (e: Exception) {
            state = State.ErrorEnabling(e)
            onError(OnServiceOperation.ENABLE, e)
        }
    }


    public override suspend fun disable() {
        state = State.Disabling
        try {
            onDisable()
            state = State.Disabled
        } catch (e: Exception) {
            state = State.ErrorDisabling(e)
            onError(OnServiceOperation.DISABLE, e)
        }
    }


    override suspend fun reload() {
        try {
            onReload()
        } catch (e: Exception) {
            state = State.ErrorEnabling(e)
            onError(OnServiceOperation.RELOAD, e)
        }
    }


    protected open fun onError(operation: OnServiceOperation, exception: Exception) {
        val text = when (operation) {
            OnServiceOperation.ENABLE -> "enable"
            OnServiceOperation.DISABLE -> "disable"
            OnServiceOperation.RELOAD -> "reload"
        }

        logger.error(exception) { "Failed to $text service: ${this::class.simpleName}" }
    }

}

