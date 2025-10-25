/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 * Copyright (c) 2024-present "Nik De Kur"
 */

@file:OptIn(ExperimentalTime::class)

package dev.nikdekur.ndkore.cooldown

import kotlin.math.ln
import kotlin.time.Clock
import kotlin.time.Duration
import kotlin.time.ExperimentalTime

public abstract class GrowPolicy {

    public abstract val clock: Clock

    /**
     * The maximum step that can be reached.
     */
    public open val maxStep: Int = Int.MAX_VALUE

    public open val stepOnError: Int = 1

    /**
     * The step to set when the maximum step is reached.
     */
    public open val stepOnMax: Int = 1

    /**
     * Get the cooldown for the given step.
     *
     * @param step The step to get the cooldown for.
     * @return The cooldown for the given step.
     */
    public abstract fun getCooldown(step: Int): Duration

    public companion object {
        public fun linear(clock: Clock, base: Duration): GrowPolicy {
            return object : GrowPolicy() {
                override val clock = clock
                override val maxStep: Int = Int.MAX_VALUE / base.inWholeMilliseconds.toInt()
                override fun getCooldown(step: Int) = base * step
            }
        }

        public fun exponential(clock: Clock, base: Duration, maxStep: Int? = null): GrowPolicy {
            return object : GrowPolicy() {
                override val clock = clock
                override val maxStep: Int = maxStep ?: (ln(Int.MAX_VALUE.toDouble()) / ln(2.0)).toInt()
                override fun getCooldown(step: Int) = base * (1 shl step)
            }
        }

        public fun constant(clock: Clock, base: Duration): GrowPolicy {
            return object : GrowPolicy() {
                override val clock = clock
                override fun getCooldown(step: Int) = base
            }
        }
    }
}