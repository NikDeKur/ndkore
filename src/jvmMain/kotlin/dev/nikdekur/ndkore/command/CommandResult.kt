/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 * Copyright (c) 2024-present "Nik De Kur"
 */

package dev.nikdekur.ndkore.command

data class CommandResult(
    val commands: List<String>,
    val output: List<String>,
    val errorOutput: List<String>,
    val exitCode: Int,
)