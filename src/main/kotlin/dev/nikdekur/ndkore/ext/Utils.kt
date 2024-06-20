@file:Suppress("NOTHING_TO_INLINE")

package dev.nikdekur.ndkore.ext

import java.util.*

inline fun <T : Enum<T>> Class<T>.getEnumValues(): EnumSet<T> {
    return EnumSet.allOf(this)
}

inline fun <reified T : Enum<T>> enumValueOfOrNull(name: String): T? =
    try {
        enumValueOf<T>(name)
    } catch (e: IllegalArgumentException) {
        null
    }


inline fun input(text: String): String {
    print(text)
    return readln()
}




/**
 * Converts the number to a comparable result.
 *
 * If the number is greater than 0, returns 1.
 * If the number is less than 0, returns -1.
 * Otherwise, returns 0.
 *
 * @return The comparable result.
 */
inline fun Number.toComparableResult(): Int {
    return when {
        this > 0 -> 1
        this < 0 -> -1
        else -> 0
    }
}


typealias CompAny = Comparable<Any>

/**
 * Returns beautiful string representation of the object.
 *
 * Insert all public fields and their values to the string.
 *
 * @param obj The object to convert to string.
 * @return The string representation of the object.
 */
inline fun buildRepresentation(obj: Any): String {
    val clazz = obj.javaClass

    val values = HashMap<String, Any?>()

    val fields = clazz.declaredFields
    for (field in fields) {
        if (field.isAccessible) {
            values[field.name] = field[obj]

        } else {
            val getterName = field.name.asCamelCaseGetter()
            try {
                val publicGetter = clazz.getMethod(getterName)
                var value: Any? = publicGetter.invoke(obj)
                if (value is String) {
                    value = "\"$value\""
                }
                values[field.name] = value
            } catch (e: Exception) {
                continue
            }
        }
    }

    return clazz.simpleName + values.entries.joinToString(
        prefix = "(",
        postfix = ")",
        separator = ", "
    ) {
        "${it.key}=${it.value}"
    }
}


inline infix fun <K, V> K.singleMap(value: V): Map<K, V> = mapOf(this to value)