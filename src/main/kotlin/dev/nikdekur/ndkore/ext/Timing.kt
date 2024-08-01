/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 * Copyright (c) 2024-present "Nik De Kur"
 */

package dev.nikdekur.ndkore.ext

inline fun measureAverageTime(iterations: Int, operation: (Int) -> Unit): Double {
    var totalTime = 0L
    var indx = 0
    while (indx < iterations) {
        val startTime = System.nanoTime()
        operation(indx)
        val endTime = System.nanoTime()
        totalTime += endTime - startTime
        indx++
    }

    return totalTime.toDouble() / iterations
}

inline fun printAverageExecTime(iterations: Int, text: String, operation: (Int) -> Unit) {
    val time = measureAverageTime(iterations, operation)
    val elapsedTime = time / 1_000_000
    println("$text: ${elapsedTime.format(6)} ms")
}
inline fun printAverageExecTime(iterations: Int, operation: (Int) -> Unit) = printAverageExecTime(iterations, "Execution time", operation)


inline fun <T> printExecTime(text: String, operation: () -> T): T {
    val startTime = System.nanoTime()
    val r = operation()
    val endTime = System.nanoTime()
    val elapsedTime = (endTime - startTime).toDouble() / 1_000_000
    println("$text: ${elapsedTime.format(6)} ms")
    return r
}

inline fun <T> printExecTime(operation: () -> T) = printExecTime("Execution time", operation)