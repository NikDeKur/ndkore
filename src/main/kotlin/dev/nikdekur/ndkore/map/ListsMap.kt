/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 * Copyright (c) 2024-present "Nik De Kur"
 */

@file:Suppress("NOTHING_TO_INLINE")

package dev.nikdekur.ndkore.map

import java.util.ArrayList

typealias ListsMap<K, V> = Map<K, List<V>>
typealias MutableListsMap<K, V> = MutableMap<K, MutableList<V>>


@Suppress("UNCHECKED_CAST", "kotlin:S6524")
inline fun <K, V> MutableListsMap<K, V>.add(
    key: K,
    value: V,
    listGen: () -> MutableList<V> = { ArrayList() }
) {
    val list = getOrPut(key, listGen)
    list.add(value)
}