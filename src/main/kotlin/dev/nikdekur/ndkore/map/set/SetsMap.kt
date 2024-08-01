/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 * Copyright (c) 2024-present "Nik De Kur"
 */

package dev.nikdekur.ndkore.map.set

interface SetsMap<K, V> : MutableMap<K, MutableSet<out V>> {

    /**
     * Get the set at the specified key.
     *
     * @param key the key whose associated set is to be returned
     * @return the set to which the specified key is associated, or `null` if this map contains no set for the key
     */
    fun getIfPresent(key: K): MutableSet<out V>?

    /**
     * Add the specified value to the set at the specified key.
     *
     * If a key is not already associated with a set, the method creates a new set and associates it with the key.
     *
     * @param key the key whose associated set is to be modified
     * @param value the value to be added to the set
     */
    fun add(key: K, value: V)

    /**
     * Delete the specified value from the set at the specified key.
     *
     * If key is not already associated with a set, the method does nothing.
     *
     * If the set does not contain the specified value, the method does nothing.
     *
     * @param key the key whose associated set is to be modified
     * @param value the value to be removed from the set
     */
    fun delete(key: K, value: V)

    /**
     * Check if the map contains the set at the specified key.
     *
     * Still returns `true` if the set is empty.
     * See [containsKey] to return true only if the set is not empty.
     *
     * @param key the key whose presence in this map is to be tested
     * @return `true` if the map contains the set at the specified key
     */
    fun containsSet(key: K): Boolean {
        return containsKey(key)
    }

    /**
     * Check if the map contains the set at the specified key and the set is not empty.
     *
     * @param key the key whose presence in this map is to be tested
     * @return `true` if the map contains the set at the specified key and the set is not empty
     */
    override fun containsKey(key: K): Boolean
}