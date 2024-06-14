package dev.nikdekur.ndkore.map.multi

open class MultiHashMap<K1, K2, V : Any> : MultiMap<K1, K2, V>, HashMap<K1, MutableMap<K2, V>>() {

    override fun getMap(k1: K1): MutableMap<K2, V> {
        return computeIfAbsent(k1) { HashMap() }
    }

    override fun put(k1: K1, k2: K2, value: V) {
        getMap(k1)[k2] = value
    }

    override fun getOrDefault(k1: K1, k2: K2, default: V?): V? {
        return get(k1)?.getOrDefault(k2, default)
    }

    override fun remove(k1: K1, k2: K2): V? {
        return get(k1)?.remove(k2)
    }

    override fun contains(k1: K1, k2: K2): Boolean {
        return get(k1)?.containsKey(k2) ?: false
    }



    override fun computeIfAbsent(k1: K1, k2: K2, orElse: (K2) -> V): V {
        return getMap(k1).computeIfAbsent(k2) { orElse(it) }
    }
}