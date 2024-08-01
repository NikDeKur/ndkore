/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 * Copyright (c) 2024-present "Nik De Kur"
 */

package dev.nikdekur.ndkore.cooldown

import java.util.concurrent.TimeUnit


data class Cooldown(val duration: Long, val unit: TimeUnit) {
    fun toMillis(): Long {
        return unit.toMillis(duration)
    }

    companion object {
        val ONE_SECOND = Cooldown(1, TimeUnit.SECONDS)
        val ONE_MINUTE = Cooldown(1, TimeUnit.MINUTES)
    }
}