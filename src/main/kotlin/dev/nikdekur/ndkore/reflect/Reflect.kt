/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 * Copyright (c) 2024 Nik De Kur
 */

@file:Suppress("NOTHING_TO_INLINE")

package dev.nikdekur.ndkore.reflect

import dev.nikdekur.ndkore.ext.*
import sun.misc.Unsafe
import java.lang.reflect.Field
import java.lang.reflect.Method

object Reflect {

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

    fun searchField(clazz: Class<*>, name: String): Field? {
        return try {
            clazz.getDeclaredField(name)
        } catch (_: NoSuchFieldException) {
            null
        }
    }

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


    fun searchMethod(clazz: Class<*>, name: String, classes: Array<out Class<*>>): Method? {
        return try {
            clazz.getMethod(name, *classes)
        } catch (_: NoSuchMethodException) {
            null
        }
    }

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

    fun setFieldValue(clazz: Class<*>, obj: Any?, name: String, value: Any?) {
        val field = searchFieldRecursive(clazz, name)
        field?.withUnlock {
            field[obj] = value
        }
    }


    inline fun callMethodTyped(clazz: Class<*>, obj: Any?, name: String, classes: Array<out Class<*>>, vararg args: Any?): ReflectResult {
        val method = searchMethodRecursive(clazz, name, classes) ?: return ReflectResult.Missing
        return ReflectResult(method.withUnlock {
            method.invoke(obj, *args)
        })
    }

    inline fun callMethod(clazz: Class<*>, obj: Any?, name: String, vararg args: Any?): ReflectResult {
        val classes = args.mapNotNull { it?.javaClass }.toTypedArray()
        return callMethodTyped(clazz, obj, name, classes, *args)
    }

    fun getUnsafe(): Unsafe {
        val result = getFieldValue(Unsafe::class.java, null, "theUnsafe")
        if (result is ReflectResult.Missing) {
            throw IllegalAccessException("Failed to get Unsafe instance")
        }

        return result.value as Unsafe
    }
}
