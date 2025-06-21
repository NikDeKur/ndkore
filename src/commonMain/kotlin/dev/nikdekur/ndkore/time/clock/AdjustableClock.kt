package dev.nikdekur.ndkore.time.clock

import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlin.time.Duration

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