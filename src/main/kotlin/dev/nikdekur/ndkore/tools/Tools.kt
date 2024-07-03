/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 * Copyright (c) 2024 Nik De Kur
 */

@file:Suppress("NOTHING_TO_INLINE")

package dev.nikdekur.ndkore.tools

import com.google.common.reflect.ClassPath
import dev.nikdekur.ndkore.duration.DurationFormatter.Companion.format
import java.io.File
import java.io.IOException
import java.net.URI
import java.net.URISyntaxException
import java.net.URLDecoder
import java.nio.file.Files
import java.nio.file.Paths
import java.nio.file.StandardCopyOption
import java.time.Duration
import java.time.OffsetDateTime
import java.util.*
import java.util.jar.JarEntry
import java.util.jar.JarFile


@Suppress("unused")
object Tools {




    @JvmOverloads
    fun packDateTimeBeautiful(time: OffsetDateTime = OffsetDateTime.now()): String {
        var day = time.dayOfMonth.toString()
        if (day.length == 1) {
            day = "0$day"
        }
        var month = time.monthValue.toString()
        if (month.length == 1) {
            month = "0$month"
        }
        val year = time.year.toString()
        var hour = time.hour.toString()
        if (hour.length == 1) {
            hour = "0$hour"
        }
        var minute = time.minute.toString()
        if (minute.length == 1) {
            minute = "0$minute"
        }
        var second = time.second.toString()
        if (second.length == 1) {
            second = "0$second"
        }
        return "$day.$month.$year $hour:$minute:$second"
    }

    inline fun packDuration(duration: Duration): String {
        return duration.toString().replaceFirst("PT", "")
    }

    inline fun packDurationBeautiful(duration: Duration, language: String): String {
        return format(duration, language)
    }

    inline fun unpackDuration(string: String): Duration {
        return Duration.parse("PT$string")
    }


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

    fun findFileInJar(filePath: String): File? {
        // Получаем путь к текущему JAR-файлу
        val jarPath = Tools::class.java.protectionDomain.codeSource.location.path
        try {
            JarFile(jarPath).use { jarFile ->
                // Перебираем все записи внутри JAR-файла
                val entries: Enumeration<JarEntry> = jarFile.entries()
                while (entries.hasMoreElements()) {
                    val entry: JarEntry = entries.nextElement()
                    // Проверяем, является ли текущая запись файлом и имеет ли нужный путь
                    if (!entry.isDirectory && (entry.name == filePath)) {
                        // Найден нужный файл внутри JAR-файла

                        // Создаем временный файл для копирования содержимого
                        val tempFile: File = File.createTempFile("tempfile", ".tmp")
                        tempFile.deleteOnExit()
                        jarFile.getInputStream(entry).use { inputStream ->
                            Files.copy(
                                inputStream,
                                tempFile.toPath(),
                                StandardCopyOption.REPLACE_EXISTING
                            )
                        }

                        // Возвращаем объект File для временного файла
                        return tempFile
                    }
                }

                // Файл не найден
                return null
            }
        } catch (e: IOException) {
            e.printStackTrace()
            return null
        }
    }

    val jarPath: String
        get() = Paths.get(jarFile.toURI()).toAbsolutePath().toString()


    val jarFile: File
        get() {
            val klass: Class<*> = Tools::class.java
            val className = klass.simpleName + ".class"
            val classPath = klass.getResource(className)!!.toString()
            return if (classPath.startsWith("jar:file:")) {
                try {
                    val jarFilePath = classPath.substring("jar:file:".length, classPath.indexOf("!"))
                    File(URI(jarFilePath))
                } catch (e: URISyntaxException) {
                    throw RuntimeException(e)
                }
            } else if (classPath.startsWith("file:")) {
                val endIndex = classPath.indexOf(className)
                val filePath = classPath.substring("file:".length, endIndex)
                File(filePath)
            } else {
                throw UnsupportedOperationException("Unsupported URL format: $classPath")
            }
        }

    class FileNotFoundException(fileName: String) : RuntimeException(fileName)


    fun findClasses(classLoader: ClassLoader, packageName: String): List<Class<*>> {
        return ClassPath.from(classLoader)
            .allClasses
            .filter{
                it.packageName.startsWith(packageName)
            }
            .map {
                it.load()
            }
    }
}






