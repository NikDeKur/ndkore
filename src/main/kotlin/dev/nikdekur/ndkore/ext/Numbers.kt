/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 * Copyright (c) 2024-present "Nik De Kur"
 */

@file:Suppress("NOTHING_TO_INLINE")

package dev.nikdekur.ndkore.ext

import java.math.BigDecimal
import java.math.BigInteger
import java.math.RoundingMode
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import kotlin.math.*
import kotlin.reflect.KClass
import java.lang.Math as nativeMath

/**
 * Raises this number to the power of the specified value.
 *
 * @param value the exponent to which this number is raised.
 * @return the result of raising this number to the power of the specified value.
 */
@Suppress("ReplaceJavaStaticMethodWithKotlinAnalog")
inline fun Number.pow(value: Number) = nativeMath.pow(this.toDouble(), value.toDouble())

/**
 * Squares this number.
 *
 * @return the result of squaring this number.
 */
@Suppress("ReplaceJavaStaticMethodWithKotlinAnalog")
inline fun Number.pow() = nativeMath.pow(this.toDouble(), 2.0)

/**
 * Computes the square root of this number.
 *
 * @return the square root of this number.
 */
@Suppress("ReplaceJavaStaticMethodWithKotlinAnalog")
inline fun Number.sqrt() = nativeMath.sqrt(this.toDouble())

/**
 * Computes the cube root of this number.
 *
 * @return the cube root of this number.
 */
@Suppress("ReplaceJavaStaticMethodWithKotlinAnalog")
inline fun Number.cbrt() = nativeMath.cbrt(this.toDouble())

/**
 * Computes the absolute value of this number.
 *
 * @return the absolute value of this number.
 */
@Suppress("ReplaceJavaStaticMethodWithKotlinAnalog")
inline fun Number.abs() = nativeMath.abs(this.toDouble())

/**
 * Rounds this number up to the nearest integer.
 *
 * @return the smallest integer greater than or equal to this number.
 */
@Suppress("ReplaceJavaStaticMethodWithKotlinAnalog")
inline fun Number.ceil() = nativeMath.ceil(this.toDouble())

/**
 * Rounds this number down to the nearest integer.
 *
 * @return the largest integer less than or equal to this number.
 */
@Suppress("ReplaceJavaStaticMethodWithKotlinAnalog")
inline fun Number.floor() = nativeMath.floor(this.toDouble())

//
// TRIGONOMETRY
//

/**
 * Computes the sine of this number (in radians).
 *
 * @return the sine of this number.
 */
inline fun Number.sin() = sin(this.toDouble())

/**
 * Computes the cosine of this number (in radians).
 *
 * @return the cosine of this number.
 */
inline fun Number.cos() = cos(this.toDouble())

/**
 * Computes the tangent of this number (in radians).
 *
 * @return the tangent of this number.
 */
inline fun Number.tan() = tan(this.toDouble())

/**
 * Computes the arc sine of this number.
 *
 * @return the arc sine of this number.
 */
inline fun Number.asin() = asin(this.toDouble())

/**
 * Computes the arc cosine of this number.
 *
 * @return the arc cosine of this number.
 */
inline fun Number.acos() = acos(this.toDouble())

/**
 * Computes the arc tangent of this number.
 *
 * @return the arc tangent of this number.
 */
inline fun Number.atan() = atan(this.toDouble())

/**
 * Computes the hyperbolic sine of this number.
 *
 * @return the hyperbolic sine of this number.
 */
inline fun Number.sinh() = sinh(this.toDouble())

/**
 * Computes the hyperbolic cosine of this number.
 *
 * @return the hyperbolic cosine of this number.
 */
inline fun Number.cosh() = cosh(this.toDouble())

/**
 * Computes the hyperbolic tangent of this number.
 *
 * @return the hyperbolic tangent of this number.
 */
inline fun Number.tanh() = tanh(this.toDouble())


const val DEGREES_TO_RADIANS = 0.017453292519943295
const val RADIANS_TO_DEGREES = 57.29577951308232

/**
 * Converts this number from radians to degrees.
 *
 * @return the equivalent value of this number in degrees.
 */
inline fun Number.toDegrees() = this.toDouble() * RADIANS_TO_DEGREES

/**
 * Converts this number from degrees to radians.
 *
 * @return the equivalent value of this number in radians.
 */
inline fun Number.toRadians() = this.toDouble() * DEGREES_TO_RADIANS

//
// Mathematical Operators
//

/**
 * Subtracts the specified value from this number.
 *
 * @param value the value to subtract from this number.
 * @return the result of subtracting the specified value from this number.
 */
inline operator fun Number.minus(value: Number) = this.toDouble() - value.toDouble()

/**
 * Multiplies this number by the specified value.
 *
 * @param value the value to multiply this number by.
 * @return the result of multiplying this number by the specified value.
 */
inline operator fun Number.times(value: Number) = this.toDouble() * value.toDouble()

/**
 * Divides this number by the specified value.
 *
 * @param value the value to divide this number by.
 * @return the result of dividing this number by the specified value.
 */
inline operator fun Number.div(value: Number) = this.toDouble() / value.toDouble()

/**
 * Computes the remainder of dividing this number by the specified value.
 *
 * @param value the divisor.
 * @return the remainder of dividing this number by the specified value.
 */
inline operator fun Number.rem(value: Number) = this.toDouble() % value.toDouble()

/**
 * Subtracts the specified BigInteger value from this BigInteger.
 *
 * @param value the value to subtract from this BigInteger.
 * @return the result of subtracting the specified value from this BigInteger.
 */
inline operator fun BigInteger.minus(value: BigInteger): BigInteger = this.subtract(value)

/**
 * Adds the specified BigInteger value to this BigInteger.
 *
 * @param value the value to add to this BigInteger.
 * @return the result of adding the specified value to this BigInteger.
 */
inline operator fun BigInteger.plus(value: BigInteger): BigInteger = this.add(value)

/**
 * Divides this BigInteger by the specified BigInteger value.
 *
 * @param value the divisor.
 * @return the result of dividing this BigInteger by the specified value.
 */
inline operator fun BigInteger.div(value: BigInteger): BigInteger = this.divide(value)

/**
 * Compares this number with the specified value.
 *
 * @param value the value to compare this number with.
 * @return a negative integer, zero, or a positive integer as this number is less than, equal to, or greater than the specified value.
 */
inline operator fun Number.compareTo(value: Number) = toDouble().compareTo(value.toDouble())

/**
 * Casts this number to the specified type.
 *
 * @param clazz the KClass representing the type to cast to.
 * @return this number cast to the specified type.
 * @throws IllegalArgumentException if the specified type is not supported.
 */
@Suppress("UNCHECKED_CAST")
fun <T : Number> Number.castTo(clazz: KClass<T>): T {
    return when (clazz) {
        Byte::class -> toByte()
        Short::class -> toShort()
        Int::class -> toInt()
        Long::class -> toLong()
        Float::class -> toFloat()
        Double::class -> toDouble()
        else -> throw IllegalArgumentException()
    } as T
}

//
// Generics
//

val DEFAULT_DECIMAL_FORMAT_SYMBOLS = DecimalFormatSymbols().apply {
    groupingSeparator = ','
    decimalSeparator = '.'
}

/**
 * Formats this number to a string with the specified number of decimal places and rounding mode.
 *
 * @param places the number of decimal places.
 * @param roundingMode the rounding mode to apply.
 * @param formatSymbols the decimal format symbols to use.
 * @return the formatted string representation of this number.
 */
fun <T : Number> T.format(
    places: Int,
    roundingMode: RoundingMode = RoundingMode.HALF_UP,
    formatSymbols: DecimalFormatSymbols = DEFAULT_DECIMAL_FORMAT_SYMBOLS,
): String {
    require(places >= 0) { "Places must be positive" }
    val format = if (places > 0) {
        "#,##0." + buildString { repeat(places) { append('0') } }
    } else {
        "#,##0"
    }
    val df = DecimalFormat(format, formatSymbols)
    df.roundingMode = roundingMode

    var formattedString = df.format(this)

    formattedString = formattedString.replace(Patterns.REMOVE_TRAILING_COMA, "")
    formattedString = formattedString.removeSuffix(".")

    return formattedString
}

val NUMBER_UNITS = arrayOf("K", "M", "B", "T", "Qa", "Qi", "Sx", "Sp", "Oc", "No", "Dc", "Ud", "Dd", "Td", "Qad", "Qid", "Sxd", "Spd", "Ocd", "Nod", "Vg", "Uvg", "Dvg", "Tvg", "Qavg", "Qivg", "Sxvg", "Spvg", "Ocvg", "Novg", "Tg", "Utg", "Dtg", "Ttg", "Qatg", "Qitg", "Sxtg", "Sptg", "Octg", "Notg", "Qaa", "Uqaa", "Dqaa", "Tqaa", "Qaqaa", "Qiqaa", "Sxqaa", "Spqaa", "Ocqaa", "Noqaa", "Qia", "Uqia", "Dqia", "Tqia", "Qaqia", "Qiqia", "Sxqia", "Spqia", "Ocqia", "Noqia", "Sx", "U", "D", "T", "Qa", "Qi", "Sx", "Sp", "Oc", "No", "Dc", "Ud", "Dd", "Td", "Qad", "Qid", "Sxd", "Spd", "Ocd", "Nod", "Vg", "Uvg", "Dvg", "Tvg", "Qavg", "Qivg", "Sxvg", "Spvg", "Ocvg", "Novg", "Tg", "Utg", "Dtg", "Ttg", "Qatg", "Qitg", "Sxtg", "Sptg", "Octg", "Notg", "Qaa", "Uqaa", "Dqaa", "Tqaa", "Qaqaa", "Qiqaa", "Sxqaa", "Spqaa", "Ocqaa", "Noqaa", "Qia", "Uqia", "Dqia", "Tqia", "Qaqia", "Qiqia", "Sxqia", "Spqia", "Ocqia", "Noqia")


/**
 * Converts this number to a human-readable string with units.
 *
 * @param rounding the number of decimal places.
 * @param roundingMode the rounding mode to apply.
 * @param formatSymbols the decimal format symbols to use.
 * @param units the array of units to use.
 * @return the human-readable string representation of this number with units.
 */
fun Number.toBeautifulString(
    rounding: Int = 2,
    roundingMode: RoundingMode = RoundingMode.HALF_UP,
    formatSymbols: DecimalFormatSymbols = DEFAULT_DECIMAL_FORMAT_SYMBOLS,
    units: Array<String> = NUMBER_UNITS,
): String {
    var number = this.toDouble()
    var index = -1

    while (number >= 1000) {
        number /= 1000
        index++
    }

    return number.format(rounding, roundingMode, formatSymbols) + (units.getOrNull(index) ?: "")
}

/**
 * Converts this BigInteger to a human-readable string with units.
 *
 * @param rounding the number of decimal places.
 * @param roundingMode the rounding mode to apply.
 * @param formatSymbols the decimal format symbols to use.
 * @param units the array of units to use.
 * @return the human-readable string representation of this BigInteger with units.
 */
fun BigInteger.toBeautifulString(
    rounding: Int = 2,
    roundingMode: RoundingMode = RoundingMode.HALF_UP,
    formatSymbols: DecimalFormatSymbols = DEFAULT_DECIMAL_FORMAT_SYMBOLS,
    units: Array<String> = NUMBER_UNITS,
): String {
    var number: BigDecimal = this.toBigDecimal()
    var index = -1
    val thousand = Constants.BIGDEC_1000

    while (number >= thousand) {
        number = number.divide(thousand)
        index++
    }

    return number.format(rounding, roundingMode, formatSymbols) + (units.getOrNull(index) ?: "")
}

/**
 * Converts a human-readable string with units back to a double value.
 *
 * @param string the human-readable string with units.
 * @param units the array of units to use.
 * @return the double value represented by the string.
 */
fun fromBeautifulString(string: String, units: Array<String> = NUMBER_UNITS): Double {
    val number = string.replace(Patterns.NOT_DIGITS, "").toDouble()
    val unit = string.replace(Patterns.DIGITS, "")

    val index = units.indexOf(unit.uppercase())
    if (index == -1) return number

    return number * 1000.pow(index + 1)
}

/**
 * Checks if this BigInteger is zero.
 *
 * @return true if this BigInteger is zero, false otherwise.
 */
inline val BigInteger.isZero
    get() = this == BigInteger.ZERO

/**
 * Checks if this BigInteger is negative.
 *
 * @return true if this BigInteger is negative, false otherwise.
 */
inline val BigInteger.isNegative1
    get() = this == Constants.BIGINT_MINUS1

/**
 * Converts this Iterable of Numbers to a list of Bytes.
 *
 * @return a list of Bytes.
 */
inline fun Iterable<Number>.toByte() = map { it.toByte() }

/**
 * Converts this Iterable of Numbers to a list of Shorts.
 *
 * @return a list of Shorts.
 */
inline fun Iterable<Number>.toShort() = map { it.toShort() }

/**
 * Converts this Iterable of Numbers to a list of Ints.
 *
 * @return a list of Ints.
 */
inline fun Iterable<Number>.toInt() = map { it.toInt() }

/**
 * Converts this Iterable of Numbers to a list of Longs.
 *
 * @return a list of Longs.
 */
inline fun Iterable<Number>.toLong() = map { it.toLong() }

/**
 * Converts this Iterable of Numbers to a list of Floats.
 *
 * @return a list of Floats.
 */
inline fun Iterable<Number>.toFloat() = map { it.toFloat() }

/**
 * Converts this Iterable of Numbers to a list of Doubles.
 *
 * @return a list of Doubles.
 */
inline fun Iterable<Number>.toDouble() = map { it.toDouble() }

/**
 * Checks if this number is odd.
 *
 * @return true if this number is odd, false otherwise.
 */
inline val Number.isOdd: Boolean
    get() = toInt() % 2 == 0

/**
 * Converts this Boolean to a Byte.
 *
 * @return 1 if this Boolean is true, 0 if false.
 */
inline fun Boolean.toByte(): Byte = if (this) 1 else 0

/**
 * Converts this number to a Boolean.
 *
 * @return true if this number is not zero, false otherwise.
 */
inline fun Number.toBoolean() = this != 0

/**
 * Computes the squared distance between two points in 3D space.
 *
 * @param fromX the x-coordinate of the first point.
 * @param fromY the y-coordinate of the first point.
 * @param fromZ the z-coordinate of the first point.
 * @param toX the x-coordinate of the second point.
 * @param toY the y-coordinate of the second point.
 * @param toZ the z-coordinate of the second point.
 * @return the squared distance between the two points.
 */
inline fun distanceSquared(fromX: Double, fromY: Double, fromZ: Double, toX: Double, toY: Double, toZ: Double): Double {
    return (toX - fromX).pow(2) + (toY - fromY).pow(2) + (toZ - fromZ).pow(2)
}
