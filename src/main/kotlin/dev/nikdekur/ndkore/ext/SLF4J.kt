@file:OptIn(ExperimentalContracts::class)
@file:Suppress("NOTHING_TO_INLINE")

package dev.nikdekur.ndkore.ext

import org.slf4j.Logger
import org.slf4j.event.Level
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.contract

inline fun Logger.log(level: Level, msg: String) = atLevel(level).log(msg)
inline fun Logger.log(level: Level, msg: () -> String) = log(level, msg())

inline fun Logger.trace(msg: () -> String) = trace(msg())
inline fun Logger.debug(msg: () -> String) = debug(msg())
inline fun Logger.info(msg: () -> String) = info(msg())
inline fun Logger.warn(msg: () -> String) = warn(msg())
inline fun Logger.error(msg: () -> String) = error(msg())

inline fun <T> Logger.recordTiming(level: Level = Level.INFO, name: String, block: () -> T): T {
    contract {
        callsInPlace(block, kotlin.contracts.InvocationKind.EXACTLY_ONCE)
    }

    val start = System.nanoTime()
    val result = block()
    val end = System.nanoTime()

    val ms = ((end - start) / 1_000_000).format(2)
    atLevel(level)
        .log("$name took ${ms}ms")

    return result
}


inline fun <T> Logger.recordTimingNano(level: Level, name: String, block: () -> T): T {
    val start = System.nanoTime()
    val result = block()
    val end = System.nanoTime()

    val nanos = (end - start)
    atLevel(level)
        .log("$name took ${nanos}nanos")

    return result
}
