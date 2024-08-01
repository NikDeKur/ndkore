/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 * Copyright (c) 2024-present "Nik De Kur"
 */

package dev.nikdekur.ndkore.ext

import org.junit.jupiter.api.Test
import java.math.RoundingMode
import kotlin.test.assertEquals

class BeautifulStringFormattingTest {

    @Test
    fun `test int formatting`() {
        val number = 123456789
        val formatted = number.toBeautifulString(
            rounding = 2
        )
        assertEquals("123.46M", formatted)
    }

    @Test
    fun `test double formatting`() {
        val number = 123456789.123456789
        val formatted = number.toBeautifulString(
            rounding = 2
        )
        assertEquals("123.46M", formatted)
    }

    @Test
    fun `test big decimal formatting`() {
        val number = 545454.341.toBigDecimal()
        val formatted = number.toBeautifulString(
            rounding = 2
        )
        assertEquals("545.45K", formatted)
    }

    @Test
    fun `test big integer formatting`() {
        val number = "675627567244865258123456789".toBigInteger()
        val formatted = number.toBeautifulString(
            rounding = 3
        )
        assertEquals("675.628Sp", formatted)
    }

    @Test
    fun `test rounding mode change`() {
        val number = "675627567244865258123456789".toBigInteger()
        val formatted = number.toBeautifulString(
            rounding = 2,
            roundingMode = RoundingMode.CEILING
        )
        assertEquals("675.63Sp", formatted)
    }
}