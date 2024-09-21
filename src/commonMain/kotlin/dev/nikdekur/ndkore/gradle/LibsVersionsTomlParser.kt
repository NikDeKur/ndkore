/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 * Copyright (c) 2024-present "Nik De Kur"
 */

package dev.nikdekur.ndkore.gradle

import dev.nikdekur.ndkore.ext.isBlankOrEmpty
import kotlinx.serialization.json.Json

class LibsVersionsTomlParser(val json: Json, val text: String) {

    val versions = HashMap<String, String>()
    val libraries = mutableListOf<Library>()

    fun parse() {
        var part = Part.NONE

        val lines = text.lineSequence()

        for (line in lines) {
            // Skip empty lines
            if (line.isBlankOrEmpty()) continue

            // Skip comments
            if (line.startsWith("#")) continue

            when {
                line.startsWith("[versions]") -> {
                    part = Part.VERSIONS
                    continue
                }

                line.startsWith("[libraries]") -> {
                    part = Part.LIBRARIES
                    continue
                }

                line.startsWith("[bundles]") -> {
                    part = Part.BUNDLES
                    continue
                }

                line.startsWith("[plugins]") -> {
                    part = Part.PLUGINS
                    continue
                }
            }

            when (part) {
                Part.VERSIONS -> parseVersion(line)
                Part.LIBRARIES -> parseDependency(line)
                Part.BUNDLES -> { /* Ignore */
                }

                Part.PLUGINS -> { /* Ignore */
                }

                Part.NONE -> throw IllegalStateException("Invalid TOML file")
            }
        }
    }


    fun parseVersion(string: String) {
        val data = string
            .replace(" ", "")
            .replace("'", "")
            .replace("\"", "")
            .split("=")

        check(data.size == 2) { "Invalid version string: $string" }

        val (key, value) = data
        versions[key] = value
    }

    fun parseDependency(string: String) {
        val stringData = string
            .replace(" ", "")
            .split("=", limit = 2)

        check(stringData.size == 2) { "Invalid dependency string: $string" }

        val (key, rawJson) = stringData

        val json = rawJson
            .replace(" ", "")
            .replace("=", ":")

        val dependencyMap = this.json.decodeFromString<Map<String, String>>(json)
        val module = dependencyMap["module"] ?: throw IllegalStateException("Dependency module not found")
        val groupAndArtifact = module.split(":")

        check(groupAndArtifact.size == 2) { "Invalid module string: $module" }

        val (group, artifactId) = groupAndArtifact

        val version = dependencyMap["version.ref"]?.let {
            versions[it] ?: throw IllegalStateException("Version not found for reference: $it")
        } ?: run {
            val version = dependencyMap["version"] ?: throw IllegalStateException("Dependency version not found")
            versions[key] = version
            version
        }

        val dependency = Library(group, artifactId, version)
        libraries.add(dependency)
    }


    enum class Part {
        VERSIONS,
        LIBRARIES,
        BUNDLES,
        PLUGINS,
        NONE
    }
}
