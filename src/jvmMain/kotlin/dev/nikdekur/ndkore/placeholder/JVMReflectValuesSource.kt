/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 * Copyright (c) 2024-present "Nik De Kur"
 */

package dev.nikdekur.ndkore.placeholder

import dev.nikdekur.ndkore.ext.asCamelCaseGetter
import dev.nikdekur.ndkore.ext.r_CallMethod
import dev.nikdekur.ndkore.ext.r_GetField
import dev.nikdekur.ndkore.reflect.ReflectResult

public object JVMReflectValuesSource : ValuesSource {

    override fun findValue(obj: Any, name: String): Any? {
        if (obj is Placeholder) {
            val value = obj.getPlaceholder(name)
            if (value != ValuesSource.NotFound) return value
        }
        val result1 = obj.r_GetField(name)
        if (result1 != ReflectResult.Missing) return result1.value

        val result2 = obj.r_CallMethod(name.asCamelCaseGetter())
        if (result2 != ReflectResult.Missing) return result2.value

        val result3 = obj.r_CallMethod(name)
        if (result3 != ReflectResult.Missing) return result3.value

        return ValuesSource.NotFound
    }
}