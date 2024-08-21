@file:Suppress("NOTHING_TO_INLINE")

package dev.nikdekur.ndkore.map

typealias SetsMap<K, V> = Map<K, Set<V>>
typealias MutableSetsMap<K, V> = MutableMap<K, MutableSet<V>>

inline fun <K, V> SetsMap<K, V>.contains(key: K, value: V): Boolean {
    return get(key)?.contains(value) == true
}

@Suppress("kotlin:S6524")
inline fun <K, V> MutableSetsMap<K, V>.add(key: K, value: V, setGen: () -> MutableSet<V>) {
    val list = getOrPut(key, setGen)
    list.add(value)
}

@Suppress("kotlin:S6524")
inline fun <K, V> MutableSetsMap<K, V>.remove(key: K, value: V): Boolean {
    return get(key)?.remove(value) == true
}
