@file:Suppress("NOTHING_TO_INLINE")

package dev.nikdekur.ndkore.placeholders

import dev.nikdekur.ndkore.ext.*
import java.util.regex.Pattern

class PlaceholderParser(symbolLeft: String, symbolRight: String) {
    private val pattern = Pattern.compile("$symbolLeft(.*?)$symbolRight")

    constructor(symbol: String) : this(symbol, symbol)

    fun parseExpressionHard(pathRaw: String, placeholders: Map<String, Iterable<Any>>): String? {
        var latestObject: Any? = null
        val pathRaw2 = pathRaw.split(".")
        if (pathRaw2.size == 1) return placeholders[pathRaw2[0]]?.first()?.toString()
        val mainKey = pathRaw2[0]
        val path = pathRaw2.subList(1, pathRaw2.size)
        val placeholdersForKey = placeholders[mainKey] ?: return null
        for (pathPart in path) {
            if (latestObject == null) {
                for (placeholder in placeholdersForKey) {
                    latestObject = findValue(placeholder, pathPart)
                    if (latestObject != null) break
                }
            } else {
                latestObject = findValue(latestObject, pathPart)
            }
            if (latestObject == null) return null
        }
        return latestObject?.toString()
    }

    fun parseExpression(pathRaw: String, placeholders: Map<String, Any>): String? {
        var latestObject: Any? = null
        val pathRaw2 = pathRaw.split(".")
        if (pathRaw2.size == 1) return placeholders[pathRaw2[0]]?.toString()
        val mainKey = pathRaw2[0]
        val path = pathRaw2.subList(1, pathRaw2.size)
        val value = placeholders[mainKey] ?: return null
        for (pathPart in path) {
            latestObject = if (latestObject == null) {
                findValue(value, pathPart)
            } else {
                findValue(latestObject, pathPart)
            }
            if (latestObject == null) return null
        }
        return latestObject?.toString()
    }

    fun parse(string: String, placeholders: Map<String, Iterable<Any>>): String {
        val sb = StringBuilder()
        val matcher = pattern.matcher(string)
        var lastEnd = 0
        while (matcher.find()) {
            sb.append(string, lastEnd, matcher.start())
            val matchString = matcher.group(1)
            sb.append(parseExpressionHard(matchString, placeholders) ?: matcher.group())
            lastEnd = matcher.end()
        }
        sb.append(string.substring(lastEnd))
        return sb.toString()
    }

    fun parse(string: String, vararg placeholders: Pair<String, Any>): String {
        val map = placeholders.toMap()
        val sb = StringBuilder()
        val matcher = pattern.matcher(string)
        var lastEnd = 0
        while (matcher.find()) {
            sb.append(string, lastEnd, matcher.start())
            val matchString = matcher.group(1)
            sb.append(parseExpression(matchString, map) ?: matcher.group())
            lastEnd = matcher.end()
        }
        sb.append(string.substring(lastEnd))
        return sb.toString()
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
            return obj.r_GetField(valueName).result
                ?: obj.r_CallMethod(valueName.asCamelCaseGetter()).result
                ?: obj.r_CallMethod(valueName).result
        }

//        class Placeholders : HashMap<String, Iterable<Placeholders>>(), Map<String, Iterable<Placeholders>> {
//        }
//
//        inline fun placeholders(generator: () -> Map<String, Iterable<StrPlaceholder>>): Map<String, Iterable<StrPlaceholder>> {
//            return generator()
//        }
//
//        inline fun newGroup(name: String, vararg elements: Pair<String, Any>): Pair<String, Iterable<StrPlaceholder>> {
//            return setOf(elements)
//        }
//
//        inline fun newElement(name: String, value: Any): Pair<String, Any> {
//            return name to value
//        }
    }
}