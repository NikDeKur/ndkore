/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 * Copyright (c) 2024-present "Nik De Kur"
 */

package dev.nikdekur.ndkore.service

class SomeService1Impl(
    override val app: App,
    override val dependencies: Dependencies = Dependencies.none()
) : MyService(), SomeService1 {

    override fun onEnable() {
        loaded = true
    }

    override fun onDisable() {
        loaded = false
    }

    override var loaded: Boolean = false
}