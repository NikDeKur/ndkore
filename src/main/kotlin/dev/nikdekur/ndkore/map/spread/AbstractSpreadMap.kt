package dev.nikdekur.ndkore.map.spread

abstract class AbstractSpreadMap<K, V> : SpreadMap<K, V>, LinkedHashMap<K, V>() {

    override val isDone: Boolean
        get() = filled == max

    override fun getValuePercent(key: K): Double {
        return getValueMultiplier(key) * 100
    }
}