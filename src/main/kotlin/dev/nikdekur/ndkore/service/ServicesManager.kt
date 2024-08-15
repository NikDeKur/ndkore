/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 * Copyright (c) 2024-present "Nik De Kur"
 */

package dev.nikdekur.ndkore.service

import kotlin.reflect.KClass

interface ServicesManager<A> {

    val app: A

    val services: Iterable<Service<A>>

    fun registerService(service: Service<A>, vararg bindTo: KClass<*>)

    fun <T : Any> getServiceOrNull(serviceClass: KClass<T>): T?
    fun <T : Any> getService(serviceClass: KClass<T>): T

    fun loadAll()
    fun unloadAll()

    fun reloadAll() {
        unloadAll()
        loadAll()
    }


}

inline fun <reified S : Any> ServicesManager<*>.getService() = getService(S::class)
inline fun <reified S : Any> ServicesManager<*>.getServiceOrNull() = getServiceOrNull(S::class)
inline fun <reified S : Any> ServicesManager<*>.inject() = lazy { getService<S>() }