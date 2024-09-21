/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 * Copyright (c) 2024-present "Nik De Kur"
 */

@file:Suppress("NOTHING_TO_INLINE")
@file:OptIn(ExperimentalContracts::class)

package dev.nikdekur.ndkore.ext

import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.contract

/**
 * Prints the stack trace of this [Throwable] and rethrows it.
 *
 * This function can be useful for debugging purposes
 * where you want to log the stack trace before rethrowing an exception.
 *
 * @throws Throwable Always rethrows the original [Throwable] after printing its stack trace.
 */
inline fun Throwable.printStackAndThrow(): Nothing {
    contract {
        returns() implies false
    }

    printStackTrace()
    throw this
}

/**
 * Executes a collection of blocks, capturing any exceptions thrown.
 *
 * This function iterates over the given [blocks] and executes each one.
 * If an exception is thrown,
 * it is either stored as the initial exception or added as a suppressed exception to the initial one.
 *
 * @param blocks A collection of functions to be executed.
 * @return The first exception that was thrown, with any subsequent exceptions added as suppressed exceptions.
 * Returns `null` if no exceptions were thrown.
 */
inline fun tryEverything(blocks: Iterable<() -> Unit>): Exception? {
    var suppress: Exception? = null
    blocks.forEach {
        try {
            it()
        } catch (e: Exception) {
            if (suppress == null) {
                suppress = e
            } else {
                suppress.addSuppressed(e)
            }
        }
    }
    return suppress
}

/**
 * Executes a vararg array of blocks, capturing any exceptions thrown.
 *
 * This function converts the vararg [blocks] to a list and delegates to [tryEverything] with the list of blocks.
 *
 * @param blocks A vararg array of functions to be executed.
 * @return The first exception that was thrown, with any subsequent exceptions added as suppressed exceptions.
 * Returns `null` if no exceptions were thrown.
 */
inline fun tryEverything(vararg blocks: () -> Unit) = tryEverything(blocks.toList())

/**
 * Safely iterates over an iterable, catching exceptions for each element.
 *
 * This function iterates over the elements of the iterable, executing the [block] for each element.
 * If an exception is thrown during the execution of the block,
 * the [onException] handler is called with the element and the exception.
 *
 * @param T The type of elements in the iterable.
 * @param onException A function that handles exceptions thrown during the execution of the block.
 * It receives the element and the exception as parameters.
 * @param block The function to be executed for each element.
 */
inline fun <T> Iterable<T>.forEachSafe(onException: (Exception, T) -> Unit = { _, _ -> }, block: (T) -> Unit) {
    forEach {
        try {
            block(it)
        } catch (e: Exception) {
            onException(e, it)
        }
    }
}

/**
 * Safely iterates over an iterable, catching exceptions for each element.
 *
 * This function iterates over the elements of the iterable, executing the [block] for each element.
 * If an exception is thrown during the execution of the block,
 * the [onException] handler is called with the element and the exception.
 *
 * @param T The type of elements in the iterable.
 * @param onException A function that handles exceptions thrown during the execution of the block.
 * It receives the element and the exception as parameters.
 * @param block The function to be executed for each element.
 */
inline fun <T> Iterable<T>.forEachSafe(onException: (Exception) -> Unit = {}, block: (T) -> Unit) {
    forEach {
        try {
            block(it)
        } catch (e: Exception) {
            onException(e)
        }
    }
}

/**
 * Safely iterates over an array, catching exceptions for each element.
 *
 * This function iterates over the elements of the array, executing the [block] for each element.
 * If an exception is thrown during the execution of the block,
 * the [onException] handler is called with the element and the exception.
 *
 * @param T The type of elements in the array.
 * @param onException A function that handles exceptions thrown during the execution of the block.
 * It receives the element and the exception as parameters.
 * @param block The function to be executed for each element.
 */
inline fun <T> Array<T>.forEachSafe(onException: (Exception, T) -> Unit = { _, _ -> }, block: (T) -> Unit) {
    forEach {
        try {
            block(it)
        } catch (e: Exception) {
            onException(e, it)
        }
    }
}

/**
 * Safely iterates over an array, catching exceptions for each element.
 *
 * This function iterates over the elements of the array, executing the [block] for each element.
 * If an exception is thrown during the execution of the block,
 * the [onException] handler is called with the element and the exception.
 *
 * @param T The type of elements in the array.
 * @param onException A function that handles exceptions thrown during the execution of the block.
 * It receives the element and the exception as parameters.
 * @param block The function to be executed for each element.
 */
inline fun <T> Array<T>.forEachSafe(onException: (Exception) -> Unit = {}, block: (T) -> Unit) {
    forEach {
        try {
            block(it)
        } catch (e: Exception) {
            onException(e)
        }
    }
}


/**
 * Safely iterates over a map, catching exceptions for each entry.
 *
 * This function iterates over the entries of the map, executing the [block] for each entry.
 * If an exception is thrown during the execution of the block,
 * the [onException] handler is called with the entry and the exception.
 *
 * @param K The type of keys in the map.
 * @param V The type of values in the map.
 * @param onException A function that handles exceptions thrown during the execution of the block.
 * It receives the entry and the exception as parameters.
 * @param block The function to be executed for each entry.
 */
inline fun <K, V> Map<K, V>.forEachSafe(
    onException: (Exception, Map.Entry<K, V>) -> Unit = { _, _ -> },
    block: (Map.Entry<K, V>) -> Unit,
) {
    forEach {
        try {
            block(it)
        } catch (e: Exception) {
            onException(e, it)
        }
    }
}


