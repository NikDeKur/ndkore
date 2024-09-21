/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 * Copyright (c) 2024-present "Nik De Kur"
 */

@file:Suppress("NOTHING_TO_INLINE")

package dev.nikdekur.ndkore.memory

/**
 * Represents a unit of memory. Each memory unit is defined by the number of bytes it contains.
 *
 * The `MemoryUnit` interface provides a unified way to handle different memory units (e.g., bytes, kilobytes, megabytes, etc.)
 * and offers a set of predefined constants for common memory units.
 *
 * This interface is particularly useful when working with memory-related calculations, conversions, and comparisons.
 *
 * ### Example usage with `MemoryAmount`
 *
 * The `MemoryAmount` class uses `MemoryUnit` to define a specific amount of memory in a particular unit.
 *
 * ```kotlin
 * val memoryInBytes = MemoryAmount(MemoryUnit.Byte, BigInteger.valueOf(1024))
 * val memoryInKB = memoryInBytes.convertTo(MemoryUnit.KB)
 * println(memoryInKB) // Output: MemoryAmount(unit=KB, amount=1)
 * ```
 *
 * ### MemoryUnit Constants
 *
 * The companion object provides a set of predefined constants for commonly used memory units:
 * - `Byte`: 1 byte
 * - `KB`: 1024 bytes
 * - `MB`: 1,048,576 bytes
 * - `GB`: 1,073,741,824 bytes
 * - `TB`: 1,099,511,627,776 bytes
 * - `PB`: 1,125,899,906,842,624 bytes
 * - `EB`: 1,152,921,504,606,846,976 bytes
 * - `ZB`: 1,180,591,620,717,411,303,424 bytes
 * - `YB`: 1,237,940,039,285,380,274,899,124,224 bytes
 */
interface MemoryUnit {
    /**
     * The number of bytes in this memory unit.
     */
    val bytes: Long

    companion object {
        /**
         * Creates a custom memory unit with the specified number of bytes.
         *
         * @param bytes The number of bytes in the custom memory unit.
         * @return A new memory unit with the specified number of bytes.
         */
        inline fun unit(bytes: Long) = object : MemoryUnit {
            override val bytes: Long = bytes
        }

        /**
         * A memory unit representing one byte.
         */
        val Byte = unit(1)

        /**
         * A memory unit representing one kilobyte (1024 bytes).
         */
        val KB = unit(1024)

        /**
         * A memory unit representing one megabyte (1048576 bytes).
         */
        val MB = unit(KB.bytes * 1024)

        /**
         * A memory unit representing one gigabyte (1073741824 bytes).
         */
        val GB = unit(MB.bytes * 1024)

        /**
         * A memory unit representing one terabyte (1099511627776 bytes).
         */
        val TB = unit(GB.bytes * 1024)

        /**
         * A memory unit representing one petabyte (1125899906842624 bytes).
         */
        val PB = unit(TB.bytes * 1024)

        /**
         * A memory unit representing one exabyte (1180591620717411303424 bytes).
         */
        val EB = unit(PB.bytes * 1024)

        /**
         * A memory unit representing one zettabyte (1208925819614629174706176 bytes).
         */
        val ZB = unit(EB.bytes * 1024)

        /**
         * A memory unit representing one yottabyte (1237940039285380274899124224 bytes).
         */
        val YB = unit(ZB.bytes * 1024)
    }
}
