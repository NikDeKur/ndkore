@file:Suppress("NOTHING_TO_INLINE")

package dev.nikdekur.ndkore.text.build

import dev.nikdekur.ndkore.text.Span

public interface TextBuilder {
    public fun append(span: Span): TextBuilder

    public fun collect(): Collection<Span>
}

public inline operator fun TextBuilder.plusAssign(span: Span) {
    append(span)
}

public inline fun TextBuilder.span(text: String, spanBuilder: Span.Builder.() -> Unit = {}): TextBuilder {
    val span = dev.nikdekur.ndkore.text.span(text, spanBuilder)
    append(span)
    return this
}

public inline fun TextBuilder.newLine(builder: Span.Builder.() -> Unit = {}): TextBuilder {
    return span("") {
        flag(Span.NEW_LINE)
        builder()
    }
}