@file:JvmName("StringJvmKt")
@file:Suppress("NOTHING_TO_INLINE")

package dev.nikdekur.ndkore.ext

import java.util.UUID

inline fun String.toJUUID(): UUID {
    return UUID.fromString(this)
}

inline fun String.toJUUIDOrNull(): UUID? {
    return try {
        this.toJUUID()
    } catch (e: IllegalArgumentException) {
        null
    }
}