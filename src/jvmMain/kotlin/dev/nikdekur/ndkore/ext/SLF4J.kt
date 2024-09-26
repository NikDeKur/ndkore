/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 * Copyright (c) 2024-present "Nik De Kur"
 */

@file:Suppress("NOTHING_TO_INLINE")

package dev.nikdekur.ndkore.ext

import org.slf4j.Logger
import org.slf4j.event.Level
import kotlin.time.TimeSource
import kotlin.time.TimedValue

public inline fun Logger.log(level: Level, msg: String) = atLevel(level).log(msg)

public inline fun Logger.log(level: Level, msg: () -> String) = log(level, msg())
public inline fun Logger.trace(msg: () -> String) = trace(msg())
public inline fun Logger.debug(msg: () -> String) = debug(msg())
public inline fun Logger.info(msg: () -> String) = info(msg())
public inline fun Logger.warn(msg: () -> String) = warn(msg())
public inline fun Logger.error(msg: () -> String) = error(msg())


public inline fun Logger.log(level: Level, throwable: Throwable, msg: () -> String) =
    atLevel(level).setCause(throwable).log(msg())

public inline fun Logger.trace(throwable: Throwable, msg: () -> String) = trace(msg(), throwable)
public inline fun Logger.debug(throwable: Throwable, msg: () -> String) = debug(msg(), throwable)
public inline fun Logger.info(throwable: Throwable, msg: () -> String) = info(msg(), throwable)
public inline fun Logger.warn(throwable: Throwable, msg: () -> String) = warn(msg(), throwable)
public inline fun Logger.error(throwable: Throwable, msg: () -> String) = error(msg(), throwable)

public inline fun <T> Logger.recordTiming(
    source: TimeSource,
    name: String,
    level: Level = Level.INFO,
    block: () -> T
): TimedValue<T> = source.recordTimingImpl({ log(level, it) }, name, block)
