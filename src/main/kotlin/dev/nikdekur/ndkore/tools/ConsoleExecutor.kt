package dev.nikdekur.ndkore.tools

import dev.nikdekur.ndkore.ext.*
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

class ConsoleExecutor(val dispatcher: CoroutineDispatcher) {

    suspend fun run(command: List<String>): CommandResult {
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


    suspend fun run(command: String): CommandResult {
        val commandsList = command.split(Patterns.WORD_SPLIT)
        return run(commandsList)
    }
}


data class CommandResult(
    val commands: List<String>,
    val output: List<String>,
    val errorOutput: List<String>,
    val exitCode: Int,
)