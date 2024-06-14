package dev.nikdekur.ndkore.map.set

import java.util.concurrent.ConcurrentHashMap

open class ConcurrentSetsHashMap<K : Any, V> : ConcurrentHashMap<K, MutableSet<out V>>(), SetsMap<K, V> {
    override fun get(key: K): MutableSet<out V> {
        return computeIfAbsent(key) { newKeySet() }
    }

    override fun getIfPresent(key: K): MutableSet<out V>? {
        return super.get(key)
    }

    @Suppress("UNCHECKED_CAST")
    override fun add(key: K, value: V) {
        val list: MutableSet<V> = get(key) as MutableSet<V>
        list.add(value)
    }

    @Suppress("UNCHECKED_CAST")
    override fun delete(key: K, value: V) {
        val list: MutableSet<V> = get(key) as MutableSet<V>
        list.remove(value)
    }
}
