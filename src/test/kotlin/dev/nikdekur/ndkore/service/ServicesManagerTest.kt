/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 * Copyright (c) 2024-present "Nik De Kur"
 */

package dev.nikdekur.ndkore.service

import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.koin.core.context.GlobalContext
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.environmentProperties
import kotlin.test.assertEquals

interface App {
    val flag: Boolean
}

class TestApp(override val flag: Boolean) : App


class ServicesManagerTest {

    private lateinit var manager: ServicesManager<App>

    @BeforeEach
    fun setUp() {
        stopKoin()
        startKoin {
            environmentProperties()
        }
        manager = KoinServicesManager(GlobalContext, TestApp(true))
    }


    // ----------------------------------
    //       Normal Behavior Test
    // ----------------------------------

    @Test
    fun serviceAddTest() {
        val service = SomeService1Impl(manager)
        manager.registerService(service)
    }


    @Test
    fun serviceAddAndGetTest() {
        val service = SomeService1Impl(manager)
        manager.registerService(service)
        assertEquals(service, manager.getService(SomeService1Impl::class))
    }


    @Test
    fun serviceAddAndInjectTest() {
        val service = SomeService1Impl(manager)
        manager.registerService(service)
        val injected = manager.inject<SomeService1Impl>()
        assertEquals(service, injected.value)
    }


    @Test
    fun multiAddTest() {
        val service1 = SomeService1Impl(manager)
        val service2 = SomeService2Impl(manager)
        manager.registerService(service1)
        manager.registerService(service2)
    }


    @Test
    fun multiAddAndGetTest() {
        val service1 = SomeService1Impl(manager)
        val service2 = SomeService2Impl(manager)
        manager.registerService(service1)
        manager.registerService(service2)
        assertEquals(service1, manager.getService(SomeService1Impl::class))
        assertEquals(service2, manager.getService(SomeService2Impl::class))
    }


    @Test
    fun multiAddAndInjectTest() {
        val service1 = SomeService1Impl(manager)
        val service2 = SomeService2Impl(manager)
        manager.registerService(service1)
        manager.registerService(service2)
        val injected1 = manager.inject<SomeService1Impl>()
        val injected2 = manager.inject<SomeService2Impl>()
        assertEquals(service1, injected1.value)
        assertEquals(service2, injected2.value)
    }


    @Test
    fun serviceAddAndBindTest() {
        val service = SomeService1Impl(manager)
        manager.registerService(service, SomeService1::class)
    }


    @Test
    fun serviceAddAndBindAndGetTest() {
        val service = SomeService1Impl(manager)
        manager.registerService(service, SomeService1::class)
        assertEquals(service, manager.getService(SomeService1Impl::class))
        assertEquals(service, manager.getService(SomeService1::class))
    }


    @Test
    fun serviceAddAndBindAndInjectTest() {
        val service = SomeService1Impl(manager)
        manager.registerService(service, SomeService1::class)
        val injected = manager.inject<SomeService1>()
        val injected2 = manager.inject<SomeService1Impl>()
        assertEquals(service, injected2.value)
        assertEquals(service, injected.value)
    }


    @Test
    fun multiAddAndBindTest() {
        val service1 = SomeService1Impl(manager)
        val service2 = SomeService2Impl(manager)
        manager.registerService(service1, SomeService1::class)
        manager.registerService(service2, SomeService2::class)
    }


    @Test
    fun multiAddAndBindAndGetTest() {
        val service1 = SomeService1Impl(manager)
        val service2 = SomeService2Impl(manager)
        manager.registerService(service1, SomeService1::class)
        manager.registerService(service2, SomeService2::class)
        assertEquals(service1, manager.getService(SomeService1Impl::class))
        assertEquals(service1, manager.getService(SomeService1::class))
        assertEquals(service2, manager.getService(SomeService2Impl::class))
        assertEquals(service2, manager.getService(SomeService2::class))
    }


    @Test
    fun multiAddAndBindAndInjectTest() {
        val service1 = SomeService1Impl(manager)
        val service2 = SomeService2Impl(manager)
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
    fun servicesLoadTest() {
        val service = SomeService1Impl(manager)
        manager.registerService(service, SomeService1::class)
        assertEquals(false, service.loaded)
        manager.loadAll()
        assertEquals(true, service.loaded)
        manager.unloadAll()
        assertEquals(false, service.loaded)
    }


    @Test
    fun servicesReloadTest() {
        val service = SomeService1Impl(manager)
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
        val service1 = DependencyServiceImpl(manager, onTestLoad = {
            loadedFirst = true
            assertEquals(false, loadedSecond)
        })

        val service2 = DependencyServiceImpl(manager, onTestLoad = {
            loadedSecond = true
            assertEquals(true, loadedFirst)
        })

        manager.registerService(service1, DependencyServiceImpl::class)
        manager.registerService(service2, DependencyService::class)
        manager.loadAll()
    }


    @Test
    fun reverseOrderTest() {
        var loadedFirst = false
        var loadedSecond = false
        val service1 = DependencyServiceImpl(manager, onTestLoad = {
            loadedFirst = true
            assertEquals(true, loadedSecond)
        })

        val service2 = DependencyServiceImpl(manager, onTestLoad = {
            loadedSecond = true
            assertEquals(false, loadedFirst)
        })

        manager.registerService(service2, DependencyService::class)
        manager.registerService(service1, DependencyServiceImpl::class)
        manager.loadAll()
    }


    @Test
    fun dependencyAfterLoadTest() {
        var loadedFirst = false
        var loadedSecond = false
        val service1 = DependencyServiceImpl(manager, Dependencies.after(DependencyService::class), onTestLoad = {
            loadedFirst = true
            assertEquals(true, loadedSecond)
        })

        val service2 = DependencyServiceImpl(manager, onTestLoad = {
            loadedSecond = true
            assertEquals(false, loadedFirst)
        })

        manager.registerService(service1, DependencyServiceImpl::class)
        manager.registerService(service2, DependencyService::class)
        manager.loadAll()
    }


    @Test
    fun dependencyBeforeLoadTest() {
        var loadedFirst = false
        var loadedSecond = false
        val service1 = DependencyServiceImpl(manager, Dependencies.before(DependencyService::class), onTestLoad = {
            loadedFirst = true
            assertEquals(true, loadedSecond)
        })

        val service2 = DependencyServiceImpl(manager, onTestLoad = {
            loadedSecond = true
            assertEquals(false, loadedFirst)
        })

        manager.registerService(service1, DependencyServiceImpl::class)
        manager.registerService(service2, DependencyService::class)
        // right order: service2, service1
        manager.loadAll()
    }


    @Test
    fun dependencyLastLoadTest() {
        var loadedFirst = false
        var loadedSecond = false
        val service1 = DependencyServiceImpl(manager, Dependencies.last(), onTestLoad = {
            loadedFirst = true
            assertEquals(true, loadedSecond)
        })

        val service2 = DependencyServiceImpl(manager, onTestLoad = {
            loadedSecond = true
            assertEquals(false, loadedFirst)
        })

        manager.registerService(service1, DependencyServiceImpl::class)
        manager.registerService(service2, DependencyService::class)
        // right order: service2, service1
        manager.loadAll()
    }

    @Test
    fun dependencyFirstLoadTest() {
        var loadedFirst = false
        var loadedSecond = false
        val service1 = DependencyServiceImpl(manager, onTestLoad = {
            loadedFirst = true
            assertEquals(true, loadedSecond)
        })

        val service2 = DependencyServiceImpl(manager, Dependencies.first(), onTestLoad = {
            loadedSecond = true
            assertEquals(false, loadedFirst)
        })

        manager.registerService(service1, DependencyServiceImpl::class)
        manager.registerService(service2, DependencyService::class)
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
        val service = DependencyServiceImpl(manager, Dependencies.after(DependencyServiceImpl::class))
        manager.registerService(service, DependencyServiceImpl::class)
        assertThrows<CircularDependencyException> {
            manager.loadAll()
        }
    }

    @Test
    fun recursiveDependencyTest() {
        val service1 = DependencyServiceImpl(manager, Dependencies.after(DependencyService::class))
        val service2 = DependencyServiceImpl(manager, Dependencies.after(DependencyServiceImpl::class))
        manager.registerService(service1, DependencyService::class)
        manager.registerService(service2, DependencyServiceImpl::class)
        assertThrows<CircularDependencyException> {
            manager.loadAll()
        }
    }


    @Test
    fun wrongBindTest() {

        class SomeClass

        val service = SomeService1Impl(manager)
        assertThrows<IllegalArgumentException> {
            manager.registerService(service, SomeClass::class)
        }
    }

}