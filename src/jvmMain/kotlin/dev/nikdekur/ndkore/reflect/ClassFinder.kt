/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 * Copyright (c) 2024-present "Nik De Kur"
 */

package dev.nikdekur.ndkore.reflect


/**
 * # Class Finder
 *
 * A class finder is used to find classes in a specified package.
 *
 * Finding classes is a hard mechanism to implement,
 * so this interface provides a way to abstract the class finding logic.
 * Implementations of this interface can use different mechanisms to find classes in a package.
 *
 * There are two basic implementations of this interface:
 * - [ClassPathClassFinder] uses the Google Guava library to find classes with ClassPath.
 * - [SimpleClassFinder] uses getResource to find classes in a package.
 *
 * As default implementation [SimpleClassFinder] is recommended,
 * because it does not require any additional dependencies,
 * but in cases where multiple class loaders are used, [ClassPathClassFinder] is recommended.
 *
 * ### Example usage:
 * ```kotlin
 * val classFinder: ClassFinder = SimpleClassFinder
 * classFinder.find(javaClass.classLoader, "com.example.package", ::println)
 *
 * // Example output:
 * // - lass com.example.package.ClassName
 * // - class com.example.package.AnotherClassName
 * ```
 */
fun interface ClassFinder {

    /**
     * Finds classes in the specified package and calls the consumer for each class found.
     *
     * @param classLoader The class loader used for loading classes.
     * @param packageName The package name to search for classes in.
     * @param consumer The consumer to call for each class found.
     * @see ClassLoader
     */
    fun find(classLoader: ClassLoader, packageName: String, consumer: (Class<*>) -> Unit)
}