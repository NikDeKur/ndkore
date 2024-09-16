/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 * Copyright (c) 2024-present "Nik De Kur"
 */

@file:Suppress("NOTHING_TO_INLINE")

package dev.nikdekur.ndkore.placeholder

/**
 * A functional interface for retrieving values based on a given key from a source.
 *
 * ## Overview
 * The `Placeholder` interface is designed to provide a way to access values using keys, which is useful in
 * scenarios where you need to dynamically retrieve values based on variable input.
 * This can be particularly
 * beneficial in template engines, configuration management, and other contexts where key-value lookups are common.
 *
 * ## Key Concepts
 * - **Dynamic Value Retrieval**: Allows retrieving values from a source like a map using a key.
 * - **Flexibility**: Can be implemented in various ways to source values from different data structures.
 *
 * ## Practical Use Cases
 * - **Template Engines**: Replacing placeholders in templates with dynamic values.
 * - **Configuration Management**: Accessing configuration values based on keys.
 *
 * ## Companion Object
 * The companion object provides factory methods to create instances of `Placeholder`:
 * - `of(map: Map<String, Any>)`: Creates a `Placeholder` implementation that retrieves values from a given map.
 * - `of(vararg pair: Pair<String, Any>)`: Allows creating a `Placeholder` from a list of key-value pairs,
 * making it convenient to initialize with multiple entries.
 * - `ofSingle(key: String, value: Any)`: Quickly creates a `Placeholder` for a single key-value pair.
 *
 * ## Examples
 *
 * ### Example 1: Creating a Placeholder from a Map
 * ```kotlin
 * val placeholder = Placeholder.of(mapOf("name" to "John Doe"))
 * println(placeholder.getPlaceholder("name")) // Output: John Doe
 * ```
 *
 * ### Example 2: Creating a Placeholder from Key-Value Pairs
 * ```kotlin
 * val placeholder = Placeholder.of("greeting" to "Hello")
 * println(placeholder.getPlaceholder("greeting")) // Output: Hello
 * ```
 */
interface Placeholder {

    /**
     * Retrieves the value associated with the specified key from the source.
     *
     * @param key The key whose associated value is to be retrieved.
     * @return The value associated with the key, or [ValuesSource.NotFound] if the key does not exist in the source.
     */
    fun getPlaceholder(key: String): Any? {
        return ValuesSource.NotFound
    }

    companion object {
        /**
         * Creates a `Placeholder` implementation that uses the provided map for value retrieval.
         *
         * @param map A map where keys are placeholder names and values are the corresponding values.
         * @return A `Placeholder` instance backed by the map.
         */
        @JvmStatic
        fun of(map: Map<String, Any>): Placeholder {
            return object : Placeholder {
                override fun getPlaceholder(key: String): Any? {
                    return map[key]
                }
                override fun toString(): String {
                    return "Placeholder(map=$map)"
                }
            }
        }

        /**
         * Creates a `Placeholder` implementation from a variable number of key-value pairs.
         *
         * @param pair A list of key-value pairs to be used for value retrieval.
         * @return A `Placeholder` instance initialized with the provided pairs.
         */
        @JvmStatic
        inline fun of(vararg pair: Pair<String, Any>): Placeholder {
            return of(mapOf(*pair))
        }

        /**
         * Creates a `Placeholder` implementation for a single key-value pair.
         *
         * @param key The key for which the value is provided.
         * @param value The value associated with the key.
         * @return A `Placeholder` instance for the single key-value pair.
         */
        @JvmStatic
        inline fun ofSingle(key: String, value: Any): Placeholder {
            return of(key to value)
        }
    }
}
