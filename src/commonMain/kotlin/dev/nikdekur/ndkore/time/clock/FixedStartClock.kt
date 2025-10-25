@file:Suppress("NOTHING_TO_INLINE")

package dev.nikdekur.ndkore.time.clock

import dev.nikdekur.ndkore.time.mark.AcceleratedTimeMark
import dev.nikdekur.ndkore.time.mark.FixedTimeMark
import kotlin.time.*


/**
 * # Fixed Start Clock
 *
 * A clock that starts at a fixed time and advances with the specified time mark.
 *
 * The time mark used to determine the elapsed time since the start time.
 * TimeMark can be used to speed up or slow down the clock.
 *
 *
 * ### Example usage:
 * ```kotlin
 * val systemTime = Clock.System.now()
 *
 * println(systemTime)
 * // Output: 2021-10-15T12:00:00Z
 *
 * val start = systemTime - 5.days
 * val clock = FixedStartClock(start)
 *
 * println(clock.now())
 * // Output: 2021-10-10T12:00:00Z
 * ```
 *
 * @param start The start time of the clock.
 * @param timeMark The time mark to use for the clock. Defaults to [TimeSource.Monotonic.markNow].
 * @see [AcceleratedTimeMark]
 */
@OptIn(ExperimentalTime::class)
public class FixedStartClock(
    public val start: Instant,
    public val timeMark: TimeMark = TimeSource.Monotonic.markNow()
) : Clock {

    override fun now(): Instant {
        return start + timeMark.elapsedNow()
    }

    public companion object {
        public inline fun Zero(timeMark: TimeMark = TimeSource.Monotonic.markNow()): FixedStartClock {
            return FixedStartClock(Instant.fromEpochSeconds(0), timeMark)
        }

        public val AlwaysZero: FixedStartClock = FixedStartClock(Instant.fromEpochSeconds(0), FixedTimeMark.Zero)
    }
}