package dev.nikdekur.ndkore.text.build

import dev.nikdekur.ndkore.text.Span

public class MergeTextBuilder : TextBuilder {
    public val spans: MutableCollection<Span> = ArrayList()

    public var last: Span? = null

    override fun append(span: Span): TextBuilder {
        val last = last
        if (last == null) {
            this.last = span
            return this
        }

        if (!canMerge(last, span)) {
            spans.add(last)
            this.last = span
            return this
        }

        this.last = merge(last, span)
        return this
    }

    public fun canMerge(a: Span, b: Span): Boolean {
        if (a.flags != b.flags) return false
        if (a.data != b.data) return false
        return true
    }

    public fun merge(a: Span, b: Span): Span {
        return when {
            a.text == b.text -> a.copy(count = a.count + b.count)
            else -> {
                val text = buildString {
                    repeat(a.count) { append(a.text) }
                    repeat(b.count) { append(b.text) }
                }
                a.copy(text = text, count = 1)
            }
        }
    }

    override fun collect(): Collection<Span> {
        val last = last
        if (last != null) {
            spans.add(last)
            this.last = null
        }
        return spans.toList()
    }
}

public inline fun mergedText(block: MergeTextBuilder.() -> Unit): Collection<Span> {
    return MergeTextBuilder().apply(block).collect()
}