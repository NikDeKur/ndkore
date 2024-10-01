/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 * Copyright (c) 2024-present "Nik De Kur"
 */

package dev.nikdekur.ndkore.service.manager

import dev.nikdekur.ndkore.service.*
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
 * Common implementations: [RuntimeServicesManager], [KoinServicesManager]
 *
 * @see ServicesManager
 */
public abstract class AbstractServicesManager : ServicesManager {

    public val logger: KLogger = KotlinLogging.logger {}

    override var state: ServicesManager.State = ServicesManager.State.DISABLED

    public val servicesCollection: MutableSet<ServiceRef> = LinkedHashSet()

    override val services: Collection<Service>
        get() = sortServices()

    override fun <C : Any, S : C> registerService(service: S, vararg bindTo: KClass<out C>) {
        if (service !is Service)
            throw ClassIsNotServiceException(service::class)

        // If services manager is enabled and bind override existing services,
        // then disable existing services and enable the new one
        if (state == ServicesManager.State.ENABLED) {
            getServiceInternal(service::class)?.doDisable()
            bindTo.forEach { getServiceInternal(it)?.doDisable() }
            service.doEnable()
        }

        val reference = ServiceRef(service, bindTo)
        servicesCollection.add(reference)
    }

    public fun getServiceInternal(serviceClass: KClass<*>): Service? {
        val service = getServiceOrNull(serviceClass) ?: return null
        return service as? Service ?: throw ClassIsNotServiceException(serviceClass)
    }



    override fun enable() {
        check(state == ServicesManager.State.DISABLED) {
            "Services Manager is not disabled!"
        }

        state = ServicesManager.State.ENABLING

        val sorted = sortServices()
        logger.info { "Load order: ${sorted.joinToString { it::class.simpleName ?: "UnknownClassName" }}" }
        sorted.forEach {
            try {
                it.doEnable()
            } catch (e: Exception) {
                logger.error(e) { "Error while loading module '$it'!" }
            }
        }

        state = ServicesManager.State.ENABLED
    }

    override fun disable() {
        check(state == ServicesManager.State.ENABLED) {
            "Services Manager is not enabled!"
        }

        state = ServicesManager.State.DISABLING

        // Unload in reverse order, because of dependencies
        sortServices().reversed().forEach {
            try {
                it.doDisable()
            } catch (e: Exception) {
                logger.error(e) { "Error while unloading module '$it'!" }
            }
        }

        state = ServicesManager.State.DISABLED
    }


    public fun reload(service: Service) {
        service.doDisable()
        service.doEnable()
    }


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
            val service = ref.service

            // Проверяем, помечен ли сервис как first или last
            when {
                service.dependencies.first -> firstServices.add(service)
                service.dependencies.last -> lastServices.add(service)
                else -> regularServices.add(service)
            }

            val dependencies = service.dependencies.dependsOn.mapTo(ArrayList()) {
                if (ref.isApplicable(it)) throw CircularDependencyException(service)
                getServiceInternal(it) ?: throw ServiceNotFoundException(it)
            }

            graph[ref.service] = dependencies
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


    /**
     * # Service Reference
     *
     * Internal class to store a reference to a service and the classes it should be bound to.
     *
     * Used for checking for self-dependencies and comparing services by their bindTo classes.
     *
     * @property service Service to store
     * @property bindTo Classes to bind the service to
     */
    public data class ServiceRef(
        val service: Service,
        val bindTo: Array<out KClass<out Any>>
    ) {
        public fun isApplicable(clazz: KClass<out Any>): Boolean {
            return bindTo.any { it == clazz }
        }


        override fun equals(other: Any?): Boolean {
            val other = other as? ServiceRef ?: return false

            return bindTo.contentDeepEquals(other.bindTo)
        }

        override fun hashCode(): Int {
            return bindTo.contentDeepHashCode()
        }
    }
}