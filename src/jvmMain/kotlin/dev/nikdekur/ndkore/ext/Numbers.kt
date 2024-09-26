@file:JvmName("NumbersJvmKt")
@file:Suppress("NOTHING_TO_INLINE")

package dev.nikdekur.ndkore.ext

import java.math.BigInteger

/**
 * Subtracts the specified BigInteger value from this BigInteger.
 *
 * @param value the value to subtract from this BigInteger.
 * @return the result of subtracting the specified value from this BigInteger.
 */
public inline operator fun BigInteger.minus(value: BigInteger): BigInteger = this.subtract(value)

/**
 * Adds the specified BigInteger value to this BigInteger.
 *
 * @param value the value to add to this BigInteger.
 * @return the result of adding the specified value to this BigInteger.
 */
public inline operator fun BigInteger.plus(value: BigInteger): BigInteger = this.add(value)

/**
 * Divides this BigInteger by the specified BigInteger value.
 *
 * @param value the divisor.
 * @return the result of dividing this BigInteger by the specified value.
 */
public inline operator fun BigInteger.div(value: BigInteger): BigInteger = this.divide(value)


/**
 * Checks if this BigInteger is zero.
 *
 * @return true if this BigInteger is zero, false otherwise.
 */
public inline val BigInteger.isZero
    get() = this == BigInteger.ZERO