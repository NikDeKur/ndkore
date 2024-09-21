/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 * Copyright (c) 2024-present "Nik De Kur"
 */

package dev.nikdekur.ndkore.ext

object Patterns {
    val RGB_1 = Regex("(\\d{1,3}) (\\d{1,3}) (\\d{1,3})")
    val RGB_2 = Regex("(\\d{1,3}), (\\d{1,3}), (\\d{1,3})")
    val RGB_3 = Regex("(\\d{1,3}); (\\d{1,3}); (\\d{1,3})")
    val HEX_1 = Regex("([A-Za-z0-9]){6}")
    val HEX_2 = Regex("#${HEX_1.pattern}")
    val ALL_RGB = listOf(RGB_1, RGB_2, RGB_3)
    val ALL_HEX = listOf(HEX_1, HEX_2)
    val NOT_DIGITS = Regex("[^\\d.]")
    val DIGITS = Regex("[\\d.]")

    val UUID = Regex("^[a-fA-F0-9]{8}-[a-fA-F0-9]{4}-[a-fA-F0-9]{4}-[a-fA-F0-9]{4}-[a-fA-F0-9]{12}$")
    val WORD_SPLIT: Regex = Regex("\\s+")

    val REMOVE_TRAILING_COMA = Regex("\\.?0*$")
    val NEXT_LINE_PATTERN = Regex("\n")
}

object Constants {
    fun <T> alwaysTrue() = { _: T -> true }
    fun <T> alwaysFalse() = { _: T -> false }
}