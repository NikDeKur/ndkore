/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 * Copyright (c) 2024-present "Nik De Kur"
 */

@file:Suppress("NOTHING_TO_INLINE")

package dev.nikdekur.ndkore.placeholder

import dev.nikdekur.ndkore.reflect.ReflectMethod
import dev.nikdekur.ndkore.reflect.ReflectMethod.NotFound

/**
 * A concrete implementation of `PlaceholderParser` that uses regular expressions to find and replace placeholders
 * within a string.
 *
 * ## Overview
 * `PatternPlaceholderParser` is a versatile parser that uses regular expressions to identify placeholders in
 * a string and replace them with values from a provided map.
 * It is designed to handle various placeholder formats
 * and can be customized using different regular expression patterns.
 *
 * ## Constructors
 * - `PatternPlaceholderParser(pattern: Pattern)`
 *   - Initializes the parser with a custom regular expression pattern for identifying placeholders.
 * - `PatternPlaceholderParser(symbolLeft: String, symbolRight: String)`
 *   - Initializes the parser with custom symbols for the start and end of placeholders (e.g., `{` and `}`).
 * - `PatternPlaceholderParser(symbol: String)`
 *   - Initializes the parser with the same symbol for both the start and end of placeholders (e.g., `%`).
 *
 * ## Companion Object
 * - `HASH`, `PROCENT`, `CURLY_BRACKET`
 *   - Predefined instances of `PatternPlaceholderParser` with common placeholder formats: `#`, `%`, and `{}`.
 * - `findValue(obj: Any, valueName: String): Any?`
 *   - Utility function to find a value within an object based on a specified name using reflection or direct lookup.
 *   - Useful for retrieving nested values or values from complex objects.
 *
 * ## Practical Use Cases
 * - **Dynamic Content Generation**: Replacing placeholders in dynamic content such as HTML or configuration strings.
 * - **Template Processing**: Supporting flexible and customizable templates with various placeholder formats.
 *
 * ## Design Considerations
 * - **Pattern Customization**: Allows flexible configuration of placeholder patterns to suit different needs.
 * - **Performance**: Uses regular expressions for efficient matching and replacement.
 * - **Error Handling**:
 * Ensure that placeholder patterns and provided data are correctly defined to avoid parsing errors.
 *
 *
 * ## Examples
 *
 * ### Example 1: Parsing with `#` Placeholders
 * ```kotlin
 * val parser = PatternPlaceholderParser.HASH
 * val result = parser.parse("#name# is #age# years old", mapOf("name" to "Alice", "age" to 25))
 * println(result) // Output: Alice is 25 years old
 * ```
 *
 * ### Example 2: Parsing with Nested Placeholders
 * ```kotlin
 * val parser = PatternPlaceholderParser.CURLY_BRACKET
 * val result = parser.parse("{user.name} is {user.age} years old", mapOf(
 *     "user" to Placeholder.of("name" to "Bob", "age" to 30)
 * ))
 * println(result) // Output: Bob is 30 years old
 * ```
 */
public open class PatternPlaceholderParser(
    public val pattern: Regex,
    public val source: ReflectMethod
) : PlaceholderParser {

    public constructor(
        symbolLeft: String,
        symbolRight: String,
        source: ReflectMethod
    ) : this(Regex("$symbolLeft(.*?)$symbolRight"), source)

    public constructor(symbol: String, source: ReflectMethod) : this(symbol, symbol, source)

    override fun parseExpression(path: String, placeholders: Map<String, Any?>): String? {
        if (placeholders.isEmpty()) return null

        val parts = path.split(".")
        var currentObject: Any? = placeholders[parts[0]] ?: return null

        if (parts.size == 1)
            return (currentObject as? Iterable<*>)
                ?.firstOrNull()
                ?.toString()
                ?: currentObject.toString()


        for (partI in 1 until parts.size) {
            val part = parts[partI]
            currentObject = source.findValue(currentObject!!, part)
            if (currentObject == NotFound) return path
        }

        return currentObject.toString()
    }

    override fun parse(pattern: String, placeholders: Map<String, Any?>): String {
        if (placeholders.isEmpty()) return pattern

        val sb = StringBuilder()
        var lastIndex = 0

        this@PatternPlaceholderParser.pattern.findAll(pattern).forEach { match ->
            sb.append(pattern.substring(lastIndex, match.range.first))

            val pathRaw = match.groupValues[1]
            val value = parseExpression(pathRaw, placeholders)
            sb.append(value ?: match.value)

            lastIndex = match.range.last + 1
        }

        // Append the remaining part of the string
        if (lastIndex < pattern.length)
            sb.append(pattern.substring(lastIndex))

        return sb.toString()
    }
}