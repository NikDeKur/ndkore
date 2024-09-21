/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 * Copyright (c) 2024-present "Nik De Kur"
 */

package dev.nikdekur.ndkore.math

import dev.nikdekur.ndkore.ext.addProcent

enum class MathAction {
    SET,
    PLUS,
    MINUS,
    PLUS_PROCENT,
    MINUS_PROCENT;

    fun apply(val1: Double, val2: Double): Double {
        return when (this) {
            SET -> val2
            PLUS -> val1 + val2
            MINUS -> val1 - val2
            PLUS_PROCENT -> val1.addProcent(val2)
            MINUS_PROCENT -> val1.addProcent(val2)
        }
    }

    override fun toString(): String {
        return name
    }
}
