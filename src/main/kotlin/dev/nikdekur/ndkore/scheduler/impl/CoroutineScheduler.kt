@file:Suppress("NOTHING_TO_INLINE")

package dev.nikdekur.ndkore.scheduler.impl

import dev.nikdekur.ndkore.scheduler.AbstractScheduler
import dev.nikdekur.ndkore.scheduler.Scheduler
import dev.nikdekur.ndkore.scheduler.SchedulerTask
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onEach
import kotlin.coroutines.CoroutineContext

class CoroutineScheduler(val scope: CoroutineScope) : AbstractScheduler(), CoroutineScope by scope {

    override fun runTask(task: suspend () -> Unit): SchedulerTask {
        val taskId = nextId()
        val job = scope.launch {
            task()
            unregisterTask(taskId)
        }
        return newTask(taskId, job)
    }


    @OptIn(ObsoleteCoroutinesApi::class)
    override fun runTaskTimer(delay: Long, interval: Long, task: suspend () -> Unit): SchedulerTask {
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

    override fun runTaskLater(delay: Long, task: suspend () -> Unit): SchedulerTask {
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
            override val scheduler: Scheduler = this@CoroutineScheduler

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


    companion object {
        inline fun fromSupervisor(context: CoroutineContext) =
            CoroutineScheduler(CoroutineScope(context + SupervisorJob()))
    }
}