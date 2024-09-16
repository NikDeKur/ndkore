/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 * Copyright (c) 2024-present "Nik De Kur"
 */

package dev.nikdekur.ndkore.service

import dev.nikdekur.ndkore.service.manager.KoinServicesManager
import dev.nikdekur.ndkore.service.manager.RuntimeServicesManager
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.koin.core.context.GlobalContext
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals
import kotlin.test.assertSame

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
        val injected by manager.inject<SomeService1Impl>()
        assertEquals(service, injected)
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
        assertEquals(service1, manager.getService(SomeService1Impl::class.java))
        assertEquals(service2, manager.getService(SomeService2Impl::class.java))
    }


    @Test
    fun multiAddAndInjectTest() {
        val service1 = SomeService1Impl(app)
        val service2 = SomeService2Impl(app)
        manager.registerService(service1)
        manager.registerService(service2)
        val injected1 by manager.inject<SomeService1Impl>()
        val injected2 by manager.inject<SomeService2Impl>()
        assertEquals(service1, injected1)
        assertEquals(service2, injected2)
    }


    @Test
    fun serviceAddAndBindTest() {
        val service = SomeService1Impl(app)
        manager.registerService(service, SomeService1::class.java)
    }


    @Test
    fun serviceAddAndBindAndGetTest() {
        val service = SomeService1Impl(app)
        manager.registerService(service, SomeService1::class.java)
        assertEquals(service, manager.getService(SomeService1Impl::class.java))
        assertEquals(service, manager.getService(SomeService1::class.java))
    }


    @Test
    fun serviceAddAndBindAndInjectTest() {
        val service = SomeService1Impl(app)
        manager.registerService(service, SomeService1::class.java)
        val injected by manager.inject<SomeService1>()
        val injected2 by manager.inject<SomeService1Impl>()
        assertEquals(service, injected2)
        assertEquals(service, injected)
    }


    @Test
    fun multiAddAndBindTest() {
        val service1 = SomeService1Impl(app)
        val service2 = SomeService2Impl(app)
        manager.registerService(service1, SomeService1::class.java)
        manager.registerService(service2, SomeService2::class.java)
    }


    @Test
    fun multiAddAndBindAndGetTest() {
        val service1 = SomeService1Impl(app)
        val service2 = SomeService2Impl(app)
        manager.registerService(service1, SomeService1::class.java)
        manager.registerService(service2, SomeService2::class.java)
        assertEquals(service1, manager.getService(SomeService1Impl::class.java))
        assertEquals(service1, manager.getService(SomeService1::class.java))
        assertEquals(service2, manager.getService(SomeService2Impl::class.java))
        assertEquals(service2, manager.getService(SomeService2::class.java))
    }


    @Test
    fun multiAddAndBindAndInjectTest() {
        val service1 = SomeService1Impl(app)
        val service2 = SomeService2Impl(app)
        manager.registerService(service1, SomeService1::class.java)
        manager.registerService(service2, SomeService2::class.java)
        val injected1 by manager.inject<SomeService1>()
        val injected11 by manager.inject<SomeService1Impl>()
        val injected2 by manager.inject<SomeService2>()
        val injected22 by manager.inject<SomeService2Impl>()
        assertEquals(service1, injected11)
        assertEquals(service1, injected1)
        assertEquals(service2, injected22)
        assertEquals(service2, injected2)
    }

    @Test
    fun serviceReBindTest() {
        val service1 = object : MyService() {
            override val app = this@ServicesManagerTest.app
            override val manager = this@ServicesManagerTest.manager
        }
        val service2 = object : MyService() {
            override val app = this@ServicesManagerTest.app
            override val manager = this@ServicesManagerTest.manager
        }
        manager.registerService(service1, MyService::class.java)
        assertSame(manager.getService(MyService::class.java), service1)
        manager.registerService(service2, MyService::class.java)
        assertSame(manager.getService(MyService::class.java), service2)
    }


    @Test
    fun servicesLoadTest() {
        val service = SomeService1Impl(app)
        manager.registerService(service, SomeService1::class.java)
        assertEquals(false, service.loaded)
        manager.enable()
        assertEquals(true, service.loaded)
        manager.disable()
        assertEquals(false, service.loaded)
    }


    @Test
    fun servicesReloadTest() {
        val service = SomeService1Impl(app)
        manager.registerService(service, SomeService1::class.java)
        assertEquals(false, service.loaded)
        manager.enable()
        assertEquals(true, service.loaded)
        manager.reload()
        assertEquals(true, service.loaded)
    }

    @Test
    fun `disable previous service and enable new after rebind`() {
        val service1 = SomeService1Impl(app)
        manager.registerService(service1, SomeService1::class.java)
        manager.enable()

        assertEquals(true, service1.loaded)

        val service2 = SomeService2Impl(app)
        manager.registerService(service2, SomeService1::class.java)

        assertEquals(false, service1.loaded)
        assertEquals(true, service2.loaded)
    }


    @Test
    fun `disable previous service and enable new after rebind with multiple services`() {
        val service1 = SomeService1Impl(app)
        val service2 = SomeService2Impl(app)
        manager.registerService(service1, SomeService1::class.java)
        manager.registerService(service2, SomeService2::class.java)
        manager.enable()

        assertEquals(true, service1.loaded)
        assertEquals(true, service2.loaded)

        val service3 = SomeService1Impl(app)
        val service4 = SomeService2Impl(app)
        manager.registerService(service3, SomeService1::class.java)
        manager.registerService(service4, SomeService2::class.java)

        assertEquals(false, service1.loaded)
        assertEquals(false, service2.loaded)
        assertEquals(true, service3.loaded)
        assertEquals(true, service4.loaded)
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

        manager.registerService(service1, ConfigurableService1Impl::class.java)
        manager.registerService(service2, ConfigurableService::class.java)
        manager.enable()
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

        manager.registerService(service2, ConfigurableService::class.java)
        manager.registerService(service1, ConfigurableService1Impl::class.java)
        manager.enable()
    }


    @Test
    fun dependencyAfterLoadTest() {
        var loadedFirst = false
        var loadedSecond = false
        val service1 = ConfigurableService1Impl(app, Dependencies.after(ConfigurableService::class.java), onTestLoad = {
            loadedFirst = true
            assertEquals(true, loadedSecond)
        })

        val service2 = ConfigurableService1Impl(app, onTestLoad = {
            loadedSecond = true
            assertEquals(false, loadedFirst)
        })

        manager.registerService(service1, ConfigurableService1Impl::class.java)
        manager.registerService(service2, ConfigurableService::class.java)
        manager.enable()
    }


    @Test
    fun dependencyBeforeLoadTest() {
        var loadedFirst = false
        var loadedSecond = false
        val service1 =
            ConfigurableService1Impl(app, Dependencies.before(ConfigurableService::class.java), onTestLoad = {
            loadedFirst = true
            assertEquals(true, loadedSecond)
        })

        val service2 = ConfigurableService1Impl(app, onTestLoad = {
            loadedSecond = true
            assertEquals(false, loadedFirst)
        })

        manager.registerService(service1, ConfigurableService1Impl::class.java)
        manager.registerService(service2, ConfigurableService::class.java)
        // right order: service2, service1
        manager.enable()
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

        manager.registerService(service1, ConfigurableService1Impl::class.java)
        manager.registerService(service2, ConfigurableService::class.java)
        // right order: service2, service1
        manager.enable()
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

        manager.registerService(service1, ConfigurableService1Impl::class.java)
        manager.registerService(service2, ConfigurableService::class.java)
        // right order: service2, service1
        manager.enable()
    }


    @Test
    fun `services list return list in order of dependencies`() {
        val service1 = SomeService1Impl(app, Dependencies.after(SomeService2::class.java))
        val service2 = SomeService2Impl(app)


        manager.registerService(service1, SomeService1::class.java)
        manager.registerService(service2, SomeService2::class.java)
        manager.enable()
        assertContentEquals(listOf(service2, service1), manager.services)
    }


    // ----------------------------------
    //        Error Behavior Test
    // ----------------------------------

    @Test
    fun serviceNotFountTest() {
        assertThrows<ServiceNotFoundException> {
            manager.getService(SomeService1::class.java)
        }
    }

    @Test
    fun selfDependencyTest() {
        val service = ConfigurableService1Impl(app, Dependencies.after(ConfigurableService1Impl::class.java))
        manager.registerService(service, ConfigurableService1Impl::class.java)
        assertThrows<CircularDependencyException> {
            manager.enable()
        }
    }

    @Test
    fun recursiveDependencyTest() {
        val service1 = ConfigurableService1Impl(app, Dependencies.after(ConfigurableService::class.java))
        val service2 = ConfigurableService1Impl(app, Dependencies.after(ConfigurableService1Impl::class.java))
        manager.registerService(service1, ConfigurableService::class.java)
        manager.registerService(service2, ConfigurableService1Impl::class.java)
        assertThrows<CircularDependencyException> {
            manager.enable()
        }
    }
}