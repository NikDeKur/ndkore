/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 * Copyright (c) 2024-present "Nik De Kur"
 */

@file:Suppress("NOTHING_TO_INLINE")

package dev.nikdekur.ndkore.di

import dev.nikdekur.ndkore.service.manager.DIContainerBuilder
import org.koin.ext.getFullName
import kotlin.reflect.KClass

public open class RuntimeDIContainer(
    public val map: MutableMap<String, Any>
) : DIContainer {

    public open fun classId(clazz: KClass<*>): String {
        // Not great, that we have to use Koin's internal function to get the full name
        return clazz.getFullName()
    }


    public open fun definitionId(serviceClass: KClass<*>, qualifier: Qualifier): String {
        val classId = classId(serviceClass)
        val qualifierId = qualifier.value
        return "$classId:$qualifierId"
    }

    public inline fun definitionId(definition: Definition<*>): String =
        definitionId(definition.obj::class, definition.qualifier)


    override fun add(definition: Definition<*>) {
        val service = definition.obj
        val qualifier = definition.qualifier
        val bindTo = definition.bindTo

        bindTo.forEach { clazz ->
            val id = definitionId(clazz, qualifier)
            map[id] = service
        }

        val id = definitionId(definition)
        map[id] = service
    }

    override fun <C : Any> getOrNull(serviceClass: KClass<out C>, qualifier: Qualifier): C? {
        val id = definitionId(serviceClass, qualifier)
        println("Looking for service with id: $id")
        println("Available services: ${map.keys.joinToString(", ")}")
        val service = map[id] ?: return null

        @Suppress("UNCHECKED_CAST", "kotlin:S6531")
        return service as C
    }

    override fun <C : Any> get(serviceClass: KClass<out C>, qualifier: Qualifier): C {
        return getOrNull(serviceClass, qualifier) ?: throw DependencyNotFoundException(serviceClass, qualifier)
    }
}


public open class RuntimeDIContainerBuilder : DIContainerBuilder<RuntimeDIContainer>() {
    public var actualServicesMap: MutableMap<String, Any>? = null

    public fun servicesMap(map: MutableMap<String, Any>) {
        actualServicesMap = map
    }

    override fun build(): RuntimeDIContainer {
        val map = actualServicesMap ?: mutableMapOf()
        return RuntimeDIContainer(map)
    }
}

public inline fun RuntimeDIContainer(builder: RuntimeDIContainerBuilder.() -> Unit): RuntimeDIContainer {
    val b = RuntimeDIContainerBuilder().apply(builder)
    return b.build()
}