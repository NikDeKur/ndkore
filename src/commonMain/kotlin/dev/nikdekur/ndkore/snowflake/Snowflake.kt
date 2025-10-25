@file:Suppress("NOTHING_TO_INLINE")

package dev.nikdekur.ndkore.snowflake

import dev.nikdekur.ndkore.ext.readULong
import kotlinx.serialization.Serializable
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

/**
 * A distributed unique ID generator based on the Snowflake algorithm.
 *
 * SnowflakeV2 represents a 128-bit ID composed of two ULong values with the following structure:
 * - 4 bits: Version identifier
 * - 64 bits: Timestamp (milliseconds since epoch)
 * - 10 bits: Datacenter ID
 * - 10 bits: Worker ID
 * - 10 bits: Process ID
 * - 30 bits: Sequence/Increment number
 *
 * The bits are split across two ULong values:
 * - data1:
 *   - 4 bits: Version
 *   - 60 bits: Timestamp (least significant bits)
 * - data2:
 *   - 4 bits: Timestamp (most significant bits)
 *   - 10 bits: Datacenter ID
 *   - 10 bits: Worker ID
 *   - 10 bits: Process ID
 *   - 30 bits: Increment
 *
 * @property data1 The first 64 bits of the snowflake ID
 * @property data2 The second 64 bits of the snowflake ID
 */
@OptIn(ExperimentalTime::class)
@Serializable
public open class SnowflakeV2(
    public val data1: ULong,
    public val data2: ULong,
) {

    public constructor() : this(0u, 0u)

    /**
     * The version component of the ID (4 bits).
     *
     * Used to distinguish between different ID generation schemes or versions.
     */
    public val version: Int
        get() = (data1 shr 60).toInt()

    /**
     * The timestamp component of the ID (64 bits).
     *
     * Represents the number of milliseconds since the Unix epoch,
     * allowing for approximately 584,942 years of timestamp values.
     */
    public val timestampMs: ULong
        get() = ((data1 and 0x0FFFFFFFFFFFFFFF.toULong()) shl 4) or (data2 shr 60)


    /**
     * The timestamp component as an [Instant].
     *
     * Represents the number of milliseconds since the Unix epoch,
     * allowing for approximately 584,942 years of timestamp values.
     */
    public val timestamp: Instant
        get() = Instant.fromEpochMilliseconds(timestampMs.toLong())


    /**
     * The datacenter ID component (10 bits).
     *
     * Allows for 1024 unique datacenter identifiers.
     */
    public val datacenterId: ULong
        get() = (data2 shr 50) and 0x3FFu

    /**
     * The worker ID component (10 bits).
     *
     * Allows for 1024 unique worker identifiers per datacenter.
     */
    public val workerId: ULong
        get() = (data2 shr 40) and 0x3FFu

    /**
     * The process ID component (10 bits).
     *
     * Allows for 1024 unique process identifiers per worker.
     */
    public val processId: ULong
        get() = (data2 shr 30) and 0x3FFu

    /**
     * The sequence/increment component (30 bits).
     *
     * Allows for up to 1,073,741,824 unique IDs per millisecond per process.
     */
    public val increment: ULong
        get() = data2 and 0x3FFFFFFFu

    /**
     * Returns a string representation of the SnowflakeV2 with all component values.
     *
     * @return A string containing the values of all ID components.
     */
    override fun toString(): String {
        return "SnowflakeV2(version=$version, timestamp=$timestampMs, datacenterId=$datacenterId, workerId=$workerId, processId=$processId, increment=$increment)"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is SnowflakeV2) return false

        if (data1 != other.data1) return false
        if (data2 != other.data2) return false

        return true
    }

    override fun hashCode(): Int {
        var result = data1.hashCode()
        result = 31 * result + data2.hashCode()
        return result
    }


    /**
     * Companion object containing maximum values for each component of the Snowflake ID.
     */
    public companion object {
        /**
         * Maximum value for the timestamp component (64 bits).
         */
        public val MAX_TIMESTAMP: ULong = 0x0FFFFFFFFFFFFFFFu

        /**
         * Maximum value for the version component (4 bits).
         */
        public val MAX_VERSION: ULong = 0xFu

        /**
         * Maximum value for the datacenter ID component (10 bits).
         */
        public val MAX_DATACENTER_ID: ULong = 0x3FFu

        /**
         * Maximum value for the worker ID component (10 bits).
         */
        public val MAX_WORKER_ID: ULong = 0x3FFu

        /**
         * Maximum value for the process ID component (10 bits).
         */
        public val MAX_PROCESS_ID: ULong = 0x3FFu

        /**
         * Maximum value for the increment component (30 bits).
         */
        public val MAX_INCREMENT: ULong = 0x3FFFFFFFu
    }
}

/**
 * Creates a SnowflakeV2 instance with the specified component values.
 *
 * This function encodes the provided components into the correct bit positions
 * within the two ULong values that make up a SnowflakeV2 ID.
 *
 * @param version The version component (4 bits)
 * @param timestamp The timestamp component (64 bits)
 * @param datacenterId The datacenter ID component (10 bits)
 * @param workerId The worker ID component (10 bits)
 * @param processId The process ID component (10 bits)
 * @param increment The increment component (30 bits)
 * @return A new SnowflakeV2 instance with the encoded component values
 */
public inline fun SnowflakeV2(
    version: ULong,
    timestamp: ULong,
    datacenterId: ULong,
    workerId: ULong,
    processId: ULong,
    increment: ULong,
): SnowflakeV2 {
    val data1 = (version shl 60) or (timestamp shr 4)
    val data2 =
        (timestamp shl 60) or
                (datacenterId shl 50) or
                (workerId shl 40) or
                (processId shl 30) or
                (increment)

    return SnowflakeV2(data1, data2)
}


/**
 * Creates a SnowflakeV2 instance from a byte array representation.
 *
 * The byte array must be exactly 16 bytes long, where:
 * - First 8 bytes represent the first ULong (data1)
 * - Last 8 bytes represent the second ULong (data2)
 *
 * @param data The byte array containing the binary representation of the SnowflakeV2 ID
 * @return A new SnowflakeV2 instance
 * @throws IllegalArgumentException if the byte array is not 16 bytes long
 */
public inline fun SnowflakeV2(data: ByteArray): SnowflakeV2 {
    require(data.size == 16) { "Byte array must be 16 bytes long (2 * Long)" }

    val data1 = data.readULong(0)
    val data2 = data.readULong(8)

    return SnowflakeV2(data1, data2)
}


/**
 * Converts the SnowflakeV2 instance to a numeric string representation.
 *
 * The string is structured as follows:
 * - First 20 characters represent the first ULong (data1)
 * - Last 20 characters represent the second ULong (data2)
 *
 * @receiver this The SnowflakeV2 instance
 * @return A string containing the numeric representation of the SnowflakeV2 ID
 */
public inline fun SnowflakeV2.asString(): String {
    val data1 = data1.toString().padStart(20, '0')
    val data2 = data2.toString().padStart(20, '0')
    return "$data1$data2"
}

/**
 * Converts the SnowflakeV2 string representation to a SnowflakeV2 instance.
 *
 * The string must be a 40-character numeric representation of the SnowflakeV2 ID,
 * where:
 * - First 20 characters represent the first ULong (data1)
 * - Last 20 characters represent the second ULong (data2)
 *
 * @receiver this The string representation of the SnowflakeV2 ID
 * @return A new SnowflakeV2 instance
 * @throws IllegalArgumentException if the string is not 32 characters long
 * @throws NumberFormatException if the string contains non-numeric characters
 */
public inline fun String.toSnowflake(): SnowflakeV2 {
    require(length == 40) { "String must be exactly 40 characters long (2 * ULong)" }

    val data1 = this.substring(0, 20).toULong()
    val data2 = this.substring(20, 40).toULong()

    return SnowflakeV2(data1, data2)
}

/**
 * Converts the SnowflakeV2 instance to a byte array representation.
 *
 * The byte array is structured as follows:
 * - First 8 bytes represent the first ULong (data1)
 * - Last 8 bytes represent the second ULong (data2)
 *
 * @return A byte array containing the binary representation of the SnowflakeV2 ID
 */
public inline fun SnowflakeV2.toByteArray(): ByteArray {
    val byteArray = ByteArray(16)
    byteArray[0] = (data1 shr 56).toByte()
    byteArray[1] = (data1 shr 48).toByte()
    byteArray[2] = (data1 shr 40).toByte()
    byteArray[3] = (data1 shr 32).toByte()
    byteArray[4] = (data1 shr 24).toByte()
    byteArray[5] = (data1 shr 16).toByte()
    byteArray[6] = (data1 shr 8).toByte()
    byteArray[7] = data1.toByte()

    byteArray[8] = (data2 shr 56).toByte()
    byteArray[9] = (data2 shr 48).toByte()
    byteArray[10] = (data2 shr 40).toByte()
    byteArray[11] = (data2 shr 32).toByte()
    byteArray[12] = (data2 shr 24).toByte()
    byteArray[13] = (data2 shr 16).toByte()
    byteArray[14] = (data2 shr 8).toByte()
    byteArray[15] = data2.toByte()

    return byteArray
}