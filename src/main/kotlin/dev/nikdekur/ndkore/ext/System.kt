@file:Suppress("NOTHING_TO_INLINE")

package dev.nikdekur.ndkore.ext

import kotlinx.coroutines.runBlocking
import java.lang.Runtime
import java.lang.Thread

inline val runtime: Runtime
    get() = Runtime.getRuntime()

@JvmInline
value class ShutdownHook(val thread: Thread) {
    fun remove() = runtime.removeShutdownHook(thread)
    fun tryRemove() = try {
        remove()
        true
    } catch (e: IllegalStateException ) {
        false
    }
}

inline fun addShutdownHook(hook: Thread): ShutdownHook {
    runtime.addShutdownHook(hook)
    return ShutdownHook(hook)
}

inline fun addShutdownHook(crossinline hook: () -> Unit): ShutdownHook {
    return addShutdownHook(
        object : Thread() {
            override fun run() = hook()
        }
    )
}

inline fun addBlockingShutdownHook(crossinline hook: suspend () -> Unit): ShutdownHook {
    return addShutdownHook {
        runBlocking {
            hook()
        }
    }
}

