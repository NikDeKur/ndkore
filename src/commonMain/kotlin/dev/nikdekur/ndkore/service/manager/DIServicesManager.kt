/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 * Copyright (c) 2024-present "Nik De Kur"
 */

@file:Suppress("NOTHING_TO_INLINE")

package dev.nikdekur.ndkore.service.manager

import dev.nikdekur.ndkore.di.*
import dev.nikdekur.ndkore.ext.forEachSafe
import dev.nikdekur.ndkore.service.Service
import io.github.oshai.kotlinlogging.KLogger
import io.github.oshai.kotlinlogging.KotlinLogging
import kotlin.reflect.KClass

/**
 * # Abstract Services Manager
 *
 * Abstract implementation of [ServicesManager].
 * Handles service registration, enabling, disabling, sorting, and other predefined methods.
 *
 * Doesn't handle services storing, it should be implemented in subclasses.
 *
 * Common implementations: [RuntimeDIContainer], [KoinServicesManager]
 *
 * @see ServicesManager
 */
public open class DIServicesManager(
    public val diContainer: DIContainer
) : ServicesManager {

    public val logger: KLogger = KotlinLogging.logger {}

    override var state: ServicesManager.State = ServicesManager.State.DISABLED

    public val servicesCollection: MutableSet<ServiceDefinition> = LinkedHashSet()

    override val services: Collection<Service>
        get() = sortServices()

    override suspend fun registerService(definition: ServiceDefinition) {

        val service = definition.obj

        val qualifier = definition.qualifier
        val bindTo = definition.bindTo

        // If services manager is enabled and bind override existing services,
        // then disable existing services and enable the new one
        if (state == ServicesManager.State.ENABLED) {
            getServiceInternal(service::class, qualifier)?.disable()
            bindTo.forEach { getServiceInternal(it, qualifier)?.disable() }
            service.enable()
        }

        servicesCollection.add(definition)

        diContainer.add(definition)
    }

    public fun getServiceInternal(serviceClass: KClass<out Service>, qualifier: Qualifier): Service? {
        return getServiceOrNull(serviceClass, qualifier)
    }

    override fun <C : Service> getServiceOrNull(serviceClass: KClass<out C>, qualifier: Qualifier): C? {
        return diContainer.getOrNull(serviceClass, qualifier)
    }

    override fun <C : Service> getService(serviceClass: KClass<out C>, qualifier: Qualifier): C {
        return diContainer.get(serviceClass, qualifier)
    }


    public inline fun onEachService(operation: OnServiceOperation, block: (Service) -> Unit) {
        sortServices().forEachSafe({ exception, service ->
            logger.error(exception) { "Error while `${operation.name.lowercase()}` service ${service::class.simpleName}" }
        }, block)
    }


    override suspend fun enable() {
        check(state == ServicesManager.State.DISABLED) {
            "Services Manager is not disabled!"
        }

        state = ServicesManager.State.ENABLING

        onEachService(
            OnServiceOperation.ENABLE
        ) { it.enable() }


        state = ServicesManager.State.ENABLED
    }

    override suspend fun disable() {
        check(state == ServicesManager.State.ENABLED) {
            "Services Manager is not enabled!"
        }

        state = ServicesManager.State.DISABLING

        onEachService(
            OnServiceOperation.DISABLE
        ) { it.disable() }

        state = ServicesManager.State.DISABLED
    }


    public suspend fun reload(service: Service) {
        onEachService(
            OnServiceOperation.RELOAD
        ) { it.reload() }
    }


    // TODO: Reduce complexity of this function
    public fun sortServices(): Collection<Service> {
        val visited = mutableSetOf<Service>()
        val inProcess = mutableSetOf<Service>() // Для отслеживания стека вызовов (для циклов)
        val sorted = mutableListOf<Service>()

        val graph: MutableMap<Service, List<Service>> = LinkedHashMap()

        // Разделим сервисы на категории: first, last и остальные
        val firstServices = mutableListOf<Service>()
        val lastServices = mutableListOf<Service>()
        val regularServices = mutableListOf<Service>()

        // Инициализация графа зависимостей
        servicesCollection.forEach { ref ->
            val service = ref.obj

            // Проверяем, помечен ли сервис как first или last
            when {
                service.dependencies?.first == true -> firstServices.add(service)
                service.dependencies?.last == true -> lastServices.add(service)
                else -> regularServices.add(service)
            }

            val dependencies = service.dependencies?.dependsOn?.mapNotNull {
                if (it.optional) return@mapNotNull null

                val qualifier = it.qualifier
                val clazz = it.service

                if (ref.isApplicable(clazz, qualifier)) throw CircularDependencyException(service)
                getServiceInternal(clazz, qualifier) ?: throw DependentServiceNotFoundException(
                    service,
                    clazz,
                    qualifier
                )
            } ?: emptyList()

            graph[ref.obj] = dependencies
        }


        // Вспомогательная функция для DFS (поиск в глубину)
        fun dfs(group: Iterable<Service>) {
            group.forEach { service ->
                if (service in visited) return@forEach

                if (service in inProcess)
                    throw CircularDependencyException(service) // Обнаружение циклической зависимости

                if (service !in visited) {
                    inProcess.add(service)

                    graph[service]?.let(::dfs)

                    inProcess.remove(service)
                    visited.add(service)
                    sorted.add(service) // Добавляем в список сортировки
                }
            }
        }

        dfs(firstServices)
        dfs(regularServices)
        dfs(lastServices)

        return sorted
    }
}

public inline fun Definition<*>.isApplicable(clazz: KClass<out Any>, qualifier: Qualifier): Boolean {
    return bindTo.contains(clazz) && this.qualifier == qualifier
}

public abstract class DIContainerBuilder<R : DIContainer> {

    public var onErrorFunc: OnErrorContext.() -> Unit = {
        manager.logger.error(exception) { "Error while `${operation.name.lowercase()}` service ${service::class.simpleName}" }
    }

    public fun onError(block: OnErrorContext.() -> Unit) {
        onErrorFunc = block
    }

    public abstract fun build(): R
}


public inline fun DIContainerBuilder<*>.throwOnError(): Unit = onError { throw exception }