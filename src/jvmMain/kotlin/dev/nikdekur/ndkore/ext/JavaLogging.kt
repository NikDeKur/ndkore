/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 * Copyright (c) 2024-present "Nik De Kur"
 */

@file:OptIn(ExperimentalContracts::class)

package dev.nikdekur.ndkore.ext

import java.util.logging.Level
import java.util.logging.Logger
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.contract

inline fun <T> recordTimingImpl(log: (String) -> Unit, name: String, block: () -> T): T {
    contract {
        callsInPlace(block, kotlin.contracts.InvocationKind.EXACTLY_ONCE)
    }

    val start = System.nanoTime()
    val result = block()
    val end = System.nanoTime()

    // Round to 2dp
    val ms = ((end - start) / 1_000_000.0).round(2)
    log("`$name` took `$ms` ms")
    return result
}

inline fun <T> recordTimingNanoImpl(log: (String) -> Unit, name: String, block: () -> T): T {
    val start = System.nanoTime()
    val result = block()
    val end = System.nanoTime()

    val nanos = (end - start)
    log("`$name` took `$nanos` nanos")
    return result
}


inline fun Logger.finest(msg: () -> String) = finest(msg())
inline fun Logger.finer(msg: () -> String) = finer(msg())
inline fun Logger.fine(msg: () -> String) = fine(msg())
inline fun Logger.config(msg: () -> String) = config(msg())
inline fun Logger.info(msg: () -> String) = info(msg())
inline fun Logger.warning(msg: () -> String) = warning(msg())
inline fun Logger.severe(msg: () -> String) = severe(msg())

inline fun Logger.finest(throwable: Throwable, msg: () -> String) = log(Level.FINEST, msg(), throwable)
inline fun Logger.finer(throwable: Throwable, msg: () -> String) = log(Level.FINER, msg(), throwable)
inline fun Logger.fine(throwable: Throwable, msg: () -> String) = log(Level.FINE, msg(), throwable)
inline fun Logger.config(throwable: Throwable, msg: () -> String) = log(Level.CONFIG, msg(), throwable)
inline fun Logger.info(throwable: Throwable, msg: () -> String) = log(Level.INFO, msg(), throwable)
inline fun Logger.warning(throwable: Throwable, msg: () -> String) = log(Level.WARNING, msg(), throwable)
inline fun Logger.severe(throwable: Throwable, msg: () -> String) = log(Level.SEVERE, msg(), throwable)

inline fun <T> Logger.recordTiming(level: Level = Level.INFO, name: String, block: () -> T): T =
    recordTimingImpl({ log(level, it) }, name, block)

inline fun <T> Logger.recordTimingNano(level: Level, name: String, block: () -> T): T =
    recordTimingNanoImpl({ log(level, it) }, name, block)
