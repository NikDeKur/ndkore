/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 * Copyright (c) 2024-present "Nik De Kur"
 */

@file:Suppress("NOTHING_TO_INLINE", "OVERRIDE_BY_INLINE")

package dev.nikdekur.ndkore.reflect

import java.io.File
import java.util.function.Predicate

/**
 * A utility class for finding and working with classes within a specified package.
 *
 * This class provides functionality to discover classes within a given package, filter them based on custom criteria,
 * create instances of these classes, and handle exceptions that might occur during the class discovery process.
 * It is useful in scenarios such as plugin systems or dynamic class loading where classes need to be discovered
 * and instantiated at runtime.
 *
 * **Overview of `ClassFinder`:**
 * - **Class Discovery:** Finds all classes within the specified package.
 * - **Filtering:** Allows filtering classes based on custom criteria.
 * - **Instance Creation:** Provides functionality to create instances of found classes.
 * - **Error Handling:** Customizable error handling during class discovery.
 *
 * **Properties:**
 * - `classLoader`: The class loader used for loading classes.
 * - `packageName`: The name of the package to search for classes in.
 *
 * @param classLoader The class loader used for loading classes.
 * @param packageName The name of the package to search for classes in.
 */
open class ClassFinder(val classLoader: ClassLoader, val packageName: String) {

    /**
     * Determines whether a class should be included in the results based on custom filtering logic.
     *
     * This method is called after a class is found in the package.
     * Subclasses can override this method
     * to apply specific filtering criteria.
     *
     * @param clazz The class object to filter.
     * @return `true` if the class should be included in the results, `false` otherwise.
     */
    open fun filter(clazz: Class<*>): Boolean {
        return true
    }

    /**
     * Handles exceptions that occur during the class discovery process.
     *
     * This method is called when an error occurs while trying to find classes.
     * By default, it returns
     * an empty list, but subclasses can override it to provide custom error handling.
     *
     * @param e The exception that occurred.
     * @return A list of classes that should be returned in the case of an error. By default, returns an empty list.
     */
    open fun onGlobalError(e: Throwable): List<Class<*>> {
        return emptyList()
    }

    /**
     * Finds a class by its fully qualified name.
     *
     * This method attempts to load a class with the specified name.
     * If the class cannot be found,
     * it returns `null`.
     *
     * @param name The fully qualified name of the class to find.
     * @return The class object if found, or `null` if the class cannot be found.
     */
    open fun findClass(name: String): Class<*>? {
        return try {
            Class.forName(name)
        } catch (e: ClassNotFoundException) {
            null
        }
    }

    /**
     * Creates a new instance of a class.
     *
     * This method attempts to create a new instance of the specified class.
     * If instantiation fails,
     * it returns `null`.
     *
     * @param clazz The class object to create an instance of.
     * @return The new instance of the class if created successfully, or `null` otherwise.
     */
    open fun newInstance(clazz: Class<*>): Any? {
        return try {
            clazz.newInstance()
        } catch (e: Throwable) {
            null
        }
    }

    /**
     * Finds all classes in the specified package.
     *
     * This method scans the package directory for `.class` files, loads the class objects, applies
     * the filter, and returns a list of classes that pass the filter criteria.
     *
     * @return A list of classes found in the package, after applying the filter.
     */
    fun findClasses(): List<Class<*>> {
        val path = packageName.replace('.', '/')
        return try {
            val resources = classLoader.getResources(path)
            val classes = mutableListOf<Class<*>>()
            while (resources.hasMoreElements()) {
                val resource = resources.nextElement()
                if (resource.protocol != "file") continue
                val directory = File(resource.file)
                val files = directory.listFiles() ?: continue
                for (file in files) {
                    if (!file.isFile || !file.name.endsWith(".class")) continue
                    val className = packageName + '.' + file.name.substring(0, file.name.length - 6)
                    val clazz = findClass(className) ?: continue
                    if (!filter(clazz)) continue
                    classes.add(clazz)
                }
            }
            classes
        } catch (e: Throwable) {
            onGlobalError(e)
        }
    }

    /**
     * Finds and creates instances of classes in the specified package.
     *
     * This method first finds all classes in the package and then attempts to create instances of
     * these classes.
     * Classes that cannot be instantiated are excluded from the result.
     *
     * @return An instance's list of the classes found in the package that could be instantiated.
     */
    fun findInstances(): List<Any> {
        return findClasses().mapNotNull(this::newInstance)
    }

    companion object {

        /**
         * Creates a new instance of `ClassFinder` with a custom filter.
         *
         * This factory method allows creating a `ClassFinder` with a specific filter function applied to
         * determine which classes should be included.
         *
         * @param packageName The package name to search for classes in.
         * @param classLoader The class loader to use for class loading.
         * @param filter The filter function to apply to each class to determine if it should be included.
         * @return A new `ClassFinder` instance with the specified parameters.
         */
        inline fun new(packageName: String, classLoader: ClassLoader, filter: Predicate<Class<*>>): ClassFinder {
            return object : ClassFinder(classLoader, packageName) {
                override fun filter(clazz: Class<*>): Boolean {
                    return filter.test(clazz)
                }
            }
        }

        /**
         * Finds all classes in a package using a custom filter.
         *
         * This utility method creates a new `ClassFinder` instance and uses it to find classes in the
         * specified package with the provided filter function.
         *
         * @param packageName The package name to search for classes in.
         * @param classLoader The class loader to use for class loading.
         * @param filter The filter function to apply to each class to determine if it should be included.
         * @return A list of classes found in the package that pass the filter.
         */
        inline fun findClasses(packageName: String, classLoader: ClassLoader, filter: Predicate<Class<*>>): List<Class<*>> {
            return new(packageName, classLoader, filter).findClasses()
        }

        /**
         * Finds and creates instances of classes in a package using a custom filter.
         *
         * This utility method creates a new `ClassFinder` instance and uses it to find classes and
         * create instances of these classes in the specified package with the provided filter function.
         *
         * @param packageName The package name to search for classes in.
         * @param classLoader The class loader to use for class loading.
         * @param filter The filter function to apply to each class to determine if it should be included.
         * @return An instance's list of the classes found in the package that could be instantiated.
         */
        inline fun findInstances(packageName: String, classLoader: ClassLoader, filter: Predicate<Class<*>>): List<Any> {
            return new(packageName, classLoader, filter).findInstances()
        }
    }
}
