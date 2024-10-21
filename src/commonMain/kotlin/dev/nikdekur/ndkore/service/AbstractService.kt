/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 * Copyright (c) 2024-present "Nik De Kur"
 */


package dev.nikdekur.ndkore.service

import dev.nikdekur.ndkore.service.Service.State
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
    public open suspend fun onEnable() {
        // Do nothing by default
    }

    /**
     * Function that will be called when the service is disabling.
     *
     * May throw an exception which will be caught in [disable]
     *
     * @see disable
     */
    public open suspend fun onDisable() {
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
    public open suspend fun onReload() {
        onDisable()
    }


    public override suspend fun enable() {
        state = State.Enabling
        try {
            onEnable()
            state = State.Enabled
        } catch (e: Exception) {
            logger.error(e) { "Failed to enable service: ${this::class.simpleName}" }
            state = State.ErrorEnabling(e)
        }
    }


    public override suspend fun disable() {
        state = State.Disabling
        try {
            onDisable()
            state = State.Disabled
        } catch (e: Exception) {
            logger.error(e) { "Failed to disable service: ${this::class.simpleName}" }
            state = State.ErrorDisabling(e)
        }
    }


    override suspend fun reload() {
        state = State.Disabling
        try {
            onReload()
            state = State.Disabled
        } catch (e: Exception) {
            logger.error(e) { "Failed to reload service: ${this::class.simpleName}" }
            state = State.ErrorDisabling(e)
        }

        enable()
    }
}

