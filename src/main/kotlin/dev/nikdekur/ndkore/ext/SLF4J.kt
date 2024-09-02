/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 * Copyright (c) 2024-present "Nik De Kur"
 */

@file:OptIn(ExperimentalContracts::class)
@file:Suppress("NOTHING_TO_INLINE")

package dev.nikdekur.ndkore.ext

import org.slf4j.Logger
import org.slf4j.event.Level
import kotlin.contracts.ExperimentalContracts

inline fun Logger.log(level: Level, msg: String) = atLevel(level).log(msg)

inline fun Logger.log(level: Level, msg: () -> String) = log(level, msg())
inline fun Logger.trace(msg: () -> String) = trace(msg())
inline fun Logger.debug(msg: () -> String) = debug(msg())
inline fun Logger.info(msg: () -> String) = info(msg())
inline fun Logger.warn(msg: () -> String) = warn(msg())
inline fun Logger.error(msg: () -> String) = error(msg())


inline fun Logger.log(level: Level, throwable: Throwable, msg: () -> String) =
    atLevel(level).setCause(throwable).log(msg())

inline fun Logger.trace(throwable: Throwable, msg: () -> String) = trace(msg(), throwable)
inline fun Logger.debug(throwable: Throwable, msg: () -> String) = debug(msg(), throwable)
inline fun Logger.info(throwable: Throwable, msg: () -> String) = info(msg(), throwable)
inline fun Logger.warn(throwable: Throwable, msg: () -> String) = warn(msg(), throwable)
inline fun Logger.error(throwable: Throwable, msg: () -> String) = error(msg(), throwable)

fun <T> Logger.recordTiming(level: Level = Level.INFO, name: String, block: () -> T): T =
    recordTimingImpl({ log(level, it) }, name, block)


fun <T> Logger.recordTimingNano(level: Level, name: String, block: () -> T): T =
    recordTimingNanoImpl({ log(level, it) }, name, block)
