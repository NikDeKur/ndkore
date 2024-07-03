/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 * Copyright (c) 2024 Nik De Kur
 */

package dev.nikdekur.ndkore.list

class InfinityQueue<T> : ArrayList<T>, MutableList<T> {
    var index = 0
        private set

    constructor() : super()
    constructor(list: List<T>) : super(list)

    fun get(): T? {
        if (isEmpty)
            return null
        val element = get(index)
        index = (index + 1) % size
        return element
    }
}
