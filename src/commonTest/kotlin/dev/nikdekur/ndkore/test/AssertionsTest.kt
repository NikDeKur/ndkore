package dev.nikdekur.ndkore.test

import kotlin.test.Test
import kotlin.test.assertFails

class AssertionsTest {


    // ASSERT EMPTY

    @Test
    fun testAssertEmptyOnIterableFails() {
        val list = listOf("a", "b", "c")

        assertFails {
            assertEmpty(list)
        }
    }

    @Test
    fun testAssertEmptyOnIterableNotFails() {
        val list = emptyList<String>()

        assertEmpty(list)
    }


    @Test
    fun testAssertEmptyOnMapFails() {
        val map = mapOf("a" to 1, "b" to 2, "c" to 3)

        assertFails {
            assertEmpty(map)
        }
    }


    @Test
    fun testAssertEmptyOnMapNotFails() {
        val map = emptyMap<String, Int>()

        assertEmpty(map)
    }


    // ASSERT NOT EMPTY


    @Test
    fun testAssertNotEmptyFails() {
        val list = emptyList<String>()

        assertFails {
            assertNotEmpty(list)
        }
    }

    @Test
    fun testAssertNotEmptyNotFails() {
        val list = listOf("a", "b", "c")

        assertNotEmpty(list)
    }

    @Test
    fun testAssertNotEmptyOnMapFails() {
        val map = emptyMap<String, Int>()

        assertFails {
            assertNotEmpty(map)
        }
    }


    @Test
    fun testAssertNotEmptyOnMapNotFails() {
        val map = mapOf("a" to 1, "b" to 2, "c" to 3)

        assertNotEmpty(map)
    }


    // ASSERT SIZE

    @Test
    fun testAssertSizeOnIterableFails() {
        val list = listOf("a", "b", "c")

        assertFails {
            assertSize(list, 2)
        }
    }

    @Test
    fun testAssertSizeOnIterableNotFails() {
        val list = listOf("a", "b", "c")

        assertSize(list, 3)
    }

    @Test
    fun testAssertSizeOnMapFails() {
        val map = mapOf("a" to 1, "b" to 2, "c" to 3)

        assertFails {
            assertSize(map, 2)
        }
    }

    @Test
    fun testAssertSizeOnMapNotFails() {
        val map = mapOf("a" to 1, "b" to 2, "c" to 3)

        assertSize(map, 3)
    }

    @Test
    fun testAssertSizeOnIterableWithCountFails() {
        val list = listOf("a", "b", "c")

        assertFails {
            assertSize(list, 2)
        }
    }


    @Test
    fun testAssertSizeOnIterableWithCountNotFails() {
        val list = listOf("a", "b", "c")

        assertSize(list, 3)
    }
}