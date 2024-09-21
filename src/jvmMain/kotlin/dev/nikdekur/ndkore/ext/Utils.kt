@file:JvmName("UtilsJvmKt")
@file:Suppress("NOTHING_TO_INLINE")

package dev.nikdekur.ndkore.ext

inline fun <reified T : Enum<T>> enumValueOfOrNull(name: String): T? =
    try {
        enumValueOf<T>(name)
    } catch (e: IllegalArgumentException) {
        null
    }


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
                values[field.name] = publicGetter.invoke(obj).also {
                    if (it is String) "\"$it\""
                }
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
