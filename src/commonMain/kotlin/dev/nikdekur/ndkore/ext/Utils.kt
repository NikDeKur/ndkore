/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 * Copyright (c) 2024-present "Nik De Kur"
 */

@file:Suppress("NOTHING_TO_INLINE")

package dev.nikdekur.ndkore.ext

inline fun input(text: String): String {
    print(text)
    return readln()
}



typealias CompAny = Comparable<Any>

inline infix fun <K, V> K.singleMap(value: V): Map<K, V> = mapOf(this to value)