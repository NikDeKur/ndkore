package dev.nikdekur.ndkore.text.format.tg

import dev.nikdekur.ndkore.text.Span
import dev.nikdekur.ndkore.text.Text
import dev.nikdekur.ndkore.text.format.TextFormat

public object TelegramMarkdown2 : TextFormat {
    override fun format(text: Text): String = buildString {
        text.forEach { span ->
            repeat(span.count) { renderSpan(span) }
        }
    }

    public fun StringBuilder.renderSpan(span: Span) {
        val content = span.text
        val escapedContent = escape(content)

        val flags = span.flags
        val data = span.data

        val blockCodeLang = data?.get(Span.CODE_LANG) as? String
        if (blockCodeLang != null) {
            append("```").append(blockCodeLang).appendLine()
            append(content)
            appendLine().append("```")
            return // Skip further formatting for block code
        }

        val newLine = flags[Span.NEW_LINE]
        val bold = flags[Span.BOLD]
        val italic = flags[Span.ITALIC]
        val underline = flags[Span.UNDERLINE]
        val strikethrough = flags[Span.STRIKETHROUGH]
        val spoiler = flags[Span.SPOILER]
        val inlineCode = flags[Span.INLINE_CODE]

        // Telegram specific
        val blockQuote = flags[TelegramFormat.BLOCK_QUOTE]
        val expandableQuote = flags[TelegramFormat.EXPANDABLE_QUOTE]

        if (expandableQuote) append("**>")
        else if (blockQuote) append(">")

        if (spoiler) append("||")
        if (inlineCode) append("`")
        if (bold) append("*")
        if (italic) append("_")
        if (underline) append("__")
        if (strikethrough) append("~")


        val link = data?.get(Span.LINK) as? String

        when {
            link != null -> append("[").append(escapedContent).append("](").append(link).append(")")
            inlineCode -> append(content)
            else -> append(escapedContent)
        }

        if (strikethrough) append("~")
        if (underline) append("__")
        if (italic) append("_")
        if (bold) append("*")
        if (inlineCode) append("`")
        if (spoiler) append("||")

        if (newLine) appendLine()
    }

    private val ESCAPE_SET: Set<Char> = setOf(
        '_', '*', '[', ']', '(', ')',
        '~', '`', '>', '#', '+', '-',
        '=', '|', '{', '}', '.', '!'
    )

    public fun escape(text: String): String = buildString(text.length + 8) {
        text.forEach { char ->
            if (char in ESCAPE_SET) append('\\')
            append(char)
        }
    }
}