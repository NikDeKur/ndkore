/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 * Copyright (c) 2024 Nik De Kur
 */

@file:Suppress("NOTHING_TO_INLINE", "OVERRIDE_BY_INLINE")

package dev.nikdekur.ndkore.reflect

import java.io.File
import java.util.function.Predicate

/**
 * Class finder for finding classes in a package.
 *
 * Has a functionality to filter classes, create instances of classes and handle exceptions.
 * Most of those methods are open for overriding.
 *
 * @param classLoader The class loader to use for class loading.
 * @param packageName The package name to search for classes in.
 */
open class ClassFinder(val classLoader: ClassLoader, val packageName: String) {

    /**
     * Filter classes by class object.
     *
     * Called after finding a class and deciding whether to add it to the result list.
     *
     * @param clazz The class object to filter.
     * @return True if the class should be added to the result list, false otherwise.
     */
    open fun filter(clazz: Class<*>): Boolean {
        return true
    }

    /**
     * Called when an exception occurs during class search.
     *
     * @param e The exception that occurred.
     * @return A list that will be returned by the findClasses method.
     */
    open fun onGlobalError(e: Throwable): List<Class<*>> {
        return emptyList()
    }

    /**
     * Find a class by name.
     *
     * Called when a class is found in the package. If the class is not found, it will return null.
     *
     * If null is returned, class will not be added to the result list.
     *
     * @param name The name of the class to find.
     * @return The class object if found, null otherwise.
     */
    open fun findClass(name: String): Class<*>? {
        return try {
            Class.forName(name)
        } catch (e: ClassNotFoundException) {
            null
        }
    }

    /**
     * Create a new instance of a class.
     *
     * Called after finding a class and deciding to create an instance of it.
     *
     * If null is returned, the class will not be added to the result list.
     *
     * @param clazz The class object to create an instance of.
     * @return The instance of the class if created, null otherwise.
     */
    open fun newInstance(clazz: Class<*>): Any? {
        return try {
            clazz.newInstance()
        } catch (e: Throwable) {
            null
        }
    }

    /**
     * Find classes in the package.
     *
     * @return A list of classes found in the package.
     */
    fun findClasses(): List<Class<*>> {
        val packageName = packageName
        return try {
            val path = packageName.replace('.', '/')
            val resources = classLoader.getResources(path)
            val classes = ArrayList<Class<*>>()
            while (resources.hasMoreElements()) {
                val resource = resources.nextElement()
                if (resource.protocol != "file") continue
                val directory = File(resource.file)
                val files = directory.listFiles() ?: continue
                for (file: File in files) {
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
     * Find instances of classes in the package.
     *
     * @return A list of classes' instances found in the package.
     */
    fun findInstances(): List<Any> {
        return findClasses().mapNotNull(this::newInstance)
    }

    companion object {

        /**
         * Create a new instance of the class finder.
         *
         * @param packageName The package name to search for classes in.
         * @param classLoader The class loader to use for class loading.
         * @param filter The filter to use for filtering classes.
         * @return The created class finder.
         */
        inline fun new(packageName: String, classLoader: ClassLoader, filter: Predicate<Class<*>>): ClassFinder {
            return object : ClassFinder(classLoader, packageName) {
                override inline fun filter(clazz: Class<*>): Boolean {
                    return filter.test(clazz)
                }
            }
        }

        /**
         * Find classes in the package.
         *
         * @param packageName The package name to search for classes in.
         * @param classLoader The class loader to use for class loading.
         * @param filter The filter to use for filtering classes.
         * @return A list of classes found in the package.
         */
        inline fun findClasses(packageName: String, classLoader: ClassLoader, filter: Predicate<Class<*>>): List<Class<*>> {
            return new(packageName, classLoader, filter).findClasses()
        }

        /**
         * Find instances of classes in the package.
         *
         * @param packageName The package name to search for classes in.
         * @param classLoader The class loader to use for class loading.
         * @param filter The filter to use for filtering classes.
         * @return A list of classes' instances found in the package.
         */
        inline fun findInstances(packageName: String, classLoader: ClassLoader, filter: Predicate<Class<*>>): List<Any> {
            return new(packageName, classLoader, filter).findInstances()
        }
    }
}