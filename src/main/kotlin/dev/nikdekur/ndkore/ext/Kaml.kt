/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 * Copyright (c) 2024-present "Nik De Kur"
 */

package dev.nikdekur.ndkore.ext

import com.charleskorn.kaml.Yaml
import kotlinx.serialization.decodeFromString
import org.intellij.lang.annotations.Language
import java.io.File


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
inline fun <reified T> Yaml.loadConfig(@Language("yaml") text: String): T = decodeFromString(text)

/**
 * Loads a configuration of the specified type from a YAML file.
 *
 * This function reads the contents of the given file, which should be a valid YAML document, and then decodes it
 * into an instance of the specified type using Kotlin serialization. This method is useful for loading configurations
 * from external files, allowing configurations to be managed and modified outside the application.
 *
 * @param T the type of the configuration object.
 * @param file the file containing the YAML formatted configuration.
 * @return the configuration object decoded from the YAML file's contents.
 */
inline fun <reified T> Yaml.loadConfig(file: File): T = loadConfig(file.readText())