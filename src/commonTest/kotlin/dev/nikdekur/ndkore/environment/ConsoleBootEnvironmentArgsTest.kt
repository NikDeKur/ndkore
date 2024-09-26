/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 * Copyright (c) 2024-present "Nik De Kur"
 */

package dev.nikdekur.ndkore.environment

import dev.nikdekur.ndkore.environment.ConsoleEnvironment.Companion.fromCommandLineArgs
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNull


class ConsoleEnvironmentArgsTest {

    @Test
    fun testParsingSingleArgs() {
        val args = arrayOf("key1=value1")
        val environment = fromCommandLineArgs(args)
        assertEquals("value1", environment.getValue("key1"))
    }

    @Test
    fun testParsingMultipleArgs() {
        val args = arrayOf("key1=value", "key2=value2")
        val environment = fromCommandLineArgs(args)
        assertEquals("value", environment.getValue("key1"))
        assertEquals("value2", environment.getValue("key2"))
    }

    @Test
    fun testParsingEmptyArgs() {
        val environment = fromCommandLineArgs(emptyArray())
        assertNull(environment.getValue("key1"))
    }

    @Test
    fun testReturningNullOnMissingArg() {
        val args = arrayOf("key1=value1")
        val environment = fromCommandLineArgs(args)
        assertNull(environment.getValue("key2"))
    }

    @Test
    fun testThrowingExceptionOnInvalidArgs() {
        val args = arrayOf("key1")
        assertFailsWith<IllegalArgumentException> {
            fromCommandLineArgs(args)
        }
    }
}