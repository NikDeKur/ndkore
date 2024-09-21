/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 * Copyright (c) 2024-present "Nik De Kur"
 */

package dev.nikdekur.ndkore.map.spread

abstract class AbstractSpreadMap<K : Any, V : Any>(
    val map: MutableMap<K, V> = mutableMapOf(),
) : SpreadMap<K, V>, MutableMap<K, V> by map {

    override val isDone: Boolean
        get() = filled == max

    override fun getValuePercent(key: K): Double {
        return getValueMultiplier(key) * 100
    }
}