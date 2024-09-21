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

inline fun String.removeDoubleSpaces(): String {
    var s = this
    while (s.contains("  ")) {
        s = s.replace("  ", " ")
    }
    return s
}

inline fun String.match(condition: MatchCondition, otherString: String): Boolean {
    return condition.match(this, otherString)
}

inline fun String.asCamelCaseGetter() =
    if (this.isEmpty()) {
        "get"
    } else {
        "get${this[0].uppercase()}${this.substring(1)}"
    }

inline fun String.isBlankOrEmpty(): Boolean {
    return isBlank() || isEmpty()
}

inline fun String.isUUID(): Boolean {
    return this.matches(Patterns.UUID)
}

inline fun String.toUUID(): Uuid {
    return Uuid.parse(this)
}

inline fun String.toUUIDOrNull(): Uuid? {
    return try {
        this.toUUID()
    } catch (e: IllegalArgumentException) {
        null
    }
}

inline fun String.toBooleanSmart(): Boolean {
    return when (this) {
        "true", "1", "yes", "on" -> true
        "false", "0", "no", "off" -> false
        else -> throw IllegalArgumentException("Cannot convert '$this' to boolean")
    }
}

inline fun String.toBooleanSmartOrNull(): Boolean? {
    return when (this) {
        "true", "1", "yes", "on" -> true
        "false", "0", "no", "off" -> false
        else -> null
    }
}


inline fun String.capitalizeFirstLetter(): String {
    return if (this.isEmpty()) {
        this
    } else {
        this[0].uppercase() + this.substring(1)
    }
}