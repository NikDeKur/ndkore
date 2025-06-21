/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 * Copyright (c) 2024-present "Nik De Kur"
 */

package dev.nikdekur.ndkore.memory

import com.ionspin.kotlin.bignum.integer.toBigInteger
import dev.nikdekur.ndkore.ext.LenientMemoryAmountSerializer
import dev.nikdekur.ndkore.ext.LenientMemoryUnitSerializer
import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertEquals

class MemoryTest {

    @Test
    fun testCreatingMemoryAmount() {
        val int = "1024".toBigInteger()
        val memory = MemoryAmount(int)
        assertEquals(int, memory.bytes)
    }

    @Test
    fun testAddingMemoryAmount() {
        val int1 = "1024".toBigInteger()
        val int2 = "2048".toBigInteger()
        val memory1 = MemoryAmount(int1)
        val memory2 = MemoryAmount(int2)
        val result = memory1 + memory2
        assertEquals(int1 + int2, result.bytes)
    }

    @Test
    fun testSubtractingMemoryAmount() {
        val int1 = "2048".toBigInteger()
        val int2 = "1024".toBigInteger()
        val memory1 = MemoryAmount(int1)
        val memory2 = MemoryAmount(int2)
        val result = memory1 - memory2
        assertEquals(int1 - int2, result.bytes)
    }

    @Test
    fun testCreatingMemoryAmountWithUnitKiB() {
        val int = "1024".toBigInteger()
        val memory = MemoryAmount(int, MemoryUnit.KiB)
        assertEquals(int * int, memory.bytes)
    }

    @Test
    fun testCreatingMemoryAmountWithUnitMiB() {
        val int = "1024".toBigInteger()
        val memory = MemoryAmount(int, MemoryUnit.MiB)
        assertEquals(int * int * int, memory.bytes)
    }


    @Test
    fun testConvertingMemoryAmountToUnitKiB() {
        val memory = 1024.bytes
        val result = memory.toBigInteger(MemoryUnit.KiB)
        assertEquals("1".toBigInteger(), result)
    }

    @Test
    fun testConvertingMemoryAmountToUnitMiB() {
        val memory = 2048.kibiBytes
        val result = memory.toBigInteger(MemoryUnit.MiB)
        assertEquals("2".toBigInteger(), result)
    }


    @Test
    fun testDefaultSerializationOfMemoryUnit() {
        val json = Json
        val memoryUnit = MemoryUnit.KiB

        val serialized = json.encodeToString(MemoryUnit.serializer(), memoryUnit)
        assertEquals("\"1024\"", serialized)

        val deserialized = json.decodeFromString(MemoryUnit.serializer(), serialized)
        assertEquals(memoryUnit, deserialized)
    }

    @Test
    fun testLenientSerializationOfMemoryUnit() {
        val json = Json
        val memoryUnit = MemoryUnit.KiB

        val serialized = json.encodeToString(LenientMemoryUnitSerializer, memoryUnit)
        assertEquals("\"KiB\"", serialized)

        val deserialized = json.decodeFromString(LenientMemoryUnitSerializer, serialized)
        assertEquals(memoryUnit, deserialized)
    }


    @Test
    fun testDefaultSerializationOfMemoryAmount() {
        val json = Json
        val memoryAmount = 1024.bytes

        val serialized = json.encodeToString(MemoryAmount.serializer(), memoryAmount)
        assertEquals("\"1024\"", serialized)

        val deserialized = json.decodeFromString(MemoryAmount.serializer(), serialized)
        assertEquals(memoryAmount, deserialized)
    }


    @Test
    fun testLenientSerializationOfMemoryAmount() {
        val json = Json
        val memoryAmount = 1023.bytes

        val serialized = json.encodeToString(LenientMemoryAmountSerializer, memoryAmount)
        assertEquals("\"1023B\"", serialized)

        val deserialized = json.decodeFromString(LenientMemoryAmountSerializer, serialized)
        assertEquals(memoryAmount, deserialized)
    }

    @Test
    fun testHugeLenientSerializationOfMemoryAmount() {
        val json = Json
        val memoryAmount = 1024.tebiBytes

        val serialized = json.encodeToString(LenientMemoryAmountSerializer, memoryAmount)

        // 1024 tebibytes = 1 pebibyte, so the serialized string should simplify to "1 PiB"
        assertEquals("\"1PiB\"", serialized)

        val deserialized = json.decodeFromString(LenientMemoryAmountSerializer, serialized)
        assertEquals(memoryAmount, deserialized)
    }

}