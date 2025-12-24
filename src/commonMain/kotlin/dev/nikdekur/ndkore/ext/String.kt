/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 * Copyright (c) 2024-present "Nik De Kur"
 */

@file:OptIn(ExperimentalUuidApi::class)
@file:Suppress("NOTHING_TO_INLINE")

package dev.nikdekur.ndkore.ext

import dev.nikdekur.ndkore.extra.MatchCondition
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

public inline fun String.removeDoubleSpaces(): String {
    var s = this
    while (s.contains("  ")) {
        s = s.replace("  ", " ")
    }
    return s
}

public inline fun String.match(condition: MatchCondition, otherString: String): Boolean {
    return condition.match(this, otherString)
}

public inline fun String.asCamelCaseGetter(): String =
    if (this.isEmpty()) {
        "get"
    } else {
        "get${this[0].uppercase()}${this.substring(1)}"
    }

public inline fun String.isBlankOrEmpty(): Boolean {
    return isBlank() || isEmpty()
}

public inline fun String.isUUID(): Boolean {
    return this.matches(Patterns.UUID)
}

public inline fun String.toUUID(): Uuid {
    return Uuid.parse(this)
}

public inline fun String.toUUIDOrNull(): Uuid? {
    return try {
        this.toUUID()
    } catch (e: IllegalArgumentException) {
        null
    }
}

public inline fun String.toBooleanSmart(): Boolean {
    return when (this) {
        "true", "1", "yes", "on" -> true
        "false", "0", "no", "off" -> false
        else -> throw IllegalArgumentException("Cannot convert '$this' to boolean")
    }
}

public inline fun String.toBooleanSmartOrNull(): Boolean? {
    return when (this) {
        "true", "1", "yes", "on" -> true
        "false", "0", "no", "off" -> false
        else -> null
    }
}


public inline fun String.capitalizeFirstLetter(): String {
    return if (this.isEmpty()) {
        this
    } else {
        this[0].uppercase() + this.substring(1)
    }
}


@OptIn(ExperimentalStdlibApi::class)
public inline fun ByteArray.toHEX(): String {
    return toHexString(HexFormat.Default)
}

@OptIn(ExperimentalStdlibApi::class)
public inline fun String.fromHEX(): ByteArray {
    return hexToByteArray(HexFormat.Default)
}

/**
 * Returns a sequence where the given [separator] is interspersed between elements of the original sequence.
 *
 * @param separator A function that takes the index of the preceding element and returns the separator element.
 * @return A sequence with the separator interspersed between elements.
 */
public fun <T> Sequence<T>.intersperse(separator: (index: Int) -> T): Sequence<T> = sequence {
    var first = true
    var i = 0
    this@intersperse.forEach { element ->
        if (!first) {
            yield(separator(i - 1))
        }
        yield(element)
        first = false
        i++
    }
}


/**
 * Groups strings from the sequence into a list of strings, each not exceeding the specified size limit.
 *
 * @param sizeLimit The maximum size of each grouped string.
 * Strings that exceed this limit will be added as separate entries.
 * @return A list of grouped strings.
 */
public fun Sequence<String>.groupStrings(sizeLimit: Int): List<String> {
    val result = mutableListOf<String>()
    val current = StringBuilder()

    forEach { text ->
        if (current.isNotEmpty() && current.length + text.length > sizeLimit) {
            result += current.toString()
            current.setLength(0)
        }

        if (text.length > sizeLimit) {
            result += text
            return@forEach
        }

        current.append(text)
    }

    if (current.isNotEmpty()) {
        result += current.toString()
    }

    return result
}
