/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 * Copyright (c) 2024-present "Nik De Kur"
 */

package dev.nikdekur.ndkore.ext

import kotlin.test.Test
import kotlin.test.assertEquals

class BeautifulStringFormattingTest {

    @Test
    fun testIntFormatting() {
        val number = 123456789
        val formatted = number.toBeautifulString(
            rounding = 2
        )
        assertEquals("123.46M", formatted)
    }

    @Test
    fun testLongFormatting() {
        val number = 1_234_567_890_123_456_789
        val formatted = number.toBeautifulString(
            rounding = 2
        )
        assertEquals("1.23Qi", formatted)
    }

    @Test
    fun testDoubleFormatting() {
        val number = 123456789.12345678
        val formatted = number.toBeautifulString(
            rounding = 2
        )
        assertEquals("123.46M", formatted)
    }

    @Test
    fun testFloatFormatting() {
        val number = 12345.123f
        val formatted = number.toBeautifulString(
            rounding = 2
        )
        assertEquals("12.35K", formatted)
    }

    @Test
    fun testByteFormatting() {
        val number = 123.toByte()
        val formatted = number.toBeautifulString(
            rounding = 2
        )
        assertEquals("123", formatted)
    }

    @Test
    fun testShortFormatting() {
        val number = 12345.toShort()
        val formatted = number.toBeautifulString(
            rounding = 2
        )
        assertEquals("12.35K", formatted)
    }
}