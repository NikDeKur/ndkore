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