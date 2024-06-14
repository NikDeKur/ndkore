package dev.nikdekur.ndkore.tools

import dev.nikdekur.ndkore.placeholders.Placeholder


enum class SimpleDataType : Placeholder {
    STRING,
    INT,
    LONG,
    FLOAT,
    DOUBLE,
    BOOLEAN,
    CHAR,
    BYTE,
    SHORT,
    BIG_INTEGER,
    BIG_DECIMAL,

    ;


    override val placeholderMap: MutableMap<String, Any> = mutableMapOf(
        "name" to name
    )

    fun convert(string: String): Any {
        return when (this) {
            STRING -> string
            INT -> string.toInt()
            LONG -> string.toLong()
            FLOAT -> string.toFloat()
            DOUBLE -> string.toDouble()
            BOOLEAN -> string.toBoolean()
            CHAR -> string.single()
            BYTE -> string.toByte()
            SHORT -> string.toShort()
            BIG_INTEGER -> string.toBigInteger()
            BIG_DECIMAL -> string.toBigDecimal()
        }
    }


    companion object {

        @JvmStatic
        fun fromStringOrNull(type: String): SimpleDataType? {
            return entries.firstOrNull { it.name.equals(type, ignoreCase = true) }
        }

        @JvmStatic
        fun fromString(type: String): SimpleDataType {
            return fromStringOrNull(type) ?: throw IllegalArgumentException("Unknown type: $type")
        }
    }
}