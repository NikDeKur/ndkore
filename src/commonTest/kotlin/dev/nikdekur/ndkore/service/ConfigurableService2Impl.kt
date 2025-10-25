/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 * Copyright (c) 2024-present "Nik De Kur"
 */

package dev.nikdekur.ndkore.service

class ConfigurableService2Impl(
    override val app: App,
    override val dependencies: Dependencies<out Service> = Dependencies.none(),
    val onTestLoad: ConfigurableService2Impl.() -> Unit = {},
    val onTestUnload: ConfigurableService2Impl.() -> Unit = {}
) : MyService(), ConfigurableService {

    val service by inject<SomeService1>()

    override suspend fun onEnable() {
        onTestLoad()
    }

    override suspend fun onDisable() {
        onTestUnload()
    }
}