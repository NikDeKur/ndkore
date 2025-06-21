@file:Suppress("NOTHING_TO_INLINE")
@file:OptIn(ExperimentalSerializationApi::class)

package dev.nikdekur.ndkore.ext

import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerializationStrategy
import kotlinx.serialization.json.*

public fun JsonElement.toAny(): Any? {
    return when (this) {
        is JsonPrimitive -> toPrimitive()
        is JsonArray -> toList()
        is JsonObject -> toMap()
    }
}

public fun JsonPrimitive.toPrimitive(): Any? {
    return booleanOrNull
        ?: intOrNull
        ?: longOrNull
        ?: floatOrNull
        ?: doubleOrNull
        ?: contentOrNull
}

public fun JsonArray.toList(): List<Any?> {
    return this.map { it.toAny() }
}

public fun JsonObject.toMap(): Map<String, Any?> {
    return this.mapValues { it.value.toAny() }
}


public fun Any?.toJsonElement(quoteString: Boolean = false): JsonElement {
    return when (this) {
        is Number -> JsonPrimitive(this)
        is Boolean -> JsonPrimitive(this)
        is String -> if (quoteString) JsonPrimitive(this) else JsonUnquotedLiteral(this)
        is Array<*> -> toJsonArray()
        is Iterable<*> -> toJsonArray()
        is Map<*, *> -> toJsonObject()
        is JsonElement -> this
        else -> JsonNull
    }
}

public fun Array<*>.toJsonArray(): JsonArray {
    val array = mutableListOf<JsonElement>()
    forEach { array.add(it.toJsonElement()) }
    return JsonArray(array)
}

public fun Iterable<*>.toJsonArray(): JsonArray {
    val array = mutableListOf<JsonElement>()
    forEach { array.add(it.toJsonElement()) }
    return JsonArray(array)
}

public fun Map<*, *>.toJsonObject(): JsonObject {
    val map = mutableMapOf<String, JsonElement>()
    forEach {
        map[it.key.toString()] = it.value.toJsonElement()
    }
    return JsonObject(map)
}


public inline fun <T> Json.decodeFromMap(deserializer: DeserializationStrategy<T>, map: Map<*, *>): T {
    return decodeFromJsonElement(deserializer, map.toJsonObject())
}

public inline fun <reified T> Json.decodeFromMap(map: Map<*, *>): T {
    return decodeFromJsonElement(map.toJsonObject())
}


public inline fun <T> Json.encodeToMap(value: T, serializer: SerializationStrategy<T>): Map<String, Any?> {
    return encodeToJsonElement(serializer, value).jsonObject.toMap()
}

public inline fun <reified T> Json.encodeToMap(value: T): Map<String, Any?> {
    return encodeToJsonElement(value).jsonObject.toMap()
}