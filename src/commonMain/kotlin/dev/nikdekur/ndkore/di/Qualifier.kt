@file:Suppress("NOTHING_TO_INLINE")

package dev.nikdekur.ndkore.di

public typealias QualifierValue = String

public interface Qualifier {
    public val value: QualifierValue

    public companion object Empty : Qualifier {
        override val value: QualifierValue = ""

        override fun toString(): String {
            return "Qualifier.Empty"
        }
    }
}

public inline fun Qualifier(value: QualifierValue): Qualifier {
    if (value.isEmpty()) return Qualifier.Empty

    return object : Qualifier {
        override val value: QualifierValue = value

        override fun toString(): String {
            return "Qualifier($value)"
        }
    }
}

public inline val Enum<*>.qualifier: Qualifier
    get() = Qualifier(name)

public inline val String.qualifier: Qualifier
    get() = Qualifier(this)