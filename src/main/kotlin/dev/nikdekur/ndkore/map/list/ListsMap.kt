/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 * Copyright (c) 2024-present "Nik De Kur"
 */

package dev.nikdekur.ndkore.map.list

interface ListsMap<K, V> : MutableMap<K, MutableList<out V>> {

    /**
     * Get the list at the specified key.
     *
     * @param key the key whose associated list is to be returned
     * @return the list to which the specified key is associated, or `null` if this map contains no list for the key
     */
    fun getIfPresent(key: K): MutableList<out V>?

    /**
     * Add the specified value to the list at the specified key.
     *
     * If key is not already associated with a list, the method creates a new list and associates it with the key.
     *
     * @param key the key whose associated list is to be modified
     * @param value the value to be added to the list
     */
    fun add(key: K, value: V)

    /**
     * Delete the specified value from the list at the specified key.
     *
     * If key is not already associated with a list, the method does nothing.
     *
     * If the list does not contain the specified value, the method does nothing.
     *
     * @param key the key whose associated list is to be modified
     * @param value the value to be removed from the list
     */
    fun delete(key: K, value: V)

    /**
     * Check if the map contains the list at the specified key.
     *
     * Still returns `true` if the list is empty.
     * See [containsKey] to return true only if the list is not empty.
     *
     * @param key the key whose presence in this map is to be tested
     * @return `true` if the map contains the list at the specified key
     */
    fun containsList(key: K): Boolean {
        return containsKey(key)
    }

    /**
     * Check if the map contains the list at the specified key and the list is not empty.
     *
     * @param key the key whose presence in this map is to be tested
     * @return `true` if the map contains the list at the specified key and the list is not empty
     */
    override fun containsKey(key: K): Boolean
}