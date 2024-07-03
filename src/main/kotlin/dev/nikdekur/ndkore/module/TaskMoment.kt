/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 * Copyright (c) 2024 Nik De Kur
 */

package dev.nikdekur.ndkore.module

enum class TaskMoment(val isBefore: Boolean) {
    BEFORE_LOAD(true),
    AFTER_LOAD(false),

    BEFORE_UNLOAD(true),
    AFTER_UNLOAD(false),

    ;

    val isAfter: Boolean
        get() = !isBefore
}