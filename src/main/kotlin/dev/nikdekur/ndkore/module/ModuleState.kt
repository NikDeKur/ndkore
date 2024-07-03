/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 * Copyright (c) 2024 Nik De Kur
 */

package dev.nikdekur.ndkore.module

enum class ModuleState {
    LOADING,
    LOADED,
    UNLOADED,
    UNLOADING

    ;

    fun toMoment(): TaskMoment {
        return when (this) {
            LOADING -> TaskMoment.BEFORE_LOAD
            LOADED -> TaskMoment.AFTER_LOAD
            UNLOADING -> TaskMoment.BEFORE_UNLOAD
            UNLOADED -> TaskMoment.AFTER_UNLOAD
        }
    }
}