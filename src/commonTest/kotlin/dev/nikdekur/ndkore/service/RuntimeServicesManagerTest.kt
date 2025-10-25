/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 * Copyright (c) 2024-present "Nik De Kur"
 */

package dev.nikdekur.ndkore.service

import dev.nikdekur.ndkore.di.RuntimeDIContainer
import dev.nikdekur.ndkore.service.manager.DIServicesManager
import kotlin.test.BeforeTest

class DIServicesManagerTest : ServicesManagerTest() {

    override lateinit var app: App

    @BeforeTest
    fun setUp() {
        val dIContainer = RuntimeDIContainer {}
        val manager = DIServicesManager(dIContainer)
        app = TestApp(manager, true)
    }
}