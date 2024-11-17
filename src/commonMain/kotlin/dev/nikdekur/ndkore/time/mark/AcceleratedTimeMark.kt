package dev.nikdekur.ndkore.time.mark

import kotlin.time.Duration
import kotlin.time.TimeMark

/**
 * # Accelerated Time Mark
 *
 * A time mark that accelerates the elapsed time by a specified factor.
 *
 * ### Example usage:
 * ```kotlin
 * val systemTimeMark = TimeSource.Monotonic.markNow()
 * val acceleratedTime = AcceleratedTimeMark(systemTimeMark, 5.0)
 *
 * delay(1.seconds)
 *
 * println(systemTimeMark.elapsedNow())
 * // Output: 1 second
 *
 * println(acceleratedTime.elapsedNow())
 * // Output: 5 seconds
 * ```
 *
 * @param original The original time mark to multiply the elapsed time by.
 * @param factor The acceleration factor to multiply the elapsed time by.
 * Can be negative to decelerate (slow down) the time.
 */
public class AcceleratedTimeMark(
    public val original: TimeMark,
    public val factor: Double
) : TimeMark {

    override fun elapsedNow(): Duration {
        return original.elapsedNow() * factor
    }
}