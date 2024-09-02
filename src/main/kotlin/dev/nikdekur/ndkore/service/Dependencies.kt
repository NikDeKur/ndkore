/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 * Copyright (c) 2024-present "Nik De Kur"
 */

package dev.nikdekur.ndkore.service

import dev.nikdekur.ndkore.annotation.NdkoreDSL
import java.util.LinkedList
import kotlin.reflect.KClass

/**
 * Represents dependencies of a module.
 *
 * @param before List of modules that should be loaded before this module
 * @param after List of modules that should be loaded after this module
 * @param first True if this module should be loaded as first as possible by other dependencies
 * @param last True if this module should be loaded as last as possible by other dependencies
 * @see Service
 */
data class Dependencies(
    val before: List<KClass<out Any>>,
    val after: List<KClass<out Any>>,
    val first: Boolean = false,
    val last: Boolean = false,
) {

    companion object {
        /**
         * Creates a new instance of [Dependencies] with the given modules that should be loaded before this module.
         *
         * @param modules List of modules that should be loaded before this module
         * @return New instance of [Dependencies]
         */
        @JvmStatic
        fun after(vararg modules: KClass<out Any>) =
            Dependencies(emptyList(), modules.toList(), first = false, last = false)

        /**
         * Creates a new instance of [Dependencies] with the given modules that should be loaded after this module.
         *
         * @param modules List of modules that should be loaded after this module
         * @return New instance of [Dependencies]
         */
        @JvmStatic
        fun before(vararg modules: KClass<out Any>) =
            Dependencies(modules.toList(), emptyList(), first = false, last = false)

        private val EMPTY by lazy {
            Dependencies(emptyList(), emptyList(), first = false, last = false)
        }
        private val FIRST by lazy {
            Dependencies(emptyList(), emptyList(), first = true, last = false)
        }
        private val LAST by lazy {
            Dependencies(emptyList(), emptyList(), first = false, last = true)
        }

        /**
         * Returns an empty instance of [Dependencies].
         *
         * @return Empty instance of [Dependencies]
         */
        @JvmStatic
        fun none() = EMPTY

        /**
         * Returns an instance of [Dependencies] that should be loaded as first as possible by other dependencies.
         *
         * @return Instance of [Dependencies] that should be loaded as first as possible by other dependencies
         */
        @JvmStatic
        fun first() = FIRST

        /**
         * Returns an instance of [Dependencies] that should be loaded as last as possible by other dependencies.
         *
         * @return Instance of [Dependencies] that should be loaded as last as possible by other dependencies
         */
        @JvmStatic
        fun last() = LAST
    }
}


class DependenciesBuilder {
    private val before = LinkedList<KClass<out Any>>()
    private val after = LinkedList<KClass<out Any>>()
    private var first = false
    private var last = false

    fun first() {
        first = true
    }

    fun last() {
        last = true
    }

    fun before(vararg services: KClass<out Any>) {
        before.addAll(services)
    }

    fun after(vararg services: KClass<out Any>) {
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