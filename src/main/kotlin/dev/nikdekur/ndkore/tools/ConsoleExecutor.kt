package dev.nikdekur.ndkore.tools

import dev.nikdekur.ndkore.ext.*
import java.util.concurrent.CompletableFuture

object ConsoleExecutor {

    @JvmStatic
    fun run(command: List<String>): CompletableFuture<CommandResult> {
        val future = CompletableFuture<CommandResult>()

        Thread {
            val processBuilder = ProcessBuilder(command)
            val process = processBuilder.start()

            val output: MutableList<String> = mutableListOf()
            process.inputStream.bufferedReader().useLines { lines ->
                lines.forEach { line -> output.add(line) }
            }

            val exitCode = process.waitFor()
            future.complete(CommandResult(command, output, exitCode))
        }.start()

        return future
    }

    @JvmStatic
    val PATTERN_WORD_SPLIT: Regex = Regex("\\s+")

    @JvmStatic
    fun run(command: String): CompletableFuture<CommandResult> {
        val commandsList = command.split(PATTERN_WORD_SPLIT)
        return run(commandsList)
    }

    @JvmStatic
    fun runGroup(commands: List<String>): CompletableFuture<List<CommandResult>> {
        val firstCommand = run(commands.first())
        return commands.drop(1).fold(listOf(firstCommand)) { acc, cmd ->
            acc + acc.last().thenCompose { run(cmd) }
        }.let { allOfList ->
            CompletableFuture.allOf(*allOfList.toTArray())
                .thenApply { allOfList.map { it.join() } }
        }
    }
}


data class CommandResult(val commands: List<String>,
                         val consoleOutput: List<String>,
                         val exitCode: Int,
                         val command: String = commands.joinToString(" "))