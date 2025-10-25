/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 * Copyright (c) 2024-present "Nik De Kur"
 */

package dev.nikdekur.ndkore.service

class SomeService2Impl(
    override val app: App,
    override val dependencies: Dependencies<out Service> = Dependencies.none()
) : MyService(), SomeService2 {

    override suspend fun onEnable() {
        loaded = true
    }

    override suspend fun onDisable() {
        loaded = false
    }

    override var loaded: Boolean = false
}