/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 * Copyright (c) 2024 Nik De Kur
 */

package dev.nikdekur.ndkore.scheduler

import dev.nikdekur.ndkore.interfaces.Snowflake

interface SchedulerTask : Snowflake<Int> {

    /**
     * The scheduler that created this task.
     *
     * @see Scheduler
     */
    val scheduler: Scheduler

    /**
     * Cancels the task.
     *
     * If the task has already been cancelled, the method does nothing.
     * @see isCancelled
     */
    fun cancel()

    /**
     * Checks if the task has been cancelled.
     *
     * @return true if the task has been cancelled, false otherwise.
     */
    fun isCancelled(): Boolean
}