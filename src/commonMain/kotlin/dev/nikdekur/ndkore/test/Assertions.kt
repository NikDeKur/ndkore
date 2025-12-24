@file:Suppress("NOTHING_TO_INLINE")

package dev.nikdekur.ndkore.test

import kotlin.test.assertEquals
import kotlin.test.assertNotEquals
import kotlin.test.assertTrue

public inline fun assertEmpty(iterable: Iterable<*>, message: String? = null) {
    if (iterable is Collection<*>)
        assertEquals(0, iterable.size, message)

    val hasNext = iterable.iterator().hasNext()
    assertTrue(!hasNext, message)
}

public inline fun assertEmpty(map: Map<*, *>, message: String? = null) {
    assertEquals(0, map.size, message)
}

public inline fun assertNotEmpty(iterable: Iterable<*>, message: String? = null) {
    if (iterable is Collection<*>)
        assertNotEquals(0, iterable.size, message)

    val hasNext = iterable.iterator().hasNext()
    assertTrue(hasNext, message)
}

public inline fun assertNotEmpty(map: Map<*, *>, message: String? = null) {
    assertNotEquals(0, map.size, message)
}

public inline fun assertSize(iterable: Iterable<*>, expected: Int, message: String? = null) {
    if (iterable is Collection<*>)
        assertEquals(expected, iterable.size, message)

    assertEquals(expected, iterable.count(), message)
}

public inline fun assertSize(map: Map<*, *>, expected: Int, message: String? = null) {
    assertEquals(expected, map.size, message)
}