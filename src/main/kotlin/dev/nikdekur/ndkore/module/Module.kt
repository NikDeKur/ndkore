/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 * Copyright (c) 2024-present "Nik De Kur"
 */

@file:Suppress("NOTHING_TO_INLINE")

package dev.nikdekur.ndkore.module

import dev.nikdekur.ndkore.`interface`.Snowflake

interface Module<A> : Snowflake<String> {

    val app: A

    override val id: String
        get() = idOf(javaClass)

    fun onLoad() {
        // Plugin Module does not require to implement onLoad
    }
    fun onUnload() {
        // Plugin Module does not require to implement onUnload
    }

    val dependencies: Dependencies
        get() = Dependencies.none()

    companion object {
        @JvmStatic
        inline fun idOf(clazz: Class<out Module<*>>): String {
            return clazz.simpleName
        }

        @JvmStatic
        inline val Class<out Module<*>>.id
            get() = idOf(this)
    }
}

