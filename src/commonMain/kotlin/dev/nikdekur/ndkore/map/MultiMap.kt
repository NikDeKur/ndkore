/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 * Copyright (c) 2024-present "Nik De Kur"
 */

@file:Suppress("NOTHING_TO_INLINE")

package dev.nikdekur.ndkore.map

public typealias MultiMap<K1, K2, V> = Map<K1, Map<K2, V>>
public typealias MutableMultiMap<K1, K2, V> = MutableMap<K1, MutableMap<K2, V>>

public inline operator fun <K1, K2, V> MultiMap<K1, K2, V>.get(k1: K1, k2: K2): V? {
    return get(k1)?.get(k2)
}

public inline fun <K1, K2, V> MultiMap<K1, K2, V>.getOrDefault(
    k1: K1,
    k2: K2,
    default: V? = null
): V? {
    return get(k1)?.get(k2) ?: default
}

public inline fun <K1, K2, V> MultiMap<K1, K2, V>.contains(k1: K1, k2: K2): Boolean {
    return get(k1)?.containsKey(k2) == true
}

public inline fun <K1, K2, V> MutableMultiMap<K1, K2, V>.put(
    k1: K1,
    k2: K2,
    value: V,
    mapGen: () -> MutableMap<K2, V> = ::mutableMapOf
) {
    val map = getOrPut(k1, mapGen)
    map[k2] = value
}


@Suppress("kotlin:S6524")
public inline fun <K1, K2, V> MutableMultiMap<K1, K2, V>.remove(k1: K1, k2: K2): V? {
    val map = get(k1)
    val removed = map?.remove(k2)

    if (map != null && map.isEmpty()) {
        remove(k1)
    }

    return removed
}

