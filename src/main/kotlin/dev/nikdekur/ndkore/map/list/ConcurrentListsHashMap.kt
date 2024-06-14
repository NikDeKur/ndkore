package dev.nikdekur.ndkore.map.list

import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.CopyOnWriteArrayList

open class ConcurrentListsHashMap<K : Any, V> : ConcurrentHashMap<K, MutableList<out V>>(), ListsMap<K, V> {
    override fun get(key: K): MutableList<out V> {
        return computeIfAbsent(key) { CopyOnWriteArrayList() }
    }

    override fun getIfPresent(key: K): MutableList<out V>? {
        return super.get(key)
    }

    @Suppress("UNCHECKED_CAST")
    override fun add(key: K, value: V) {
        val list: MutableList<V> = get(key) as MutableList<V>
        list.add(value)
    }

    @Suppress("UNCHECKED_CAST")
    override fun delete(key: K, value: V) {
        val list: MutableList<V> = get(key) as MutableList<V>
        list.remove(value)
    }
}
