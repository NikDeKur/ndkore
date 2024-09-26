/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 * Copyright (c) 2024-present "Nik De Kur"
 */

@file:Suppress("NOTHING_TO_INLINE")

package dev.nikdekur.ndkore.memory

import com.ionspin.kotlin.bignum.integer.BigInteger

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
public interface MemoryUnit {
    /**
     * The number of bytes in this memory unit.
     */
    public val bytes: BigInteger

    public companion object {
        /**
         * Creates a custom memory unit with the specified number of bytes.
         *
         * @param bytes The number of bytes in the custom memory unit.
         * @return A new memory unit with the specified number of bytes.
         */
        public inline fun unit(bytes: BigInteger): MemoryUnit = object : MemoryUnit {
            override val bytes = bytes
        }

        /**
         * A memory unit representing one byte.
         */
        public val Byte: MemoryUnit = unit(BigInteger.ONE)

        /**
         * A memory unit representing one kilobyte (1024 bytes).
         */
        public val KB: MemoryUnit = unit(Byte.bytes * 1024)

        /**
         * A memory unit representing one megabyte (1,048,576 bytes).
         */
        public val MB: MemoryUnit = unit(KB.bytes * 1024)

        /**
         * A memory unit representing one gigabyte (1,073,741,824 bytes).
         */
        public val GB: MemoryUnit = unit(MB.bytes * 1024)

        /**
         * A memory unit representing one terabyte (1099511627776 bytes).
         */
        public val TB: MemoryUnit = unit(GB.bytes * 1024)

        /**
         * A memory unit representing one petabyte (1,125,899,906,842,624 bytes).
         */
        public val PB: MemoryUnit = unit(TB.bytes * 1024)

        /**
         * A memory unit representing one exabyte (1,180,591,620,717,411,303,424 bytes).
         */
        public val EB: MemoryUnit = unit(PB.bytes * 1024)

        /**
         * A memory unit representing one zetta-byte (1,208,925,819,614,629,174,706,176 bytes).
         */
        public val ZB: MemoryUnit = unit(EB.bytes * 1024)

        /**
         * A memory unit representing one yotta-byte (1,237,940,039,285,380,274,899,124,224 bytes).
         */
        public val YB: MemoryUnit = unit(ZB.bytes * 1024)
    }
}
