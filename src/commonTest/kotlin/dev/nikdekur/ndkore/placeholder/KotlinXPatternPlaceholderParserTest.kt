/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 * Copyright (c) 2024-present "Nik De Kur"
 */

@file:OptIn(ExperimentalSerializationApi::class)

package dev.nikdekur.ndkore.placeholder

import kotlinx.serialization.ExperimentalSerializationApi

//class KotlinXPatternPlaceholderParserTest : PatternPlaceholderParserTest {
//
//    val source = KotlinxValuesSource(Properties)
//
//    override fun getParser(symbolR: String, symbolL: String): PatternPlaceholderParser {
//        // "hrsPassword1"
//        return PatternPlaceholderParser(symbolR, symbolL, source)
//    }
//
//
//    @Serializable
//    data class UserData(override val name: String, override val age: Int) : User
//    override fun getUser(name: String, age: Int) = UserData(name, age)
//
//    @Serializable
//    data class MemberData(override val user: UserData) : Member
//    override fun getMember(user: User) = MemberData(user as UserData)
//}