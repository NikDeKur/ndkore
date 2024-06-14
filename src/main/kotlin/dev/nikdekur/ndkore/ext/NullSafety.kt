@file:Suppress("NOTHING_TO_INLINE")

package dev.nikdekur.ndkore.ext

inline val <T> T?.isNull
    get() = this == null

inline fun <T> T?.orElse(func: () -> T): T {
    if (this != null)
        return this
    return func.invoke()
}

inline fun <T> T?.orElse(value: T): T {
    if (this != null)
        return this
    return value
}


/**
 * If a value is present, returns the value, otherwise throws
 * `NoSuchElementException`.
 *
 * @return the non-`null` value described by this `Optional`
 * @throws NoSuchElementException if no value is present
 * @since 10
 */
inline fun <T> T?.orElseThrow(): T {
    if (this == null) {
        throw NoSuchElementException("No value present")
    }
    return this
}

/**
 * If a value is present, returns the value, otherwise throws an exception
 * produced by the exception supplying function.
 *
 * @apiNote
 * A method reference to the exception constructor with an empty argument
 * list can be used as the supplier. For example,
 * `IllegalStateException::new`
 *
 * @param <X> Type of the exception to be thrown
 * @param exceptionSupplier the supplying function that produces an
 * exception to be thrown
 * @return the value, if present
 * @throws X if no value is present
 * @throws NullPointerException if no value is present and the exception
 * supplying function is `null`
</X> */
inline fun <X : Throwable, T> T?.orElseThrow(exceptionSupplier: () -> X): T {
    return this ?: throw exceptionSupplier.invoke()
}