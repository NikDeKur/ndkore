package dev.nikdekur.ndkore.scheduler

import java.util.concurrent.ConcurrentHashMap

open class SchedulerManager<H>(val factory: (H) -> Scheduler) {

    val schedulers = ConcurrentHashMap<H, Scheduler>()

    fun getScheduler(holder: H): Scheduler {
        return schedulers.computeIfAbsent(holder) { factory(holder) }
    }


    fun cancelAllTasks() {
        schedulers.values.forEach { it.cancelAllTasks() }
    }
}