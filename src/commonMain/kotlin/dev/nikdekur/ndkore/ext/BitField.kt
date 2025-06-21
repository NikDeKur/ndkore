@file:Suppress("NOTHING_TO_INLINE")

package dev.nikdekur.ndkore.ext

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlin.jvm.JvmInline

/**
 * An immutable 64-bit field that allows accessing and manipulating individual bits.
 *
 * BitField64 provides a convenient way to work with individual bits in a 64-bit unsigned long value.
 * It supports getting and setting bits by index, where index 0 represents the least significant bit,
 * and index 63 represents the most significant bit.
 *
 * Example usage:
 * ```
 * // Create a BitField64 with specific bits set
 * var field = BitField64(0UL)
 * field = field.set(0, true)  // Set the first bit
 * field = field.set(63, true) // Set the last bit
 *
 * // Check if bits are set
 * val isFirstBitSet = field[0]  // true
 * val isSecondBitSet = field[1] // false
 * ```
 *
 * @property value The underlying 64-bit unsigned long value.
 */
@Serializable(with = BitField64.Serializer::class)
@JvmInline
public value class BitField64(
    /**
     * The underlying 64-bit unsigned long value that stores the bit field data.
     */
    public val value: ULong
) {
    /**
     * Gets the boolean value of the bit at the specified [index].
     *
     * @param index The zero-based index of the bit to get (0 to 63, where 0 is the least significant bit).
     * @return `true` if the bit at the specified index is set (1), `false` otherwise (0).
     * @throws IndexOutOfBoundsException if the index is not in the range 0..63.
     */
    public operator fun get(index: Int): Boolean {
        checkIndex(index)

        return (value and (1UL shl index)) != 0UL
    }

    /**
     * Creates a new BitField64 with the bit at the specified [index] set to the specified [value].
     *
     * This operation does not modify the original BitField64 instance (immutable operation).
     *
     * @param index The zero-based index of the bit to set (0 to 63, where 0 is the least significant bit).
     * @param value The boolean value to set the bit to (`true` for 1, `false` for 0).
     * @return A new BitField64 instance with the updated bit value.
     * @throws IndexOutOfBoundsException if the index is not in the range 0..63.
     */
    public operator fun set(index: Int, value: Boolean): BitField64 {
        checkIndex(index)

        val new = if (value) {
            this.value or (1UL shl index)
        } else {
            this.value and (1UL shl index).inv()
        }
        return BitField64(new)
    }

    /**
     * Converts the bit field to a signed 64-bit long value.
     *
     * Note that this may result in a negative number if the 63rd bit is set.
     *
     * @return A signed Long representation of the bit field.
     */
    public fun toLong(): Long {
        return value.toLong()
    }

    /**
     * Returns the bit field as an unsigned 64-bit long value.
     *
     * This is the same as the underlying [value] property.
     *
     * @return An unsigned Long representation of the bit field.
     */
    public fun toULong(): ULong {
        return value
    }

    /**
     * Companion object containing utility functions for the BitField64 class.
     */
    public companion object {
        /**
         * Validates that the provided index is within the valid range for a 64-bit field (0..63).
         *
         * @param index The index to validate.
         * @throws IndexOutOfBoundsException if the index is not in the range 0..63.
         */
        internal inline fun checkIndex(index: Int) {
            if (index !in 0..63) {
                throw IndexOutOfBoundsException("Index out of bounds: $index")
            }
        }


        /**
         * A constant representing a BitField64 with all bits set to 0.
         * This is equivalent to a BitField64 with a value of ULong.MIN_VALUE.
         */
        public val NONE: BitField64 = BitField64(ULong.MIN_VALUE)

        /**
         * A constant representing a BitField64 with all bits set to 1.
         * This is equivalent to a BitField64 with a value of ULong.MAX_VALUE.
         */
        public val ALL: BitField64 = BitField64(ULong.MAX_VALUE)
    }

    public object Serializer : KSerializer<BitField64> {
        override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("BitField64", PrimitiveKind.LONG)

        override fun serialize(encoder: Encoder, value: BitField64) {
            encoder.encodeLong(value.value.toLong())
        }

        override fun deserialize(decoder: Decoder): BitField64 {
            return BitField64(decoder.decodeLong().toULong())
        }
    }
}


/**
 * Performs a bitwise OR operation with another BitField64.
 *
 * This operation does not modify the original BitField64 instances (immutable operation).
 *
 * @param other The BitField64 to perform the OR operation with.
 * @return A new BitField64 instance where each bit is the result of this OR other.
 */
public inline infix fun BitField64.or(other: BitField64): BitField64 {
    return BitField64(this.value or other.value)
}

/**
 * Performs a bitwise AND operation with another BitField64.
 *
 * This operation does not modify the original BitField64 instances (immutable operation).
 *
 * @param other The BitField64 to perform the AND operation with.
 * @return A new BitField64 instance where each bit is the result of this AND other.
 */
public inline infix fun BitField64.and(other: BitField64): BitField64 {
    return BitField64(this.value and other.value)
}

/**
 * Performs a bitwise XOR operation with another BitField64.
 *
 * This operation does not modify the original BitField64 instances (immutable operation).
 *
 * @param other The BitField64 to perform the XOR operation with.
 * @return A new BitField64 instance where each bit is the result of this XOR other.
 */
public inline infix fun BitField64.xor(other: BitField64): BitField64 {
    return BitField64(this.value xor other.value)
}

/**
 * Performs a bitwise NOT operation (inverts all bits).
 *
 * This operation does not modify the original BitField64 instance (immutable operation).
 *
 * @return A new BitField64 instance with all bits inverted.
 */
public inline fun BitField64.inv(): BitField64 {
    return BitField64(this.value.inv())
}

/**
 * Creates a new BitField64 with all bits shifted left by the specified number of positions.
 *
 * This operation does not modify the original BitField64 instance (immutable operation).
 * Bits shifted beyond the 63rd position are discarded.
 *
 * @param n The number of positions to shift left.
 * @return A new BitField64 instance with bits shifted left.
 */
public inline fun BitField64.shl(n: Int): BitField64 {
    return BitField64(this.value shl n)
}

/**
 * Creates a new BitField64 with all bits shifted right by the specified number of positions.
 *
 * This operation does not modify the original BitField64 instance (immutable operation).
 * Bits shifted beyond the 0th position are discarded, and zeros are shifted in from the left.
 *
 * @param n The number of positions to shift right.
 * @return A new BitField64 instance with bits shifted right.
 */
public inline fun BitField64.shr(n: Int): BitField64 {
    return BitField64(this.value shr n)
}

/**
 * Checks if this BitField64 has any bits in common with another BitField64.
 *
 * @param other The BitField64 to check for common bits.
 * @return `true` if at least one bit is set to 1 in both BitField64 instances, `false` otherwise.
 */
public inline fun BitField64.intersects(other: BitField64): Boolean {
    return (this.value and other.value) != 0UL
}

/**
 * Checks if this BitField64 contains all bits from another BitField64.
 *
 * @param other The BitField64 to check if contained.
 * @return `true` if all bits set to 1 in `other` are also set to 1 in this BitField64, `false` otherwise.
 */
public inline fun BitField64.contains(other: BitField64): Boolean {
    return (this.value and other.value) == other.value
}

/**
 * Returns the number of bits set to 1 in this BitField64.
 *
 * @return The count of bits set to 1.
 */
public inline fun BitField64.countOneBits(): Int {
    return value.countOneBits()
}