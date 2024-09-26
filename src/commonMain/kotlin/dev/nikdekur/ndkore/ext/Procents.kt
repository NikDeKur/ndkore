/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 * Copyright (c) 2024-present "Nik De Kur"
 */

@file:Suppress("NOTHING_TO_INLINE")

package dev.nikdekur.ndkore.ext

import kotlin.jvm.JvmInline
import kotlin.math.pow

@JvmInline
public value class Procent(public val procent: Double) {


    public operator fun plus(num: Number): Double {
        return num / 100 * (100 + procent)
    }

    public operator fun minus(num: Number): Double {
        return num / 100 * (100 - procent)
    }

    public operator fun times(num: Number): Double {
        return num / 100 * (100 * procent)
    }

    public operator fun div(num: Number): Double {
        return num / 100 * (100 / procent)
    }

    public fun pow(num: Number): Double {
        return (num / 100).pow(100 / procent)
    }


    public companion object {
        public fun of(procent: Number): Procent {
            return Procent(procent.toDouble())
        }

        public val P_0: Procent = of(0)
        public val P_1: Procent = of(1)
        public val P_2: Procent = of(2)
        public val P_3: Procent = of(3)
        public val P_4: Procent = of(4)
        public val P_5: Procent = of(5)
        public val P_6: Procent = of(6)
        public val P_7: Procent = of(7)
        public val P_8: Procent = of(8)
        public val P_9: Procent = of(9)
        public val P_10: Procent = of(10)
        public val P_11: Procent = of(11)
        public val P_12: Procent = of(12)
        public val P_13: Procent = of(13)
        public val P_14: Procent = of(14)
        public val P_15: Procent = of(15)
        public val P_16: Procent = of(16)
        public val P_17: Procent = of(17)
        public val P_18: Procent = of(18)
        public val P_19: Procent = of(19)
        public val P_20: Procent = of(20)

    }
}


public inline operator fun Number.plus(procent: Procent): Double {
    return procent + this
}

public inline operator fun Number.minus(procent: Procent): Double {
    return procent - this
}

public inline operator fun Number.times(procent: Procent): Double {
    return procent * this
}

public inline operator fun Number.div(procent: Procent): Double {
    return procent / this
}

public inline fun Number.pow(procent: Procent): Double {
    return procent.pow(this)
}

public inline fun Number.addProcent(value: Number): Double {
    return this + Procent.of(value)
}

public inline fun Number.minusProcent(value: Number): Double {
    return this - Procent.of(value)
}

public inline fun Number.timesProcent(value: Number): Double {
    return this * Procent.of(value)
}

public inline fun Number.divProcent(value: Number): Double {
    return this / Procent.of(value)
}

public inline fun Number.powProcent(value: Number): Double {
    return this.pow(Procent.of(value))
}

