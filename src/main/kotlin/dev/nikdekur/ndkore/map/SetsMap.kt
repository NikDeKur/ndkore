/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 * Copyright (c) 2024-present "Nik De Kur"
 */

@file:Suppress("NOTHING_TO_INLINE")

package dev.nikdekur.ndkore.map

import java.util.HashSet

typealias SetsMap<K, V> = Map<K, Set<V>>
typealias MutableSetsMap<K, V> = MutableMap<K, MutableSet<V>>

inline fun <K, V> SetsMap<K, V>.contains(key: K, value: V): Boolean {
    return get(key)?.contains(value) == true
}

@Suppress("kotlin:S6524")
inline fun <K, V> MutableSetsMap<K, V>.add(
    key: K,
    value: V,
    setGen: () -> MutableSet<V> = { HashSet() }
) {
    val list = getOrPut(key, setGen)
    list.add(value)
}

@Suppress("kotlin:S6524")
inline fun <K, V> MutableSetsMap<K, V>.remove(key: K, value: V): Boolean {
    return get(key)?.remove(value) == true
}
