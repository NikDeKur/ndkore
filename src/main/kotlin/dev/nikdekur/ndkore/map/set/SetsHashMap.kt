/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 * Copyright (c) 2024-present "Nik De Kur"
 */

package dev.nikdekur.ndkore.map.set

open class SetsHashMap<K : Any, V> : HashMap<K, MutableSet<out V>>(), SetsMap<K, V> {
    override fun get(key: K): MutableSet<out V> {
        return computeIfAbsent(key) { HashSet() }
    }

    override fun getIfPresent(key: K): MutableSet<out V>? {
        return super.get(key)
    }

    @Suppress("UNCHECKED_CAST")
    override fun add(key: K, value: V) {
        val list: MutableSet<V> = get(key) as MutableSet<V>
        list.add(value)
    }

    @Suppress("UNCHECKED_CAST")
    override fun delete(key: K, value: V) {
        val list: MutableSet<V> = get(key) as MutableSet<V>
        list.remove(value)
    }
}
