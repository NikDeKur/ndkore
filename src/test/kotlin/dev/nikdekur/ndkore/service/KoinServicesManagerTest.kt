/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 * Copyright (c) 2024-present "Nik De Kur"
 */

package dev.nikdekur.ndkore.service

import dev.nikdekur.ndkore.koin.SimpleKoinContext
import org.junit.jupiter.api.BeforeEach
import org.koin.environmentProperties

class KoinServicesManagerTest : ServicesManagerTest {

    override lateinit var app: App

    @BeforeEach
    fun setUp() {
        val context = SimpleKoinContext()
        context.startKoin {
            environmentProperties()
        }
        val manager = KoinServicesManager(context)
        app = TestApp(manager, true)
    }
}