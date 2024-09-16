/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 * Copyright (c) 2024-present "Nik De Kur"
 */

package dev.nikdekur.ndkore.placeholder

import dev.nikdekur.ndkore.placeholder.PatternPlaceholderParserTest.Member
import dev.nikdekur.ndkore.placeholder.PatternPlaceholderParserTest.User

class ReflectPatternPlaceholderParserTest : PatternPlaceholderParserTest {

    val source = ReflectValuesSource

    override fun getParser(
        symbolR: String,
        symbolL: String
    ): PatternPlaceholderParser {
        return PatternPlaceholderParser(symbolR, symbolL, source)
    }

    data class UserData(override val name: String, override val age: Int) : User

    override fun getUser(name: String, age: Int) = UserData(name, age)

    data class MemberData(override val user: User) : Member

    override fun getMember(user: User) = MemberData(user)
}