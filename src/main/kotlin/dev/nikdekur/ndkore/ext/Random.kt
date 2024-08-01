/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 * Copyright (c) 2024-present "Nik De Kur"
 */

@file:Suppress("NOTHING_TO_INLINE")

package dev.nikdekur.ndkore.ext

import java.util.*

/**
 * Returns a random number between 0.0 and 1.0.
 */
inline fun Random.randomPercent(): Double {
    return nextDouble()
}

/**
 * Returns a random number between 0.0 and 100.0.
 */
inline fun Random.randomBigPercent(): Double {
    return nextDouble() * 100
}

/**
 * Returns a random integer between [min] and [max].
 *
 * @param min the minimum value
 * @param max the maximum value
 * @return the random integer
 */
inline fun Random.randInt(min: Int, max: Int): Int {
    if (min == max) return min
    require(min <= max) { "max must be greater than min"}
    return nextInt(max - min + 1) + min
}

/**
 * Returns a random double between [min] and [max].
 *
 * @param min the minimum value
 * @param max the maximum value
 * @return the random double
 */
inline fun Random.randDouble(min: Double, max: Double): Double {
    require(min <= max) { "max must be greater than min"}
    return nextDouble() * (max - min) + min
}

/**
 * Returns a random float between [min] and [max].
 *
 * @param min the minimum value
 * @param max the maximum value
 * @return the random float
 */
inline fun Random.randFloat(min: Float, max: Float): Float {
    require(min <= max) { "max must be greater than min" }
    return nextFloat() * (max - min) + min
}


/**
 * Returns true with a chance of [chance].
 *
 * @param chance the chance of returning true (0.0-100.0)
 * @return true with a chance of [chance]
 */
inline fun Random.chance(chance: Double): Boolean {
    require(chance in 0.0..100.0) { "chance must be between 0.0 and 100.0" }
    return randomBigPercent() < chance
}

/**
 * Executes [block] with a chance of [chance].
 *
 * @param chance the chance of executing [block] (0.0-100.0)
 * @param block the block to execute
 */
inline fun Random.chance(chance: Double, block: () -> Unit) {
    if (chance(chance)) block()
}