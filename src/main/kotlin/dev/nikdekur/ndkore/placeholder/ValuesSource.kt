/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 * Copyright (c) 2024-present "Nik De Kur"
 */

package dev.nikdekur.ndkore.placeholder

/**
 * # Values Source
 *
 * An interface that is used in the placeholders system to find value at a path in an object.
 */
fun interface ValuesSource {

    /**
     * Finds a value at a path in an object.
     *
     * The reason to return [NotFound] instead of simple [null] is because
     * value at the path might really be null.
     *
     * @param obj The object to find the value in.
     * @param path The path to find the value at.
     * @return The value at the path in the object or [NotFound] if no value found at the path
     */
    fun findValue(obj: Any, path: String): Any?

    /**
     * An object indicating that no result found on a path.
     */
    data object NotFound
}
