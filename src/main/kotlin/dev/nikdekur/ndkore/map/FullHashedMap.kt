@file:Suppress("OVERRIDE_BY_INLINE", "NOTHING_TO_INLINE")

package dev.nikdekur.ndkore.map

import kotlin.math.abs

/**
 * A map similar to [HashMap] but with the following differences:
 * - It doesn't support operations that require keys.
 * - Map uses only hash codes to store and retrieve values.
 */
open class FullHashedMap<K, V> : MutableMap<K, V> {

    private var iValues = arrayOfNulls<Any?>(INITIAL_CAPACITY)

    private fun hash(key: K): Int {
        return abs(key.hashCode())
    }

    override val entries: MutableSet<MutableMap.MutableEntry<K, V>>
        get() = throwUnsupported()

    override val keys: MutableSet<K>
        get() = throwUnsupported()

    final override var size: Int = 0
        private set

    override val values: MutableCollection<V>
        @Suppress("UNCHECKED_CAST")
        get() = iValues.filterNotNull() as MutableCollection<V>

    override fun clear() {
        iValues = arrayOfNulls(INITIAL_CAPACITY)
        size = 0
    }

    override fun isEmpty(): Boolean {
        return size == 0
    }


    override fun remove(key: K): V? {
        val index = hash(key)
        return if (index < iValues.size) {
            val old = iValues[index]
            iValues[index] = null
            if (old != null) size--
            @Suppress("UNCHECKED_CAST")
            old as V?
        } else {
            null
        }
    }

    override fun putAll(from: Map<out K, V>) {
        from.forEach { put(it.key, it.value) }
    }

    override fun put(key: K, value: V): V? {
        val index = hash(key)
        ensureCapacity(index)
        val old = iValues[index]
        iValues[index] = value
        if (old == null) size++
        @Suppress("UNCHECKED_CAST")
        return old as V?
    }

    override fun get(key: K): V? {
        val index = hash(key)
        @Suppress("UNCHECKED_CAST")
        return if (index < iValues.size) iValues[index] as V? else null
    }

    override fun containsValue(value: V): Boolean {
        return iValues.contains(value)
    }

    override fun containsKey(key: K): Boolean {
        return get(key) != null
    }


    private fun ensureCapacity(index: Int) {
        if (index >= iValues.size) {
            iValues = iValues.copyOf(index * 2)
        }
    }

    fun throwUnsupported(): Nothing {
        throw UnsupportedOperationException("Operation is not supported for FullHashedMap. FullHashedMap doesn't store keys.")
    }

    companion object {
        private const val INITIAL_CAPACITY = 16
    }
}