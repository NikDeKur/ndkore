/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 * Copyright (c) 2024-present "Nik De Kur"
 */

@file:OptIn(ExperimentalAtomicApi::class)

package dev.nikdekur.ndkore.scheduler

import co.touchlab.stately.collections.ConcurrentMutableMap
import kotlin.concurrent.atomics.AtomicInt
import kotlin.concurrent.atomics.ExperimentalAtomicApi
import kotlin.concurrent.atomics.incrementAndFetch

/**
 * # AbstractScheduler
 *
 * AbstractScheduler is an abstract base class that provides common functionality for different
 * scheduler implementations.
 * It manages the registration and cancellation of tasks using a
 * thread-safe map and provides unique task IDs.
 *
 * This class implements the [Scheduler] interface and provides base implementations for
 * managing tasks.
 * Subclasses are responsible for implementing the task scheduling methods.
 */
public abstract class AbstractScheduler : Scheduler {

    /**
     * A thread-safe map that holds all the registered tasks with their unique IDs.
     */
    public val tasks: MutableMap<Int, SchedulerTask> = ConcurrentMutableMap()

    // We assume that schedulers can use different threads to run tasks,
    // so we need to make thread-safe incrementation.
    public var tasksWas: AtomicInt = AtomicInt(0)

    /**
     * Generates the next unique task ID in a thread-safe manner.
     *
     * @return The next unique task ID.
     */
    public fun nextId(): Int {
        return tasksWas.incrementAndFetch()
    }

    /**
     * Registers a task by adding it to the tasks' map.
     *
     * @param task The task to register.
     */
    public fun registerTask(task: SchedulerTask) {
        tasks[task.id] = task
    }

    /**
     * Unregisters a task by removing it from the tasks' map.
     *
     * @param taskId The ID of the task to unregister.
     */
    public fun unregisterTask(taskId: Int) {
        tasks.remove(taskId)
    }

    override fun cancelAllTasks() {
        tasks.values.toList().forEach(SchedulerTask::cancel)
        tasks.clear()
    }
}