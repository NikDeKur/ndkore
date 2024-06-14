@file:Suppress("NOTHING_TO_INLINE")

package dev.nikdekur.ndkore.ext

@JvmInline
value class Procent(val procent: Double) {


    operator fun plus(num: Number): Double { return num / 100 * (100 + procent) }
    operator fun minus(num: Number): Double { return num / 100 * (100 - procent) }
    operator fun times(num: Number): Double { return num / 100 * (100 * procent) }
    operator fun div(num: Number): Double { return num / 100 * (100 / procent) }

    fun pow(num: Number): Double { return (num / 100).pow((100 / procent)) }


    companion object {
        @JvmStatic
        fun of(procent: Number): Procent {
            return Procent(procent.toDouble())
        }

        val P_0 = of(0)
        val P_1 = of(1)
        val P_2 = of(2)
        val P_3 = of(3)
        val P_4 = of(4)
        val P_5 = of(5)
        val P_6 = of(6)
        val P_7 = of(7)
        val P_8 = of(8)
        val P_9 = of(9)
        val P_10 = of(10)
        val P_11 = of(11)
        val P_12 = of(12)
        val P_13 = of(13)
        val P_14 = of(14)
        val P_15 = of(15)
        val P_16 = of(16)
        val P_17 = of(17)
        val P_18 = of(18)
        val P_19 = of(19)
        val P_20 = of(20)

    }
}



inline operator fun Number.plus(procent: Procent): Double   { return procent + this }
inline operator fun Number.minus(procent: Procent): Double  { return procent - this }
inline operator fun Number.times(procent: Procent): Double  { return procent * this }
inline operator fun Number.div(procent: Procent): Double    { return procent / this }
inline fun Number.pow(procent: Procent): Double    { return procent.pow(this) }

inline fun Number.addProcent(value: Number): Double    { return this + Procent.of(value) }
inline fun Number.minusProcent(value: Number): Double  { return this - Procent.of(value) }
inline fun Number.timesProcent(value: Number): Double  { return this * Procent.of(value) }
inline fun Number.divProcent(value: Number): Double    { return this / Procent.of(value) }
inline fun Number.powProcent(value: Number): Double    { return this.pow(Procent.of(value)) }

