/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 * Copyright (c) 2024-present "Nik De Kur"
 */

@file:Suppress("NOTHING_TO_INLINE")

package dev.nikdekur.ndkore.reflect

/**
 * Represents the result of a reflection operation.
 *
 * This class is used to encapsulate the result of operations involving reflection,
 * where the result can either be a valid object or indicate the absence of a result.
 *
 * @property value The result of the reflection operation.
 */
public open class ReflectResult(public val value: Any?) {

    /**
     * Determines whether the result is missing.
     *
     * @return `true` if the result is missing, `false` otherwise.
     */
    public inline fun isMissing(): Boolean = this is Missing

    /**
     * Represents a result indicating that the reflection operation did not yield a result.
     *
     * This singleton object is used to signify that the expected result was not found
     * or that no valid result is available.
     */
    public data object Missing : ReflectResult(null)
}
