package dev.nikdekur.ndkore.snowstar

import co.touchlab.stately.concurrency.Lock
import co.touchlab.stately.concurrency.withLock
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

/**
 * A thread-safe generator for Snowstar IDs.
 *
 * This class handles the generation of unique IDs with automatic increment management
 * and timestamp tracking. It includes protection against clock drift and increment overflows.
 *
 * @property clock The clock source used for timestamp generation
 * @property version The version identifier for generated IDs (4 bits)
 * @property datacenterId The datacenter identifier for generated IDs (10 bits)
 * @property workerId The worker identifier for generated IDs (10 bits)
 * @property processId The process identifier for generated IDs (10 bits)
 * @property defaultIncrement The starting increment value for each millisecond (defaults to 0)
 */
@OptIn(ExperimentalTime::class)
public class SnowstarGenerator(
    public val clock: Clock,
    public val version: ULong,
    public val datacenterId: ULong,
    public val workerId: ULong,
    public val processId: ULong,
    public val defaultIncrement: ULong = 0u,
) {
    /**
     * Lock used to ensure thread safety during ID generation.
     */
    public val lock: Lock = Lock()

    /**
     * Current increment value, reset to defaultIncrement when timestamp changes.
     */
    public var increment: ULong = defaultIncrement

    /**
     * The timestamp of the last generated ID, used to detect clock drift.
     */
    public var lastTimestamp: ULong? = null

    /**
     * Generates a new unique Snowstar ID.
     *
     * This method is thread-safe and handles:
     * - Incrementing the sequence number for IDs generated within the same millisecond
     * - Resetting the sequence when the timestamp changes
     * - Detecting backwards clock movement (throws an exception)
     * - Detecting increment overflow (throws an exception)
     *
     * @return A new unique Snowstar ID
     * @throws IllegalStateException If the system clock moves backwards or if the increment overflows
     */
    public fun generate(): Snowstar = lock.withLock {
        val timeStamp = clock.now().toEpochMilliseconds().toULong()

        val lastTS = lastTimestamp

        if (lastTS != null && timeStamp < lastTS) {
            error("Clock moved backwards. Refusing to generate id for ${lastTS - timeStamp} milliseconds")
        }

        if (increment >= Snowstar.MAX_INCREMENT) {
            error("Increment overflow. Refusing to generate id for ${increment - Snowstar.MAX_INCREMENT} increments")
        }

        increment = if (lastTS == timeStamp) {
            (increment + 1u)
        } else {
            defaultIncrement
        }

        lastTimestamp = timeStamp

        val snowstar = Snowstar(
            version = version,
            timestamp = timeStamp,
            datacenterId = datacenterId,
            workerId = workerId,
            processId = processId,
            increment = increment,
        )

        return snowstar
    }
}