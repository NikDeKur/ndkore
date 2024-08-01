/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 * Copyright (c) 2024-present "Nik De Kur"
 */

@file:Suppress("NOTHING_TO_INLINE")

package dev.nikdekur.ndkore.memory

import java.math.BigInteger


/**
 * Represents an amount of memory in a specific unit.
 *
 * The `MemoryAmount` class allows for various memory-related operations such as addition, subtraction, multiplication,
 * division, and comparison. It also supports conversion between different memory units.
 *
 * ### Example usage
 *
 * ```kotlin
 * val memoryInBytes = MemoryAmount(MemoryUnit.Byte, BigInteger.valueOf(1024))
 * val memoryInKB = memoryInBytes.convertTo(MemoryUnit.KB)
 * println(memoryInKB) // Output: MemoryAmount(unit=KB, amount=1)
 * ```
 *
 * ### Properties
 *
 * @property unit The unit of memory (e.g., bytes, kilobytes).
 * @property amount The amount of memory in the specified unit.
 */
data class MemoryAmount(
    val unit: MemoryUnit,
    val amount: BigInteger,
) : Comparable<MemoryAmount> {

    /**
     * Adds the specified memory amount to this memory amount.
     *
     * @param memory The memory amount to add.
     * @return A new `MemoryAmount` representing the sum.
     */
    operator fun plus(memory: MemoryAmount): MemoryAmount {
        return MemoryAmount(unit, amount + memory.amount)
    }

    /**
     * Subtracts the specified memory amount from this memory amount.
     *
     * @param memory The memory amount to subtract.
     * @return A new `MemoryAmount` representing the difference.
     */
    operator fun minus(memory: MemoryAmount): MemoryAmount {
        return MemoryAmount(unit, amount - memory.amount)
    }

    /**
     * Multiplies this memory amount by the specified memory amount.
     *
     * @param memory The memory amount to multiply by.
     * @return A new `MemoryAmount` representing the product.
     */
    operator fun times(memory: MemoryAmount): MemoryAmount {
        return MemoryAmount(unit, amount * memory.amount)
    }

    /**
     * Divides this memory amount by the specified memory amount.
     *
     * @param memory The memory amount to divide by.
     * @return A new `MemoryAmount` representing the quotient.
     */
    operator fun div(memory: MemoryAmount): MemoryAmount {
        return MemoryAmount(unit, amount / memory.amount)
    }

    /**
     * Calculates the remainder of the division of this memory amount by the specified memory amount.
     *
     * @param memory The memory amount to divide by.
     * @return A new `MemoryAmount` representing the remainder.
     */
    operator fun rem(memory: MemoryAmount): MemoryAmount {
        return MemoryAmount(unit, amount % memory.amount)
    }

    /**
     * Compares this memory amount to another memory amount.
     *
     * @param memory The memory amount to compare to.
     * @return A negative integer, zero, or a positive integer as this memory amount is less than, equal to, or greater than the specified memory amount.
     */
    override operator fun compareTo(memory: MemoryAmount): Int {
        return amount.compareTo(memory.amount)
    }

    /**
     * Converts this memory amount to the specified memory unit.
     *
     * @param unit The memory unit to convert to.
     * @return A new `MemoryAmount` representing the converted amount.
     */
    fun convertTo(unit: MemoryUnit): MemoryAmount {
        if (this.unit == unit) return this
        return MemoryAmount(unit, amount * this.unit.bytes / unit.bytes)
    }

    companion object {
        /**
         * Creates a `MemoryAmount` by converting from one unit to another.
         *
         * @param input The input memory unit.
         * @param output The output memory unit.
         * @param value The amount of memory in the input unit.
         * @return A new `MemoryAmount` converted to the output unit.
         */
        inline fun of(input: MemoryUnit, output: MemoryUnit, value: BigInteger): MemoryAmount {
            val memoryAmount = MemoryAmount(input, value)
            return memoryAmount.convertTo(output)
        }

        /**
         * Creates a `MemoryAmount` in the specified memory unit.
         *
         * @param input The memory unit.
         * @param value The amount of memory in the specified unit.
         * @return A new `MemoryAmount`.
         */
        inline fun of(input: MemoryUnit, value: BigInteger): MemoryAmount {
            return of(input, input, value)
        }
    }
}
