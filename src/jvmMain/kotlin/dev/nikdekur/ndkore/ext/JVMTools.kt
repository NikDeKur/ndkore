/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 * Copyright (c) 2024-present "Nik De Kur"
 */

package dev.nikdekur.ndkore.ext

import java.io.File
import java.net.URLDecoder
import java.nio.file.Files
import java.nio.file.StandardCopyOption
import java.util.*
import java.util.jar.JarEntry
import java.util.jar.JarFile


class FileNotFoundException(fileName: String) : RuntimeException(fileName)

fun getResource(relativePath: String): File {
    val classLoader = Thread.currentThread().contextClassLoader
    val resourceUrl = classLoader.getResource(relativePath)
    return if (resourceUrl != null) {
        File(URLDecoder.decode(resourceUrl.file, "utf-8"))
    } else {
        throw FileNotFoundException(relativePath)
    }
}

fun getResourcePath(relativePath: String): String {
    val classLoader = Thread.currentThread().contextClassLoader
    val resourceUrl = classLoader.getResource(relativePath)
    return if (resourceUrl != null) {
        URLDecoder.decode(resourceUrl.file, "utf-8")
    } else {
        throw FileNotFoundException(relativePath)
    }
}

fun findFileInJar(jarClass: Class<*>, filePath: String): File? {
    // Get the path to the JAR-file
    val jarPath = jarClass.protectionDomain.codeSource.location.path
    JarFile(jarPath).use { jarFile ->
        // Iterate over the entries and look for the file
        val entries: Enumeration<JarEntry> = jarFile.entries()
        while (entries.hasMoreElements()) {
            val entry: JarEntry = entries.nextElement()
            // Check if the entry is a file and has the correct name
            if (!entry.isDirectory && (entry.name == filePath)) {
                // File found

                // Create a temporary file and copy the contents of the entry to it
                val tempFile: File = File.createTempFile("tempfile", ".tmp")
                tempFile.deleteOnExit()
                jarFile.getInputStream(entry).use { inputStream ->
                    Files.copy(
                        inputStream,
                        tempFile.toPath(),
                        StandardCopyOption.REPLACE_EXISTING
                    )
                }

                // Return the temporary file
                return tempFile
            }
        }

        // File not found
        return null
    }
}









