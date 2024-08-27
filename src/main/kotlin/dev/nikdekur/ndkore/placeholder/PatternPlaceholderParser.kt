/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 * Copyright (c) 2024-present "Nik De Kur"
 */

@file:Suppress("NOTHING_TO_INLINE")

package dev.nikdekur.ndkore.placeholder

import dev.nikdekur.ndkore.ext.*
import java.util.regex.Pattern

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
open class PatternPlaceholderParser(val pattern: Pattern) : PlaceholderParser {

    constructor(symbolLeft: String, symbolRight: String) : this(Pattern.compile("$symbolLeft(.*?)$symbolRight"))
    constructor(symbol: String) : this(symbol, symbol)

    override fun parseExpression(pathRaw: String, placeholders: Map<String, Any?>): String? {
        val parts = pathRaw.split(".")
        var currentObject: Any? = placeholders[parts[0]] ?: return null

        if (parts.size == 1)
            return (currentObject as? Iterable<*>)
                ?.firstOrNull()
                ?.toString()
                ?: currentObject.toString()


        for (partI in 1 until parts.size) {
            val part = parts[partI]
            currentObject = if (currentObject is Iterable<*>) {
                var found: Any? = null
                for (item in currentObject) {
                    found = findValue(item!!, part)
                    if (found != null) break
                }
                found
            } else {
                findValue(currentObject!!, part)
            }
            if (currentObject == null) return null
        }

        return currentObject.toString()
    }

    override fun parse(string: String, placeholders: Map<String, Any?>): String {
        val sb = StringBuilder()
        val matcher = pattern.matcher(string)
        var lastEnd = 0
        while (matcher.find()) {
            sb.append(string, lastEnd, matcher.start())
            val matchString = matcher.group(1)
            sb.append(parseExpression(matchString, placeholders) ?: matcher.group())
            lastEnd = matcher.end()
        }
        sb.append(string.substring(lastEnd))
        return sb.toString()
    }

    open fun findValue(obj: Any, valueName: String): Any? {
        if (obj is Placeholder) {
            val value = obj.getPlaceholder(valueName)
            if (value != null) return value
        }
        return obj.r_GetField(valueName).value
            ?: obj.r_CallMethod(valueName.asCamelCaseGetter()).value
            ?: obj.r_CallMethod(valueName).value
    }


    companion object {
        val HASH = PatternPlaceholderParser("#")
        val PROCENT = PatternPlaceholderParser("%")
        val CURLY_BRACKET = PatternPlaceholderParser("\\{", "\\}")
    }
}