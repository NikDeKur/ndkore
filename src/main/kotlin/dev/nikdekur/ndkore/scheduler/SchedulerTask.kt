/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 * Copyright (c) 2024-present "Nik De Kur"
 */

package dev.nikdekur.ndkore.scheduler

import dev.nikdekur.ndkore.`interface`.Snowflake

/**
 * Represents a task scheduled for execution with unique identification.
 *
 * This interface extends [Snowflake] and provides methods to manage the lifecycle of a scheduled task,
 * including canceling the task and checking its cancellation status.
 *
 * **Overview of `SchedulerTask`:**
 * - **Cancellation:** Provides functionality to cancel the task and check if it has been cancelled.
 * - **Identification:** Inherits from [Snowflake] to include a unique identifier for each task.
 *
 * **Methods:**
 * - `cancel()`: Cancels the task if it has not already been cancelled.
 * - `isCancelled()`: Returns whether the task has been cancelled or not.
 *
 * **Example Usage:**
 * ```
 * // Create or obtain a SchedulerTask instance
 * val task: SchedulerTask = scheduler.runTask { /* Task logic here */ }
 *
 * // Check if the task is cancelled
 * if (task.isCancelled()) {
 *     println("The task has been cancelled.")
 * }
 *
 * // Cancel the task if it has not already been cancelled
 * task.cancel()
 * ```
 */
interface SchedulerTask : Snowflake<Int> {

    /**
     * Cancels the task.
     *
     * If the task has already been cancelled, this method does nothing.
     * The task will no longer be executed if it has not already started,
     * or will be interrupted if it is currently running.
     *
     * @see isCancelled
     */
    fun cancel()

    /**
     * Checks if the task has been cancelled.
     *
     * Returns `true` if the task has been cancelled and `false` otherwise.
     *
     * @return `true` if the task has been cancelled, `false` otherwise.
     */
    fun isCancelled(): Boolean

}