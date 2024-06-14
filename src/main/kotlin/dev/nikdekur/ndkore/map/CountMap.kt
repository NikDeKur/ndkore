package dev.nikdekur.ndkore.map

open class CountMap<K>(val default: Int = 0) : HashMap<K, Int>(), MutableMap<K, Int> {
    private val limitsMap = HashMap<K, Int>()


    // BASE
    fun increment(key: K) {
        val count = get(key)
        if (isLimitReached(key)) {
            return
        }
        this[key] = count + 1
    }

    override fun get(key: K): Int {
        return getOrDefault(key, default)
    }


    fun reset(key: K) {
        this.remove(key)
    }


    // LIMITS
    fun setLimit(key: K, limit: Int) {
        if (limit > 0) limitsMap[key] = limit
    }

    fun removeLimit(key: K) {
        limitsMap.remove(key)
    }

    fun getLimit(key: K): Int {
        return limitsMap.getOrDefault(key, -1)
    }

    fun hasLimit(key: K): Boolean {
        return getLimit(key) != -1
    }

    fun isLimitReached(key: K): Boolean {
        if (!hasLimit(key)) return false
        return get(key) >= getLimit(key)
    }
}
