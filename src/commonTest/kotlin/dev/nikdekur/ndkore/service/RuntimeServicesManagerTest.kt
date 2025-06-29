/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 * Copyright (c) 2024-present "Nik De Kur"
 */

package dev.nikdekur.ndkore.service

import dev.nikdekur.ndkore.service.manager.RuntimeServicesManager
import kotlin.test.BeforeTest

class RuntimeServicesManagerTest : ServicesManagerTest() {

    override lateinit var app: App

    @BeforeTest
    fun setUp() {
        val manager = RuntimeServicesManager {}
        app = TestApp(manager, true)
    }
}