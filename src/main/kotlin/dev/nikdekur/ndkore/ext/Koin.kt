/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 * Copyright (c) 2024-present "Nik De Kur"
 */

@file:Suppress("NOTHING_TO_INLINE")

package dev.nikdekur.ndkore.ext

import org.koin.core.Koin
import org.koin.core.annotation.KoinInternalApi
import org.koin.core.context.KoinContext
import org.koin.core.definition.BeanDefinition
import org.koin.core.definition.Definition
import org.koin.core.definition.Kind
import org.koin.core.definition.KoinDefinition
import org.koin.core.instance.SingleInstanceFactory
import org.koin.core.module.Module
import org.koin.core.qualifier.Qualifier
import org.koin.core.qualifier._q
import org.koin.dsl.ModuleDeclaration
import org.koin.dsl.module
import kotlin.reflect.KClass

/** Wrapper for [org.koin.dsl.module] that immediately loads the module for the current [Koin] instance. **/
inline fun KoinContext.loadModule(
    createdAtStart: Boolean = false,
    noinline moduleDeclaration: ModuleDeclaration,
): Module {
    val moduleObj = module(createdAtStart, moduleDeclaration)

    loadKoinModules(moduleObj)

    return moduleObj

}


fun <T : Any> createDefinition(
    clazz: KClass<T>,
    kind: Kind = Kind.Singleton,
    qualifier: Qualifier? = null,
    definition: Definition<T>,
    secondaryTypes: List<KClass<*>> = emptyList(),
    scopeQualifier: Qualifier,
): BeanDefinition<T> {
    return BeanDefinition(
        scopeQualifier,
        clazz,
        qualifier,
        definition,
        kind,
        secondaryTypes = secondaryTypes,
    )
}


@KoinInternalApi
fun <T : Any> singleInstanceFactory(
    clazz: KClass<T>,
    qualifier: Qualifier? = null,
    definition: Definition<T>,
    scopeQualifier: Qualifier = _q("_root_"),
): SingleInstanceFactory<T> {
    val def = createDefinition(clazz, Kind.Singleton, qualifier, definition, scopeQualifier = scopeQualifier)
    return SingleInstanceFactory(def)
}

/**
 * Declare a Single definition
 * @param qualifier
 * @param createdAtStart
 * @param definition - definition function
 */
@OptIn(KoinInternalApi::class)
fun <T : Any> Module.single(
    clazz: KClass<T>,
    qualifier: Qualifier? = null,
    createdAtStart: Boolean = false,
    definition: Definition<T>,
): KoinDefinition<T> {
    val factory = singleInstanceFactory(clazz, qualifier, definition)
    indexPrimaryType(factory)
    if (createdAtStart) {
        prepareForCreationAtStart(factory)
    }
    return KoinDefinition(this, factory)
}