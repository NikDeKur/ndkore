/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 * Copyright (c) 2024-present "Nik De Kur"
 */

package dev.nikdekur.ndkore.memory

import kotlin.test.Test
import kotlin.test.assertEquals

class MemoryTest {

    @Test
    fun testConvertingKilobytesToBytes() {
        assertEquals(
            1024, MemoryAmount.of(
                input = MemoryUnit.KB,
                output = MemoryUnit.Byte,
                value = 1
            ).amount
        )
    }


    @Test
    fun testConvertingMegabytesToBytes() {
        assertEquals(
            1048576, MemoryAmount.of(
                input = MemoryUnit.MB,
                output = MemoryUnit.Byte,
                value = 1
            ).amount
        )
    }

    @Test
    fun testBytesToMegabytes() {
        assertEquals(
            1, MemoryAmount.of(
                input = MemoryUnit.Byte,
                output = MemoryUnit.MB,
                value = 1048576
            ).amount
        )
    }

    @Test
    fun testConvertYBtoZB() {
        assertEquals(
            1024, MemoryAmount.of(
                input = MemoryUnit.YB,
                output = MemoryUnit.ZB,
                value = 1
            ).amount
        )
    }

    @Test
    fun testAddition() {
        val memory1 = MemoryAmount(MemoryUnit.KB, 1)
        val memory2 = MemoryAmount(MemoryUnit.KB, 2)
        val result = memory1 + memory2
        assertEquals(3, result.amount)
    }

}