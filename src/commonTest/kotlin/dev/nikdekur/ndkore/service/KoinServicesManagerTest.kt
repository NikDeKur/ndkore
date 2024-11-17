/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 * Copyright (c) 2024-present "Nik De Kur"
 */

package dev.nikdekur.ndkore.service

import dev.nikdekur.ndkore.koin.SimpleKoinContext
import dev.nikdekur.ndkore.service.manager.KoinServicesManager
import kotlin.test.BeforeTest

class KoinServicesManagerTest : ServicesManagerTest() {

    override lateinit var app: App

    @BeforeTest
    fun setUp() {
        val context = SimpleKoinContext()
        context.startKoin {}
        val manager = KoinServicesManager {
            context(context)
        }
        app = TestApp(manager, true)
    }
}