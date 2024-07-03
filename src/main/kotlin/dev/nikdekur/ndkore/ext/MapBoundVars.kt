/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 * Copyright (c) 2024 Nik De Kur
 */

@file:Suppress("NOTHING_TO_INLINE", "KDocUnresolvedReference", "unused")

package dev.nikdekur.ndkore.ext

import dev.nikdekur.ndkore.map.list.ListsHashMap
import dev.nikdekur.ndkore.map.list.ListsMap
import dev.nikdekur.ndkore.map.set.SetsHashMap
import java.math.BigDecimal
import java.math.BigInteger
import java.util.*
import kotlin.properties.ReadOnlyProperty
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

/**
 * Represents a field for smart data storing.
 *
 * When value is accessed first, it will be deserialised by [deserializer] and stored in map instead of the stored primitive value.
 *
 * If accessed value is already high level (deserialized), it will be returned instantly.
 *
 * When the value is set, it will store in map.
 *
 * Adding a new field type requires you to add Gson [TypeAdapter] for the type to be converted from high level to primitive value on save time.
 *
 * This specific mechanism allows to setting values in a map directly in primitive times, and deserialise them on access.
 */
open class NotNullClassDataHolderField<K, V, T>(
    val map: MutableMap<K, V>,
    val path: K,
    val default: V,
    val deserializedType: Class<T>,
    val deserializer: (V) -> T
) : ReadWriteProperty<V?, T> {

    @Suppress("UNCHECKED_CAST")
    override fun getValue(thisRef: V?, property: KProperty<*>): T {
        val v = map[path] ?: default
        if (deserializedType.isInstance(v))
            return v as T

        val deserialize = deserializer(v)
        if (v != deserialize)
            set(deserialize)
        return deserialize
    }

    @Suppress("UNCHECKED_CAST")
    inline fun set(value: T) {
        if (value == default)
            map.remove(path)
        else
            map[path] = value as V
    }

    override fun setValue(thisRef: V?, property: KProperty<*>, value: T) {
        set(value)
    }
}

/**
 * Represents a field for smart data storing.
 *
 * When value is accessed first, it will be deserialised by [deserializer] and stored in map instead of the stored primitive value.
 *
 * If accessed value is already high level (deserialized), it will be returned instantly.
 *
 * When the value is set, it will store in map.
 *
 * Adding a new field type requires you to add Gson [TypeAdapter] for the type to be converted from high level to primitive value on save time.
 *
 * This specific mechanism allows to setting values in a map directly in primitive times, and deserialise them on access.
 */
open class ClassDataHolderField<K, V, T>(
    val map: MutableMap<K, V>,
    val path: K,
    val default: V?,
    val deserializedType: Class<T>,
    val deserializer: (V) -> T?
) : ReadWriteProperty<V?, T?> {

    @Suppress("UNCHECKED_CAST")
    override fun getValue(thisRef: V?, property: KProperty<*>): T? {
        val v = map[path]
        if (v != null && deserializedType.isInstance(v))
            return v as T
        val deserialize = (v ?: default)?.let(deserializer)
        if (v != deserialize)
            set(deserialize)
        return deserialize
    }

    @Suppress("UNCHECKED_CAST")
    inline fun set(value: T?) {
        if (value == null)
            map.remove(path)
        else
            map[path] = value as V
    }

    override fun setValue(thisRef: V?, property: KProperty<*>, value: T?) {
        set(value)
    }
}


@Suppress("UNCHECKED_CAST")
fun <K, V> MutableMap<K, V>.intBoundVar(key: K, default: Int = 0) = NotNullClassDataHolderField(this,
    key,
    default as V,
    Int::class.java
) {
    when (it) {
        is Int -> it
        is Number -> it.toInt()
        is String -> it.toIntOrNull() ?: default
        else -> default
    }
}

@Suppress("UNCHECKED_CAST")
fun <K, V> MutableMap<K, V>.longBoundVar(key: K, default: Long = 0) = NotNullClassDataHolderField(this,
    key,
    default as V,
    Long::class.java
) {
    when (it) {
        is Long -> it
        is Number -> it.toLong()
        is String -> it.toLongOrNull() ?: default
        else -> default
    }
}

fun <K, V> MutableMap<K, V>.nullableIntBoundVar(key: K) = ClassDataHolderField(this,
    key,
    null,
    Int::class.java
) {
    when (it) {
        is Number -> it.toInt()
        is String -> it.toIntOrNull()
        else -> null
    }
}
@Suppress("UNCHECKED_CAST")
fun <K, V> MutableMap<K, V>.doubleBoundVar(key: K, default: Double = 0.0) = NotNullClassDataHolderField(this,
    key,
    default as V,
    Double::class.java
) {
    when (it) {
        is Double -> it
        is Number -> it.toDouble()
        is String -> it.toDoubleOrNull() ?: default
        else -> default
    }
}

fun <K, V> MutableMap<K, V>.nullableDoubleBoundVar(key: K) = ClassDataHolderField(this,
    key,
    null,
    Double::class.java
) {
    when (it) {
        is Double -> it
        is Number -> it.toDouble()
        is String -> it.toDoubleOrNull()
        else -> null
    }
}

@Suppress("UNCHECKED_CAST", "USELESS_CAST")
fun <K, V> MutableMap<K, V>.stringBoundVar(key: K, def: String? = null) = ClassDataHolderField(this,
    key,
    def as V,
    String::class.java
) {
    when (it) {
        is String -> it
        else -> def as String
    }
}

@Suppress("UNCHECKED_CAST")
fun <K, V> MutableMap<K, V>.stringNotNullBoundVar(key: K, default: String) = NotNullClassDataHolderField(this,
    key,
    default as V,
    String::class.java
) {
    when (it) {
        is String -> it
        else -> default
    }
}


@Suppress("UNCHECKED_CAST")
fun <K, V> MutableMap<K, V>.booleanBoundVar(key: K, default: Boolean = false) = NotNullClassDataHolderField(this,
    key,
    default as V,
    Boolean::class.java
) {
    when (it) {
        is Boolean -> it
        is String -> it.toBoolean()
        else -> default
    }
}

fun <K, V> MutableMap<K, V>.nullableBooleanBoundVar(key: K) = ClassDataHolderField(this,
    key,
    null,
    Boolean::class.java
) {
    when (it) {
        is Boolean -> it
        is String -> it.toBoolean()
        else -> null
    }
}



/**
 * For GSON require type adapter to convert BigInteger to either String, Number or BigInteger
 *
 * @param key key to get or set
 * @param default default value (default is 0)
 * @return ReadWriteProperty for [BigInteger] value
 * @see NotNullClassDataHolderField
 */
@Suppress("UNCHECKED_CAST")
fun <K, V> MutableMap<K, V>.bigIntBoundVar(key: K, default: BigInteger = BigInteger.ZERO) = NotNullClassDataHolderField(this,
    key,
    default as V,
    BigInteger::class.java
) {
    when (it) {
        is BigInteger -> it
        is Number -> BigDecimal.valueOf(it.toDouble()).toBigInteger()
        is String -> BigInteger(it)
        else -> default
    }
}

/**
 * For GSON require type adapter to convert BigDecimal to either String, Number or BigDecimal
 *
 * @param key key to get or set
 * @param default default value (default is 0)
 * @return ReadWriteProperty for [BigInteger] value
 * @see NotNullClassDataHolderField
 */
@Suppress("UNCHECKED_CAST")
fun <K, V> MutableMap<K, V>.bigDecBoundVar(key: K, default: BigDecimal = BigDecimal.ZERO) = NotNullClassDataHolderField(this,
    key,
    default as V,
    BigDecimal::class.java
) {
    when (it) {
        is BigDecimal -> it
        is Number -> BigDecimal.valueOf(it.toDouble())
        is String -> BigDecimal(it)
        else -> default
    } as BigDecimal
}

/**
 * For GSON require type adapter (on save) to convert UUID to either String or UUID
 *
 * If no UUID provided at path or object at the path is not uuid, return null
 *
 * @param key key to get or set
 * @return ReadWriteProperty for [UUID] value
 */
@Suppress("UNCHECKED_CAST")
fun <K, V> MutableMap<K, V>.uuidBoundVar(key: K) = ClassDataHolderField(this,
    key,
    null as V,
    UUID::class.java
) {
    when (it) {
        is UUID -> it
        is String -> UUID.fromString(it)
        else -> null
    }
}


@Suppress("UNCHECKED_CAST")
fun <T, K, V> MutableMap<K, V>.mutableCollectionBoundVar(key: K, listGen: (K) -> MutableCollection<T> = { ArrayList() }) =
    ReadOnlyProperty<Any?, MutableCollection<T>> { _, _ -> computeIfAbsent(key) {listGen(it) as V} as MutableCollection<T> }



@Suppress("UNCHECKED_CAST")
fun <K, V, InK, InV> MutableMap<K, V>.mutableMapBoundVar(path: K, mapGen: (K) -> MutableMap<InK, InV> = { HashMap() }) =
    ReadOnlyProperty<Any?, MutableMap<InK, InV>> { _, _ -> computeIfAbsent(path) { mapGen(it) as V } as MutableMap<InK, InV>}


@Suppress("UNCHECKED_CAST")
fun <K, V, InK : Any, InV> MutableMap<K, V>.listsMapBoundVar(path: K, mapGen: (K) -> ListsMap<InK, InV> = { ListsHashMap() }) =
    ReadOnlyProperty<Any?, SetsHashMap<InK, InV>> { _, _ -> computeIfAbsent(path) { mapGen(it) as V } as SetsHashMap<InK, InV> }

@Suppress("UNCHECKED_CAST")
inline fun <K, V, reified T> MutableMap<K, V>.mutablePrimitiveSetBoundVar(
    key: K,
    crossinline setGen: (K) -> MutableSet<T> = { HashSet() }
): NotNullClassDataHolderField<K, V, MutableSet<T>> {

    val type = MutableSet::class.java as Class<MutableSet<T>>
    return NotNullClassDataHolderField(this, key, setGen(key) as V, type) {
        when (it) {
            is MutableSet<*> -> {
                if (it.isEmpty()) setGen(key)
                else {
                    val first = it.first()
                    if (first is T) {
                        it as MutableSet<T>
                    } else {
                        (it as MutableSet<V>).cast(T::class.java).toMutableSet()
                    }
                }
            }

            is Collection<*> -> {
                if (it.isEmpty()) setGen(key)
                else {
                    val first = it.first()
                    if (first is T) {
                        it.toMutableSet() as MutableSet<T>
                    } else {
                        (it as Collection<V>).cast(T::class.java).toMutableSet()
                    }
                }
            }
            else -> setGen(key)
        }
    }
}

@Suppress("UNCHECKED_CAST")
inline fun <K, V, reified T> MutableMap<K, V>.mutableSetBoundVar(
    key: K,
    crossinline deserializer: (V) -> T,
    crossinline setGen: (K) -> MutableSet<T> = { HashSet() }): NotNullClassDataHolderField<K, V, MutableSet<T>> {

    val type = MutableSet::class.java as Class<MutableSet<T>>
    return NotNullClassDataHolderField(this, key, setGen(key) as V, type) {
        when (it) {
            is MutableSet<*> -> {
                if (it.isEmpty()) setGen(key)
                else {
                    val first = it.first()
                    if (first is T) {
                        it as MutableSet<T>
                    } else {
                        (it as MutableSet<V>).map(deserializer).toMutableSet()
                    }
                }
            }
            is Collection<*> -> {
                if (it.isEmpty()) setGen(key)
                else {
                    val first = it.first()
                    if (first is T) {
                        it.toMutableSet() as MutableSet<T>
                    } else {
                        (it as Collection<V>).map(deserializer).toMutableSet()
                    }
                }
            }
            else -> setGen(key)
        }
    }
}

@Suppress("UNCHECKED_CAST")
inline fun <K, V, InKey, InVal : Any> MutableMap<K, V>.
        getInnerMap(key: K,
                    crossinline mapGen: () -> MutableMap<InKey, InVal> = ::newHashMap)
        : MutableMap<InKey, InVal> {
    return computeIfAbsent(key) { mapGen() as V } as MutableMap<InKey, InVal>
}
