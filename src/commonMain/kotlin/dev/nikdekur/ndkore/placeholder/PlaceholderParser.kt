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
 * Interface for parsing strings with embedded placeholders using a map of values.
 *
 * # Overview
 * `PlaceholderParser` defines methods to replace placeholders in strings with actual values from a map. This
 * is essential for scenarios where you have dynamic strings or templates that need to be populated with values
 * at runtime.
 *
 * ## Key Concepts
 * - **Expression Parsing**: Supports extracting and replacing placeholders within expressions.
 * - **String Replacement**: Handles the replacement of placeholders throughout the entire string.
 *
 * ## Practical Use Cases
 * - **Configuration Files**: Replacing placeholders in configuration files with actual values.
 * - **Templates**: Generating dynamic content based on placeholder values.
 *
 * ## Examples
 *
 * ### Example 1: Parsing a String with Single Placeholder
 * ```kotlin
 * val parser = PatternPlaceholderParser.HASH
 * val result = parser.parse("#name#", mapOf("name" to "John Doe"))
 * println(result) // Output: John Doe
 * ```
 *
 * ### Example 2: Parsing a String with Multiple Placeholders
 * ```kotlin
 * val parser = PatternPlaceholderParser.PROCENT
 * val result = parser.parse("Hello, %name%! Your balance is %balance%.", mapOf("name" to "Jane", "balance" to "100"))
 * println(result) // Output: Hello, Jane! Your balance is 100.
 * ```
 */
public interface PlaceholderParser {

    /**
     * Parses an expression containing placeholders and returns the resolved value as a string.
     *
     * @param path The path to parse, which can be a simple key or a complex nested expression.
     * @param placeholders A map containing key-value pairs where keys are placeholder names and values are
     *                      the corresponding replacement values.
     * @return The resolved value as a string, or null if the path could not be resolved.
     */
    public fun parseExpression(path: String, placeholders: Map<String, Any?>): String?

    /**
     * Parses the entire string with placeholders, replacing them with their corresponding values from the map.
     *
     * @param pattern The string to parse, which contains placeholders to be replaced.
     * @param placeholders A map of key-value pairs where keys are placeholder names and values are their replacements.
     * @return The string with all placeholders replaced with their values.
     */
    public fun parse(pattern: String, placeholders: Map<String, Any?>): String
}

/**
 * Extension function for `PlaceholderParser` that converts variable arguments of key-value pairs into a map
 * and then parses the string with those placeholders.
 *
 * @param pattern The string with placeholders to be replaced.
 * @param placeholders A variable number of key-value pairs where keys are placeholder names and values are
 *                      their replacements.
 * @return The string with placeholders replaced based on the provided pairs.
 */
public inline fun PlaceholderParser.parse(pattern: String, vararg placeholders: Pair<String, Any?>): String {
    return parse(pattern, placeholders.toMap())
}
