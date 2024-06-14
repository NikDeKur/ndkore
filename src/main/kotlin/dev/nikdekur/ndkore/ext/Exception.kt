@file:Suppress("NOTHING_TO_INLINE")

package dev.nikdekur.ndkore.ext

inline fun Throwable.printStackAndThrow(): Nothing {
    printStackTrace()
    throw this
}

inline fun tryEverything(blocks: List<() -> Unit>): Throwable? {
    var suppress: Throwable? = null
    for (block in blocks) {
        try {
            block()
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

inline fun tryEverything(vararg blocks: () -> Unit) = tryEverything(blocks.toList())

inline fun <T> Iterable<T>.forEachSafe(onException: T.(Exception) -> Unit = {}, block: (T) -> Unit) {
    forEach {
        try {
            block(it)
        } catch (e: Exception) {
            onException(it, e)
        }
    }
}



inline fun <K, V> Map<K, V>.forEachSafe(onException: Map.Entry<K, V>.(Exception) -> Unit = {}, block: (Map.Entry<K, V>) -> Unit) {
    forEach {
        try {
            block(it)
        } catch (e: Exception) {
            onException(it, e)
        }
    }
}