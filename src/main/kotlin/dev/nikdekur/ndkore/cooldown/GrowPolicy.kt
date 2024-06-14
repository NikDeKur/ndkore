package dev.nikdekur.ndkore.cooldown

import kotlin.math.ln

abstract class GrowPolicy {

    /**
     * The maximum step that can be reached.
     */
    open val maxStep: Int = Int.MAX_VALUE

    open val stepOnError: Int = 1

    /**
     * The step to set when the maximum step is reached.
     */
    open val stepOnMax: Int = 1

    /**
     * Get the cooldown for the given step.
     *
     * @param step The step to get the cooldown for.
     * @return The cooldown for the given step.
     */
    abstract fun getCooldown(step: Int): Cooldown

    companion object {
        fun linear(base: Cooldown): GrowPolicy {
            return object : GrowPolicy() {
                override val maxStep: Int = Int.MAX_VALUE / base.duration.toInt()
                override fun getCooldown(step: Int): Cooldown {
                    return Cooldown(base.duration * step, base.unit)
                }
            }
        }

        fun exponential(base: Cooldown, maxStep: Int? = null): GrowPolicy {
            return object : GrowPolicy() {
                override val maxStep: Int = maxStep ?: (ln(Int.MAX_VALUE.toDouble()) / ln(2.0)).toInt()
                override fun getCooldown(step: Int): Cooldown {
                    return Cooldown(base.duration shl step, base.unit)
                }
            }
        }

        fun constant(base: Cooldown): GrowPolicy {
            return object : GrowPolicy() {
                override fun getCooldown(step: Int): Cooldown {
                    return base
                }
            }
        }
    }
}