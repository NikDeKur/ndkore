/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 * Copyright (c) 2024-present "Nik De Kur"
 */

package dev.nikdekur.ndkore.service.manager

import dev.nikdekur.ndkore.service.Service
import dev.nikdekur.ndkore.service.ServiceNotFoundException
import kotlin.reflect.KClass

class RuntimeServicesManager : AbstractServicesManager() {

    val servicesMap = LinkedHashMap<String, Service>()

    override fun <C : Any, S : C> registerService(service: S, vararg bindTo: KClass<out C>) {
        super.registerService(service, *bindTo)

        service as Service

        bindTo.forEach { clazz ->
            val name = clazz.java.name
            servicesMap[name] = service
        }

        val clazz = service::class
        val name = clazz.java.name
        servicesMap[name] = service
    }

    override fun <C : Any> getServiceOrNull(serviceClass: KClass<out C>): C? {
        val name = serviceClass.java.name
        val service = servicesMap[name] ?: return null
        @Suppress("UNCHECKED_CAST")
        return service as C
    }

    override fun <C : Any> getService(serviceClass: KClass<out C>): C {
        return getServiceOrNull(serviceClass) ?: throw ServiceNotFoundException(serviceClass)
    }
}