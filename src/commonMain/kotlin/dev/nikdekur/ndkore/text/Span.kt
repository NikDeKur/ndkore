@file:Suppress("NOTHING_TO_INLINE")

package dev.nikdekur.ndkore.text

import dev.nikdekur.ndkore.ext.BitField64
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable

public typealias Text = Collection<Span>

@Serializable
public data class Span(
    val text: String,
    val flags: BitField64,
    val data: Map<String, @Contextual Any>? = null,
    val count: Int = 1
) {
    init {
        require(count > 0) { "count must be > 0" }
    }


    @Suppress("MayBeConstant")
    public companion object {
        public val NEW_LINE: Int = 0
        public val BOLD: Int = 1
        public val ITALIC: Int = 2
        public val UNDERLINE: Int = 3
        public val STRIKETHROUGH: Int = 4
        public val SPOILER: Int = 5
        public val INLINE_CODE: Int = 6


        public val LINK: String = "link"
        public val COLOR: String = "color"
        public val CODE_LANG: String = "lang"

        public val NewLine: Span = Span(
            text = "",
            flags = BitField64.NONE.set(NEW_LINE, true),
            data = null,
            count = 1
        )

    }

    public class Builder {
        private var flags: BitField64 = BitField64.NONE
        private var data: MutableMap<String, Any>? = null
        private var count: Int = 1

        public fun count(count: Int): Builder {
            this.count = count
            return this
        }

        public fun flags(flags: BitField64): Builder {
            this.flags = flags
            return this
        }

        public fun data(newData: Map<String, Any>): Builder {
            data = newData.toMutableMap()
            return this
        }

        public fun flag(i: Int, value: Boolean = true): Builder {
            flags = flags.set(i, value)
            return this
        }

        public fun value(key: String, value: Any): Builder {
            val data = data ?: hashMapOf<String, Any>().also { data = it }
            data[key] = value
            return this
        }

        public fun build(text: String): Span {
            return Span(
                text = text,
                flags = flags,
                data = data,
                count = count
            )
        }
    }
}


public inline fun span(text: String, block: Span.Builder.() -> Unit = {}): Span {
    return Span.Builder().apply(block).build(text)
}