/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 * Copyright (c) 2024-present "Nik De Kur"
 */

@file:Suppress("NOTHING_TO_INLINE")

package dev.nikdekur.ndkore.test

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue

inline fun assertEmpty(iterable: Iterable<*>) {
    if (iterable is Collection<*>)
        assertEquals(0, iterable.size)

    val hasNext = iterable.iterator().hasNext()
    assertTrue(!hasNext)
}

inline fun assertEmpty(map: Map<*, *>) {
    assertEquals(0, map.size)
}


inline fun assertSize(iterable: Iterable<*>, expected: Int) {
    if (iterable is Collection<*>)
        assertEquals(expected, iterable.size)

    assertEquals(expected, iterable.count())
}

inline fun assertSize(map: Map<*, *>, expected: Int) {
    assertEquals(expected, map.size)
}


