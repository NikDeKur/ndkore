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
     * May throw an exception which will be caught in [doEnable]
     *
     * @see doEnable
     */
    public open fun onEnable() {
        // Do nothing by default
    }

    /**
     * Function that will be called when the service is disabling.
     *
     * May throw an exception which will be caught in [doDisable]
     *
     * @see doDisable
     */
    public open fun onDisable() {
        // Do nothing by default
    }


    public override fun doEnable() {
        state = State.Enabling
        try {
            onEnable()
            state = State.Enabled
        } catch (e: Exception) {
            logger.error(e) { "Failed to enable service: ${this::class.simpleName}" }
            state = State.ErrorEnabling(e)
        }
    }


    public override fun doDisable() {
        state = State.Disabling
        try {
            onDisable()
            state = State.Disabled
        } catch (e: Exception) {
            logger.error(e) { "Failed to disable service: ${this::class.simpleName}" }
            state = State.ErrorDisabling(e)
        }
    }

    public override fun doReload() {
        doDisable()
        doEnable()
    }

}

