/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 * Copyright (c) 2024-present "Nik De Kur"
 */

package dev.nikdekur.ndkore.ext

import com.charleskorn.kaml.Yaml
import kotlinx.serialization.KSerializer
import kotlinx.serialization.serializer
import kotlin.reflect.KType
import kotlin.reflect.typeOf

/**
 * Loads a configuration of the specified type from a YAML formatted string.
 *
 * This function uses Kotlin serialization to decode the given YAML string into an instance of the specified type.
 * The string should be a valid YAML document. This method is useful for loading configurations directly from
 * string literals, for instance, when configurations are provided dynamically or in tests.
 *
 * @param T the type of the configuration object.
 * @param text the YAML formatted string containing the configuration.
 * @param clazz the class of the configuration object.
 * @return the configuration object decoded from the YAML string.
 */
inline fun <T : Any> Yaml.loadConfig(text: String, type: KType): T {
    @Suppress("UNCHECKED_CAST")
    val serializer = serializersModule.serializer(type) as KSerializer<T>
    return decodeFromString(serializer, text)
}


/**
 * Loads a configuration of the specified type from a YAML formatted string.
 *
 * This function uses Kotlin serialization to decode the given YAML string into an instance of the specified type.
 * The string should be a valid YAML document. This method is useful for loading configurations directly from
 * string literals, for instance, when configurations are provided dynamically or in tests.
 *
 * @param T the type of the configuration object.
 * @param text the YAML formatted string containing the configuration.
 * @return the configuration object decoded from the YAML string.
 */
inline fun <reified T : Any> Yaml.loadConfig(text: String): T = loadConfig(text, typeOf<T>())