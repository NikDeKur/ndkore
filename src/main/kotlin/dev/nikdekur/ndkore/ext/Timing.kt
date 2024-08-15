/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 * Copyright (c) 2024-present "Nik De Kur"
 */

package dev.nikdekur.ndkore.ext


/**
 * Measures the average time taken by a given operation over a specified number of iterations.
 *
 * @param iterations The number of times the operation should be executed.
 * @param operation The operation to measure the execution time for, accepting the current iteration index as a parameter.
 * @return The average time taken by the operation in nanoseconds.
 */
inline fun measureAverageTime(iterations: Int, operation: (Int) -> Unit): Double {
    var totalTime = 0L
    var index = 0
    while (index < iterations) {
        val startTime = System.nanoTime()
        operation(index)
        val endTime = System.nanoTime()
        totalTime += endTime - startTime
        index++
    }
    return totalTime.toDouble() / iterations
}

/**
 * Measures and prints the average execution time of a given operation over a specified number of iterations.
 *
 * @param iterations The number of times the operation should be executed.
 * @param text The label to display before the measured time.
 * @param operation The operation to measure, accepting the current iteration index as a parameter.
 */
inline fun printAverageExecTime(iterations: Int, text: String, operation: (Int) -> Unit) {
    val time = measureAverageTime(iterations, operation)
    val elapsedTime = time / 1_000_000
    println("$text: ${elapsedTime.format(6)} ms")
}

/**
 * Measures and prints the average execution time of a given operation over a specified number of iterations with a default label.
 *
 * @param iterations The number of times the operation should be executed.
 * @param operation The operation to measure, accepting the current iteration index as a parameter.
 */
inline fun printAverageExecTime(iterations: Int, operation: (Int) -> Unit) =
    printAverageExecTime(iterations, "Execution time", operation)

/**
 * Measures and prints the execution time of a given operation.
 *
 * @param T The return type of the operation.
 * @param text The label to display before the measured time.
 * @param operation The operation to measure the execution time for.
 * @return The result of the operation.
 */
inline fun <T> printExecTime(text: String, operation: () -> T): T {
    val startTime = System.nanoTime()
    val r = operation()
    val endTime = System.nanoTime()
    val elapsedTime = (endTime - startTime).toDouble() / 1_000_000
    println("$text: ${elapsedTime.format(6)} ms")
    return r
}

/**
 * Measures and prints the execution time of a given operation with a default label.
 *
 * @param T The return type of the operation.
 * @param operation The operation to measure the execution time for.
 * @return The result of the operation.
 */
inline fun <T> printExecTime(operation: () -> T) = printExecTime("Execution time", operation)

/**
 * Formats a given double value to a string with a specified number of decimal places.
 *
 * @param decimalPlaces The number of decimal places to display.
 * @return The formatted string.
 */
fun Double.format(decimalPlaces: Int): String = "%.${decimalPlaces}f".format(this)
