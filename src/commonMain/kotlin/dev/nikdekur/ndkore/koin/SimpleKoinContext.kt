/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 * Copyright (c) 2024-present "Nik De Kur"
 */

package dev.nikdekur.ndkore.koin

import org.koin.core.Koin
import org.koin.core.KoinApplication
import org.koin.core.context.KoinContext
import org.koin.core.error.KoinApplicationAlreadyStartedException
import org.koin.core.module.Module
import org.koin.dsl.KoinAppDeclaration

/**
 * The [KoinContext] for some application instance.
 *
 * This contains the [KoinApplication] and its [Koin] instance for dependency injection.
 */
class SimpleKoinContext : KoinContext {

    /** The current [Koin] instance. */
    private var koin: Koin? = null

    /** The current [KoinApplication]. */
    private var koinApp: KoinApplication? = null

    /**
     * Gets the [Koin] instance.
     *
     * @throws IllegalStateException [KoinApplication] has not yet been started.
     */
    override fun get(): Koin = koin ?: error("KoinApplication has not been started")

    /** Gets the [Koin] instance or null if the [KoinApplication] has not yet been started. */
    override fun getOrNull(): Koin? = koin

    /** Gets the [KoinApplication] or null if the [KoinApplication] has not yet been started. */
    fun getKoinApplicationOrNull(): KoinApplication? = koinApp

    /**
     * Registers a [KoinApplication] to as the current one for this context.
     *
     * @param koinApplication The application to registers.
     *
     * @throws KoinApplicationAlreadyStartedException The [KoinApplication] has already been instantiated.
     */
    private fun register(koinApplication: KoinApplication) {
        if (koin != null) {
            throw KoinApplicationAlreadyStartedException("Koin Application has already been started")
        }

        koinApp = koinApplication
        koin = koinApplication.koin
    }

    /** Closes and removes the current [Koin] instance. */
    override fun stopKoin() {
        koin?.close()
        koin = null
    }

    /**
     * Starts using the provided [KoinApplication] as the current one for this context.
     *
     * @param koinApplication The application to start with.
     *
     * @throws KoinApplicationAlreadyStartedException The [KoinApplication] has already been instantiated.
     */
    override fun startKoin(koinApplication: KoinApplication): KoinApplication {
        register(koinApplication)
        koinApplication.createEagerInstances()

        return koinApplication
    }

    /**
     * Starts using the provided [KoinAppDeclaration] to create the [KoinApplication] for this context.
     *
     * @param appDeclaration The application declaration to start with.
     *
     * @throws KoinApplicationAlreadyStartedException The [KoinApplication] has already been instantiated.
     */
    override fun startKoin(appDeclaration: KoinAppDeclaration): KoinApplication {
        val koinApplication = KoinApplication.init()

        register(koinApplication)
        appDeclaration(koinApplication)
        koinApplication.createEagerInstances()

        return koinApplication
    }

    /** Mirroring the global Koin instance implementation. **/
    override fun loadKoinModules(module: Module, createEagerInstances: Boolean) {
        get().loadModules(listOf(module), createEagerInstances = createEagerInstances)
    }

    /** Mirroring the global Koin instance implementation. **/
    override fun loadKoinModules(modules: List<Module>, createEagerInstances: Boolean) {
        get().loadModules(modules, createEagerInstances = createEagerInstances)
    }

    /**
     * Unloads a module from the [Koin] instance.
     *
     * @param module The module to unload.
     */
    override fun unloadKoinModules(module: Module) {
        get().unloadModules(listOf(module))
    }

    /**
     * Unloads modules from the [Koin] instance.
     *
     * @param modules The modules to unload.
     */
    override fun unloadKoinModules(modules: List<Module>) {
        get().unloadModules(modules)
    }
}