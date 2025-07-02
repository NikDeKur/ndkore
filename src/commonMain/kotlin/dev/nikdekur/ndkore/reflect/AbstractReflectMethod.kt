package dev.nikdekur.ndkore.reflect

import dev.nikdekur.ndkore.placeholder.Placeholder
import dev.nikdekur.ndkore.reflect.ReflectMethod.NotFound

public abstract class AbstractReflectMethod : ReflectMethod {
    override fun findValue(obj: Any, name: String): Any? {
        val asObj = findAsObject(obj, name)
        if (asObj != NotFound) return asObj

        return NotFound
    }

    internal fun findAsObject(obj: Any, name: String): Any? {
        if (obj is Placeholder) {
            val value = obj.getPlaceholder(name)
            if (value != NotFound) return value
        }

        if (obj is Map<*, *>) {
            val contains = obj.containsKey(name)
            if (contains) return obj[name]
        }

        if (obj is Iterable<*>) {
            val index = name.toIntOrNull()
            if (index != null) {
                val value = obj.elementAtOrNull(index)
                if (value != null) return value
            } else {
                // Not an index, try to find placeholder in each element
                val result = obj.mapNotNull {
                    if (it == null) return@mapNotNull null
                    val find = findValue(it, name)
                    if (find == NotFound) return@mapNotNull null
                    find
                }
                val actual = result.firstOrNull()
                if (actual != null) return actual
            }
        }

        return NotFound
    }
}