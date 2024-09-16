/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 * Copyright (c) 2024-present "Nik De Kur"
 */

@file:Suppress("NOTHING_TO_INLINE")

package dev.nikdekur.ndkore.scheduler.impl

import dev.nikdekur.ndkore.scheduler.AbstractScheduler
import dev.nikdekur.ndkore.scheduler.SchedulerTask
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onEach
import kotlin.coroutines.CoroutineContext
import kotlin.time.Duration

/**
 * # CoroutineScheduler
 *
 * CoroutineScheduler is a concrete implementation of [AbstractScheduler] that uses Kotlin coroutines
 * to schedule and run tasks.
 * It allows tasks to be run immediately, after a delay, or at fixed intervals.
 * This scheduler is suitable for applications that require a high degree of concurrency and efficiency.
 *
 * The tasks are managed within a [CoroutineScope], which ensures proper lifecycle management and
 * structured concurrency.
 *
 * ### Example usage:
 *
 * ```
 * val scheduler = CoroutineScheduler(CoroutineScope(Dispatchers.Default))
 *
 * // Run a task immediately
 * scheduler.runTask {
 *     println("Task running immediately")
 * }
 *
 * // Run a task after a 1000ms delay
 * scheduler.runTaskLater(1000L) {
 *     println("Task running after 1 second delay")
 * }
 *
 * // Run a task every 2000ms with an initial delay of 1000ms
 * scheduler.runTaskTimer(1000L, 2000L) {
 *     println("Task running every 2 seconds after an initial 1 second delay")
 * }
 * ```
 *
 * @property scope The [CoroutineScope] in which the tasks will be run.
 */
open class CoroutineScheduler(val scope: CoroutineScope) : AbstractScheduler(), CoroutineScope by scope {

    override fun runTask(task: suspend () -> Unit): SchedulerTask {
        val taskId = nextId()
        val job = scope.launch {
            task()
            unregisterTask(taskId)
        }
        return newTask(taskId, job)
    }


    @OptIn(ObsoleteCoroutinesApi::class)
    override fun runTaskTimer(delay: Duration, interval: Duration, task: suspend () -> Unit): SchedulerTask {
        val taskId = nextId()
        val flow = flow {
            delay(delay)
            emit(Unit)
            while (true) {
                delay(interval)
                emit(Unit)
            }
        }
            .onEach {
                scope.launch {
                    task()
                }
            }
            .onCompletion { unregisterTask(taskId) }
            .launchIn(scope)

        return newTask(taskId, flow)
    }

    override fun runTaskLater(delay: Duration, task: suspend () -> Unit): SchedulerTask {
        val taskId = nextId()

        val job = scope.launch {
            delay(delay)
            task()
        }.also {
            it.invokeOnCompletion {
                unregisterTask(taskId)
            }
        }

        return newTask(taskId, job)
    }

    fun newTask(id: Int, job: Job): SchedulerTask {
        val task = object : SchedulerTask {
            override fun cancel() {
                try {
                    job.cancel()
                } finally {
                    unregisterTask(id)
                }
            }

            override fun isCancelled() = job.isCancelled

            override val id: Int = id
        }

        registerTask(task)
        return task
    }


    override fun shutdown() {
        cancelAllTasks()
        scope.cancel()
    }


    companion object {
        /**
         * Creates a [CoroutineScheduler] with a [SupervisorJob] in the provided [CoroutineContext].
         *
         * @param context The [CoroutineContext] to be used for the [CoroutineScope].
         * @return A new instance of [CoroutineScheduler].
         */
        @JvmStatic
        inline fun fromSupervisor(context: CoroutineContext) =
            CoroutineScheduler(CoroutineScope(context + SupervisorJob()))

        /**
         * A globally available [CoroutineScheduler] using the [GlobalScope].
         * This should be used with caution as tasks in the GlobalScope are not bound to any specific lifecycle.
         */
        @JvmStatic
        @DelicateCoroutinesApi
        val Global by lazy {
            CoroutineScheduler(GlobalScope)
        }
    }
}