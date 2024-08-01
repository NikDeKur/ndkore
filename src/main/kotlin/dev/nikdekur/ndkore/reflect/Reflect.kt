/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 * Copyright (c) 2024-present "Nik De Kur"
 */

@file:Suppress("NOTHING_TO_INLINE")

package dev.nikdekur.ndkore.reflect

import dev.nikdekur.ndkore.ext.*
import sun.misc.Unsafe
import java.lang.reflect.Field
import java.lang.reflect.Method

/**
 * A utility object for performing reflection-based operations on classes and objects.
 *
 * This object provides various methods for inspecting and manipulating class fields and methods,
 * as well as searching for fields and methods either in a specific class or recursively through
 * its superclass hierarchy.
 * Additionally, it supports invoking methods and accessing the `Unsafe` instance.
 */
object Reflect {

    /**
     * Retrieves all fields of a class and its superclasses, along with their values from the given object.
     *
     * This method traverses the class hierarchy starting from the specified class and collects
     * the values of all declared fields.
     * It collects fields from the class itself and its
     * superclasses up to the root of the hierarchy.
     *
     * @param clazz The class from which to start retrieving fields.
     * @param obj The object from which to retrieve the field values. If `null`, field values cannot be retrieved.
     * @return A map where the keys are field names and the values are the corresponding field values from the given object.
     */
    fun getClassFields(clazz: Class<*>, obj: Any?): Map<String, Any?> {
        val fieldsMap: HashMap<String, Any?> = HashMap()
        var objClass: Class<*>? = clazz
        while (objClass != null) {
            val fields = objClass.declaredFields
            for (f in fields) {
                f.withUnlock {
                    fieldsMap[f.name] = f[obj]
                }
            }
            objClass = objClass.superclass
        }
        return fieldsMap
    }

    /**
     * Retrieves all methods of a class and its superclasses.
     *
     * This method collects all declared methods starting from the specified class and includes
     * methods from its superclasses.
     *
     * @param clazz The class from which to start retrieving methods.
     * @return A map where the keys are method names and the values are `Method` objects representing the methods.
     */
    fun getClassMethods(clazz: Class<*>): HashMap<String, Method> {
        var objClass: Class<*>? = clazz
        val methodsMap: HashMap<String, Method> = HashMap()

        while (objClass != null) {
            val methods = objClass.declaredMethods
            for (m in methods) {
                methodsMap[m.name] = m
            }
            objClass = objClass.superclass
        }

        return methodsMap
    }

    /**
     * Searches for a field with the specified name in the given class.
     *
     * This method looks for a field in the specified class only.
     * If the field is not found, it returns `null`.
     *
     * @param clazz The class in which to search for the field.
     * @param name The name of the field to search for.
     * @return The `Field` object representing the field if found, or `null` if the field does not exist.
     */
    fun searchField(clazz: Class<*>, name: String): Field? {
        return try {
            clazz.getDeclaredField(name)
        } catch (_: NoSuchFieldException) {
            null
        }
    }

    /**
     * Searches for a field with the specified name in the given class and its superclasses.
     *
     * This method recursively searches for the field starting from the specified class and includes
     * its superclasses.
     * If the field is found, it returns the `Field` object; otherwise, it returns `null`.
     *
     * @param clazz The class in which to start the search.
     * @param name The name of the field to search for.
     * @return The `Field` object representing the field if found, or `null` if the field does not exist.
     */
    fun searchFieldRecursive(clazz: Class<*>, name: String): Field? {
        var objClass: Class<*>? = clazz
        while (objClass != null) {
            val field = try {
                objClass.getDeclaredField(name)
            } catch (_: NoSuchFieldException) {
                null
            }

            if (field != null) {
                return field
            }

            objClass = objClass.superclass
        }

        return null
    }

    /**
     * Searches for a method with the specified name and parameter types in the given class.
     *
     * This method looks for a method in the specified class with the given name and parameter types.
     * If the method is not found, it returns `null`.
     *
     * @param clazz The class in which to search for the method.
     * @param name The name of the method to search for.
     * @param classes The parameter types of the method.
     * @return The `Method` object representing the method if found, or `null` if the method does not exist.
     */
    fun searchMethod(clazz: Class<*>, name: String, classes: Array<out Class<*>>): Method? {
        return try {
            clazz.getMethod(name, *classes)
        } catch (_: NoSuchMethodException) {
            null
        }
    }

    /**
     * Searches for a method with the specified name and parameter types in the given class and its superclasses.
     *
     * This method recursively searches for the method starting from the specified class and includes
     * its superclasses.
     * If the method is found, it returns the `Method` object; otherwise, it returns `null`.
     *
     * @param clazz The class in which to start the search.
     * @param name The name of the method to search for.
     * @param classes The parameter types of the method.
     * @return The `Method` object representing the method if found, or `null` if the method does not exist.
     */
    fun searchMethodRecursive(clazz: Class<*>, name: String, classes: Array<out Class<*>>): Method? {
        var objClass: Class<*>? = clazz
        while (objClass != null) {
            val method = try {
                objClass.getMethod(name, *classes)
            } catch (_: NoSuchMethodException) {
                null
            }

            if (method != null) {
                return method
            }

            objClass = objClass.superclass
        }

        return null
    }

    /**
     * Retrieves the value of a field from the given object.
     *
     * This method first searches for the field recursively starting from the specified class.
     * If the field is found,
     * it retrieves its value from the given object.
     * If the field is not found, it returns a `ReflectResult.Missing` instance.
     *
     * @param clazz The class in which to start the search for the field.
     * @param obj The object from which to retrieve the field value.
     * @param name The name of the field whose value is to be retrieved.
     * @return A `ReflectResult` object containing the value of the field if found,
     * or `ReflectResult.Missing` if the field does not exist.
     */
    fun getFieldValue(clazz: Class<*>, obj: Any?, name: String): ReflectResult {
        val field = searchFieldRecursive(clazz, name)
        return if (field != null) {
            ReflectResult(field.withUnlock {
                field[obj]
            })
        } else {
            ReflectResult.Missing
        }
    }

    /**
     * Sets the value of a field in the given object.
     *
     * This method first searches for the field recursively starting from the specified class.
     * If the field is found,
     * it sets its value in the given object.
     * If the field is not found, no action is taken.
     *
     * @param clazz The class in which to start the search for the field.
     * @param obj The object in which to set the field value.
     * @param name The name of the field whose value is to be set.
     * @param value The value to be set in the field.
     */
    fun setFieldValue(clazz: Class<*>, obj: Any?, name: String, value: Any?) {
        val field = searchFieldRecursive(clazz, name)
        field?.withUnlock {
            field[obj] = value
        }
    }

    /**
     * Invokes a method on the given object with specified arguments and returns the result.
     *
     * This method searches for the method recursively starting from the specified class.
     * If the method is found,
     * it is invoked with the given arguments.
     * If the method is not found, it returns `ReflectResult.Missing`.
     *
     * @param clazz The class in which to start the search for the method.
     * @param obj The object on which to invoke the method.
     * @param name The name of the method to be invoked.
     * @param classes The parameter types of the method.
     * @param args The arguments to be passed to the method.
     * @return A `ReflectResult` object containing the result of the method invocation if the method is found,
     * or `ReflectResult.Missing` if the method does not exist.
     */
    inline fun callMethodTyped(clazz: Class<*>, obj: Any?, name: String, classes: Array<out Class<*>>, vararg args: Any?): ReflectResult {
        val method = searchMethodRecursive(clazz, name, classes) ?: return ReflectResult.Missing
        return ReflectResult(method.withUnlock {
            method.invoke(obj, *args)
        })
    }

    /**
     * Invokes a method on the given object with specified arguments and returns the result.
     *
     * This method infers the parameter types from the provided arguments.
     * It then searches for the method recursively
     * starting from the specified class.
     * If the method is found, it is invoked with the given arguments.
     * If the method is not found, it returns `ReflectResult.Missing`.
     *
     * @param clazz The class in which to start the search for the method.
     * @param obj The object on which to invoke the method.
     * @param name The name of the method to be invoked.
     * @param args The arguments to be passed to the method.
     * @return A `ReflectResult` object containing the result of the method invocation if the method is found,
     * or `ReflectResult.Missing` if the method does not exist.
     */
    inline fun callMethod(clazz: Class<*>, obj: Any?, name: String, vararg args: Any?): ReflectResult {
        val classes = args.mapNotNull { it?.javaClass }.toTypedArray()
        return callMethodTyped(clazz, obj, name, classes, *args)
    }

    /**
     * Retrieves the `Unsafe` instance for low-level operations.
     *
     * This method attempts to retrieve the `Unsafe` instance, which is a class used for low-level operations.
     * It retrieves the instance by accessing the `theUnsafe` field in the `Unsafe` class.
     * If the field is not accessible,
     * it throws an `IllegalAccessException`.
     *
     * @return The `Unsafe` instance.
     * @throws IllegalAccessException If the `Unsafe` instance could not be accessed.
     */
    fun getUnsafe(): Unsafe {
        val result = getFieldValue(Unsafe::class.java, null, "theUnsafe")
        if (result is ReflectResult.Missing) {
            throw IllegalAccessException("Failed to get Unsafe instance")
        }

        return result.value as Unsafe
    }
}