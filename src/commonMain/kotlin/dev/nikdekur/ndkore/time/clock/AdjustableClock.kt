@file:OptIn(ExperimentalTime::class)

package dev.nikdekur.ndkore.time.clock

import kotlin.time.Clock
import kotlin.time.Duration
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

public class AdjustableClock(
    public val original: Clock,
    public var offset: Duration = Duration.ZERO
) : Clock {

    public fun adjustForward(offset: Duration) {
        this.offset += offset
    }

    public fun adjustBackward(offset: Duration) {
        this.offset -= offset
    }

    override fun now(): Instant {
        return original.now() + offset
    }
}