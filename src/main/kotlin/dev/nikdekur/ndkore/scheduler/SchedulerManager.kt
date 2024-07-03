/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 * Copyright (c) 2024 Nik De Kur
 */

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