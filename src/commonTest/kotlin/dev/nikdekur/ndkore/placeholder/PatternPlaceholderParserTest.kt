/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 * Copyright (c) 2024-present "Nik De Kur"
 */

package dev.nikdekur.ndkore.placeholder

import kotlin.test.Test
import kotlin.test.assertEquals


fun PatternPlaceholderParserTest.getParser(symbol: String) = getParser(symbol, symbol)

abstract class PatternPlaceholderParserTest {

    abstract fun getParser(symbolR: String, symbolL: String): PatternPlaceholderParser

    @Test
    fun singlePlaceholderResolvedCorrectly() {
        val parser = getParser("#")
        val result = parser.parse("#name#", mapOf("name" to "John Doe"))
        assertEquals("John Doe", result)
    }

    @Test
    fun singleCasePlaceholderResolvedCorrectly() {
        val parser = getParser("\\{", "\\}")
        val result = parser.parse("name is {userName}", mapOf("userName" to "John Doe"))
        assertEquals("name is John Doe", result)
    }

    @Test
    fun multiplePlaceholdersResolvedCorrectly() {
        val parser = getParser("%")
        val result =
            parser.parse("Hello, %name%! Your balance is %balance%.", mapOf("name" to "Jane Doe", "balance" to "100"))
        assertEquals("Hello, Jane Doe! Your balance is 100.", result)
    }

    @Test
    fun nestedPlaceholdersResolvedCorrectly() {
        val parser = getParser("\\{", "\\}")
        val result = parser.parse(
            "{user.name} is {user.age} years old", mapOf(
                "user" to mapOf(
                    "name" to "Alice",
                    "age" to 30
                )
            )
        )
        assertEquals("Alice is 30 years old", result)
    }

    @Test
    fun placeholderWithIterableResolvedToFirstElement() {
        val parser = getParser("%")
        val result = parser.parse("%users%", mapOf("users" to listOf("Bob", "Charlie")))
        assertEquals("Bob", result)
    }

    @Test
    fun placeholderWithMapResolvedCorrectly() {
        val parser = getParser("%")
        val result = parser.parse("%user.name%", mapOf("user" to mapOf("name" to "Diana")))
        assertEquals("Diana", result)
    }

    @Test
    fun parseWithVarargsPlaceholdersResolvedCorrectly() {
        val parser = getParser("#")
        val result = parser.parse("#name# and #age#", "name" to "Frank", "age" to "28")
        assertEquals("Frank and 28", result)
    }


    interface User {
        val name: String
        val age: Int
    }

    abstract fun getUser(name: String, age: Int): User

    @Test
    fun parseFromClass() {


        val member = getUser("John", 20)

        val parser = getParser("\\{", "\\}")
        val result = parser.parse(
            "{val} {member.name} - {member.age}", mapOf(
                "val" to "value",
                "member" to member
            )
        )
        assertEquals("value John - 20", result)
    }

    interface Member {
        val user: User
    }

    abstract fun getMember(user: User): Member

    @Test
    fun parseFromNestedClass() {

        val user = getMember(getUser("John", 20))

        val parser = getParser("\\{", "\\}")
        val result = parser.parse(
            "{val} {member.user.name} - {member.user.age}", mapOf(
                "val" to "value",
                "member" to user
            )
        )
        assertEquals("value John - 20", result)
    }

    @Test
    fun parseFromNestedClassAndExtraClassOption() {


        val member = getMember(getUser("John", 20))

        val memberPlaceholders = listOf(
            mapOf("icon" to "https://example.com/icon.png"),
            member
        )

        val parser = getParser("\\{", "\\}")
        val result = parser.parse(
            "{val} {member.icon} {member.user.name} - {member.user.age}", mapOf(
                "val" to "value",
                "member" to memberPlaceholders,
            )
        )

        assertEquals("value https://example.com/icon.png John - 20", result)
    }


    @Test
    fun parseFromNonExistingPlaceholder() {
        val parser = getParser("#")
        val result = parser.parse("Her name is #name#", mapOf("age" to 30))
        assertEquals("Her name is #name#", result)
    }


    @Test
    fun parseFromList() {
        val parser = getParser("#")
        val result = parser.parse("#names.1#", mapOf("names" to listOf("Bob", "Charlie")))
        assertEquals("Charlie", result)
    }
}