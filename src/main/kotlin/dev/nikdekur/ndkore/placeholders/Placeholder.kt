/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 * Copyright (c) 2024 Nik De Kur
 */

@file:Suppress("NOTHING_TO_INLINE")

package dev.nikdekur.ndkore.placeholders

interface Placeholder {

    val placeholderMap: MutableMap<String, Any>
        get() = HashMap()

    companion object {
        @JvmStatic
        fun of(map: MutableMap<String, Any>): Placeholder {
            return object : Placeholder {
                override val placeholderMap: MutableMap<String, Any> = map

                override fun toString(): String {
                    return "Placeholder(map=$placeholderMap)"
                }
            }
        }

        fun ofObject(obj: Any): Placeholder {
            if (obj is Placeholder) return obj
            val map = object : HashMap<String, Any>() {
                @Suppress("OVERRIDE_BY_INLINE")
                override inline fun get(key: String): Any? {
                    return PlaceholderParser.findValue(obj, key)
                }
            }
            return of(map)
        }

        inline val Any.placeholder: Placeholder
            get() = ofObject(this)

        @JvmStatic
        inline fun of(vararg pair: Pair<String, Any>): Placeholder {
            return of(mutableMapOf(*pair))
        }

        inline fun ofSingle(key: String, value: Any): Placeholder {
            return of(key to value)
        }
    }
}