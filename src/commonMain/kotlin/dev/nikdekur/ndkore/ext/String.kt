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