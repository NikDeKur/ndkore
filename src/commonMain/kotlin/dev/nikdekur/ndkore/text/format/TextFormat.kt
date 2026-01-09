package dev.nikdekur.ndkore.text.format

import dev.nikdekur.ndkore.text.Text

public interface TextFormat {
    public fun format(text: Text): String
}