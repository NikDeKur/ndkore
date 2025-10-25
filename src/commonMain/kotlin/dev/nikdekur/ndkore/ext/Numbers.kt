/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 * Copyright (c) 2024-present "Nik De Kur"
 */

@file:Suppress("NOTHING_TO_INLINE")

package dev.nikdekur.ndkore.ext

import kotlin.math.*
import kotlin.reflect.KClass

/**
 * Squares this number.
 *
 * @return the result of squaring this number.
 */
public inline fun Double.pow(): Double = pow(2.0)

/**
 * Computes the square root of this number.
 *
 * @return the square root of this number.
 */
public inline fun Number.sqrt(): Double = sqrt(this.toDouble())

/**
 * Computes the cube root of this number.
 *
 * @return the cube root of this number.
 */
public inline fun Number.cbrt(): Double = cbrt(this.toDouble())

/**
 * Computes the absolute value of this number.
 *
 * @return the absolute value of this number.
 */
public inline fun Number.abs(): Double = abs(this.toDouble())

/**
 * Rounds this number up to the nearest integer.
 *
 * @return the smallest integer greater than or equal to this number.
 */
public inline fun Number.ceil(): Double = ceil(this.toDouble())

/**
 * Rounds this number down to the nearest integer.
 *
 * @return the largest integer less than or equal to this number.
 */

public inline fun Number.floor(): Double = floor(this.toDouble())

//
// TRIGONOMETRY
//

/**
 * Computes the sine of this number (in radians).
 *
 * @return the sine of this number.
 */
public inline fun Number.sin(): Double = sin(this.toDouble())

/**
 * Computes the cosine of this number (in radians).
 *
 * @return the cosine of this number.
 */
public inline fun Number.cos(): Double = cos(this.toDouble())

/**
 * Computes the tangent of this number (in radians).
 *
 * @return the tangent of this number.
 */
public inline fun Number.tan(): Double = tan(this.toDouble())

/**
 * Computes the arc sine of this number.
 *
 * @return the arc sine of this number.
 */
public inline fun Number.asin(): Double = asin(this.toDouble())

/**
 * Computes the arc cosine of this number.
 *
 * @return the arc cosine of this number.
 */
public inline fun Number.acos(): Double = acos(this.toDouble())

/**
 * Computes the arc tangent of this number.
 *
 * @return the arc tangent of this number.
 */
public inline fun Number.atan(): Double = atan(this.toDouble())

/**
 * Computes the hyperbolic sine of this number.
 *
 * @return the hyperbolic sine of this number.
 */
public inline fun Number.sinh(): Double = sinh(this.toDouble())

/**
 * Computes the hyperbolic cosine of this number.
 *
 * @return the hyperbolic cosine of this number.
 */
public inline fun Number.cosh(): Double = cosh(this.toDouble())

/**
 * Computes the hyperbolic tangent of this number.
 *
 * @return the hyperbolic tangent of this number.
 */
public inline fun Number.tanh(): Double = tanh(this.toDouble())


public const val DEGREES_TO_RADIANS: Double = 0.017453292519943295
public const val RADIANS_TO_DEGREES: Double = 57.29577951308232

/**
 * Converts this number from radians to degrees.
 *
 * @return the equivalent value of this number in degrees.
 */
public inline fun Number.toDegrees(): Double = this.toDouble() * RADIANS_TO_DEGREES

/**
 * Converts this number from degrees to radians.
 *
 * @return the equivalent value of this number in radians.
 */
public inline fun Number.toRadians(): Double = this.toDouble() * DEGREES_TO_RADIANS

//
// Mathematical Operators
//

/**
 * Subtracts the specified value from this number.
 *
 * @param value the value to subtract from this number.
 * @return the result of subtracting the specified value from this number.
 */
public inline operator fun Number.minus(value: Number): Double = this.toDouble() - value.toDouble()

/**
 * Multiplies this number by the specified value.
 *
 * @param value the value to multiply this number by.
 * @return the result of multiplying this number by the specified value.
 */
public inline operator fun Number.times(value: Number): Double = this.toDouble() * value.toDouble()

/**
 * Divides this number by the specified value.
 *
 * @param value the value to divide this number by.
 * @return the result of dividing this number by the specified value.
 */
public inline operator fun Number.div(value: Number): Double = this.toDouble() / value.toDouble()

/**
 * Computes the remainder of dividing this number by the specified value.
 *
 * @param value the divisor.
 * @return the remainder of dividing this number by the specified value.
 */
public inline operator fun Number.rem(value: Number): Double = this.toDouble() % value.toDouble()



/**
 * Compares this number with the specified value.
 *
 * @param value the value to compare this number with.
 * @return a negative integer, zero, or a positive integer as this number is less than, equal to, or greater than the specified value.
 */
public inline operator fun Number.compareTo(value: Number): Int = toDouble().compareTo(value.toDouble())

/**
 * Casts this number to the specified type.
 *
 * @param clazz the KClass representing the type to cast to.
 * @return this number cast to the specified type.
 * @throws IllegalArgumentException if the specified type is not supported.
 */
@Suppress("UNCHECKED_CAST")
public fun <T : Number> Number.castTo(clazz: KClass<T>): T {
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


/**
 * # Rounding Strategy
 *
 * Interface representing a strategy for rounding floating-point numbers.
 * Implementations define how numbers are rounded, particularly when they are
 * exactly halfway between two possible rounded values.
 *
 * @see RoundingStrategy.Companion
 */
public fun interface RoundingStrategy {
    /**
     * Rounds a number according to the strategy.
     *
     * @param factor the scale factor to divide the rounded number by (used for decimal scaling)
     * @param scaledNumber the number to round, already scaled by [factor] if necessary
     * @return the rounded result
     */
    public fun round(factor: Double, scaledNumber: Double): Double

    public companion object {
        /**
         * Always rounds towards positive infinity.
         *
         * Examples:
         * ```
         * 1.5 -> 2.0
         * -1.5 -> -1.0
         * ```
         */
        public val UP: RoundingStrategy = RoundingStrategy { factor, scaledNumber ->
            ceil(scaledNumber) / factor
        }

        /**
         * Always rounds towards negative infinity.
         *
         * Examples:
         * ```
         * 1.5 -> 1.0
         * -1.5 -> -2.0
         * ```
         */
        public val DOWN: RoundingStrategy = RoundingStrategy { factor, scaledNumber ->
            floor(scaledNumber) / factor
        }

        /**
         * Rounds towards the nearest neighbour unless equidistant,
         * then rounds up ("round half up").
         *
         * Examples:
         * ```
         * 1.5 -> 2.0
         * 1.49 -> 1.0
         * ```
         */
        public val HALF_UP: RoundingStrategy = RoundingStrategy { factor, scaledNumber ->
            if (scaledNumber % 1 >= 0.5) ceil(scaledNumber) / factor
            else floor(scaledNumber) / factor
        }

        /**
         * Rounds towards the nearest neighbour unless equidistant,
         * then rounds down ("round half down").
         *
         * Examples:
         * ```
         * 1.5 -> 1.0
         * 1.51 -> 2.0
         * ```
         */
        public val HALF_DOWN: RoundingStrategy = RoundingStrategy { factor, scaledNumber ->
            if (scaledNumber % 1 > 0.5) ceil(scaledNumber) / factor
            else floor(scaledNumber) / factor
        }

        /**
         * Rounds towards the nearest neighbour unless equidistant,
         * then rounds to the nearest even number ("bankers' rounding").
         *
         * Examples:
         * ```
         * 1.5 -> 2.0
         * 2.5 -> 2.0
         * ```
         */
        public val HALF_EVEN: RoundingStrategy = RoundingStrategy { factor, scaledNumber ->
            if (scaledNumber % 1 == 0.5) {
                if (scaledNumber.toLong() % 2 == 0L) floor(scaledNumber) / factor
                else ceil(scaledNumber) / factor
            } else {
                round(scaledNumber) / factor
            }
        }
    }
}

public fun Double.round(
    decimalPlaces: Int,
    strategy: RoundingStrategy = RoundingStrategy.HALF_UP
): Double {
    require(decimalPlaces >= 0) { "Decimal places must be positive." }

    val factor = 10.0.pow(decimalPlaces)
    val scaledNumber = this * factor

    return strategy.round(factor, scaledNumber)
}


public fun Float.round(
    decimalPlaces: Int,
    strategy: RoundingStrategy = RoundingStrategy.HALF_UP
): Float {
    require(decimalPlaces >= 0) { "Decimal places must be positive." }

    val factor = 10.0.pow(decimalPlaces)
    val scaledNumber = this * factor

    return strategy.round(factor, scaledNumber).toFloat()
}

/**
 * Formats this number to a string with the specified number of decimal places and rounding mode.
 *
 * @param places the number of decimal places.
 * @param roundingMode the rounding mode to apply.
 * @return the formatted string representation of this number.
 */
public fun Number.format(
    places: Int,
    roundingMode: RoundingStrategy = RoundingStrategy.HALF_UP
): String {

    return toDouble()
        .round(places, roundingMode)
        .toString()
        .replace(Patterns.REMOVE_TRAILING_COMA, "")
}

public val NUMBER_UNITS: Array<String> = arrayOf(
    "K",
    "M",
    "B",
    "T",
    "Qa",
    "Qi",
    "Sx",
    "Sp",
    "Oc",
    "No",
    "Dc",
    "Ud",
    "Dd",
    "Td",
    "Qad",
    "Qid",
    "Sxd",
    "Spd",
    "Ocd",
    "Nod",
    "Vg",
    "Uvg",
    "Dvg",
    "Tvg",
    "Qavg",
    "Qivg",
    "Sxvg",
    "Spvg",
    "Ocvg",
    "Novg",
    "Tg",
    "Utg",
    "Dtg",
    "Ttg",
    "Qatg",
    "Qitg",
    "Sxtg",
    "Sptg",
    "Octg",
    "Notg",
    "Qaa",
    "Uqaa",
    "Dqaa",
    "Tqaa",
    "Qaqaa",
    "Qiqaa",
    "Sxqaa",
    "Spqaa",
    "Ocqaa",
    "Noqaa",
    "Qia",
    "Uqia",
    "Dqia",
    "Tqia",
    "Qaqia",
    "Qiqia",
    "Sxqia",
    "Spqia",
    "Ocqia",
    "Noqia",
    "Sx",
    "U",
    "D",
    "T",
    "Qa",
    "Qi",
    "Sx",
    "Sp",
    "Oc",
    "No",
    "Dc",
    "Ud",
    "Dd",
    "Td",
    "Qad",
    "Qid",
    "Sxd",
    "Spd",
    "Ocd",
    "Nod",
    "Vg",
    "Uvg",
    "Dvg",
    "Tvg",
    "Qavg",
    "Qivg",
    "Sxvg",
    "Spvg",
    "Ocvg",
    "Novg",
    "Tg",
    "Utg",
    "Dtg",
    "Ttg",
    "Qatg",
    "Qitg",
    "Sxtg",
    "Sptg",
    "Octg",
    "Notg",
    "Qaa",
    "Uqaa",
    "Dqaa",
    "Tqaa",
    "Qaqaa",
    "Qiqaa",
    "Sxqaa",
    "Spqaa",
    "Ocqaa",
    "Noqaa",
    "Qia",
    "Uqia",
    "Dqia",
    "Tqia",
    "Qaqia",
    "Qiqia",
    "Sxqia",
    "Spqia",
    "Ocqia",
    "Noqia"
)


/**
 * Converts this number to a human-readable string with units.
 *
 * @param rounding the number of decimal places.
 * @param roundingMode the rounding mode to apply.
 * @param units the array of units to use.
 * @return the human-readable string representation of this number with units.
 */
public fun Number.toBeautifulString(
    rounding: Int = 2,
    roundingMode: RoundingStrategy = RoundingStrategy.HALF_UP,
    units: Array<String> = NUMBER_UNITS,
): String {
    var number = this.toDouble()
    var index = -1

    while (number >= 1000) {
        number /= 1000
        index++
    }

    return number.format(rounding, roundingMode) + (units.getOrNull(index) ?: "")
}

/**
 * Converts a human-readable string with units back to a double value.
 *
 * @param string the human-readable string with units.
 * @param units the array of units to use.
 * @return the double value represented by the string.
 */
public fun fromBeautifulString(
    string: String,
    units: Array<String> = NUMBER_UNITS
): Double {
    val number = string.replace(Patterns.NOT_DIGITS, "").toDouble()
    val unit = string.replace(Patterns.DIGITS, "")

    val index = units.indexOf(unit.uppercase())
    if (index == -1) return number

    return (number * 1000).pow(index + 1)
}



/**
 * Converts this Iterable of Numbers to a list of Bytes.
 *
 * @return a list of Bytes.
 */
public inline fun Iterable<Number>.toByte(): List<Byte> = map { it.toByte() }

/**
 * Converts this Iterable of Numbers to a list of Shorts.
 *
 * @return a list of Shorts.
 */
public inline fun Iterable<Number>.toShort(): List<Short> = map { it.toShort() }

/**
 * Converts this Iterable of Numbers to a list of Ints.
 *
 * @return a list of Ints.
 */
public inline fun Iterable<Number>.toInt(): List<Int> = map { it.toInt() }

/**
 * Converts this Iterable of Numbers to a list of Longs.
 *
 * @return a list of Longs.
 */
public inline fun Iterable<Number>.toLong(): List<Long> = map { it.toLong() }

/**
 * Converts this Iterable of Numbers to a list of Floats.
 *
 * @return a list of Floats.
 */
public inline fun Iterable<Number>.toFloat(): List<Float> = map { it.toFloat() }

/**
 * Converts this Iterable of Numbers to a list of Doubles.
 *
 * @return a list of Doubles.
 */
public inline fun Iterable<Number>.toDouble(): List<Double> = map { it.toDouble() }

/**
 * Checks if this number is odd.
 *
 * @return true if this number is odd, false otherwise.
 */
public inline val Number.isOdd: Boolean
    get() = toInt() % 2 == 0

/**
 * Converts this Boolean to a Byte.
 *
 * @return 1 if this Boolean is true, 0 if false.
 */
public inline fun Boolean.toByte(): Byte = if (this) 1 else 0

/**
 * Converts this number to a Boolean.
 *
 * @return true if this number is not zero, false otherwise.
 */
public inline fun Number.toBoolean(): Boolean = this != 0



/**
 * Calculates the squared distance between two points given by their individual coordinates in 3D space.
 *
 * @param x1 The x-coordinate of the first point.
 * @param y1 The y-coordinate of the first point.
 * @param z1 The z-coordinate of the first point.
 * @param x2 The x-coordinate of the second point.
 * @param y2 The y-coordinate of the second point.
 * @param z2 The z-coordinate of the second point.
 * @return The squared distance between the two points.
 */
public inline fun distanceSquared(
    x1: Double, y1: Double, z1: Double,
    x2: Double, y2: Double, z2: Double,
): Double {
    val a = x1 - x2
    val b = y1 - y2
    val c = z1 - z2
    return a * a + b * b + c * c
}


/**
 * Calculates the mean of the specified numbers.
 *
 * Mean is the average of a set of numbers.
 *
 * It is calculated by adding up all the numbers and then dividing the sum by the count of numbers.
 *
 * @param numbers the numbers to calculate the mean of.
 * @return the mean of the specified numbers.
 * @throws ArithmeticException if the count of numbers is zero.
 * @see sum
 */
public inline fun mean(
    vararg numbers: Number
): Double {
    var sum = 0.0
    for (number in numbers) {
        sum += number.toDouble()
    }
    return sum / numbers.size
}