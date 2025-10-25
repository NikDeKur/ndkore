/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 * Copyright (c) 2024-present "Nik De Kur"
 */

package dev.nikdekur.ndkore.service

import dev.nikdekur.ndkore.di.CircularDependencyException
import dev.nikdekur.ndkore.di.DependencyNotFoundException
import dev.nikdekur.ndkore.di.Qualifier
import dev.nikdekur.ndkore.di.bind
import dev.nikdekur.ndkore.service.manager.ServicesManager
import dev.nikdekur.ndkore.service.manager.registerService
import kotlinx.coroutines.test.runTest
import kotlin.test.*

interface App {
    val manager: ServicesManager

    val flag: Boolean
}

class TestApp(override val manager: ServicesManager, override val flag: Boolean) : App

abstract class ServicesManagerTest {

    abstract val app: App
    val manager
        get() = app.manager

    // ----------------------------------
    //       Normal Behavior Test
    // ----------------------------------

    @Test
    fun serviceAddTest() = runTest {
        val service = SomeService1Impl(app)
        manager.registerService(service)
    }


    @Test
    fun serviceAddAndGetTest() = runTest {
        val service = SomeService1Impl(app)
        manager.registerService(service)
        assertEquals(service, manager.get<SomeService1Impl>())
    }


    @Test
    fun serviceAddAndInjectTest() = runTest {
        val service = SomeService1Impl(app)
        manager.registerService(service)
        val injected by manager.inject<SomeService1Impl>()
        assertEquals(service, injected)
    }


    @Test
    fun multiAddTest() = runTest {
        val service1 = SomeService1Impl(app)
        val service2 = SomeService2Impl(app)
        manager.registerService(service1)
        manager.registerService(service2)
    }


    @Test
    fun multiAddAndGetTest() = runTest {
        val service1 = SomeService1Impl(app)
        val service2 = SomeService2Impl(app)
        manager.registerService(service1)
        manager.registerService(service2)
        assertEquals(service1, manager.get<SomeService1Impl>())
        assertEquals(service2, manager.get<SomeService2Impl>())
    }


    @Test
    fun multiAddAndInjectTest() = runTest {
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
    fun serviceAddAndBindTest() = runTest {
        val service = SomeService1Impl(app)
        manager.registerService(service bind SomeService1::class)
    }


    @Test
    fun serviceAddAndBindAndGetTest() = runTest {
        val service = SomeService1Impl(app)
        manager.registerService(service bind SomeService1::class)
    }


    @Test
    fun serviceAddAndBindAndInjectTest() = runTest {
        val service = SomeService1Impl(app)
        manager.registerService(service bind SomeService1::class)
        val injected by manager.inject<SomeService1>()
        val injected2 by manager.inject<SomeService1Impl>()
        assertEquals(service, injected2)
        assertEquals(service, injected)
    }


    @Test
    fun multiAddAndBindTest() = runTest {
        val service1 = SomeService1Impl(app)
        val service2 = SomeService2Impl(app)
        manager.registerService(service1 bind SomeService1::class)
        manager.registerService(service2 bind SomeService2::class)
    }


    @Test
    fun multiAddAndBindAndGetTest() = runTest {
        val service1 = SomeService1Impl(app)
        val service2 = SomeService2Impl(app)
        manager.registerService(service1 bind SomeService1::class)
        manager.registerService(service2 bind SomeService2::class)
        assertEquals(service1, manager.getService(SomeService1Impl::class))
        assertEquals(service1, manager.getService(SomeService1::class))
        assertEquals(service2, manager.getService(SomeService2Impl::class))
        assertEquals(service2, manager.getService(SomeService2::class))
    }


    @Test
    fun multiAddAndBindAndInjectTest() = runTest {
        val service1 = SomeService1Impl(app)
        val service2 = SomeService2Impl(app)
        manager.registerService(service1 bind SomeService1::class)
        manager.registerService(service2 bind SomeService2::class)
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
    fun serviceReBindTest() = runTest {
        val service1 = object : MyService() {
            override val app = this@ServicesManagerTest.app
            override val manager = this@ServicesManagerTest.manager
        }
        val service2 = object : MyService() {
            override val app = this@ServicesManagerTest.app
            override val manager = this@ServicesManagerTest.manager
        }
        manager.registerService(service1 bind MyService::class)
        assertSame(manager.getService(MyService::class), service1)
        manager.registerService(service2 bind MyService::class)
        assertSame(manager.getService(MyService::class), service2)
    }


    @Test
    fun servicesLoadTest() = runTest {
        val service = SomeService1Impl(app)
        manager.registerService(service bind SomeService1::class)
        assertEquals(false, service.loaded)
        manager.enable()
        assertEquals(true, service.loaded)
        manager.disable()
        assertEquals(false, service.loaded)
    }


    @Test
    fun servicesReloadTest() = runTest {
        val service = SomeService1Impl(app)
        manager.registerService(service bind SomeService1::class)
        assertEquals(false, service.loaded)
        manager.enable()
        assertEquals(true, service.loaded)
        manager.reload()
        assertEquals(true, service.loaded)
    }

    @Test
    fun testDisablePreviousServiceAndEnableNewAfterRebind() = runTest {
        val service1 = SomeService1Impl(app)
        manager.registerService(service1 bind SomeService1::class)
        manager.enable()

        assertEquals(true, service1.loaded)

        val service2 = SomeService1Impl(app)
        manager.registerService(service2 bind SomeService1::class)

        assertEquals(false, service1.loaded)
        assertEquals(true, service2.loaded)
    }


    @Test
    fun disablePreviousServiceAndEnableNewAfterRebindWithMultipleServices() = runTest {
        val service1 = SomeService1Impl(app)
        val service2 = SomeService2Impl(app)
        manager.registerService(service1 bind SomeService1::class)
        manager.registerService(service2 bind SomeService2::class)
        manager.enable()

        assertEquals(true, service1.loaded)
        assertEquals(true, service2.loaded)

        val service3 = SomeService1Impl(app)
        val service4 = SomeService2Impl(app)
        manager.registerService(service3 bind SomeService1::class)
        manager.registerService(service4 bind SomeService2::class)

        assertEquals(false, service1.loaded)
        assertEquals(false, service2.loaded)
        assertEquals(true, service3.loaded)
        assertEquals(true, service4.loaded)
    }


    @Test
    fun defaultOrderTest() = runTest {
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

        // Right order: service1, service2
        manager.registerService(service1 bind ConfigurableService1Impl::class)
        manager.registerService(service2 bind ConfigurableService::class)
        manager.enable()
    }


    @Test
    fun reverseOrderTest() = runTest {
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

        // Right order:
        manager.registerService(service2 bind ConfigurableService::class)
        manager.registerService(service1 bind ConfigurableService1Impl::class)
        manager.enable()
    }


    @Test
    fun dependencyDependencyLoadTest() = runTest {
        var loadedFirst = false
        var loadedSecond = false
        val service1 = ConfigurableService1Impl(app, dependencies { +ConfigurableService::class }, onTestLoad = {
            loadedFirst = true
            assertEquals(true, loadedSecond)
        })

        val service2 = ConfigurableService1Impl(app, onTestLoad = {
            loadedSecond = true
            assertEquals(false, loadedFirst)
        })

        // right order: service2, service1
        manager.registerService(service1 bind ConfigurableService1Impl::class)
        manager.registerService(service2 bind ConfigurableService::class)
        manager.enable()
    }


    @Test
    fun dependencyLastLoadTest() = runTest {
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

        manager.registerService(service1 bind ConfigurableService1Impl::class)
        manager.registerService(service2 bind ConfigurableService::class)
        // right order: service2, service1
        manager.enable()
    }

    @Test
    fun dependencyFirstLoadTest() = runTest {
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

        manager.registerService(service1 bind ConfigurableService1Impl::class)
        manager.registerService(service2 bind ConfigurableService::class)
        // right order: service2, service1
        manager.enable()
    }


    @Test
    fun testServicesListReturnListInOrderOfDependencies() = runTest {
        val service1 = SomeService1Impl(app, dependencies { +SomeService2::class })
        val service2 = SomeService2Impl(app)


        // Right order: service2, service1
        manager.registerService(service1 bind SomeService1::class)
        manager.registerService(service2 bind SomeService2::class)
        assertContentEquals(listOf(service2, service1), manager.services)
    }


    // ----------------------------------
    //         Qualifiers Test
    // ----------------------------------

    @Test
    fun serviceAddAndBindWithQualifierTest() = runTest {
        val service = SomeService1Impl(app)
        manager.registerService(service bind SomeService1::class, qualifier = Qualifier("test"))

        val get = manager.getService(SomeService1::class, qualifier = Qualifier("test"))
        assertEquals(service, get)
    }

    @Test
    fun serviceAddAndBindAndGetWithUnknownQualifierTest() = runTest {
        val service = SomeService1Impl(app)
        manager.registerService(service bind SomeService1::class, qualifier = Qualifier("test"))


        val result = manager.getServiceOrNull(SomeService1::class, qualifier = Qualifier("unknown"))
        assertNull(result)
    }


    // ----------------------------------
    //        Error Behavior Test
    // ----------------------------------

    @Test
    fun serviceNotFountTest() {
        assertFailsWith<DependencyNotFoundException> {
            manager.getService(SomeService1::class)
        }
    }

    @Test
    fun selfDependencyTest() = runTest {
        val service = ConfigurableService1Impl(app, dependencies { +ConfigurableService1Impl::class })
        manager.registerService(service bind ConfigurableService1Impl::class)
        assertFailsWith<CircularDependencyException> {
            manager.enable()
        }
    }

    @Test
    fun recursiveDependencyTest() = runTest {
        val service1 = ConfigurableService1Impl(app, dependencies { +ConfigurableService2Impl::class })
        val service2 = ConfigurableService2Impl(app, dependencies { +ConfigurableService1Impl::class })
        manager.registerService(service1 bind ConfigurableService1Impl::class)
        manager.registerService(service2 bind ConfigurableService2Impl::class)
        val exception = assertFailsWith<CircularDependencyException> {
            manager.enable()
        }

        assertEquals(exception.service, service1)
    }
}