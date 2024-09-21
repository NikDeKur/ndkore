/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 * Copyright (c) 2024-present "Nik De Kur"
 */

package dev.nikdekur.ndkore.memory

import com.ionspin.kotlin.bignum.integer.BigInteger
import com.ionspin.kotlin.bignum.integer.toBigInteger
import kotlin.test.Test
import kotlin.test.assertEquals

class MemoryTest {

    @Test
    fun testConvertingKilobytesToBytes() {
        assertEquals(
            1024.toBigInteger(), MemoryAmount.of(
                input = MemoryUnit.KB,
                output = MemoryUnit.Byte,
                value = BigInteger.ONE
            ).amount
        )
    }


    @Test
    fun testConvertingMegabytesToBytes() {
        assertEquals(
            1048576.toBigInteger(), MemoryAmount.of(
                input = MemoryUnit.MB,
                output = MemoryUnit.Byte,
                value = BigInteger.ONE
            ).amount
        )
    }

    @Test
    fun testBytesToMegabytes() {
        assertEquals(
            BigInteger.ONE, MemoryAmount.of(
                input = MemoryUnit.Byte,
                output = MemoryUnit.MB,
                value = 1048576.toBigInteger()
            ).amount
        )
    }

    @Test
    fun testConvertYBtoZB() {
        assertEquals(
            1024.toBigInteger(), MemoryAmount.of(
                input = MemoryUnit.YB,
                output = MemoryUnit.ZB,
                value = BigInteger.ONE
            ).amount
        )
    }

    @Test
    fun testAddition() {
        val memory1 = MemoryAmount(MemoryUnit.KB, BigInteger.ONE)
        val memory2 = MemoryAmount(MemoryUnit.KB, BigInteger.TWO)
        val result = memory1 + memory2
        assertEquals(3.toBigInteger(), result.amount)
    }

}