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


class MapNestedTest {

    @Test
    fun getNestedSingleTest() {
        val map = mapOf("key" to "value")
        val value = map.getNested(listOf("key"))
        assertEquals("value", value)
    }

    @Test
    fun getNestedMultiTest() {
        val map = mapOf("key1" to mapOf("key2" to "value"))
        val value = map.getNested(listOf("key1", "key2"))
        assertEquals("value", value)
    }

    @Test
    fun getNestedNullTest() {
        val map = mapOf("key1" to mapOf("key2" to "value"))
        val value = map.getNested(listOf("key1", "key3"))
        assertEquals(null, value)
    }


    @Test
    fun setNestedSingleTest() {
        val map = mutableMapOf<String, Any?>()
        map.setNested(listOf("key"), "value")
        assertEquals<Any>(mapOf("key" to "value"), map)
    }

    @Test
    fun setNestedMultiTest() {
        val map = mutableMapOf<String, Any?>()
        map.setNested(listOf("key1", "key2"), "value")
        assertEquals<Any>(mapOf("key1" to mapOf("key2" to "value")), map)
    }
}