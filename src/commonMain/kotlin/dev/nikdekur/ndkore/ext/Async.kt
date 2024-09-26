/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 * Copyright (c) 2024-present "Nik De Kur"
 */

@file:OptIn(ExperimentalCoroutinesApi::class)

package dev.nikdekur.ndkore.ext

import kotlinx.coroutines.*
import kotlinx.coroutines.selects.SelectBuilder
import kotlinx.coroutines.selects.select
import kotlinx.coroutines.sync.Semaphore
import kotlinx.coroutines.sync.withPermit

/**
 * Awaits for the completion of this [Deferred] without throwing [CancellationException] when completed.
 *
 * If the deferred is completed normally, this function returns the result.
 *
 * If the deferred is cancelled, this function would rethrow a [CancellationException].
 *
 * @param T the type of the deferred value
 * @return the result value of the deferred
 */
public suspend inline fun <T> Deferred<T>.smartAwait(): T {
    return try {
        await()
    } catch (e: CancellationException) {
        try {
            getCompleted()
        } catch (_: IllegalStateException) {
            // Rethrow the original cancellation exception
            throw e
        }
    }
}


/**
 * Executes the given [block] with [async] function from the [CoroutineScope] with maximum [parallelism].
 *
 * The [block] is executed concurrently for each element in the provided scope with a maximum of [parallelism] coroutines.
 *
 * @param T the type of the elements in the collection
 * @param collection the collection to iterate over
 * @param parallelism the maximum number of coroutines to run concurrently
 * @param block the function to be executed concurrently
 * @return a list of [Deferred] that represent the coroutines running the [block]
 */
public inline fun <T, R> CoroutineScope.parallel(
    parallelism: Int,
    collection: Iterable<T>,
    crossinline block: suspend (T) -> R
): List<Deferred<R>> {

    val semaphore = Semaphore(parallelism)
    return collection.map {
        this@parallel.async {
            semaphore.withPermit {
                block(it)
            }
        }
    }
}

/**
 * Delays coroutine for at least the given time without blocking a thread and resumes it after a specified time.
 * If the given [timeMillis] is non-positive, this function returns immediately.
 *
 * This suspending function is cancellable: if the [Job] of the current coroutine is cancelled while this
 * suspending function is waiting, this function immediately resumes with [CancellationException].
 * There is a **prompt cancellation guarantee**: even if this function is ready to return the result, but was cancelled
 * while suspended, [CancellationException] will be thrown. See [suspendCancellableCoroutine] for low-level details.
 *
 * If you want to delay forever (until cancellation), consider using [awaitCancellation] instead.
 *
 * Note that delay can be used in [select] invocation with [onTimeout][SelectBuilder.onTimeout] clause.
 *
 * Implementation note: how exactly time is tracked is an implementation detail of [CoroutineDispatcher] in the context.
 * @param timeMillis time in milliseconds.
 */
public suspend inline fun delay(timeMillis: Number): Unit = kotlinx.coroutines.delay(timeMillis.toLong())