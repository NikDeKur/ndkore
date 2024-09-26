/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 * Copyright (c) 2024-present "Nik De Kur"
 */

package dev.nikdekur.ndkore.math

public class Ratio(public val ratio: Iterable<Int>) {

    public constructor(vararg ratio: Int) : this(ratio.toList())

    public fun split(number: Int): List<Int> {
        val oneEl: Int = number / ratio.sum()
        return ratio.map { it * oneEl }
    }
}
