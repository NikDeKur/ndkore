/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 * Copyright (c) 2024-present "Nik De Kur"
 */

@file:Suppress("NOTHING_TO_INLINE", "KDocUnresolvedReference", "unused")

package dev.nikdekur.ndkore.ext

import dev.nikdekur.ndkore.`interface`.Snowflake

/**
 * Removes entries from this mutable map if they satisfy the given predicate.
 *
 * The predicate takes a key-value pair and returns true if the entry should be removed. This method
 * iterates over the entries of the map and applies the predicate to each entry, removing those that match.
 * This allows for flexible and efficient bulk removal based on custom criteria.
 *
 * @param predicate a function that evaluates each key-value pair and returns true for entries to be removed.
 */
@Suppress("kotlin:S6524")
public inline fun <K, V> MutableMap<K, V>.removeIf(predicate: (K, V) -> Boolean) {
    val iterator = entries.iterator()
    while (iterator.hasNext()) {
        val entry = iterator.next()
        if (predicate(entry.key, entry.value)) {
            iterator.remove()
        }
    }
}

/**
 * Retrieves the value associated with the given key and applies a mapping function to it.
 *
 * If the key is present in the map, the associated value is passed to the ifNotNullMapper function
 * and the result is returned. If the key is not present, null is returned. This method provides a convenient
 * way to transform values while retrieving them from the map.
 *
 * @param key the key whose associated value is to be retrieved and mapped.
 * @param ifNotNullMapper a function to apply to the value if the key is present.
 * @return the result of applying the mapping function to the value, or null if the key is not present.
 */
public inline fun <K, V, NV> Map<K, V>.getAndMap(key: K, ifNotNullMapper: (V) -> NV): NV? {
    val v = this[key] ?: return null
    return ifNotNullMapper.invoke(v)
}

/**
 * Retrieves the value associated with the given key as a String, or returns the default value if the key is not present.
 *
 * This method attempts to cast the value associated with the given key to a String. If the key is not present
 * or the value cannot be cast to a String, the specified default value is returned. This is useful for maps
 * where values are expected to be strings but might be missing or of a different type.
 *
 * @param key the key whose associated value is to be returned as a String.
 * @param default the default value to return if the key is not present or the value cannot be cast to a String.
 * @return the value associated with the key as a String, or the default value.
 */
public inline fun <K> Map<K, *>.getString(key: K, default: String?): String? {
    return getAndMap(key) { it as String } ?: default
}

/**
 * Retrieves the value associated with the given key as a String, or throws an exception if the key is not present.
 *
 * This method attempts to cast the value associated with the given key to a String. If the key is not present
 * or the value cannot be cast to a String, a NoSuchElementException is thrown. This is useful for cases where
 * a missing or invalid value should result in an immediate error.
 *
 * @param key the key whose associated value is to be returned as a String.
 * @return the value associated with the key as a String.
 * @throws NoSuchElementException if the key is not present or the value cannot be cast to a String.
 */
public inline fun <K> Map<K, *>.getStringOrThrow(key: K): String {
    return getString(key, null) ?: throw NoSuchElementException(key.toString())
}

public inline fun <K, V> entry(key: K, value: V): Map.Entry<K, V> = object : Map.Entry<K, V> {
    override val key = key
    override val value = value
}

/**
 * Transforms this map into a new mutable map using the specified key and value mappers.
 *
 * The keyMapper function is applied to each entry to produce the new keys, and the valueMapper function is applied
 * to produce the new values. Entries for which the key or value mapper returns null are skipped. This allows for
 * flexible transformation of maps, including filtering and changing the types of keys and values.
 *
 * @param keyMapper a function that maps entries to new keys.
 * @param valueMapper a function that maps entries to new values.
 * @param mapGen a function to generate the resulting mutable map.
 * @return a new mutable map with the transformed keys and values.
 */
public inline fun <K, V, NK, NV> Map<K, V>.map(
    keyMapper: (Map.Entry<K, V>) -> NK?,
    valueMapper: (Map.Entry<NK, V>) -> NV?,
    mapGen: () -> MutableMap<NK, NV> = ::HashMap,
): MutableMap<NK, NV> {

    val newMap = mapGen()
    for ((key, value) in this) {
        val keyEntry = entry(key, value)
        val newKey = keyMapper(keyEntry) ?: continue
        val valEntry = entry(newKey, value)
        val newValue = valueMapper(valEntry) ?: continue
        newMap[newKey] = newValue
    }
    return newMap
}




/**
 * Extension function for removing empty maps and collections from an Iterable.
 *
 * Returns a new list with empty maps and collections removed.
 *
 * @param maps If true, empty maps will be removed. Default is true.
 * @param collections If true, empty collections will be removed. Default is true.
 * @return A new list with empty maps and collections removed.
 */
public fun <T> Iterable<T>.removeEmpty(maps: Boolean = true, collections: Boolean = true): List<T> {
    if (!maps && !collections) return this.toList()

    val result = mutableListOf<T>()
    for (element in this) {
        when {
            maps && element is Map<*, *> -> {
                val cleanedMap = element.removeEmpty(true, collections)
                if (cleanedMap.isNotEmpty()) {
                    @Suppress("UNCHECKED_CAST")
                    result.add(cleanedMap as T)
                }
            }
            collections && element is Iterable<*> -> {
                val cleanedCollection = element.removeEmpty(maps, true)
                if (cleanedCollection.isNotEmpty()) {
                    @Suppress("UNCHECKED_CAST")
                    result.add(cleanedCollection as T)
                }
            }
            else -> result.add(element)
        }
    }
    return result
}

/**
 * Extension function for removing empty maps and collections from a Map.
 *
 * Returns a new map with empty maps and collections removed.
 *
 * @param maps If true, empty maps will be removed. Default is true.
 * @param collections If true, empty collections will be removed. Default is true.
 * @return A new map with empty maps and collections removed.
 */
public fun <K, V> Map<K, V>.removeEmpty(
    maps: Boolean = true,
    collections: Boolean = true
): Map<K, V> {
    if (!maps && !collections) return this

    val result = mutableMapOf<K, V>()
    for ((key, value) in this) {
        when {
            maps && value is Map<*, *> -> {
                val cleanedMap = value.removeEmpty(true, collections)
                if (cleanedMap.isNotEmpty()) {
                    @Suppress("UNCHECKED_CAST")
                    result[key] = cleanedMap as V
                }
            }
            collections && value is Iterable<*> -> {
                val cleanedCollection = value.removeEmpty(maps, true)
                if (cleanedCollection.isNotEmpty()) {
                    @Suppress("UNCHECKED_CAST")
                    result[key] = cleanedCollection as V
                }
            }
            else -> result[key] = value
        }
    }
    return result
}



/**
 * Applicable to MutableMap, where value is Snowflake of a type same as a key
 * Add value to map by its id
 *
 * @param obj value to add
 * @return previous value associated with the key, or null if there was no mapping for the key
 */
public inline fun <IdT, V : Snowflake<IdT>> MutableMap<IdT, V>.addById(obj: V): V? {
    return put(obj.id, obj)
}

/**
 * Applicable to MutableMap, where value is Snowflake of a type same as a key
 * Add all values to map by their ids
 *
 * @param objs values to add
 */
public inline fun <IdT, V : Snowflake<IdT>> MutableMap<IdT, V>.addAllById(objs: Iterable<V>) {
    objs.forEach {
        addById(it)
    }
}

/**
 * Applicable to MutableMap, where value is Snowflake of a type same as a key
 * Remove value from map by its id
 *
 * @param obj value to remove
 * @return previous value associated with the key, or null if there was no mapping for the key
 */
public inline fun <IdT, V : Snowflake<IdT>> MutableMap<IdT, V>.removeById(obj: V): V? {
    return remove(obj.id)
}


/**
 * Checks if this map contains the specified object by its ID.
 *
 * This method looks up the key in the map using the ID of the provided object.
 * It is useful when you have objects implementing the Snowflake interface, which provides an ID,
 * and you want to check for their presence in a map by their IDs.
 *
 * @param obj the object whose ID is used to check for presence in the map.
 * @return true if the map contains the key corresponding to the object's ID, false otherwise.
 */
public inline fun <IdT, V : Snowflake<IdT>> Map<IdT, V>.containsById(obj: V): Boolean {
    return containsKey(obj.id)
}

/**
 * Finds the first map entry that matches the given predicate.
 *
 * This method iterates over the entries of the map and returns the first one that satisfies the predicate.
 * It is useful for finding specific entries based on custom criteria.
 *
 * @param predicate a function that evaluates each entry and returns true for the desired entry.
 * @return the first map entry that matches the predicate.
 * @throws NoSuchElementException if no entry matches the predicate.
 */
public inline fun <K, V> Map<K, V>.firstEntry(predicate: Map.Entry<K, V>.() -> Boolean) = entries.first(predicate)

/**
 * Finds the first map entry that matches the given predicate or returns null if none match.
 *
 * This method iterates over the entries of the map and returns the first one that satisfies the predicate,
 * or null if no entry matches. It is useful for safely finding specific entries without throwing exceptions.
 *
 * @param predicate a function that evaluates each entry and returns true for the desired entry.
 * @return the first map entry that matches the predicate, or null if no entry matches.
 */
public inline fun <K, V> Map<K, V>.firstEntryOrNull(predicate: Map.Entry<K, V>.() -> Boolean) =
    entries.firstOrNull(predicate)

/**
 * Finds the first key that matches the given predicate.
 *
 * This method iterates over the keys of the map and returns the first one that satisfies the predicate.
 * It is useful for finding specific keys based on custom criteria.
 *
 * @param predicate a function that evaluates each key and returns true for the desired key.
 * @return the first key that matches the predicate.
 * @throws NoSuchElementException if no key matches the predicate.
 */
public inline fun <K, V> Map<K, V>.firstKey(predicate: (K) -> Boolean) = keys.first(predicate)

/**
 * Finds the first key that matches the given predicate or returns null if none match.
 *
 * This method iterates over the keys of the map and returns the first one that satisfies the predicate,
 * or null if no key matches. It is useful for safely finding specific keys without throwing exceptions.
 *
 * @param predicate a function that evaluates each key and returns true for the desired key.
 * @return the first key that matches the predicate, or null if no key matches.
 */
public inline fun <K, V> Map<K, V>.firstKeyOrNull(predicate: (K) -> Boolean) = keys.firstOrNull(predicate)

/**
 * Finds the first value that matches the given predicate.
 *
 * This method iterates over the values of the map and returns the first one that satisfies the predicate.
 * It is useful for finding specific values based on custom criteria.
 *
 * @param predicate a function that evaluates each value and returns true for the desired value.
 * @return the first value that matches the predicate.
 * @throws NoSuchElementException if no value matches the predicate.
 */
public inline fun <K, V> Map<K, V>.firstValue(predicate: (V) -> Boolean) = values.first(predicate)

/**
 * Finds the first value that matches the given predicate or returns null if none match.
 *
 * This method iterates over the values of the map and returns the first one that satisfies the predicate,
 * or null if no value matches. It is useful for safely finding specific values without throwing exceptions.
 *
 * @param predicate a function that evaluates each value and returns true for the desired value.
 * @return the first value that matches the predicate, or null if no value matches.
 */
public inline fun <K, V> Map<K, V>.firstValueOrNull(predicate: (V) -> Boolean) = values.firstOrNull(predicate)


/**
 * Similar to [Map.all] but accept kotlin style lambda
 *
 * Could be very useful, when you need to pass existing function to [Map.all] but it requires function, that takes [Map.Entry], not [K] and [V]
 */
public inline fun <K, V> Map<K, V>.all(action: Map.Entry<K, V>.() -> Boolean): Boolean {
    for (entry in this) {
        if (!action(entry)) return false
    }
    return true
}

/**
 * Similar to [Map.any] but accept kotlin style lambda
 *
 * Could be very useful, when you need to pass existing function to [Map.any] but it requires function, that takes [Map.Entry], not [K] and [V]
 */
public inline fun <K, V> Map<K, V>.any(action: Map.Entry<K, V>.() -> Boolean): Boolean {
    for (entry in this) {
        if (action(entry)) return true
    }
    return false
}


/**
 * Get nested value from a map
 *
 * Will split key by separator and get value from nested maps step by step
 *
 * If any of the keys is not found, will return null
 *
 * Example:
 * - map: { "a": { "b": { "c": 1 } } }
 * - key: listOf("a", "b", "c")
 * - result: 1
 *
 * @param key key to get
 * @param separator separator to split key
 * @return value or null if not found
 */
@Suppress("UNCHECKED_CAST")
public fun Map<String, Any>.getNested(keys: Iterable<String>): Any? {
    var current: Any? = this
    for (key in keys) {
        if (current !is Map<*, *>) return null
        current = (current as Map<String, Any>)[key]
    }
    return current
}

/**
 * Call [block] for each entry in the map and clear the map
 *
 * @param block block to call for each entry
 */
public inline fun <K, V> MutableMap<K, V>.clear(block: Map.Entry<K, V>.() -> Unit) {
    forEach {
        block(it)
    }
    clear()
}


/**
 * Creates a map from the elements of this iterable using the provided key and value functions.
 *
 * @param map the map to populate with the elements of this iterable.
 * @param key a function that maps an element to a key.
 * @param value a function that maps an element to a value.
 * @return the populated map.
 */
public inline fun <O, K, V> Iterable<O>.toMapTo(
    map: MutableMap<K, V>,
    key: (O) -> K,
    value: (O) -> V,
): Map<K, V> {
    for (element in this) {
        map[key(element)] = value(element)
    }
    return map
}


public inline fun <O, K, V> Iterable<O>.toMap(
    key: (O) -> K,
    value: (O) -> V,
): Map<K, V> {
    return toMapTo(mutableMapOf(), key, value)
}