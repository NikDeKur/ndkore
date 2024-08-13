/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 * Copyright (c) 2024-present "Nik De Kur"
 */

package dev.nikdekur.ndkore.service

class SomeService2Impl(
    override val manager: ServicesManager<App>
) : MyModule, SomeService2 {

    override val dependencies = dependencies {
        after(SomeService1::class)
    }

    override fun onLoad() {
        loaded = true
    }

    override fun onUnload() {
        loaded = false
    }

    override var loaded: Boolean = false
}