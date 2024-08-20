/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 * Copyright (c) 2024-present "Nik De Kur"
 */

package dev.nikdekur.ndkore.reflect

import com.google.common.reflect.ClassPath

/**
 * # ClassPathClassFinder
 *
 * `ClassPathClassFinder` is an advanced implementation of the `ClassFinder` interface.
 *
 * This implementation leverages Google's Guava library to scan the class path for classes
 * in the specified package. It uses `ClassPath.from(classLoader)` to find top-level classes
 * recursively within the package, making it well-suited for environments where multiple class
 * loaders are used or when classes are loaded from non-file-based resources (e.g., JAR files).
 *
 * `ClassPathClassFinder` is recommended when you are dealing with complex class loading environments
 * that require a more robust solution. However, it requires the Guava library as an additional dependency,
 * which may not be necessary for simpler scenarios.
 *
 * ### Example usage:
 * ```kotlin
 * val classFinder: ClassFinder = ClassPathClassFinder
 * classFinder.find(javaClass.classLoader, "com.example.package", ::println)
 *
 * // Example output:
 * // - class com.example.package.ClassName
 * // - class com.example.package.AnotherClassName
 * ```
 *
 * @see ClassFinder
 * @see ClassLoader
 * @see com.google.common.reflect.ClassPath
 */
object ClassPathClassFinder : ClassFinder {

    override fun find(classLoader: ClassLoader, packageName: String, consumer: (Class<*>) -> Unit) {
        ClassPath.from(classLoader)
            .getTopLevelClassesRecursive(packageName)
            .forEach {
                consumer(it.load())
            }
    }
}