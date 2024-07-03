/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 * Copyright (c) 2024 Nik De Kur
 */

package dev.nikdekur.ndkore.math

class Ratio {
    var ratio: List<Int>

    constructor(vararg ratio: Int) {
        this.ratio = ratio.toList()
    }

    constructor(ratio: List<Int>) {
        this.ratio = ratio
    }

    fun split(number: Int): List<Int> {
        val oneEl: Int = number / ratio.sum()
        return ratio.map { it * oneEl }
    }
}
