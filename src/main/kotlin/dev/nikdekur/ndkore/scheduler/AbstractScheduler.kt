/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 * Copyright (c) 2024-present "Nik De Kur"
 */

package dev.nikdekur.ndkore.scheduler

import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicInteger

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
abstract class AbstractScheduler : Scheduler {

    /**
     * A thread-safe map that holds all the registered tasks with their unique IDs.
     */
    val tasks = ConcurrentHashMap<Int, SchedulerTask>()

    // We assume that schedulers can use different threads to run tasks,
    // so we need to make thread-safe incrementation.
    var tasksWas = AtomicInteger(0)

    /**
     * Generates the next unique task ID in a thread-safe manner.
     *
     * @return The next unique task ID.
     */
    fun nextId(): Int {
        return tasksWas.incrementAndGet()
    }

    /**
     * Registers a task by adding it to the tasks' map.
     *
     * @param task The task to register.
     */
    fun registerTask(task: SchedulerTask) {
        tasks[task.id] = task
    }

    /**
     * Unregisters a task by removing it from the tasks' map.
     *
     * @param taskId The ID of the task to unregister.
     */
    fun unregisterTask(taskId: Int) {
        tasks.remove(taskId)
    }

    override fun cancelAllTasks() {
        tasks.values.forEach(SchedulerTask::cancel)
        tasks.clear()
    }
}