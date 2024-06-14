@file:Suppress("NOTHING_TO_INLINE", "KDocUnresolvedReference", "unused")

package dev.nikdekur.ndkore.ext

import org.checkerframework.checker.units.qual.K
import dev.nikdekur.ndkore.interfaces.Snowflake
import java.util.*
import java.util.AbstractMap.SimpleEntry
import java.util.concurrent.ConcurrentHashMap
import java.util.function.Function


fun <K, V> newHashMap() = HashMap<K, V>()
fun <K, V> newConcurrentHashMap() = ConcurrentHashMap<K, V>()
fun <K, V> newLinkedHashMap() = LinkedHashMap<K, V>()
fun <K, V> newTreeMap() = TreeMap<K, V>()

inline fun <K, V> Map<K, V>.getOptional(key: K): Optional<V & Any> {
    return Optional.ofNullable(get(key))
}

inline fun <K, V> Pair<K, V>.toMap() = mapOf(this)

// Supress sonarlint warning because of sonarlint bug.
// Collection could not be immutable, because immutable collections return immutable iterators.
@Suppress("kotlin:S6524")
inline fun <K, V> MutableMap<K, V>.removeIf(predicate: (K, V) -> Boolean) {
    val iterator = entries.iterator()
    while (iterator.hasNext()) {
        val entry = iterator.next()
        if (predicate(entry.key, entry.value)) {
            iterator.remove()
        }
    }
}


inline fun <K, V, NV> Map<K, V>.getAndMap(key: K, ifNotNullMapper: (V) -> NV): NV? {
    val v = this[key] ?: return null
    return ifNotNullMapper.invoke(v)
}




inline fun <K, Any> Map<K, Any>.getString(key: K, default: String?): String? {
    return getAndMap(key) { it as String } ?: default
}

inline fun <K, Any> Map<K, Any>.getStringOrThrow(key: K): String {
    return getString(key, null) ?: throw NoSuchElementException(key.toString())
}


inline fun <K, V, NK, NV> Map<K, V>.map(
    keyMapper: Function<Map.Entry<K, V>, NK?>,
    valueMapper: Function<Map.Entry<NK, V>, out NV?>,
    mapGen: () -> MutableMap<NK, NV> = ::newHashMap
): MutableMap<NK, NV> {
    val newMap = mapGen()
    for ((key, value) in this) {
        val keyEntry = SimpleEntry(key, value)
        val newKey = keyMapper.apply(keyEntry) ?: continue
        val valEntry = SimpleEntry(newKey, value)
        val newValue = valueMapper.apply(valEntry) ?: continue
        newMap[newKey] = newValue

    }
    return newMap
}

inline fun <T, K, V> Collection<T>.toMap(key: Function<T, K>, value: Function<T, V>): MutableMap<K, V> {
    return stream().toMap(key, value)
}
inline fun <T, K, V> Collection<T>.toConcurrentMap(key: Function<T, K>, value: Function<T, V>): MutableMap<K, V> {
    return stream().toConcurrentMap(key, value)
}
inline fun <T, K, V> Collection<T>.toUnmodifiableMap(key: Function<T, K>, value: Function<T, V>): Map<K, V> {
    return stream().toUnmodifiableMap(key, value)
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
fun <T> Iterable<T>.removeEmpty(maps: Boolean = true, collections: Boolean = true): List<T> {
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
fun <K, V> Map<K, V>.removeEmpty(maps: Boolean = true, collections: Boolean = true): Map<K, V> {
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
inline fun <IdT, V : Snowflake<IdT>> MutableMap<IdT, V>.addById(obj: V): V? {
    return put(obj.id, obj)
}

/**
 * Applicable to MutableMap, where value is Snowflake of a type same as a key
 * Add all values to map by their ids
 *
 * @param objs values to add
 */
inline fun <IdT, V : Snowflake<IdT>> MutableMap<IdT, V>.addAllById(objs: Iterable<V>) {
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
inline fun <IdT, V : Snowflake<IdT>> MutableMap<IdT, V>.removeById(obj: V): V? {
    return remove(obj.id)
}


// Contains
inline fun <IdT, V : Snowflake<IdT>> Map<IdT, V>.containsById(obj: V): Boolean {
    return containsKey(obj.id)
}


inline fun <T : Any> MutableMap<Class<out T>, T>.addByClazz(obj: T): T? {
    return put(obj.javaClass, obj)
}

inline fun <T : Any> MutableMap<Class<out T>, T>.addAllByClazz(objs: Iterable<T>) {
    objs.forEach {
        addByClazz(it)
    }
}

inline fun <T : Any> MutableMap<Class<out T>, T>.removeByClazz(obj: T): T? {
    return remove(obj.javaClass)
}

inline fun <T : Any> Map<Class<out T>, T>.containsByClazz(obj: T): Boolean {
    return containsKey(obj.javaClass)
}



inline fun <K, V> Map<K, V>.firstEntry() = entries.first()
inline fun <K, V> Map<K, V>.firstEntryOrNull() = entries.firstOrNull()

inline fun <K, V> Map<K, V>.firstKey() = keys.first()
inline fun <K, V> Map<K, V>.firstKeyOrNull() = keys.firstOrNull()

inline fun <K, V> Map<K, V>.firstValue() = values.first()
inline fun <K, V> Map<K, V>.firstValueOrNull() = values.firstOrNull()

/**
 * Similar to [Map.all] but accept kotlin style lambda
 *
 * Could be very useful, when you need to pass existing function to [Map.all] but it requires function, that takes [Map.Entry], not [K] and [V]
 */
inline fun <K, V> Map<K, V>.all(action: (K, V) -> Boolean): Boolean {
    for ((k, v) in this) {
        if (!action(k, v)) return false
    }
    return true
}

/**
 * Similar to [Map.any] but accept kotlin style lambda
 *
 * Could be very useful, when you need to pass existing function to [Map.any] but it requires function, that takes [Map.Entry], not [K] and [V]
 */
inline fun <K, V> Map<K, V>.any(action: (K, V) -> Boolean): Boolean {
    for ((k, v) in this) {
        if (action(k, v)) return true
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
 * - key: "a.b.c"
 * - separator: "."
 * - result: 1
 *
 * @param key key to get
 * @param separator separator to split key
 * @return value or null if not found
 */
@Suppress("UNCHECKED_CAST")
fun <V> Map<String, V>.getNested(key: String, separator: String): V? {
    val keys = key.split(separator)
    var map = this
    for (i in 0 until keys.size - 1) {
        map = map[keys[i]] as? Map<String, V> ?: return null
    }
    return map[keys.last()]
}