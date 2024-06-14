package dev.nikdekur.ndkore.map.multi


interface MultiMap<K1, K2, V> : MutableMap<K1, MutableMap<K2, V>> {

    fun getMap(k1: K1): MutableMap<K2, V>

    fun put(k1: K1, k2: K2, value: V)

    fun getOrDefault(k1: K1, k2: K2, default: V? = null): V?
    operator fun get(k1: K1, k2: K2): V? = getOrDefault(k1, k2, null)

    fun remove(k1: K1, k2: K2): V?

    fun contains(k1: K1, k2: K2): Boolean

    fun computeIfAbsent(k1: K1, k2: K2, orElse: (K2) -> V): V
}