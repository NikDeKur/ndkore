/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 * Copyright (c) 2024-present "Nik De Kur"
 */

@file:Suppress("NOTHING_TO_INLINE")

package dev.nikdekur.ndkore.service

import dev.nikdekur.ndkore.annotation.NdkoreDSL
import java.util.*
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
    val before: List<Class<out Any>>,
    val after: List<Class<out Any>>,
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
        fun after(vararg modules: Class<out Any>) =
            Dependencies(emptyList(), modules.toList(), first = false, last = false)

        /**
         * Creates a new instance of [Dependencies] with the given modules that should be loaded after this module.
         *
         * @param modules List of modules that should be loaded after this module
         * @return New instance of [Dependencies]
         */
        @JvmStatic
        fun before(vararg modules: Class<out Any>) =
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
    private val before = LinkedList<Class<out Any>>()
    private val after = LinkedList<Class<out Any>>()
    private var first = false
    private var last = false

    fun first() {
        first = true
    }

    fun last() {
        last = true
    }

    fun before(vararg services: Class<out Any>) {
        before.addAll(services)
    }

    fun after(vararg services: Class<out Any>) {
        after.addAll(services)
    }

    operator fun plus(service: Class<out Any>) {
        after.add(service)
    }

    operator fun minus(service: Class<out Any>) {
        before.add(service)
    }


    fun build() = Dependencies(before, after, first, last)
}

inline fun DependenciesBuilder.before(vararg services: KClass<out Any>) {
    services.forEach { before(it.java) }
}

inline fun DependenciesBuilder.after(vararg services: KClass<out Any>) {
    services.forEach { after(it.java) }
}

inline operator fun DependenciesBuilder.plus(service: KClass<out Any>) {
    plus(service.java)
}

inline operator fun DependenciesBuilder.minus(service: KClass<out Any>) {
    minus(service.java)
}

@NdkoreDSL
inline fun dependencies(block: DependenciesBuilder.() -> Unit): Dependencies {
    val builder = DependenciesBuilder()
    builder.block()
    return builder.build()
}