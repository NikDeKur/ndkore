/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 * Copyright (c) 2024-present "Nik De Kur"
 */

package dev.nikdekur.ndkore.service

class ConfigurableService1Impl(
    override val app: App,
    override val dependencies: Dependencies = Dependencies.none(),
    val onTestLoad: ConfigurableService1Impl.() -> Unit = {},
    val onTestUnload: ConfigurableService1Impl.() -> Unit = {}
) : MyService(), ConfigurableService {

    val service by inject<SomeService1>()

    override suspend fun onEnable() {
        onTestLoad()
    }

    override suspend fun onDisable() {
        onTestUnload()
    }
}