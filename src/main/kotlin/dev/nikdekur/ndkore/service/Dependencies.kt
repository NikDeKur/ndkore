/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 * Copyright (c) 2024-present "Nik De Kur"
 */

package dev.nikdekur.ndkore.service

import dev.nikdekur.ndkore.NdkoreDSL
import java.util.LinkedList
import kotlin.reflect.KClass

/**
 * Represents dependencies of a module.
 *
 * @param before List of modules that should be loaded before this module
 * @param after List of modules that should be loaded after this module
 * @param first True if this module should be loaded first
 * @param last True if this module should be loaded last
 * @see Service
 */
data class Dependencies(
    val before: List<KClass<*>>,
    val after: List<KClass<*>>,
    val first: Boolean = false,
    val last: Boolean = false,
) {

    companion object {
        @JvmStatic
        fun after(vararg modules: KClass<*>) = Dependencies(emptyList(), modules.toList(), first = false, last = false)
        @JvmStatic
        fun before(vararg modules: KClass<*>) = Dependencies(modules.toList(), emptyList(), first = false, last = false)

        private val EMPTY by lazy {
            Dependencies(emptyList(), emptyList(), first = false, last = false)
        }
        private val FIRST by lazy {
            Dependencies(emptyList(), emptyList(), first = true, last = false)
        }
        private val LAST by lazy {
            Dependencies(emptyList(), emptyList(), first = false, last = true)
        }

        @JvmStatic
        fun none() = EMPTY
        @JvmStatic
        fun first() = FIRST
        @JvmStatic
        fun last() = LAST
    }
}


class DependenciesBuilder {
    private val before = LinkedList<KClass<*>>()
    private val after = LinkedList<KClass<*>>()
    private var first = false
    private var last = false

    fun first() {
        first = true
    }

    fun last() {
        last = true
    }

    fun before(vararg services: KClass<*>) {
        before.addAll(services)
    }

    fun after(vararg services: KClass<*>) {
        after.addAll(services)
    }


    fun build() = Dependencies(before, after, first, last)
}

@NdkoreDSL
inline fun dependencies(block: DependenciesBuilder.() -> Unit): Dependencies {
    val builder = DependenciesBuilder()
    builder.block()
    return builder.build()
}