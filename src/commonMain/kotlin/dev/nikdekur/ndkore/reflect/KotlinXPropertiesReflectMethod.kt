/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 * Copyright (c) 2024-present "Nik De Kur"
 */

@file:OptIn(ExperimentalSerializationApi::class)

package dev.nikdekur.ndkore.reflect

import dev.nikdekur.ndkore.annotation.ExperimentalAPI
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.properties.Properties
import kotlinx.serialization.serializer

@ExperimentalAPI
public class KotlinXPropertiesReflectMethod(
    public val properties: Properties
) : ReflectMethod {

    override fun findValue(obj: Any, name: String): Any? {
        println("Looking for `$name` in `$obj`")
        val serializer = properties.serializersModule.serializer(obj::class, emptyList(), false)
        val map = properties.encodeToMap(serializer, obj)
        return map[name]
    }
}