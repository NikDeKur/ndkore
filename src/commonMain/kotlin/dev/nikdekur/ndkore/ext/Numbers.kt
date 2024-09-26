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
public inline fun Double.pow() = pow(2.0)

/**
 * Computes the square root of this number.
 *
 * @return the square root of this number.
 */
public inline fun Number.sqrt() = sqrt(this.toDouble())

/**
 * Computes the cube root of this number.
 *
 * @return the cube root of this number.
 */
public inline fun Number.cbrt() = cbrt(this.toDouble())

/**
 * Computes the absolute value of this number.
 *
 * @return the absolute value of this number.
 */
public inline fun Number.abs() = abs(this.toDouble())

/**
 * Rounds this number up to the nearest integer.
 *
 * @return the smallest integer greater than or equal to this number.
 */
public inline fun Number.ceil() = ceil(this.toDouble())

/**
 * Rounds this number down to the nearest integer.
 *
 * @return the largest integer less than or equal to this number.
 */

public inline fun Number.floor() = floor(this.toDouble())

//
// TRIGONOMETRY
//

/**
 * Computes the sine of this number (in radians).
 *
 * @return the sine of this number.
 */
public inline fun Number.sin() = sin(this.toDouble())

/**
 * Computes the cosine of this number (in radians).
 *
 * @return the cosine of this number.
 */
public inline fun Number.cos() = cos(this.toDouble())

/**
 * Computes the tangent of this number (in radians).
 *
 * @return the tangent of this number.
 */
public inline fun Number.tan() = tan(this.toDouble())

/**
 * Computes the arc sine of this number.
 *
 * @return the arc sine of this number.
 */
public inline fun Number.asin() = asin(this.toDouble())

/**
 * Computes the arc cosine of this number.
 *
 * @return the arc cosine of this number.
 */
public inline fun Number.acos() = acos(this.toDouble())

/**
 * Computes the arc tangent of this number.
 *
 * @return the arc tangent of this number.
 */
public inline fun Number.atan() = atan(this.toDouble())

/**
 * Computes the hyperbolic sine of this number.
 *
 * @return the hyperbolic sine of this number.
 */
public inline fun Number.sinh() = sinh(this.toDouble())

/**
 * Computes the hyperbolic cosine of this number.
 *
 * @return the hyperbolic cosine of this number.
 */
public inline fun Number.cosh() = cosh(this.toDouble())

/**
 * Computes the hyperbolic tangent of this number.
 *
 * @return the hyperbolic tangent of this number.
 */
public inline fun Number.tanh() = tanh(this.toDouble())


public const val DEGREES_TO_RADIANS: Double = 0.017453292519943295
public const val RADIANS_TO_DEGREES: Double = 57.29577951308232

/**
 * Converts this number from radians to degrees.
 *
 * @return the equivalent value of this number in degrees.
 */
public inline fun Number.toDegrees() = this.toDouble() * RADIANS_TO_DEGREES

/**
 * Converts this number from degrees to radians.
 *
 * @return the equivalent value of this number in radians.
 */
public inline fun Number.toRadians() = this.toDouble() * DEGREES_TO_RADIANS

//
// Mathematical Operators
//

/**
 * Subtracts the specified value from this number.
 *
 * @param value the value to subtract from this number.
 * @return the result of subtracting the specified value from this number.
 */
public inline operator fun Number.minus(value: Number) = this.toDouble() - value.toDouble()

/**
 * Multiplies this number by the specified value.
 *
 * @param value the value to multiply this number by.
 * @return the result of multiplying this number by the specified value.
 */
public inline operator fun Number.times(value: Number) = this.toDouble() * value.toDouble()

/**
 * Divides this number by the specified value.
 *
 * @param value the value to divide this number by.
 * @return the result of dividing this number by the specified value.
 */
public inline operator fun Number.div(value: Number) = this.toDouble() / value.toDouble()

/**
 * Computes the remainder of dividing this number by the specified value.
 *
 * @param value the divisor.
 * @return the remainder of dividing this number by the specified value.
 */
public inline operator fun Number.rem(value: Number) = this.toDouble() % value.toDouble()



/**
 * Compares this number with the specified value.
 *
 * @param value the value to compare this number with.
 * @return a negative integer, zero, or a positive integer as this number is less than, equal to, or greater than the specified value.
 */
public inline operator fun Number.compareTo(value: Number) = toDouble().compareTo(value.toDouble())

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
 * # Rounding Mode
 *
 * Specifies different strategies for rounding floating-point numbers.
 * Used to determine the behavior when a number is exactly halfway between two possible rounded values.
 *
 * Used in the [round] function.
 */
public enum class RoundingMode {
    /**
     * Always rounds towards positive infinity.
     * Example: 1.5 becomes 2.0, -1.5 becomes -1.0.
     */
    UP,

    /**
     * Always rounds towards negative infinity.
     * Example: 1.5 becomes 1.0, -1.5 becomes -2.0.
     */
    DOWN,

    /**
     * Rounds towards the nearest neighbor unless both neighbors are equidistant,
     * in which case it rounds up. Commonly known as "round half up."
     * Example: 1.5 becomes 2.0, 1.49 becomes 1.0.
     */
    HALF_UP,

    /**
     * Rounds towards the nearest neighbor unless both neighbors are equidistant,
     * in which case it rounds down. Known as "round half down."
     * Example: 1.5 becomes 1.0, 1.51 becomes 2.0.
     */
    HALF_DOWN,

    /**
     * Rounds towards the nearest neighbor unless both neighbors are equidistant,
     * in which case it rounds towards the nearest even number. Known as "bankers' rounding."
     * Example: 1.5 becomes 2.0, 2.5 becomes 2.0.
     */
    HALF_EVEN
}

public fun Double.round(
    decimalPlaces: Int,
    strategy: RoundingMode = RoundingMode.HALF_UP
): Double {

    require(decimalPlaces >= 0) { "Decimal places must be positive." }

    val factor = 10.0.pow(decimalPlaces)
    val scaledNumber = this * factor

    return when (strategy) {
        RoundingMode.UP -> ceil(scaledNumber) / factor
        RoundingMode.DOWN -> floor(scaledNumber) / factor
        RoundingMode.HALF_UP -> {
            if (scaledNumber % 1 >= 0.5) ceil(scaledNumber) / factor
            else floor(scaledNumber) / factor
        }

        RoundingMode.HALF_DOWN -> {
            if (scaledNumber % 1 > 0.5) ceil(scaledNumber) / factor
            else floor(scaledNumber) / factor
        }

        RoundingMode.HALF_EVEN -> {
            if (scaledNumber % 1 == 0.5) {
                if ((scaledNumber.toLong() % 2) == 0L) floor(scaledNumber) / factor
                else ceil(scaledNumber) / factor
            } else {
                round(scaledNumber) / factor
            }
        }
    }
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
    roundingMode: RoundingMode = RoundingMode.HALF_UP
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
 * @param formatSymbols the decimal format symbols to use.
 * @param units the array of units to use.
 * @return the human-readable string representation of this number with units.
 */
public fun Number.toBeautifulString(
    rounding: Int = 2,
    roundingMode: RoundingMode = RoundingMode.HALF_UP,
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
public fun fromBeautifulString(string: String, units: Array<String> = NUMBER_UNITS): Double {
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
public inline fun Iterable<Number>.toByte() = map { it.toByte() }

/**
 * Converts this Iterable of Numbers to a list of Shorts.
 *
 * @return a list of Shorts.
 */
public inline fun Iterable<Number>.toShort() = map { it.toShort() }

/**
 * Converts this Iterable of Numbers to a list of Ints.
 *
 * @return a list of Ints.
 */
public inline fun Iterable<Number>.toInt() = map { it.toInt() }

/**
 * Converts this Iterable of Numbers to a list of Longs.
 *
 * @return a list of Longs.
 */
public inline fun Iterable<Number>.toLong() = map { it.toLong() }

/**
 * Converts this Iterable of Numbers to a list of Floats.
 *
 * @return a list of Floats.
 */
public inline fun Iterable<Number>.toFloat() = map { it.toFloat() }

/**
 * Converts this Iterable of Numbers to a list of Doubles.
 *
 * @return a list of Doubles.
 */
public inline fun Iterable<Number>.toDouble() = map { it.toDouble() }

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
public inline fun Number.toBoolean() = this != 0

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
public inline fun distanceSquared(
    fromX: Double,
    fromY: Double,
    fromZ: Double,
    toX: Double,
    toY: Double,
    toZ: Double
): Double {
    return (toX - fromX).pow(2) + (toY - fromY).pow(2) + (toZ - fromZ).pow(2)
}

