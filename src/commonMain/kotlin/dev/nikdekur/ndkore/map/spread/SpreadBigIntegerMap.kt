/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 * Copyright (c) 2024-present "Nik De Kur"
 */

package dev.nikdekur.ndkore.map.spread

import com.ionspin.kotlin.bignum.decimal.BigDecimal
import com.ionspin.kotlin.bignum.integer.BigInteger

public open class SpreadBigIntegerMap<K : Any>(
    override val max: BigInteger,
    override val onMax: (K) -> Unit = {},
    public val map: MutableMap<K, BigInteger> = mutableMapOf()
) : SpreadMap<K, BigInteger, BigDecimal>, Map<K, BigInteger> by map {

    override var filled: BigInteger = BigInteger.ZERO

    override val left: BigInteger
        get() = max - filled

    override fun register(key: K, value: BigInteger) {
        if (filled >= max) return
        if (value + filled >= max) {
            registerInternal(key, max - filled)
        } else {
            registerInternal(key, value)
        }
    }

    private fun registerInternal(key: K, damage: BigInteger) {
        if (damage <= BigInteger.ZERO) return
        map[key] = getValue(key) + damage
        filled += damage
        if (filled >= max) {
            onMax.invoke(key)
            filled = max
        }
    }

    override fun getValue(key: K): BigInteger {
        return getOrElse(key) { BigInteger.ZERO }
    }

    override fun getValueMultiplier(key: K): Double {
        return getValue(key).doubleValue(false) / max.doubleValue(false)
    }

    override fun toMultiplierMap(): Map<K, Double> {
        return mapValues { it.value.doubleValue(false) / max.doubleValue(false) }
    }


    override fun split(value: BigInteger): Map<K, BigDecimal> {
        val decimal = BigDecimal.fromBigInteger(value)
        return mapValues {
            val percent = getValueMultiplier(it.key)
            decimal * BigDecimal.fromDouble(percent)
        }
    }

    override fun clear() {
        map.clear()
        filled = BigInteger.ZERO
    }


}