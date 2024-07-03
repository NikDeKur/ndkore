/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 * Copyright (c) 2024 Nik De Kur
 */

@file:Suppress("NOTHING_TO_INLINE")

package dev.nikdekur.ndkore.placeholders

import dev.nikdekur.ndkore.ext.*
import java.util.regex.Pattern

class PlaceholderParser(symbolLeft: String, symbolRight: String) {
    private val pattern = Pattern.compile("$symbolLeft(.*?)$symbolRight")

    constructor(symbol: String) : this(symbol, symbol)

    fun parseExpression(pathRaw: String, placeholders: Map<String, Any>): String? {
        val parts = pathRaw.split(".")
        var currentObject: Any? = placeholders[parts[0]] ?: return null

        if (parts.size == 1) {
            return if (currentObject is Iterable<*>) {
                currentObject.iterator().next()?.toString()
            } else {
                currentObject.toString()
            }
        }

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

    fun parse(string: String, placeholders: Map<String, Any>): String {
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

    inline fun parse(string: String, vararg placeholders: Pair<String, Any>): String {
        return parse(string, placeholders.toMap())
    }

    companion object {
        val HASH = PlaceholderParser("#")
        val PROCENT = PlaceholderParser("%")
        val DOLLAR = PlaceholderParser("$")
        val CURLY_BRACKET = PlaceholderParser("\\{", "\\}")


        inline fun findValue(obj: Any, valueName: String): Any? {
            if (obj is Placeholder) {
                val value = obj.placeholderMap[valueName]
                if (value != null) return value
            }
            return obj.r_GetField(valueName).value
                ?: obj.r_CallMethod(valueName.asCamelCaseGetter()).value
                ?: obj.r_CallMethod(valueName).value
        }
    }
}