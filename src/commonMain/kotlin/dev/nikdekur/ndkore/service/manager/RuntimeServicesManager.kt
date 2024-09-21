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

open class RuntimeServicesManager : AbstractServicesManager() {

    val servicesMap = LinkedHashMap<String, Service>()

    open fun classId(clazz: KClass<*>): String = clazz.toString()

    override fun <C : Any, S : C> registerService(service: S, vararg bindTo: KClass<out C>) {
        super.registerService(service, *bindTo)

        service as Service

        bindTo.forEach { clazz ->
            val id = classId(clazz)
            servicesMap[id] = service
        }

        val id = classId(service::class)
        servicesMap[id] = service
    }

    override fun <C : Any> getServiceOrNull(serviceClass: KClass<out C>): C? {
        val id = classId(serviceClass)
        val service = servicesMap[id] ?: return null

        @Suppress("UNCHECKED_CAST", "kotlin:S6531")
        return service as C
    }

    override fun <C : Any> getService(serviceClass: KClass<out C>): C {
        return getServiceOrNull(serviceClass) ?: throw ServiceNotFoundException(serviceClass)
    }
}