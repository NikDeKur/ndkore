/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 * Copyright (c) 2024-present "Nik De Kur"
 */

package dev.nikdekur.ndkore.service

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.koin.core.context.GlobalContext
import kotlin.test.assertEquals
import kotlin.test.assertTrue

interface App {
    val manager: ServicesManager

    val flag: Boolean
}

class TestApp(override val manager: ServicesManager, override val flag: Boolean) : App

interface ServicesManagerTest {

    val app: App
    val manager
        get() = app.manager


    fun managerProvider(): List<ServicesManager> {
        return listOf(
            KoinServicesManager(GlobalContext),
            RuntimeServicesManager() // Предполагается, что RuntimeServicesManager можно создать без параметров
        )
    }

    // ----------------------------------
    //       Normal Behavior Test
    // ----------------------------------

    @Test
    fun serviceAddTest() {
        val service = SomeService1Impl(app)
        manager.registerService(service)
    }


    @Test
    fun serviceAddAndGetTest() {
        val service = SomeService1Impl(app)
        manager.registerService(service)
        assertEquals(service, manager.getService<SomeService1Impl>())
    }


    @Test
    fun serviceAddAndInjectTest() {
        val service = SomeService1Impl(app)
        manager.registerService(service)
        val injected = manager.inject<SomeService1Impl>()
        assertEquals(service, injected.value)
    }


    @Test
    fun multiAddTest() {
        val service1 = SomeService1Impl(app)
        val service2 = SomeService2Impl(app)
        manager.registerService(service1)
        manager.registerService(service2)
    }


    @Test
    fun multiAddAndGetTest() {
        val service1 = SomeService1Impl(app)
        val service2 = SomeService2Impl(app)
        manager.registerService(service1)
        manager.registerService(service2)
        assertEquals(service1, manager.getService(SomeService1Impl::class))
        assertEquals(service2, manager.getService(SomeService2Impl::class))
    }


    @Test
    fun multiAddAndInjectTest() {
        val service1 = SomeService1Impl(app)
        val service2 = SomeService2Impl(app)
        manager.registerService(service1)
        manager.registerService(service2)
        val injected1 = manager.inject<SomeService1Impl>()
        val injected2 = manager.inject<SomeService2Impl>()
        assertEquals(service1, injected1.value)
        assertEquals(service2, injected2.value)
    }


    @Test
    fun serviceAddAndBindTest() {
        val service = SomeService1Impl(app)
        manager.registerService(service, SomeService1::class)
    }


    @Test
    fun serviceAddAndBindAndGetTest() {
        val service = SomeService1Impl(app)
        manager.registerService(service, SomeService1::class)
        assertEquals(service, manager.getService(SomeService1Impl::class))
        assertEquals(service, manager.getService(SomeService1::class))
    }


    @Test
    fun serviceAddAndBindAndInjectTest() {
        val service = SomeService1Impl(app)
        manager.registerService(service, SomeService1::class)
        val injected = manager.inject<SomeService1>()
        val injected2 = manager.inject<SomeService1Impl>()
        assertEquals(service, injected2.value)
        assertEquals(service, injected.value)
    }


    @Test
    fun multiAddAndBindTest() {
        val service1 = SomeService1Impl(app)
        val service2 = SomeService2Impl(app)
        manager.registerService(service1, SomeService1::class)
        manager.registerService(service2, SomeService2::class)
    }


    @Test
    fun multiAddAndBindAndGetTest() {
        val service1 = SomeService1Impl(app)
        val service2 = SomeService2Impl(app)
        manager.registerService(service1, SomeService1::class)
        manager.registerService(service2, SomeService2::class)
        assertEquals(service1, manager.getService(SomeService1Impl::class))
        assertEquals(service1, manager.getService(SomeService1::class))
        assertEquals(service2, manager.getService(SomeService2Impl::class))
        assertEquals(service2, manager.getService(SomeService2::class))
    }


    @Test
    fun multiAddAndBindAndInjectTest() {
        val service1 = SomeService1Impl(app)
        val service2 = SomeService2Impl(app)
        manager.registerService(service1, SomeService1::class)
        manager.registerService(service2, SomeService2::class)
        val injected1 = manager.inject<SomeService1>()
        val injected11 = manager.inject<SomeService1Impl>()
        val injected2 = manager.inject<SomeService2>()
        val injected22 = manager.inject<SomeService2Impl>()
        assertEquals(service1, injected11.value)
        assertEquals(service1, injected1.value)
        assertEquals(service2, injected22.value)
        assertEquals(service2, injected2.value)
    }

    @Test
    fun serviceReBindTest() {
        val service1 = object : MyService {
            override val app = this@ServicesManagerTest.app
            override val manager = this@ServicesManagerTest.manager
        }
        val service2 = object : MyService {
            override val app = this@ServicesManagerTest.app
            override val manager = this@ServicesManagerTest.manager
        }
        manager.registerService(service1, MyService::class)
        assertTrue(manager.getService(MyService::class) === service1)
        manager.registerService(service2, MyService::class)
        assertTrue(manager.getService(MyService::class) === service2)
    }


    @Test
    fun servicesLoadTest() {
        val service = SomeService1Impl(app)
        manager.registerService(service, SomeService1::class)
        assertEquals(false, service.loaded)
        manager.loadAll()
        assertEquals(true, service.loaded)
        manager.unloadAll()
        assertEquals(false, service.loaded)
    }


    @Test
    fun servicesReloadTest() {
        val service = SomeService1Impl(app)
        manager.registerService(service, SomeService1::class)
        assertEquals(false, service.loaded)
        manager.loadAll()
        assertEquals(true, service.loaded)
        manager.reloadAll()
        assertEquals(true, service.loaded)
    }


    @Test
    fun defaultOrderTest() {
        var loadedFirst = false
        var loadedSecond = false
        val service1 = ConfigurableService1Impl(app, onTestLoad = {
            loadedFirst = true
            assertEquals(false, loadedSecond)
        })

        val service2 = ConfigurableService2Impl(app, onTestLoad = {
            loadedSecond = true
            assertEquals(true, loadedFirst)
        })

        manager.registerService(service1, ConfigurableService1Impl::class)
        manager.registerService(service2, ConfigurableService::class)
        println(manager.services)
        manager.loadAll()
    }


    @Test
    fun reverseOrderTest() {
        var loadedFirst = false
        var loadedSecond = false
        val service1 = ConfigurableService1Impl(app, onTestLoad = {
            loadedFirst = true
            assertEquals(true, loadedSecond)
        })

        val service2 = ConfigurableService1Impl(app, onTestLoad = {
            loadedSecond = true
            assertEquals(false, loadedFirst)
        })

        manager.registerService(service2, ConfigurableService::class)
        manager.registerService(service1, ConfigurableService1Impl::class)
        manager.loadAll()
    }


    @Test
    fun dependencyAfterLoadTest() {
        var loadedFirst = false
        var loadedSecond = false
        val service1 = ConfigurableService1Impl(app, Dependencies.after(ConfigurableService::class), onTestLoad = {
            loadedFirst = true
            assertEquals(true, loadedSecond)
        })

        val service2 = ConfigurableService1Impl(app, onTestLoad = {
            loadedSecond = true
            assertEquals(false, loadedFirst)
        })

        manager.registerService(service1, ConfigurableService1Impl::class)
        manager.registerService(service2, ConfigurableService::class)
        manager.loadAll()
    }


    @Test
    fun dependencyBeforeLoadTest() {
        var loadedFirst = false
        var loadedSecond = false
        val service1 = ConfigurableService1Impl(app, Dependencies.before(ConfigurableService::class), onTestLoad = {
            loadedFirst = true
            assertEquals(true, loadedSecond)
        })

        val service2 = ConfigurableService1Impl(app, onTestLoad = {
            loadedSecond = true
            assertEquals(false, loadedFirst)
        })

        manager.registerService(service1, ConfigurableService1Impl::class)
        manager.registerService(service2, ConfigurableService::class)
        // right order: service2, service1
        manager.loadAll()
    }


    @Test
    fun dependencyLastLoadTest() {
        var loadedFirst = false
        var loadedSecond = false
        val service1 = ConfigurableService1Impl(app, Dependencies.last(), onTestLoad = {
            loadedFirst = true
            assertEquals(true, loadedSecond)
        })

        val service2 = ConfigurableService1Impl(app, onTestLoad = {
            loadedSecond = true
            assertEquals(false, loadedFirst)
        })

        manager.registerService(service1, ConfigurableService1Impl::class)
        manager.registerService(service2, ConfigurableService::class)
        // right order: service2, service1
        manager.loadAll()
    }

    @Test
    fun dependencyFirstLoadTest() {
        var loadedFirst = false
        var loadedSecond = false
        val service1 = ConfigurableService1Impl(app, onTestLoad = {
            loadedFirst = true
            assertEquals(true, loadedSecond)
        })

        val service2 = ConfigurableService1Impl(app, Dependencies.first(), onTestLoad = {
            loadedSecond = true
            assertEquals(false, loadedFirst)
        })

        manager.registerService(service1, ConfigurableService1Impl::class)
        manager.registerService(service2, ConfigurableService::class)
        // right order: service2, service1
        manager.loadAll()
    }


    // ----------------------------------
    //        Error Behavior Test
    // ----------------------------------

    @Test
    fun serviceNotFountTest() {
        assertThrows<ServiceNotFoundException> {
            manager.getService(SomeService1::class)
        }
    }

    @Test
    fun selfDependencyTest() {
        val service = ConfigurableService1Impl(app, Dependencies.after(ConfigurableService1Impl::class))
        manager.registerService(service, ConfigurableService1Impl::class)
        assertThrows<CircularDependencyException> {
            manager.loadAll()
        }
    }

    @Test
    fun recursiveDependencyTest() {
        val service1 = ConfigurableService1Impl(app, Dependencies.after(ConfigurableService::class))
        val service2 = ConfigurableService1Impl(app, Dependencies.after(ConfigurableService1Impl::class))
        manager.registerService(service1, ConfigurableService::class)
        manager.registerService(service2, ConfigurableService1Impl::class)
        assertThrows<CircularDependencyException> {
            manager.loadAll()
        }
    }
}