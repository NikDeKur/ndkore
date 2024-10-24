/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 * Copyright (c) 2024-present "Nik De Kur"
 */

@file:Suppress("NOTHING_TO_INLINE")

package dev.nikdekur.ndkore.map


public typealias CountMap<K> = Map<K, Int>
public typealias MutableCountMap<K> = MutableMap<K, Int>

public inline fun <K> MutableCountMap<K>.increment(key: K) {
    val count = get(key, 0)
    this[key] = count + 1
}

public fun <K> MutableCountMap<K>.reset(key: K) {
    remove(key)
}

public inline fun <K> CountMap<K>.get(key: K, default: Int = 0): Int {
    return getOrElse(key) { default }
}