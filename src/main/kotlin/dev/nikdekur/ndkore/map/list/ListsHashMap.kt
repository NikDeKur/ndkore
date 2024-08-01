/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 * Copyright (c) 2024-present "Nik De Kur"
 */

package dev.nikdekur.ndkore.map.list

open class ListsHashMap<K : Any, V> : HashMap<K, MutableList<out V>>(), ListsMap<K, V> {
    override fun get(key: K): MutableList<out V> {
        return computeIfAbsent(key) { ArrayList() }
    }

    override fun getIfPresent(key: K): MutableList<out V>? {
        return super.get(key)
    }

    @Suppress("UNCHECKED_CAST")
    override fun add(key: K, value: V) {
        val list: MutableList<V> = get(key) as MutableList<V>
        list.add(value)
    }

    @Suppress("UNCHECKED_CAST")
    override fun delete(key: K, value: V) {
        val list: MutableList<V> = get(key) as MutableList<V>
        list.remove(value)
    }
}
