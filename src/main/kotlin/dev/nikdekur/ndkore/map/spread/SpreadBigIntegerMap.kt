/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 * Copyright (c) 2024-present "Nik De Kur"
 */

package dev.nikdekur.ndkore.map.spread

import dev.nikdekur.ndkore.ext.map
import java.math.BigInteger

open class SpreadBigIntegerMap<K>(
    override val max: () -> BigInteger,
    override val onMax: (K) -> Unit = {}
) : AbstractSpreadMap<K, BigInteger>() {

    override var filled: BigInteger = BigInteger.ZERO

    override fun register(key: K, value: BigInteger) {
        val max = max()
        if (filled >= max) return
        if (value + filled >= max) {
            registerInternal(key, max - filled)
        } else {
            registerInternal(key, value)
        }
    }

    private fun registerInternal(key: K, damage: BigInteger) {
        if (damage == BigInteger.ZERO) return
        this[key] = getOrDefault(key, BigInteger.ZERO) + damage
        filled += damage
        val max = max()
        if (filled >= max) {
            onMax.invoke(key)
            filled = max
        }
    }

    override fun getValue(key: K): BigInteger {
        return getOrDefault(key, BigInteger.ZERO)
    }

    override fun getValueMultiplier(key: K): Double {
        return getValue(key).toDouble() / max().toDouble()
    }

    override fun toMultiplier(): Map<K, Double> {
        val max = max().toDouble()
        return map(
            { it.key },
            { it.value.toDouble() / max }
        )
    }

    override fun toPercent(): Map<K, Double> {
        val max = max().toDouble()
        return map(
            { it.key },
            { it.value.toDouble() / max * 100}
        )
    }

    override fun split(value: BigInteger): Map<K, BigInteger> {
        return map(
            { it.key },
            {
                val percent = getValueMultiplier(it.key)
                (value.toDouble() * percent).toBigDecimal().toBigInteger()
            }
        )
    }

    override fun clear() {
        super.clear()
        filled = BigInteger.ZERO
    }

    override val left: BigInteger
        get() = max() - filled

}