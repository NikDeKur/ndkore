/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 * Copyright (c) 2024-present "Nik De Kur"
 */

package dev.nikdekur.ndkore.ext

import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds
import kotlin.time.TimeSource


/**
 * Measures the average time taken by a given operation over a specified number of iterations.
 *
 * @param iterations The number of times the operation should be executed.
 * @param operation The operation to measure the execution time for, accepting the current iteration index as a parameter.
 * @return The average time taken by the operation in nanoseconds.
 */
public inline fun TimeSource.measureAverageTime(iterations: Int, operation: (Int) -> Unit): Duration {
    var totalTime = 0.seconds
    var index = 0
    while (index < iterations) {
        val startTime = markNow()
        operation(index)
        totalTime += startTime.elapsedNow()
        index++
    }
    return totalTime / iterations
}

/**
 * Measures and prints the average execution time of a given operation over a specified number of iterations.
 *
 * @param iterations The number of times the operation should be executed.
 * @param text The label to display before the measured time.
 * @param operation The operation to measure, accepting the current iteration index as a parameter.
 */
public inline fun TimeSource.printAverageExecTime(iterations: Int, text: String, operation: (Int) -> Unit) {
    val time = measureAverageTime(iterations, operation)
    val nanos = time.inWholeNanoseconds / 1_000_000.0
    println("$text: ${nanos.format(6)} ms")
}

/**
 * Measures and prints the average execution time of a given operation over a specified number of iterations with a default label.
 *
 * @param iterations The number of times the operation should be executed.
 * @param operation The operation to measure, accepting the current iteration index as a parameter.
 */
public inline fun TimeSource.printAverageExecTime(iterations: Int, operation: (Int) -> Unit) =
    printAverageExecTime(iterations, "Execution time", operation)

/**
 * Measures and prints the execution time of a given operation.
 *
 * @param T The return type of the operation.
 * @param text The label to display before the measured time.
 * @param operation The operation to measure the execution time for.
 * @return The result of the operation.
 */
public inline fun <T> TimeSource.printExecTime(text: String, operation: () -> T): T {
    val startTime = markNow()
    val r = operation()
    val elapsedTime = startTime.elapsedNow()
    val nanos = elapsedTime.inWholeNanoseconds / 1_000_000.0
    println("$text: ${nanos.format(6)} ms")
    return r
}

/**
 * Measures and prints the execution time of a given operation with a default label.
 *
 * @param T The return type of the operation.
 * @param operation The operation to measure the execution time for.
 * @return The result of the operation.
 */
public inline fun <T> TimeSource.printExecTime(operation: () -> T) = printExecTime("Execution time", operation)
