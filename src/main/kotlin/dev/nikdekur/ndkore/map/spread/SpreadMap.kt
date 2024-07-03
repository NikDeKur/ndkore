/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 * Copyright (c) 2024 Nik De Kur
 */

package dev.nikdekur.ndkore.map.spread

/**
 * SpreadMap is a map that stores values up to a certain sum of values.
 *
 * SpreadMap could be very useful in some games, where you need to split a reward between multiple players by some rules.
 *
 * Example:
 * ```
 * val map = SpreadBigIntegerMap<Player>(BigInteger("1000")) { player ->
 *     player.sendMessage("You have killed the boss!")
 *     boss.death()
 * }
 *
 * map.register(player1, BigInteger("500")) // First player attacked the boss with 500 damage
 * map.register(player2, BigInteger("250")) // Second player attacked the boss with 300 damage
 * map.register(player2, BigInteger("50"))  // Second player attacked the boss again, with 50 damage
 * map.register(player3, BigInteger("400")) // Third player attacked the boss with 400 damage
 * // After the sum of all values exceeds 1000, the onMax action will be called
 *
 * // Split 1000 coins between players
 * map.split(BigInteger("1000")) {
 *    player, value -> player.giveCoins(value)
 * }
 * // player1 will receive 500 coins
 * // player2 will receive 300 coins
 * // player3 will receive 200 coins, not 400, because the boss has only 1000 health
 * ```
 */
interface SpreadMap<K, V> : Map<K, V> {

    /**
     * Maximum value that could be stored in the map.
     *
     * If the sum of all values exceeds this value, the [onMax] action will be called.
     */
    val max: () -> V

    /**
     * Sum of all values in the map.
     *
     * Cannot be greater than the [max] value.
     */
    val filled: V

    /**
     * Number of values that could be added to the map.
     *
     * Usually its [max] - [filled]
     */
    val left: V

    /**
     * Action that will be called when the sum of all values exceeds the [max] value.
     */
    val onMax: (K) -> Unit

    /**
     * Register a new value to the map.
     *
     * If some previous value was registered with the same key, the new value will be added to the previous one.
     *
     * @param key Key of the value
     * @param value Value to register
     * @see getValue
     */
    fun register(key: K, value: V)

    /**
     * Check if the map is full.
     *
     * @return true if the sum of all values is equal or greater than the [max] value
     * @see max
     */
    val isDone: Boolean

    /**
     * Clear all values from the map.
     */
    fun clear()

    /**
     * Get the value by the key.
     *
     * If the key is not present in the map, the default value will be returned.
     *
     * @param key Key of the value
     */
    fun getValue(key: K): V

    /**
     * Get the value by the key as a percentage (0-100) of the [max] value.
     *
     * Usually its getValue(key) / max
     */
    fun getValuePercent(key: K): Double

    /**
     * Get the value by the key as a percentage (0-1) of the [max] value.
     *
     * Usually its getValuePercent(key) / 100
     */
    fun getValueMultiplier(key: K): Double

    /**
     * Returns a map with keys and values as a percentage (0-100) of the [max] value.
     */
    fun toPercent(): Map<K, Double>

    /**
     * Returns a map with keys and values as a multipliers (0-1) of the [max] value.
     */
    fun toMultiplier(): Map<K, Double>

    /**
     * Split the value between all keys in the map.
     *
     * The value will be split by the percentage of the [max] value.
     */
    fun split(value: V): Map<K, V>

    /**
     * Split the value between all keys in the map and call the action for each key.
     *
     * The value will be split by the percentage of the [max] value.
     */
    fun split(value: V, action: (K, V) -> Unit) {
        split(value).forEach(action)
    }
}