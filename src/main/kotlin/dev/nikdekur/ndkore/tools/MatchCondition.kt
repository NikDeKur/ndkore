/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 * Copyright (c) 2024 Nik De Kur
 */

package dev.nikdekur.ndkore.tools

enum class MatchCondition {
    EQUALS,
    EQUALS_IGNORE_CASE,
    CONTAINS,
    CONTAINS_IGNORE_CASE,
    STARTSWITH,
    STARTSWITH_IGNORE_CASE,
    ENDSWITH,
    ENDSWITH_IGNORE_CASE;

    fun match(string: String, otherString: String): Boolean {
        return when (this) {
            EQUALS -> string == otherString
            EQUALS_IGNORE_CASE -> string.equals(otherString, ignoreCase = true)
            CONTAINS -> string.contains(otherString)
            CONTAINS_IGNORE_CASE -> string.contains(otherString, ignoreCase = true)
            STARTSWITH -> string.startsWith(otherString)
            STARTSWITH_IGNORE_CASE -> string.startsWith(otherString, ignoreCase = true)
            ENDSWITH -> string.endsWith(otherString)
            ENDSWITH_IGNORE_CASE -> string.endsWith(otherString, ignoreCase = true)
        }
    }
}
