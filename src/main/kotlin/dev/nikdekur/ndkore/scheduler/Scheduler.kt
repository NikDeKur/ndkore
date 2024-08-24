/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 * Copyright (c) 2024-present "Nik De Kur"
 */

@file:Suppress("NOTHING_TO_INLINE")

package dev.nikdekur.ndkore.scheduler

import dev.nikdekur.ndkore.scheduler.impl.CoroutineScheduler
import dev.nikdekur.ndkore.scheduler.impl.ExecutorScheduler
import java.util.concurrent.ScheduledExecutorService
import kotlin.time.Duration

/**
 * The scheduler interface is used to run tasks in different thread pools.
 *
 * There are several implementations of this interface, such as
 * - [CoroutineScheduler] - runs tasks in the kotlin coroutines context
 * - [ExecutorScheduler] - runs tasks using the java [ScheduledExecutorService]
 *
 * Different implementations can be used depending on the requirements of the project.
 * For example, [CoroutineScheduler] allows to run very big number of tasks in a single thread pool,
 * while [ExecutorScheduler] is precise in millisecond's timings (while coroutines are not).
 */
interface Scheduler {

    /**
     * Starts a task in the scheduler.
     *
     * @param task The task to be executed.
     * @return The task object.
     */
    fun runTask(task: suspend () -> Unit): SchedulerTask

    /**
     * Starts a task with a specified interval.
     *
     * @param delay Delay before the first task launch.
     * @param interval Interval between further task launches.
     * @param task The task to be executed.
     * @return The task object.
     */
    fun runTaskTimer(delay: Duration, interval: Duration, task: suspend () -> Unit): SchedulerTask

    /**
     * Starts the execution of a task after a specified time has elapsed.
     *
     * @param delay Delay before the task execution.
     * @param task The task to be executed.
     * @return The task object.
     */
    fun runTaskLater(delay: Duration, task: suspend () -> Unit): SchedulerTask


    /**
     * Cancels all running tasks.
     */
    fun cancelAllTasks()
}

/**
 * Starts a task with a specified interval.
 *
 * @param interval Interval between further task launches.
 * @param task The task to be executed.
 * @return The task object.
 */
inline fun Scheduler.runTaskTimer(
    interval: Duration,
    noinline task: suspend () -> Unit
) = runTaskTimer(interval, interval, task)