/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 * Copyright (c) 2024-present "Nik De Kur"
 */

@file:OptIn(ExperimentalSerializationApi::class)

package dev.nikdekur.ndkore.placeholder

import dev.nikdekur.ndkore.annotation.ExperimentalAPI
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.properties.Properties
import kotlinx.serialization.serializer

@ExperimentalAPI
class KotlinxValuesSource(
    val properties: Properties
) : ValuesSource {

    override fun findValue(obj: Any, path: String): Any? {
        println("Looking for `$path` in `$obj`")
        val serializer = properties.serializersModule.serializer(obj::class, emptyList(), false)
        val map = properties.encodeToMap(serializer, obj)
        return map[path]
    }
}