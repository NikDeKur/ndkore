@file:Suppress("NOTHING_TO_INLINE")

package dev.nikdekur.ndkore.test

import kotlin.test.assertEquals
import kotlin.test.assertNotEquals
import kotlin.test.assertTrue

public inline fun assertEmpty(iterable: Iterable<*>) {
    if (iterable is Collection<*>)
        assertEquals(0, iterable.size)
    val hasNext = iterable.iterator().hasNext()
    assertTrue(!hasNext)
}

public inline fun assertEmpty(map: Map<*, *>) {
    assertEquals(0, map.size)
}


public inline fun assertNotEmpty(iterable: Iterable<*>) {
    @Suppress("ReplaceSizeCheckWithIsNotEmpty")
    if (iterable is Collection<*>)
        assertNotEquals(0, iterable.size)
    val hasNext = iterable.iterator().hasNext()
    assertTrue(hasNext)
}

public inline fun assertNotEmpty(map: Map<*, *>) {
    assertNotEquals(0, map.size)
}


public inline fun assertSize(iterable: Iterable<*>, expected: Int) {
    if (iterable is Collection<*>)
        assertEquals(expected, iterable.size)

    assertEquals(expected, iterable.count())
}

public inline fun assertSize(map: Map<*, *>, expected: Int) {
    assertEquals(expected, map.size)
}


