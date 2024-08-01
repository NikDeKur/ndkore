/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 * Copyright (c) 2024-present "Nik De Kur"
 */

package dev.nikdekur.ndkore.ext

import java.math.BigDecimal
import java.math.BigInteger
import java.text.DecimalFormat
import java.time.Duration
import java.time.OffsetDateTime
import java.util.*
import java.util.concurrent.CompletableFuture
import java.util.function.Predicate
import java.util.regex.Pattern

object Patterns {
    val RGB_1: Pattern = Pattern.compile("(\\d{1,3}) (\\d{1,3}) (\\d{1,3})")
    val RGB_2: Pattern = Pattern.compile("(\\d{1,3}), (\\d{1,3}), (\\d{1,3})")
    val RGB_3: Pattern = Pattern.compile("(\\d{1,3}); (\\d{1,3}); (\\d{1,3})")
    val HEX_1: Pattern = Pattern.compile("([A-Za-z0-9]){6}")
    val HEX_2: Pattern = Pattern.compile("#" + HEX_1.pattern())
    val ALL_RGB = listOf(RGB_1, RGB_2, RGB_3)
    val ALL_HEX = listOf(HEX_1, HEX_2)
    val NOT_DIGITS = Regex("[^\\d.]")
    val DIGITS = Regex("[\\d.]")

    val UUID = Regex("^[a-fA-F0-9]{8}-[a-fA-F0-9]{4}-[a-fA-F0-9]{4}-[a-fA-F0-9]{4}-[a-fA-F0-9]{12}$")
    val WORD_SPLIT: Regex = Regex("\\s+")

    val REMOVE_TRAILING_COMA = Regex("0*$")
    val NEXT_LINE_PATTERN: Pattern = Pattern.compile("\n")

    val decimalFormat = DecimalFormat("#,##0.00")
    val COMPARATOR_STR: Comparator<Any> = Comparator.comparing { it as String }
    val COMPARATOR_OFFSETDATETIME: Comparator<Any> = Comparator.comparing { it as OffsetDateTime? }
    val COMPARATOR_INT: Comparator<Any> = Comparator.comparing { it as Int }
    val COMPARATOR_FLOAT: Comparator<Any> = Comparator.comparing { it as Float }
    val COMPARATOR_DOUBLE: Comparator<Any> = Comparator.comparing { it as Double }
    val COMPARATOR_BOOLEAN: Comparator<Any> = Comparator.comparing { it as Boolean }
    val LOCALE_RU: Locale = Locale("ru", "RU")
    val DURATION_1S: Duration = Duration.ofSeconds(1)
    val DURATION_5S: Duration = Duration.ofSeconds(5)
    val DURATION_1M: Duration = Duration.ofMinutes(1)
    val DURATION_1H: Duration = Duration.ofHours(1)
    val DURATION_1D: Duration = Duration.ofDays(1)
}

object Constants {
    val BIGINT_MINUS2 = BigInteger("-2")
    val BIGINT_MINUS1 = BigInteger("-1")
    val BIGINT_2 = BigInteger("2")

    val BIGINT_100 = BigInteger("100")
    val BIGINT_1000 = BigInteger("1000")
    val BIGDEC_100: BigDecimal = BigDecimal("100")
    val BIGDEC_1000 = BigDecimal("1000")

    val COMPLETED_FUTURE: CompletableFuture<Unit> = CompletableFuture.completedFuture(Unit)

    fun <T> alwaysTrue() = Predicate<T> { true }

    fun <T> alwaysFalse() = Predicate<T> { false }
}