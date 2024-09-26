/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 * Copyright (c) 2024-present "Nik De Kur"
 */

package dev.nikdekur.ndkore.command

import dev.nikdekur.ndkore.ext.*
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

public class ConsoleExecutor(
    public val dispatcher: CoroutineDispatcher
) {

    public suspend fun run(command: List<String>): CommandResult {
        return withContext(dispatcher) {
            val process = ProcessBuilder(command)
                .redirectOutput(ProcessBuilder.Redirect.PIPE)
                .redirectError(ProcessBuilder.Redirect.PIPE)
                .start()

            val output = process.inputStream.bufferedReader().readLines()
            val error = process.errorStream.bufferedReader().readLines()

            val exitCode = process.waitFor()

            CommandResult(command, output, error, exitCode)
        }
    }


    public suspend fun run(command: String): CommandResult {
        val commandsList = command.split(Patterns.WORD_SPLIT)
        return run(commandsList)
    }
}


