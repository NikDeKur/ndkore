/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 * Copyright (c) 2024-present "Nik De Kur"
 */

package dev.nikdekur.ndkore.scheduler.impl

import dev.nikdekur.ndkore.scheduler.AbstractScheduler
import dev.nikdekur.ndkore.scheduler.SchedulerTask
import kotlinx.coroutines.runBlocking
import java.util.concurrent.Executors
import java.util.concurrent.Future
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.TimeUnit

/**
 * ExecutorScheduler is a concrete implementation of [AbstractScheduler] that uses a Java
 * [ScheduledExecutorService] to schedule and run tasks. It allows tasks to be run immediately,
 * after a delay, or at fixed intervals. This scheduler is suitable for applications that require
 * precise timing and integration with existing Java concurrency frameworks.
 *
 * Example usage:
 *
 * ```
 * val scheduler = ExecutorScheduler(Executors.newScheduledThreadPool(4))
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
 * @param executor The [ScheduledExecutorService] to be used for scheduling tasks.
 */
open class ExecutorScheduler(val executor: ScheduledExecutorService) : AbstractScheduler() {

    override fun runTask(task: suspend () -> Unit): SchedulerTask {
        val taskId = nextId()
        val wrapped = Runnable {
            try {
                runBlocking {
                    task.invoke()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                unregisterTask(taskId)
            }
        }

        val future = executor.submit(wrapped)

        val schedulerTask = newTask(taskId, future)

        return schedulerTask
    }

    override fun runTaskTimer(delay: Long, interval: Long, task: suspend () -> Unit): SchedulerTask {
        val taskId = nextId()
        val wrapped = Runnable {
            try {
                runBlocking {
                    task.invoke()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                unregisterTask(taskId)
            }
        }

        val future = executor.scheduleAtFixedRate(wrapped, delay, interval, TimeUnit.MILLISECONDS)

        val schedulerTask = newTask(taskId, future)

        return schedulerTask
    }


    override fun runTaskLater(delay: Long, task: suspend () -> Unit): SchedulerTask {

        val taskId = nextId()
        val wrapped = Runnable {
            try {
                runBlocking {
                    task.invoke()
                }
            } catch (e: Throwable) {
                e.printStackTrace()
            } finally {
                unregisterTask(taskId)
            }
        }

        val future = executor.schedule(wrapped, delay, TimeUnit.MILLISECONDS)

        val sTask = newTask(taskId, future)
        return sTask
    }

    /**
     * Shutdown the executor service immediately, attempting to stop all actively executing tasks.
     */
    fun shutdownNow() {
        tasks.clear()
        executor.shutdownNow()
    }

    /**
     * Initiates an orderly shutdown in which previously submitted tasks are executed,
     * but no new tasks will be accepted.
     */
    fun shutdown() {
        tasks.clear()
        executor.shutdown()
    }

    /**
     * Initiates an orderly shutdown in which previously submitted tasks are executed,
     * and waits for the specified time for the termination of all running tasks.
     *
     * @param time The maximum time to wait.
     * @param unit The time unit of the time argument.
     */
    fun shutdown(time: Long, unit: TimeUnit) {
        tasks.clear()
        executor.awaitTermination(time, unit)
    }

    protected fun newTask(id: Int, future: Future<*>): SchedulerTask {
        val task = object : SchedulerTask {
            override val id: Int = id

            override fun cancel() {
                future.cancel(false)
                unregisterTask(id)
            }

            override fun isCancelled(): Boolean {
                return future.isCancelled
            }
        }
        registerTask(task)
        return task
    }


    companion object {
        /**
         * Creates an [ExecutorScheduler] with a thread pool of the specified size.
         *
         * @param size The number of threads in the pool.
         * @return A new instance of [ExecutorScheduler].
         */
        @JvmStatic
        fun threadPool(size: Int): ExecutorScheduler {
            return ExecutorScheduler(Executors.newScheduledThreadPool(size))
        }

        /**
         * Creates an [ExecutorScheduler] with a single-threaded executor.
         *
         * @return A new instance of [ExecutorScheduler].
         */
        @JvmStatic
        fun singleThread(): ExecutorScheduler {
            return ExecutorScheduler(Executors.newSingleThreadScheduledExecutor())
        }
    }
}
