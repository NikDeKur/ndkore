/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 * Copyright (c) 2024-present "Nik De Kur"
 */

package dev.nikdekur.ndkore.service

import kotlin.reflect.KClass

class RuntimeServicesManager : AbstractServicesManager() {

    val servicesMap = LinkedHashMap<String, Service<*>>()

    override val services
        get() = servicesMap.values

    override fun <S : Service<*>> registerService(service: S, vararg bindTo: KClass<out S>) {
        bindTo.forEach { clazz ->
            val name = clazz.java.name
            servicesMap[name] = service
        }

        val clazz = service::class
        val name = clazz.java.name
        servicesMap[name] = service
    }


    override fun <S : Service<*>> getServiceOrNull(serviceClass: KClass<out S>): S? {
        val name = serviceClass.java.name
        val service = servicesMap[name] ?: return null
        @Suppress("UNCHECKED_CAST")
        return service as S
    }

    override fun <S : Service<*>> getService(serviceClass: KClass<out S>): S {
        return getServiceOrNull(serviceClass) ?: throw ServiceNotFoundException(serviceClass)
    }
}