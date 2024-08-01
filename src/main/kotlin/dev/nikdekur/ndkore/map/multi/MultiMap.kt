/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 * Copyright (c) 2024-present "Nik De Kur"
 */

package dev.nikdekur.ndkore.map.multi


interface MultiMap<K1, K2, V> : MutableMap<K1, MutableMap<K2, V>> {

    fun getMap(k1: K1): MutableMap<K2, V>

    fun put(k1: K1, k2: K2, value: V)

    fun getOrDefault(k1: K1, k2: K2, default: V? = null): V?
    operator fun get(k1: K1, k2: K2): V? = getOrDefault(k1, k2, null)

    fun remove(k1: K1, k2: K2): V?

    fun contains(k1: K1, k2: K2): Boolean

    fun computeIfAbsent(k1: K1, k2: K2, orElse: (K2) -> V): V
}