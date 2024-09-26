/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 * Copyright (c) 2024-present "Nik De Kur"
 */

package dev.nikdekur.ndkore.scheduler

/**
 * Manages a collection of schedulers, each associated with a unique identifier.
 *
 * This class provides functionality to create and manage multiple schedulers, where each scheduler
 * is identified by a unique key of type [H]. The class uses a factory function to create schedulers
 * when they are requested for the first time. This is useful in scenarios where different components
 * (e.g., plugins in a system like Bukkit) need their own scheduler instances.
 *
 * **Overview of `SchedulerManager`:**
 * - **Schedulers Pool:** Maintains a pool of schedulers, each identified by a unique key.
 * - **Lazy Initialization:** Schedulers are created on-demand using the provided factory function.
 * - **Task Management:** Provides functionality to cancel all tasks across all managed schedulers.
 *
 * **Type Parameters:**
 * - `H`: The type of the unique key used to identify each scheduler. This key is used to associate
 *   schedulers with different components or contexts.
 *
 * **Properties:**
 * - `schedulers`: A concurrent map that holds the relationship between each key of type [H] and its
 *   corresponding [Scheduler] instance.
 *
 * **Functions:**
 * - `getScheduler(holder: H): Scheduler`
 *   - Retrieves the scheduler associated with the given key. If no scheduler exists for the key, it
 *     is created using the provided factory function.
 *   - **Parameters:**
 *     - `holder: H` - The unique key to identify the scheduler.
 *   - **Returns:**
 *     - The [Scheduler] instance associated with the given key.
 *
 * - `cancelAllTasks()`
 *   - Cancels all tasks in all schedulers managed by this manager. This is useful for cleaning up
 *     and stopping all ongoing tasks when necessary.
 *   - **Returns:**
 *     - No return value.
 *
 * **Example Usage:**
 * ```
 * // Define a factory function to create schedulers
 * val factory: (String) -> Scheduler = { key -> SomeSchedulerImplementation(key) }
 *
 * // Create a SchedulerManager instance
 * val manager = SchedulerManager(factory)
 *
 * // Retrieve or create a scheduler for a specific key
 * val scheduler = manager.getScheduler("pluginScheduler")
 *
 * // Cancel all tasks across all schedulers managed by this manager
 * manager.cancelAllTasks()
 * ```
 *
 * **Note:**
 * - Ensure that the provided factory function returns a valid [Scheduler] instance for each key.
 * - The `cancelAllTasks()` method will affect all schedulers managed by the instance, so use it
 *   carefully depending on the context of your application.
 */
public open class SchedulerManager<H>(public val factory: (H) -> Scheduler) {

    /**
     * A concurrent map that holds the mapping between each unique key of type [H] and its corresponding [Scheduler] instance.
     */
    public val schedulers: MutableMap<H, Scheduler> = mutableMapOf<H, Scheduler>()

    /**
     * Retrieves the scheduler associated with the specified key. If the scheduler does not exist, it is created
     * using the provided factory function.
     *
     * @param holder The unique key used to identify the scheduler.
     * @return The [Scheduler] instance associated with the specified key.
     */
    public fun getScheduler(holder: H): Scheduler {
        return schedulers.getOrPut(holder) { factory(holder) }
    }

    /**
     * Cancels all tasks in all schedulers managed by this instance.
     *
     * This method iterates over all schedulers and invokes their `cancelAllTasks()` method to stop
     * all ongoing tasks and perform any necessary cleanup.
     */
    public fun cancelAllTasks() {
        schedulers.values.forEach { it.cancelAllTasks() }
    }
}
