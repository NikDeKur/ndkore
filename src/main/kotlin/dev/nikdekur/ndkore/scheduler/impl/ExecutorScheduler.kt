/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 * Copyright (c) 2024 Nik De Kur
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
 * Implementation of the GlobalScheduler interface
 *
 * Should create a new thread to run the tasks.
 *
 * Tasks should be split between different holders.
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

    /**
     * Starts a task with a specified interval.
     *
     * @param delay Delay before the first task launch (in milliseconds).
     * @param interval Interval between further task launches (in milliseconds).
     * @param task The task to be executed.
     */
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

    /**
     * Starts the execution of a task after a specified time has elapsed.
     *
     * @param delay Delay before the task execution.
     * @param task The task to be executed.
     */
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
     * Shutdown the executor service to ensure all tasks are completed.
     */
    fun shutdownNow() {
        tasks.clear()
        executor.shutdownNow()
    }

    /**
     * Shutdown the executor service and wait for all tasks to complete.
     */
    fun shutdown() {
        tasks.clear()
        executor.shutdown()
    }

    fun shutdown(time: Long, unit: TimeUnit) {
        tasks.clear()
        executor.awaitTermination(time, unit)
    }

    protected fun newTask(id: Int, future: Future<*>): SchedulerTask {
        val task = object : SchedulerTask {
            override val scheduler: ExecutorScheduler = this@ExecutorScheduler
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
        fun threadPool(size: Int): ExecutorScheduler {
            return ExecutorScheduler(Executors.newScheduledThreadPool(size))
        }

        fun singleThread(): ExecutorScheduler {
            return ExecutorScheduler(Executors.newSingleThreadScheduledExecutor())
        }
    }
}
