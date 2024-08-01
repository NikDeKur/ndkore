/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 * Copyright (c) 2024-present "Nik De Kur"
 */

@file:Suppress("NOTHING_TO_INLINE")

package dev.nikdekur.ndkore.ext

import kotlinx.coroutines.runBlocking
import java.lang.Runtime
import java.lang.Thread

/**
 * Shortcut for [Runtime.getRuntime]
 *
 * Returns the runtime object associated with the current Java application.
 */
inline val runtime: Runtime
    get() = Runtime.getRuntime()

/**
 * Class representing a shutdown hook.
 *
 * Has a link to the thread that is the shutdown hook and provides methods to remove it.
 */
@JvmInline
value class ShutdownHook(val thread: Thread) {
    /**
     * Removes the shutdown hook.
     *
     * Equivalent to calling [Runtime.removeShutdownHook] with the [thread] property.
     *
     * @throws IllegalStateException if the virtual machine is already in the process of shutting down
     */
    fun remove() = runtime.removeShutdownHook(thread)

    /**
     * Tries to remove the shutdown hook.
     *
     * Equivalent to calling [remove] but catches the [IllegalStateException] and returns false instead.
     *
     * If the virtual machine is already in the process of shutting down, this method will return false.
     *
     * @return true if the shutdown hook was removed, false otherwise
     */
    fun tryRemove() = try {
        remove()
        true
    } catch (e: IllegalStateException ) {
        false
    }
}

/**
 * Adds a shutdown hook.
 *
 * Equivalent to calling [Runtime.addShutdownHook] with the [hook] parameter.
 *
 * @param hook the hook to add
 * @return a [ShutdownHook] object representing the added shutdown hook
 */
inline fun addShutdownHook(hook: Thread): ShutdownHook {
    runtime.addShutdownHook(hook)
    return ShutdownHook(hook)
}

/**
 * Adds a shutdown hook.
 *
 * Equivalent to calling [Runtime.addShutdownHook] with a new thread that runs the [hook] parameter.
 *
 * @param hook the hook to add
 * @return a [ShutdownHook] object representing the added shutdown hook
 */
inline fun addShutdownHook(crossinline hook: () -> Unit): ShutdownHook {
    return addShutdownHook(
        object : Thread() {
            override fun run() = hook()
        }
    )
}

/**
 * Adds a blocking (coroutine) shutdown hook.
 *
 * Accepts a suspend function as a parameter and runs it in a blocking manner,
 * suspending the shutdown process until the function completes.
 *
 * Equivalent to calling [addShutdownHook] with function containing [runBlocking] and the [hook] parameter.
 *
 * @param hook the hook to add
 * @return a [ShutdownHook] object representing the added shutdown hook
 */
inline fun addBlockingShutdownHook(crossinline hook: suspend () -> Unit): ShutdownHook {
    return addShutdownHook {
        runBlocking {
            hook()
        }
    }
}

