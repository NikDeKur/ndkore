@file:Suppress("NOTHING_TO_INLINE")

package dev.nikdekur.ndkore.ext

import kotlinx.io.files.FileSystem
import kotlinx.io.files.Path

public inline fun FileSystem.isDirectory(path: Path): Boolean =
    metadataOrNull(path)?.isDirectory == true

public inline fun FileSystem.size(path: Path): Long =
    metadataOrNull(path)?.size ?: -1


public inline fun FileSystem.ensurePathExists(path: Path, isFile: Boolean) {
    if (exists(path)) return
    if (isFile) {
        sink(path).close()
    } else {
        createDirectories(path)
    }
}
