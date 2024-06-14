package dev.nikdekur.ndkore.scheduler

import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicInteger

abstract class AbstractScheduler : Scheduler {

    val tasks = ConcurrentHashMap<Int, SchedulerTask>()

    // We assume that schedulers can use different threads to run tasks,
    // so we need to make thread-safe incrementation.
    private var tasksWas = AtomicInteger(0)
    protected fun nextId(): Int {
        return tasksWas.incrementAndGet()
    }

    internal fun registerTask(task: SchedulerTask) {
        tasks[task.id] = task
    }

    internal fun unregisterTask(taskId: Int) {
        tasks.remove(taskId)
    }

    override fun getTask(id: Int): SchedulerTask? {
        return tasks[id]
    }

    /**
     * Cancels all running tasks for the specified holder.
     */
    override fun cancelAllTasks() {
        tasks.values.forEach(SchedulerTask::cancel)
        tasks.clear()

    }

}