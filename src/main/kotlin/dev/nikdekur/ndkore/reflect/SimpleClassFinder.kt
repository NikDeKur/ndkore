/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 * Copyright (c) 2024-present "Nik De Kur"
 */

package dev.nikdekur.ndkore.reflect

import java.io.File

/**
 * # SimpleClassFinder
 *
 * `SimpleClassFinder` is a straightforward implementation of the `ClassFinder` interface.
 *
 * This implementation uses the `ClassLoader.getResources()` method to locate and find classes
 * within a specified package. It reads class files directly from the file system and attempts
 * to load them using `Class.forName()`.
 *
 * This implementation is recommended as the default choice because it does not require any additional
 * dependencies. However, it is limited to finding classes within the standard file-based class path
 * and might not be effective when dealing with more complex class loading scenarios, such as those involving
 * multiple class loaders or non-file-based resources.
 *
 * ### Example usage:
 * ```kotlin
 * val classFinder: ClassFinder = SimpleClassFinder
 * classFinder.find(javaClass.classLoader, "com.example.package", ::println)
 *
 * // Example output:
 * // - class com.example.package.ClassName
 * // - class com.example.package.AnotherClassName
 * ```
 *
 * @see ClassFinder
 * @see ClassLoader
 */
object SimpleClassFinder : ClassFinder {

    fun findClass(name: String): Class<*>? {
        return try {
            Class.forName(name)
        } catch (e: ClassNotFoundException) {
            null
        }
    }

    override fun find(
        classLoader: ClassLoader,
        packageName: String,
        consumer: (Class<*>) -> Unit
    ) {
        val path = packageName.replace('.', '/')
        val resources = classLoader.getResources(path)
        while (resources.hasMoreElements()) {
            val resource = resources.nextElement()
            if (resource.protocol != "file") continue
            val directory = File(resource.file)
            val files = directory.listFiles() ?: continue
            for (file in files) {
                if (!file.isFile || !file.name.endsWith(".class")) continue
                val className = packageName + '.' + file.name.substring(0, file.name.length - 6)
                val clazz = findClass(className) ?: continue
                consumer(clazz)
            }
        }
    }
}