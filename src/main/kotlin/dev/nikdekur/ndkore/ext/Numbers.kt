/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 * Copyright (c) 2024 Nik De Kur
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

@Suppress("ReplaceJavaStaticMethodWithKotlinAnalog")
inline fun Number.pow(value: Number) = nativeMath.pow(this.toDouble(), value.toDouble())
@Suppress("ReplaceJavaStaticMethodWithKotlinAnalog")
inline fun Number.pow() = nativeMath.pow(this.toDouble(), 2.0)
@Suppress("ReplaceJavaStaticMethodWithKotlinAnalog")
inline fun Number.sqrt() = nativeMath.sqrt(this.toDouble())
@Suppress("ReplaceJavaStaticMethodWithKotlinAnalog")
inline fun Number.cbrt() = nativeMath.cbrt(this.toDouble())
@Suppress("ReplaceJavaStaticMethodWithKotlinAnalog")
inline fun Number.abs() = nativeMath.abs(this.toDouble())
@Suppress("ReplaceJavaStaticMethodWithKotlinAnalog")
inline fun Number.ceil() = nativeMath.ceil(this.toDouble())
@Suppress("ReplaceJavaStaticMethodWithKotlinAnalog")
inline fun Number.floor() = nativeMath.floor(this.toDouble())




//
// TRIGONOMETRY
//

inline fun Number.sin() = sin(this.toDouble())
inline fun Number.cos() = cos(this.toDouble())
inline fun Number.tan() = tan(this.toDouble())


inline fun Number.asin() = asin(this.toDouble())
inline fun Number.acos() = acos(this.toDouble())
inline fun Number.atan() = atan(this.toDouble())


inline fun Number.sinh() = sinh(this.toDouble())
inline fun Number.cosh() = cosh(this.toDouble())
inline fun Number.tanh() = tanh(this.toDouble())






//
// Angles
//
const val DEGREES_TO_RADIANS = 0.017453292519943295
const val RADIANS_TO_DEGREES = 57.29577951308232

inline fun Number.toDegrees() = this.toDouble() * RADIANS_TO_DEGREES
inline fun Number.toRadians() = this.toDouble() * DEGREES_TO_RADIANS



//
// Mathematical Operators
//
inline operator fun Number.minus(value: Number) = this.toDouble() - value.toDouble()
inline operator fun Number.times(value: Number) = this.toDouble() * value.toDouble()
inline operator fun Number.div(value: Number) = this.toDouble() / value.toDouble()
inline operator fun Number.rem(value: Number) = this.toDouble() % value.toDouble()

inline operator fun BigInteger.minus(value: BigInteger): BigInteger = this.subtract(value)
inline operator fun BigInteger.plus(value: BigInteger): BigInteger = this.add(value)
inline operator fun BigInteger.div(value: BigInteger): BigInteger = this.divide(value)



inline operator fun Number.compareTo(value: Number) = toDouble().compareTo(value.toDouble())

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

val DECIMAL_FORMAT_SYMBOLS = DecimalFormatSymbols().apply {
    groupingSeparator = ','
    decimalSeparator = '.'
}


fun <T : Number> T.format(places: Int): String {
    require(places >= 0) { "Places must be positive" }
    val format = if (places > 0) {
        "#,##0." + buildString { repeat(places) { append('0') } }
    } else {
        "#,##0"
    }
    val df = DecimalFormat(format, DECIMAL_FORMAT_SYMBOLS)
    df.roundingMode = RoundingMode.HALF_UP

    var formattedString = df.format(this)


    formattedString = formattedString.replace(Patterns.REMOVE_TRAILING_COMA, "")
    formattedString = formattedString.removeSuffix(".")


    return formattedString
}


val NUMBER_UNITS = arrayOf("K", "M", "B", "T", "Qa", "Qi", "Sx", "Sp", "Oc", "No", "Dc", "Ud", "Dd", "Td", "Qad", "Qid", "Sxd", "Spd", "Ocd", "Nod", "Vg", "Uvg", "Dvg", "Tvg", "Qavg", "Qivg", "Sxvg", "Spvg", "Ocvg", "Novg", "Tg", "Utg", "Dtg", "Ttg", "Qatg", "Qitg", "Sxtg", "Sptg", "Octg", "Notg", "Qaa", "Uqaa", "Dqaa", "Tqaa", "Qaqaa", "Qiqaa", "Sxqaa", "Spqaa", "Ocqaa", "Noqaa", "Qia", "Uqia", "Dqia", "Tqia", "Qaqia", "Qiqia", "Sxqia", "Spqia", "Ocqia", "Noqia", "Sx", "U", "D", "T", "Qa", "Qi", "Sx", "Sp", "Oc", "No", "Dc", "Ud", "Dd", "Td", "Qad", "Qid", "Sxd", "Spd", "Ocd", "Nod", "Vg", "Uvg", "Dvg", "Tvg", "Qavg", "Qivg", "Sxvg", "Spvg", "Ocvg", "Novg", "Tg", "Utg", "Dtg", "Ttg", "Qatg", "Qitg", "Sxtg", "Sptg", "Octg", "Notg", "Qaa", "Uqaa", "Dqaa", "Tqaa", "Qaqaa", "Qiqaa", "Sxqaa", "Spqaa", "Ocqaa", "Noqaa", "Qia", "Uqia", "Dqia", "Tqia", "Qaqia", "Qiqia", "Sxqia", "Spqia", "Ocqia", "Noqia")
fun Number.toBeautifulString(rounding: Int = 2, units: Array<String> = NUMBER_UNITS): String {
    var number = this.toDouble()
    var index = -1

    while (number >= 1000) {
        number /= 1000
        index++
    }

    return number.format(rounding) + (units.getOrNull(index) ?: "")
}

fun BigInteger.toBeautifulString(rounding: Int = 2, units: Array<String> = NUMBER_UNITS): String {
    var number: BigDecimal = this.toBigDecimal()
    var index = -1
    val thousand = Constants.BIGDEC_1000

    while (number >= thousand) {
        number = number.divide(thousand)
        index++
    }

    return number.format(rounding) + (units.getOrNull(index) ?: "")
}


fun fromBeautifulString(string: String, units: Array<String> = NUMBER_UNITS): Double {
    val number = string.replace(Patterns.REMOVE_NOT_DIGITS, "").toDouble()
    val unit = string.replace(Patterns.REMOVE_DIGITS, "")

    val index = units.indexOf(unit.uppercase())
    if (index == -1) return number

    return number * 1000.pow(index + 1)
}

inline val BigInteger.isZero
    get() = this == BigInteger.ZERO
inline val BigInteger.isNegative1
    get() = this == Constants.BIGINT_MINUS1






inline fun Iterable<Number>.toByte() = map { it.toByte() }
inline fun Iterable<Number>.toShort() = map { it.toShort() }
inline fun Iterable<Number>.toInt() = map { it.toInt() }
inline fun Iterable<Number>.toLong() = map { it.toLong() }
inline fun Iterable<Number>.toFloat() = map { it.toFloat() }
inline fun Iterable<Number>.toDouble() = map { it.toDouble() }



inline val Number.isOdd: Boolean
    get() = toInt() % 2 == 0



inline fun Boolean.toByte(): Byte = if (this) 1 else 0

inline fun Number.toBoolean() = this != 0



inline fun distanceSquared(fromX: Double, fromY: Double, fromZ: Double, toX: Double, toY: Double, toZ: Double): Double {
    return (toX - fromX).pow(2) + (toY - fromY).pow(2) + (toZ - fromZ).pow(2)
}