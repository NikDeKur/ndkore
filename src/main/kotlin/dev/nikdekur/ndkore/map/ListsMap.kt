@file:Suppress("NOTHING_TO_INLINE")

package dev.nikdekur.ndkore.map

typealias ListsMap<K, V> = Map<K, List<V>>
typealias MutableListsMap<K, V> = MutableMap<K, MutableList<V>>


@Suppress("UNCHECKED_CAST", "kotlin:S6524")
inline fun <K, V> MutableListsMap<K, V>.add(key: K, value: V, listGen: () -> MutableList<V>) {
    val list = getOrPut(key, listGen)
    list.add(value)
}