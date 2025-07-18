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
import dev.nikdekur.ndkore.ext.toTArray
import kotlin.jvm.JvmStatic
import kotlin.reflect.KClass

/**
 * # Dependencies
 *
 * Represents dependencies of a module.
 * @param dependsOn The List of modules that should be loaded before this module
 * and module will probably use them on enabling
 * @param first True if this module should be loaded as first as possible,
 * but respecting other modules dependencies on it
 * @param last True if this module should be loaded as last as possible,
 * but respecting other modules dependencies on it
 * @see AbstractService
 */
public data class Dependencies(
    val dependsOn: Array<out Dependency>,
    val first: Boolean = false,
    val last: Boolean = false,
) {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Dependencies) return false

        if (!dependsOn.contentDeepEquals(other.dependsOn)) return false
        if (first != other.first) return false
        if (last != other.last) return false

        return true
    }

    override fun hashCode(): Int {
        var result = dependsOn.contentDeepHashCode()
        result = 31 * result + first.hashCode()
        result = 31 * result + last.hashCode()
        return result
    }



    public companion object {
        private val NONE by lazy {
            Dependencies(emptyArray(), first = false, last = false)
        }
        private val FIRST by lazy {
            Dependencies(emptyArray(), first = true, last = false)
        }
        private val LAST by lazy {
            Dependencies(emptyArray(), first = false, last = true)
        }

        /**
         * Returns an empty instance of [Dependencies].
         *
         * @return Empty instance of [Dependencies]
         */
        @JvmStatic
        public fun none(): Dependencies = NONE

        /**
         * Returns an instance of [Dependencies] that should be loaded as first as possible by other dependencies.
         *
         * @return Instance of [Dependencies] that should be loaded as first as possible by other dependencies
         */
        @JvmStatic
        public fun first(): Dependencies = FIRST

        /**
         * Returns an instance of [Dependencies] that should be loaded as last as possible by other dependencies.
         *
         * @return Instance of [Dependencies] that should be loaded as last as possible by other dependencies
         */
        @JvmStatic
        public fun last(): Dependencies = LAST
    }
}

public data class Dependency(
    val service: KClass<out Any>,
    val qualifier: Qualifier,
    val optional: Boolean
)


/**
 * Builder for [Dependencies].
 */
public class DependenciesBuilder {
    private val dependsOn = mutableListOf<Dependency>()
    private var first = false
    private var last = false

    /**
     * Sets this module to be loaded as first as possible, but respecting other modules dependencies on it.
     */
    public fun first() {
        first = true
    }

    /**
     * Sets this module to be loaded as last as possible, but respecting other modules dependencies on it.
     */
    public fun last() {
        last = true
    }


    /**
     * Adds a dependency to the dependency list.
     *
     * @param service Service's KClass to depend
     * @param qualifier Qualifier to use for the dependency
     * @param optional True if the dependency is optional, false otherwise
     */
    public fun dependsOn(
        service: KClass<out Any>,
        qualifier: Qualifier = Qualifier.Empty,
        optional: Boolean = false
    ) {
        dependsOn.add(Dependency(service, qualifier, optional))
    }


    /**
     * Build the [Dependencies] instance.
     *
     * Use the parameters set in this builder to create a new instance.
     *
     * @return [Dependencies] instance
     */
    public fun build(): Dependencies = Dependencies(dependsOn.toTArray(), first, last)


    /**
     * Adds the given service that should be loaded before this service.
     *
     * Just shorthand for [dependsOn], for DSL purposes.
     *
     * @receiver Service's KClass to depend. optional parameter is set to false.
     */
    @NdkoreDSL
    public inline operator fun KClass<out Any>.unaryPlus() {
        dependsOn(this, optional = false)
    }

    /**
     * Adds the given service that should be loaded before this service.
     *
     * Just shorthand for [dependsOn], for DSL purposes.
     *
     * @receiver Service's KClass to depend. optional parameter is set to true.
     */
    @NdkoreDSL
    public inline operator fun KClass<out Any>.unaryMinus() {
        dependsOn(this, optional = true)
    }
}

/**
 * DSL function to create a new instance of [Dependencies].
 *
 * Example:
 * ```
 * dependencies {
 *    dependsOn<Module1>()
 *    dependsOn<Module2>()
 *    first()
 * }
 * ```
 *
 * @param block Builder for [Dependencies]
 * @return New instance of [Dependencies]
 */
@NdkoreDSL
public inline fun dependencies(block: DependenciesBuilder.() -> Unit): Dependencies {
    val builder = DependenciesBuilder()
    builder.block()
    return builder.build()
}



