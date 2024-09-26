/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 * Copyright (c) 2024-present "Nik De Kur"
 */

package dev.nikdekur.ndkore.ext

public object Patterns {
    public val RGB_1: Regex = Regex("(\\d{1,3}) (\\d{1,3}) (\\d{1,3})")
    public val RGB_2: Regex = Regex("(\\d{1,3}), (\\d{1,3}), (\\d{1,3})")
    public val RGB_3: Regex = Regex("(\\d{1,3}); (\\d{1,3}); (\\d{1,3})")
    public val HEX_1: Regex = Regex("([A-Za-z0-9]){6}")
    public val HEX_2: Regex = Regex("#${HEX_1.pattern}")
    public val ALL_RGB: List<Regex> = listOf(RGB_1, RGB_2, RGB_3)
    public val ALL_HEX: List<Regex> = listOf(HEX_1, HEX_2)
    public val NOT_DIGITS: Regex = Regex("[^\\d.]")
    public val DIGITS: Regex = Regex("[\\d.]")

    public val UUID: Regex = Regex("^[a-fA-F0-9]{8}-[a-fA-F0-9]{4}-[a-fA-F0-9]{4}-[a-fA-F0-9]{4}-[a-fA-F0-9]{12}$")
    public val WORD_SPLIT: Regex = Regex("\\s+")

    public val REMOVE_TRAILING_COMA: Regex = Regex("\\.?0*$")
    public val NEXT_LINE_PATTERN: Regex = Regex("\n")
}

public object Constants {
    public fun <T> alwaysTrue(): (T) -> Boolean = { _: T -> true }
    public fun <T> alwaysFalse(): (T) -> Boolean = { _: T -> false }
}