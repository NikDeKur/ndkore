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
import com.ionspin.kotlin.bignum.integer.toBigInteger
import dev.nikdekur.ndkore.ext.BigIntegerSerializer
import kotlinx.serialization.Serializable
import kotlin.jvm.JvmInline


/**
 * # MemoryAmount
 *
 * Represents an amount of memory.
 * Each memory amount is defined by the number of bytes it contains.
 *
 * The `MemoryAmount` class provides a unified way to handle different amounts of memory
 * and offers a set of arithmetic operations for memory-related calculations.
 *
 * This class is particularly useful when working with memory-related calculations, conversions, and comparisons.
 *
 * ### Example usage with `MemoryUnit`
 * ```kotlin
 * val memory = MemoryAmount(BigInteger.valueOf(1024))
 * val memoryInKiB = memory.toBigInteger(MemoryUnit.KiB)
 * println("Memory in KiB: $memoryInKiB") // 1 KiB
 * ```
 */
@JvmInline
@Serializable
public value class MemoryAmount(
    @Serializable(with = BigIntegerSerializer::class)
    public val bytes: BigInteger,
) : Comparable<MemoryAmount> {

    /**
     * Adds the specified memory amount to this memory amount.
     *
     * @param memory The memory amount to add.
     * @return A new `MemoryAmount` representing the sum.
     */
    public operator fun plus(memory: MemoryAmount): MemoryAmount {
        return MemoryAmount(bytes + memory.bytes)
    }

    /**
     * Subtracts the specified memory amount from this memory amount.
     *
     * @param memory The memory amounts to subtract.
     * @return A new `MemoryAmount` representing the difference.
     */
    public operator fun minus(memory: MemoryAmount): MemoryAmount {
        return MemoryAmount(bytes - memory.bytes)
    }

    /**
     * Multiplies this memory amount by the specified memory amount.
     *
     * @param memory The memory amount to multiply by.
     * @return A new `MemoryAmount` representing the product.
     */
    public operator fun times(memory: MemoryAmount): MemoryAmount {
        return MemoryAmount(bytes * memory.bytes)
    }

    /**
     * Divides this memory amount by the specified memory amount.
     *
     * @param memory The memory amount to divide by.
     * @return A new `MemoryAmount` representing the quotient.
     */
    public operator fun div(memory: MemoryAmount): MemoryAmount {
        return MemoryAmount(bytes / memory.bytes)
    }

    /**
     * Calculates the remainder after the division of this memory amount by the specified memory amount.
     *
     * @param memory The memory amount to divide by.
     * @return A new `MemoryAmount` representing the remainder.
     */
    public operator fun rem(memory: MemoryAmount): MemoryAmount {
        return MemoryAmount(bytes % memory.bytes)
    }

    /**
     * Compares this memory amount to another memory amount.
     *
     * @param other The memory amount to compare to.
     * @return A negative integer, zero, or a positive integer as this memory amount is less than, equal to, or greater than the specified memory amount.
     */
    override operator fun compareTo(other: MemoryAmount): Int {
        return bytes.compareTo(other.bytes)
    }
}

public inline val BigInteger.bytes: MemoryAmount
    get() = MemoryAmount(this, MemoryUnit.Byte)

public inline val BigInteger.kibiBytes: MemoryAmount
    get() = MemoryAmount(this, MemoryUnit.KiB)

public inline val BigInteger.mebiBytes: MemoryAmount
    get() = MemoryAmount(this, MemoryUnit.MiB)

public inline val BigInteger.gibiBytes: MemoryAmount
    get() = MemoryAmount(this, MemoryUnit.GiB)

public inline val BigInteger.tebiBytes: MemoryAmount
    get() = MemoryAmount(this, MemoryUnit.TiB)

public inline val BigInteger.pebiBytes: MemoryAmount
    get() = MemoryAmount(this, MemoryUnit.PiB)

public inline val BigInteger.exbiBytes: MemoryAmount
    get() = MemoryAmount(this, MemoryUnit.EiB)

public inline val BigInteger.zebiBytes: MemoryAmount
    get() = MemoryAmount(this, MemoryUnit.ZiB)

public inline val BigInteger.yobiBytes: MemoryAmount
    get() = MemoryAmount(this, MemoryUnit.YiB)


public inline val Number.bytes: MemoryAmount
    get() = MemoryAmount(this.toLong().toBigInteger(), MemoryUnit.Byte)

public inline val Number.kibiBytes: MemoryAmount
    get() = MemoryAmount(this.toLong().toBigInteger(), MemoryUnit.KiB)

public inline val Number.mebiBytes: MemoryAmount
    get() = MemoryAmount(this.toLong().toBigInteger(), MemoryUnit.MiB)

public inline val Number.gibiBytes: MemoryAmount
    get() = MemoryAmount(this.toLong().toBigInteger(), MemoryUnit.GiB)

public inline val Number.tebiBytes: MemoryAmount
    get() = MemoryAmount(this.toLong().toBigInteger(), MemoryUnit.TiB)

public inline val Number.pebiBytes: MemoryAmount
    get() = MemoryAmount(this.toLong().toBigInteger(), MemoryUnit.PiB)

public inline val Number.exbiBytes: MemoryAmount
    get() = MemoryAmount(this.toLong().toBigInteger(), MemoryUnit.EiB)

public inline val Number.zebiBytes: MemoryAmount
    get() = MemoryAmount(this.toLong().toBigInteger(), MemoryUnit.ZiB)

public inline val Number.yobiBytes: MemoryAmount
    get() = MemoryAmount(this.toLong().toBigInteger(), MemoryUnit.YiB)

/**
 * Creates a `MemoryAmount` by converting from one unit to another.
 *
 * @param unit The unit of memory to convert from.
 * @param value The amount of memory to convert.
 * @return A new `MemoryAmount` representing the converted amount.
 */
public fun MemoryAmount(value: BigInteger, unit: MemoryUnit = MemoryUnit.Byte): MemoryAmount {
    return MemoryAmount(value * unit.bytes)
}


/**
 * Converts this memory amount to the specified memory unit and returns the result as a `BigInteger`.
 *
 * @param unit The memory unit to convert to.
 * @return BigInteger The memory amount in the specified memory unit.
 */
public fun MemoryAmount.toBigInteger(unit: MemoryUnit = MemoryUnit.Byte): BigInteger {
    return bytes / unit.bytes
}

/**
 * Converts this memory amount to the specified memory unit and returns the result as a `Long`.
 *
 * @param unit The memory unit to convert to.
 * @return Long The memory amount in the specified memory unit.
 */
public fun MemoryAmount.toLong(unit: MemoryUnit = MemoryUnit.Byte): Long {
    return toBigInteger(unit).longValue(true)
}


/**
 * Converts this memory amount to the specified memory unit and returns the result as a `ULong`.
 *
 * @param unit The memory unit to convert to.
 * @return ULong The memory amount in the specified memory unit.
 */

public fun MemoryAmount.toULong(unit: MemoryUnit = MemoryUnit.Byte): ULong {
    return toBigInteger(unit).ulongValue(true)
}


/**
 * Converts this memory amount to the specified memory unit and returns the result as an `Int`.
 *
 * @param unit The memory unit to convert to.
 * @return Int The memory amount in the specified memory unit.
 */
public fun MemoryAmount.toInt(unit: MemoryUnit = MemoryUnit.Byte): Int {
    return toBigInteger(unit).intValue(true)
}


/**
 * Converts this memory amount to the specified memory unit and returns the result as a `UInt`.
 *
 * @param unit The memory unit to convert to.
 * @return UInt The memory amount in the specified memory unit.
 */
public fun MemoryAmount.toUInt(unit: MemoryUnit = MemoryUnit.Byte): UInt {
    return toBigInteger(unit).uintValue(true)
}


public val MemoryAmount.bitsLong: Long
    get() = bytes.longValue(true) * 8L


public val MemoryAmount.bitsULong: ULong
    get() = bytes.ulongValue(true) * 8UL


public val MemoryAmount.bitsInt: Int
    get() = bytes.intValue(true) * 8


public val MemoryAmount.bitsUInt: UInt
    get() = bytes.uintValue(true) * 8U

