@file:JvmName("AsyncJvmKt")
@file:Suppress("NOTHING_TO_INLINE")

package dev.nikdekur.ndkore.ext

import java.util.concurrent.CompletableFuture

/**
 * Completes this [CompletableFuture] with the Unit value.
 */
public inline fun CompletableFuture<Unit>.complete(): Boolean = complete(Unit)

/**
 * Returns a new CompletableFuture that is already completed with the given value.
 *
 * Same as [CompletableFuture.completedFuture]
 *
 * @receiver the value to be used to complete the future
 */
public inline val <T> T.completedFuture: CompletableFuture<T>
    get() = CompletableFuture.completedFuture(this)


/**
 * Completes this [CompletableFuture] with the result of the given [supplier] function.
 *
 * If the [supplier] function throws an exception [Throwable],
 * this CompletableFuture will be completed exceptionally with the thrown exception.
 *
 * @param supplier the function supplying the value to complete this `CompletableFuture`
 * @return `true` if this future completed normally, `false` if it was already completed exceptionally
 */
public inline fun <T> CompletableFuture<T>.complete(supplier: () -> T): Boolean {
    return try {
        complete(supplier())
    } catch (e: Throwable) {
        completeExceptionally(e)
    }
}