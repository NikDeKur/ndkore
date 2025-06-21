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
import dev.nikdekur.ndkore.ext.BigIntegerSerializer
import kotlinx.serialization.Serializable
import kotlin.jvm.JvmInline

/**
 * Represents a unit of memory. Each memory unit is defined by the number of bytes it contains.
 *
 * The `MemoryUnit` class provides a unified way to handle different memory units (e.g. bytes, kibibytes, mebibytes, etc.)
 * and offers a set of predefined constants for commonly used memory units based on binary prefixes (powers of 1024).
 *
 * This class is particularly useful when working with memory-related calculations, conversions, and comparisons.
 *
 * ### Example usage with `MemoryAmount`
 *
 * The `MemoryAmount` class uses `MemoryUnit` to define a specific amount of memory in a particular unit.
 *
 * ```kotlin
 * val memoryInBytes = MemoryAmount(MemoryUnit.Byte, BigInteger.valueOf(1024))
 * val memoryInKiB = memoryInBytes.convertTo(MemoryUnit.KiB)
 * println(memoryInKiB) // Output: MemoryAmount(unit=KiB, amount=1)
 * ```
 *
 * ### MemoryUnit Constants
 *
 * The companion object provides a set of predefined constants for commonly used memory units:
 * - `Byte`: 1 byte
 * - `KiB`: 1024 bytes
 * - `MiB`: 1,048,576 bytes
 * - `GiB`: 1,073,741,824 bytes
 * - `TiB`: 1,099,511,627,776 bytes
 * - `PiB`: 1,125,899,906,842,624 bytes
 * - `EiB`: 1,152,921,504,606,846,976 bytes
 * - `ZiB`: 1,180,591,620,717,411,303,424 bytes
 * - `YiB`: 1,237,940,039,285,380,274,899,124,224 bytes
 *
 * @property bytes The number of bytes in this memory unit.
 */
@Serializable
@JvmInline
public value class MemoryUnit(
    @Serializable(with = BigIntegerSerializer::class)
    public val bytes: BigInteger,
) {

    public companion object {
        /**
         * Creates a custom memory unit with the specified number of bytes.
         *
         * @param bytes The number of bytes in the custom memory unit.
         * @return A new memory unit with the specified number of bytes.
         */
        public inline fun unit(bytes: BigInteger): MemoryUnit = MemoryUnit(bytes)

        /**
         * A memory unit representing one byte.
         */
        public val Byte: MemoryUnit = unit(BigInteger.ONE)

        /**
         * A memory unit representing one kibibyte (1024 bytes).
         */
        public val KiB: MemoryUnit = unit(Byte.bytes * 1024)

        /**
         * A memory unit representing one mebibyte (1,048,576 bytes).
         */
        public val MiB: MemoryUnit = unit(KiB.bytes * 1024)

        /**
         * A memory unit representing one gibibyte (1,073,741,824 bytes).
         */
        public val GiB: MemoryUnit = unit(MiB.bytes * 1024)

        /**
         * A memory unit representing one tebibyte (1,099,511,627,776 bytes).
         */
        public val TiB: MemoryUnit = unit(GiB.bytes * 1024)

        /**
         * A memory unit representing one pebibyte (1,125,899,906,842,624 bytes).
         */
        public val PiB: MemoryUnit = unit(TiB.bytes * 1024)

        /**
         * A memory unit representing one exbibyte (1,152,921,504,606,846,976 bytes).
         */
        public val EiB: MemoryUnit = unit(PiB.bytes * 1024)

        /**
         * A memory unit representing one zebbibyte (1,180,591,620,717,411,303,424 bytes).
         */
        public val ZiB: MemoryUnit = unit(EiB.bytes * 1024)

        /**
         * A memory unit representing one yobibyte (1,237,940,039,285,380,274,899,124,224 bytes).
         */
        public val YiB: MemoryUnit = unit(ZiB.bytes * 1024)
    }
}
