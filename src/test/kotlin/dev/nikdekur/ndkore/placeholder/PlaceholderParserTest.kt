/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 * Copyright (c) 2024-present "Nik De Kur"
 */

package dev.nikdekur.ndkore.placeholder

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class PlaceholderParserTest {

    @Test
    fun singlePlaceholderResolvedCorrectly() {
        val parser = PatternPlaceholderParser.HASH
        val result = parser.parse("#name#", mapOf("name" to "John Doe"))
        assertEquals("John Doe", result)
    }

    @Test
    fun singleCasePlaceholderResolvedCorrectly() {
        val parser = PatternPlaceholderParser.CURLY_BRACKET
        val result = parser.parse("name is {userName}", mapOf("userName" to "John Doe"))
        assertEquals("name is John Doe", result)
    }

    @Test
    fun multiplePlaceholdersResolvedCorrectly() {
        val parser = PatternPlaceholderParser.PROCENT
        val result =
            parser.parse("Hello, %name%! Your balance is %balance%.", mapOf("name" to "Jane Doe", "balance" to "100"))
        assertEquals("Hello, Jane Doe! Your balance is 100.", result)
    }

    @Test
    fun nestedPlaceholdersResolvedCorrectly() {
        val parser = PatternPlaceholderParser.CURLY_BRACKET
        val result = parser.parse(
            "{user.name} is {user.age} years old", mapOf(
                "user" to setOf(
                    Placeholder.of("name" to "Alice"),
                    Placeholder.of("age" to 30)
                )
            )
        )
        assertEquals("Alice is 30 years old", result)
    }

    @Test
    fun placeholderWithIterableResolvedToFirstElement() {
        val parser = PatternPlaceholderParser.PROCENT
        val result = parser.parse("%users%", mapOf("users" to listOf("Bob", "Charlie")))
        assertEquals("Bob", result)
    }

    @Test
    fun placeholderWithMapResolvedCorrectly() {
        val parser = PatternPlaceholderParser.PROCENT
        val result = parser.parse("%user.name%", mapOf("user" to Placeholder.of("name" to "Diana")))
        assertEquals("Diana", result)
    }

    @Test
    fun parseWithVarargsPlaceholdersResolvedCorrectly() {
        val parser = PatternPlaceholderParser.HASH
        val result = parser.parse("#name# and #age#", "name" to "Frank", "age" to "28")
        assertEquals("Frank and 28", result)
    }

    @Test
    fun parseFromClass() {

        data class Member(val name: String, val age: Int)

        val member = Member("John", 20)

        val parser = PatternPlaceholderParser.CURLY_BRACKET
        val result = parser.parse(
            "{val} {member.name} - {member.age}", mapOf(
                "val" to "value",
                "member" to member
            )
        )
        assertEquals("value John - 20", result)
    }

    @Test
    fun parseFromNestedClass() {

        data class Member(val name: String, val age: Int)
        data class User(val member: Member)

        val user = User(Member("John", 20))

        val parser = PatternPlaceholderParser.CURLY_BRACKET
        val result = parser.parse(
            "{val} {user.member.name} - {user.member.age}", mapOf(
                "val" to "value",
                "user" to user
            )
        )
        assertEquals("value John - 20", result)
    }

    @Test
    fun parseFromNestedClassAndExtraClassOption() {

        data class Member(val name: String, val age: Int)
        data class User(val member: Member)

        val user = User(Member("John", 20))

        val userPlaceholders = listOf(
            Placeholder.ofSingle("icon", "https://example.com/icon.png"),
            user
        )

        val parser = PatternPlaceholderParser.CURLY_BRACKET
        val result = parser.parse(
            "{val} {user.icon} {user.member.name} - {user.member.age}", mapOf(
                "val" to "value",
                "user" to userPlaceholders,
            )
        )

        assertEquals("value https://example.com/icon.png John - 20", result)
    }

    // TODO: Big letter problem
}