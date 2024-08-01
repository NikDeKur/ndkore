/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 * Copyright (c) 2024-present "Nik De Kur"
 */

package dev.nikdekur.ndkore.gradle

data class Library(
    val group: String,
    val artifactId: String,
    val version: String,
) {

    override fun toString(): String {
        return "$group:$artifactId:$version"
    }
}
