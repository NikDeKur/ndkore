/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 * Copyright (c) 2024-present "Nik De Kur"
 */

@file:Suppress("INVISIBLE_REFERENCE")
@file:OptIn(ExperimentalContracts::class)

package dev.nikdekur.ndkore.ext

import io.github.oshai.kotlinlogging.KLogger
import io.github.oshai.kotlinlogging.jul.internal.JulLoggerFactory.wrapJLogger
import java.util.logging.Level
import java.util.logging.Logger
import kotlin.contracts.ExperimentalContracts
import kotlin.time.TimeSource
import kotlin.time.TimedValue


public inline fun Logger.finest(msg: () -> String): Unit = finest(msg())
public inline fun Logger.finer(msg: () -> String): Unit = finer(msg())
public inline fun Logger.fine(msg: () -> String): Unit = fine(msg())
public inline fun Logger.config(msg: () -> String): Unit = config(msg())
public inline fun Logger.info(msg: () -> String): Unit = info(msg())
public inline fun Logger.warning(msg: () -> String): Unit = warning(msg())
public inline fun Logger.severe(msg: () -> String): Unit = severe(msg())

public inline fun Logger.finest(throwable: Throwable, msg: () -> String): Unit = log(Level.FINEST, msg(), throwable)
public inline fun Logger.finer(throwable: Throwable, msg: () -> String): Unit = log(Level.FINER, msg(), throwable)
public inline fun Logger.fine(throwable: Throwable, msg: () -> String): Unit = log(Level.FINE, msg(), throwable)
public inline fun Logger.config(throwable: Throwable, msg: () -> String): Unit = log(Level.CONFIG, msg(), throwable)
public inline fun Logger.info(throwable: Throwable, msg: () -> String): Unit = log(Level.INFO, msg(), throwable)
public inline fun Logger.warning(throwable: Throwable, msg: () -> String): Unit = log(Level.WARNING, msg(), throwable)
public inline fun Logger.severe(throwable: Throwable, msg: () -> String): Unit = log(Level.SEVERE, msg(), throwable)

public fun Logger.asKLogger(): KLogger = wrapJLogger(this)

public inline fun <T> Logger.recordTiming(
    source: TimeSource,
    name: String,
    level: Level = Level.INFO,
    block: () -> T
): TimedValue<T> = source.recordTimingImpl({ log(level, it) }, name, block)
