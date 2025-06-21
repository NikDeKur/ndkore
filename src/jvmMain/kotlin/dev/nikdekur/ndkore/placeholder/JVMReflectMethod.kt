/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 * Copyright (c) 2024-present "Nik De Kur"
 */

package dev.nikdekur.ndkore.placeholder

import dev.nikdekur.ndkore.ext.asCamelCaseGetter
import dev.nikdekur.ndkore.ext.getNested
import dev.nikdekur.ndkore.ext.r_CallMethod
import dev.nikdekur.ndkore.ext.r_GetField
import dev.nikdekur.ndkore.reflect.ReflectMethod
import dev.nikdekur.ndkore.reflect.ReflectMethod.NotFound
import dev.nikdekur.ndkore.reflect.ReflectResult


public open class JVMReflectMethod : ReflectMethod {

    override fun findValue(obj: Any, name: String): Any? {
        findAsObject(obj, name).takeIf { it !is NotFound }?.let { return it }
        findAsField(obj, name).takeIf { it !is NotFound }?.let { return it }
        findAsMethod(obj, name).takeIf { it !is NotFound }?.let { return it }

        return NotFound
    }

    public open fun findAsObject(obj: Any, name: String): Any? {
        if (obj is Placeholder) {
            val value = obj.getPlaceholder(name)
            if (value != NotFound) return value
        }

        if (obj is Map<*, *>) {
            val value = obj.getNested(name.split("."))
            if (value != null) return value
        }

        if (obj is Iterable<*>) {
            val index = name.toIntOrNull()
            if (index != null) {
                val value = obj.elementAtOrNull(index)
                if (value != null) return value
            } else {
                // Not an index, try to find placeholder in each element
                val result = obj.mapNotNull {
                    if (it == null) return@mapNotNull null
                    val find = findValue(it, name)
                    if (find == NotFound) return@mapNotNull null
                    find
                }
                val actual = result.firstOrNull()
                if (actual != null) return actual
            }
        }

        return NotFound
    }

    public open fun findAsField(obj: Any, name: String): Any? {
        val result = obj.r_GetField(name)
        if (result != ReflectResult.Missing) return result.value

        return NotFound
    }

    public open fun findAsMethod(obj: Any, name: String): Any? {
        val result1 = obj.r_CallMethod(name)
        if (result1 != ReflectResult.Missing) return result1.value

        val result2 = obj.r_CallMethod(name.asCamelCaseGetter())
        if (result2 != ReflectResult.Missing) return result2.value

        return NotFound
    }

    public companion object Default : JVMReflectMethod()
}