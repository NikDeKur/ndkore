@file:OptIn(ExperimentalContracts::class)
@file:Suppress("NOTHING_TO_INLINE")

package dev.nikdekur.ndkore.ext

import io.github.oshai.kotlinlogging.KLogger
import io.github.oshai.kotlinlogging.Level
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract
import kotlin.time.TimeSource
import kotlin.time.TimedValue
import kotlin.time.measureTimedValue

public inline fun <T> TimeSource.recordTimingImpl(
    log: (String) -> Unit,
    name: String,
    block: () -> T
): TimedValue<T> {

    contract {
        callsInPlace(block, InvocationKind.EXACTLY_ONCE)
    }


    val data = measureTimedValue {
        block()
    }

    // Round to 2dp
    val ms = data.duration.inWholeMilliseconds.format(2)
    log("`$name` took `$ms` ms")
    return data
}


public inline fun KLogger.log(level: Level, msg: String): Unit = at(level) {
    message = msg
}

public inline fun KLogger.log(level: Level, msg: () -> String): Unit = log(level, msg())


public inline fun KLogger.log(level: Level, throwable: Throwable, msg: String): Unit =
    at(level) {
        message = msg
        cause = throwable
    }


public inline fun KLogger.log(level: Level, throwable: Throwable, crossinline msg: () -> String): Unit =
    log(level, throwable, msg())


public inline fun <T> KLogger.recordTiming(
    source: TimeSource,
    name: String,
    level: Level = Level.INFO,
    block: () -> T
): TimedValue<T> = source.recordTimingImpl({ log(level, it) }, name, block)
