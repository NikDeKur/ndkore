/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 * Copyright (c) 2024-present "Nik De Kur"
 */

@file:Suppress("NOTHING_TO_INLINE")

package dev.nikdekur.ndkore.service

interface Service<A> {

    val manager: ServicesManager<A>
    val app
        get() = manager.app

    fun onLoad() {
        // Plugin Module does not require to implement onLoad
    }
    fun onUnload() {
        // Plugin Module does not require to implement onUnload
    }

    val dependencies: Dependencies
        get() = Dependencies.none()
}

