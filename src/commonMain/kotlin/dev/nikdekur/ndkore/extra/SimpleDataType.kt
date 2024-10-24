/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 * Copyright (c) 2024-present "Nik De Kur"
 */

package dev.nikdekur.ndkore.extra

import kotlin.jvm.JvmStatic


public enum class SimpleDataType {
    STRING,
    INT,
    LONG,
    FLOAT,
    DOUBLE,
    BOOLEAN,
    CHAR,
    BYTE,
    SHORT

    ;

    public fun convert(string: String): Any {
        return when (this) {
            STRING -> string
            INT -> string.toInt()
            LONG -> string.toLong()
            FLOAT -> string.toFloat()
            DOUBLE -> string.toDouble()
            BOOLEAN -> string.toBoolean()
            CHAR -> string.single()
            BYTE -> string.toByte()
            SHORT -> string.toShort()
        }
    }

    public fun convertOrNull(string: String): Any? {
        return when (this) {
            STRING -> string
            INT -> string.toIntOrNull()
            LONG -> string.toLongOrNull()
            FLOAT -> string.toFloatOrNull()
            DOUBLE -> string.toDoubleOrNull()
            BOOLEAN -> string.toBooleanStrictOrNull()
            CHAR -> string.singleOrNull()
            BYTE -> string.toByteOrNull()
            SHORT -> string.toShortOrNull()
        }
    }


    public companion object {

        @JvmStatic
        public fun fromStringOrNull(type: String): SimpleDataType? {
            return entries.firstOrNull { it.name.equals(type, ignoreCase = true) }
        }

        @JvmStatic
        public fun fromString(type: String): SimpleDataType {
            return fromStringOrNull(type) ?: throw IllegalArgumentException("Unknown type: $type")
        }
    }
}