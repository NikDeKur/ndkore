package dev.nikdekur.ndkore.time.clock

import kotlin.time.Clock
import kotlin.time.Duration
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

/**
 * # Offset Clock
 *
 * A clock that is offset by a specified duration from another clock.
 *
 *
 * ### Example usage:
 * ```kotlin
 * val systemClock = Clock.System
 * println(systemClock.now())
 * // Output: 2021-10-10T12:00:00Z
 *
 * val offsetClock = OffsetClock(1.days + 5.hours + 20.minutes, systemClock)
 * println(offsetClock.now())
 * // Output: 2021-10-11T17:20:00Z
 * ```
 *
 * @param offset The duration to offset the original clock by. Can be negative to go back in time.
 * @param original The original clock to offset. Defaults to [Clock.System].
 */
@OptIn(ExperimentalTime::class)
public class OffsetClock(
    public val offset: Duration,
    public val original: Clock = Clock.System
) : Clock {

    override fun now(): Instant {
        return original.now() + offset
    }
}