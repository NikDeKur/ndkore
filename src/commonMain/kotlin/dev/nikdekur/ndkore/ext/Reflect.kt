/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 * Copyright (c) 2024-present "Nik De Kur"
 */

@file:Suppress("FunctionName", "kotlin:S100")

package dev.nikdekur.ndkore.ext



/**
 * Retrieves an enum constant by its name, optionally ignoring case.
 *
 * @param name The name of the enum constant.
 * @param ignoreCase Whether to ignore a case when matching the name.
 * @return The enum constant of type `T`.
 * @throws IllegalArgumentException If no matching enum constant is found.
 */
inline fun <reified T : Enum<T>> enumValueOf(name: String, ignoreCase: Boolean = false): T {
    return if (ignoreCase) {
        enumValues<T>().find { it.name.equals(name, ignoreCase = true) }
            ?: throw IllegalArgumentException("No enum constant ${T::class}.$name")
    } else {
        kotlin.enumValueOf(name)
    }
}
