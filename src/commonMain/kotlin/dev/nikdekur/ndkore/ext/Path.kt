package dev.nikdekur.ndkore.ext

import kotlinx.io.files.Path

public inline val Path.extension: String
    get() = name.replaceBeforeLast(".", "").replace(".", "")

public inline val Path.nameWithoutExtension: String
    get() = name.replaceAfterLast(".", "").replace(".", "")