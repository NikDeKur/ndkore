/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 * Copyright (c) 2024 Nik De Kur
 */

package dev.nikdekur.ndkore.map.spread

import dev.nikdekur.ndkore.ext.*

open class SpreadDoubleMap<K>(
    override val max: () -> Double,
    override val onMax: (K) -> Unit = {}
) : AbstractSpreadMap<K, Double>() {

    override var filled: Double = 0.0

    override fun register(key: K, value: Double) {
        val max = max()
        if (filled >= max) return
        if (value + filled >= max) {
            registerInternal(key, max - filled)
        } else {
            registerInternal(key, value)
        }
    }

    private fun registerInternal(key: K, damage: Double) {
        if (damage <= 0.0) return
        this[key] = getOrDefault(key, 0.0) + damage
        filled += damage
        val max = max()
        if (filled >= max) {
            onMax.invoke(key)
            filled = max
        }
    }

    override fun getValue(key: K): Double {
        return getOrDefault(key, 0.0)
    }

    override fun getValueMultiplier(key: K): Double {
        return getValue(key) / max()
    }

    override fun toMultiplier(): Map<K, Double> {
        val max = max()
        return map(
            { it.key },
            { it.value / max}
        )
    }

    override fun toPercent(): Map<K, Double> {
        val max = max()
        return map(
            { it.key },
            { it.value / max * 100 }
        )
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
        super.clear()
        filled = 0.0
    }

    override val left: Double
        get() = max() - filled

}