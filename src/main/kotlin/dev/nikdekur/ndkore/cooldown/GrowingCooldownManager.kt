/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 * Copyright (c) 2024 Nik De Kur
 */

package dev.nikdekur.ndkore.cooldown

/**
 * Cooldown manager with growing cooldowns.
 *
 * This class is used to manage cooldowns that grow with each step.
 *
 * It Could be used to reconnection cooldowns, where the cooldown grows with each failed attempt.
 *
 * @param policy The policy to use for growing cooldowns.
 * @param onCooldownUpdate The callback to call when the cooldown is updated.
 */
class GrowingCooldownManager<K>(
    val policy: GrowPolicy,
    val onCooldownUpdate: (K, Cooldown) -> Unit
) {
    private val steps = mutableMapOf<K, Int>()
    // Key to cooldown end time (ms)
    val cooldowns = mutableMapOf<K, Long>()

    private fun increaseStep(key: K): Int {
        var step = steps.getOrDefault(key, 0) + 1
        if (step < policy.maxStep) {
            steps[key] = step
        } else {
            steps.remove(key)
            step = policy.stepOnMax
        }
        return step
    }

    /**
     * Get the cooldown for the given key and step.
     *
     * If an exception is thrown while getting the cooldown for the step, the step is set to 1 and the cooldown is fetched for the stepOnError.
     *
     * If some error occurs while fetching the cooldown for the stepOnError, the cooldown is set to 1 minute.
     *
     * @param k The key to get the cooldown for.
     * @param step The step to get the cooldown for.
     * @return The cooldown for the given key and step.
     */
    fun getCooldown(k: K, step: Int): Cooldown {
        return try {
            policy.getCooldown(step)
        } catch (e: Exception) {
            val stepOnError = policy.stepOnError
            steps[k] = stepOnError
            try {
                policy.getCooldown(stepOnError)
            } catch (e: Exception) {
                Cooldown.ONE_MINUTE
            }
        }
    }

    fun step(key: K) {
        val step = increaseStep(key)
        val cooldown = getCooldown(key, step)
        cooldowns[key] = System.currentTimeMillis() + cooldown.toMillis()
        onCooldownUpdate(key, cooldown)
    }

    fun resetStep(key: K) {
        steps.remove(key)
    }

    fun hasCooldown(key: K): Boolean {
        val cooldown = cooldowns[key] ?: return false
        if (System.currentTimeMillis() > cooldown) {
            cooldowns.remove(key)
            return false
        }
        return true
    }




}