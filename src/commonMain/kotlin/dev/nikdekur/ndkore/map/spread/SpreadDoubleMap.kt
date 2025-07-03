/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 * Copyright (c) 2024-present "Nik De Kur"
 */

package dev.nikdekur.ndkore.map.spread

import dev.nikdekur.ndkore.ext.map

public open class SpreadDoubleMap<K : Any>(
    override val max: Double,
    override val onMax: (K) -> Unit = {},
    public val map: MutableMap<K, Double> = mutableMapOf()
) : SpreadMap<K, Double, Double>, Map<K, Double> by map {

    override var filled: Double = 0.0

    override val left: Double
        get() = max - filled

    override fun register(key: K, value: Double) {
        if (filled >= max) return
        if (value + filled >= max) {
            registerInternal(key, max - filled)
        } else {
            registerInternal(key, value)
        }
    }

    private fun registerInternal(key: K, damage: Double) {
        if (damage <= 0.0) return
        map[key] = getValue(key) + damage
        filled += damage
        if (filled >= max) {
            onMax.invoke(key)
            filled = max
        }
    }

    override fun getValue(key: K): Double {
        return getOrElse(key) { 0.0 }
    }

    override fun getValueMultiplier(key: K): Double {
        return getValue(key) / max
    }

    override fun toMultiplierMap(): Map<K, Double> {
        val max = max
        return mapValues {
            it.value / max
        }
    }

    override fun split(value: Double): Map<K, Double> {
        return map(
            { it.key },
            {
                val percent = getValueMultiplier(it.key)
                value * percent
            }
        )
    }

    override fun clear() {
        map.clear()
        filled = 0.0
    }
}