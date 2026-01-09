/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 * Copyright (c) 2024-present "Nik De Kur"
 */

@file:Suppress("NOTHING_TO_INLINE")

package dev.nikdekur.ndkore.map

public typealias ListsMap<K, V> = Map<K, List<V>>
public typealias MutableListsMap<K, V> = MutableMap<K, MutableList<V>>

public inline fun <K, V> MutableListsMap<K, V>.add(
    key: K,
    value: V,
    listGen: () -> MutableList<V> = ::mutableListOf
) {
    val list = getOrPut(key, listGen)
    list.add(value)
}

public inline fun <K, V> MutableListsMap<K, V>.addAll(
    key: K,
    values: Collection<V>,
    listGen: () -> MutableList<V> = ::mutableListOf
) {
    val list = getOrPut(key, listGen)
    list.addAll(values)
}