/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 * Copyright (c) 2024-present "Nik De Kur"
 */

package dev.nikdekur.ndkore.service

class DependencyServiceImpl(
    override val manager: ServicesManager<App>,
    override val dependencies: Dependencies = Dependencies.none(),
    val onTestLoad: DependencyServiceImpl.() -> Unit = {},
    val onTestUnload: DependencyServiceImpl.() -> Unit = {}
) : MyModule, DependencyService {

    override fun onLoad() {
        onTestLoad()
    }

    override fun onUnload() {
        onTestUnload()
    }
}