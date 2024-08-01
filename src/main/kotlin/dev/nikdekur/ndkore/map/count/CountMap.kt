/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 * Copyright (c) 2024-present "Nik De Kur"
 */

package dev.nikdekur.ndkore.map.count

typealias CountMap<K> = MutableMap<K, Int>

fun <K> CountMap<K>.increment(key: K) {
    val count = get(key, 0)
    this[key] = count + 1
}

fun <K> CountMap<K>.reset(key: K) {
    remove(key)
}

fun <K> CountMap<K>.get(key: K, default: Int = 0): Int {
    return getOrDefault(key, default)
}