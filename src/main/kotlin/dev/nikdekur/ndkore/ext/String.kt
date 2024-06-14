@file:Suppress("NOTHING_TO_INLINE")

package dev.nikdekur.ndkore.ext

import dev.nikdekur.ndkore.tools.MatchCondition
import java.io.File
import java.util.*

inline fun String.removeDoubleSpaces(): String {
    var s = this
    while (s.contains("  ")) {
        s = s.replace("  ", " ")
    }
    return s
}

inline fun String.match(condition: MatchCondition, otherString: String): Boolean {
    return condition.match(this, otherString)
}

inline fun String.asCamelCaseGetter() =
    if (this.isEmpty()) {
        "get"
    } else {
        "get${this[0].uppercase()}${this.substring(1)}"
    }

inline fun String.isBlankOrEmpty(): Boolean {
    return isBlank() || isEmpty()
}

inline fun String.isUUID(): Boolean {
    return this.matches(Patterns.UUID)
}

inline fun String.toUUID(): UUID {
    return UUID.fromString(this)
}

inline fun String.toUUIDOrNull(): UUID? {
    return try {
        UUID.fromString(this)
    } catch (e: IllegalArgumentException) {
        null
    }
}

inline fun String.toBooleanSmart(): Boolean {
    return when (this) {
        "true", "1", "yes", "on" -> true
        "false", "0", "no", "off" -> false
        else -> throw IllegalArgumentException("Cannot convert '$this' to boolean")
    }
}

inline fun String.toBooleanOrNullSmart(): Boolean? {
    return when (this) {
        "true", "1", "yes", "on" -> true
        "false", "0", "no", "off" -> false
        else -> null
    }
}


inline fun String.capitalizeFirstLetter(): String {
    return if (this.isEmpty()) {
        this
    } else {
        this[0].uppercase() + this.substring(1)
    }
}


/**
 * Create a file from the string path if it doesn't exist.
 *
 * If parent directories don't exist, they will be created.
 */
inline fun String.createPath(): File {
    return File(this).also {
        if (!it.exists()) {
            it.mkdirs()
        }
    }
}
