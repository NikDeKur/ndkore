package dev.nikdekur.ndkore.ext

import kotlin.test.*

/**
 * Comprehensive test suite for BitField64 class.
 * Tests basic operations, edge cases, and various usage scenarios.
 */
class BitField64Test {

    /**
     * Tests the default state when creating a new BitField64 with 0.
     */
    @Test
    fun testInitialState() {
        val field = BitField64(0UL)

        // All bits should be 0 (false)
        for (i in 0 until 64) {
            assertFalse(field[i], "Bit at index $i should be false initially")
        }

        assertEquals(0L, field.toLong(), "Initial long value should be 0")
        assertEquals(0UL, field.toULong(), "Initial ulong value should be 0")
    }

    /**
     * Tests creating a BitField64 with a specific bit pattern.
     */
    @Test
    fun testInitWithValue() {
        // Set alternating bit pattern 101010...
        val value = 0xAAAAAAAAAAAAAAAAUL
        val field = BitField64(value)

        // Even indices should be 0, odd indices should be 1
        for (i in 0 until 64) {
            assertEquals(i % 2 == 1, field[i], "Bit at index $i should match expected pattern")
        }

        assertEquals(value.toLong(), field.toLong(), "toLong() should return the correct value")
        assertEquals(value, field.toULong(), "toULong() should return the original value")
    }

    /**
     * Tests the get operator for retrieving bit values.
     */
    @Test
    fun testGetOperator() {
        // Set specific bits
        val value = (1UL shl 0) or (1UL shl 5) or (1UL shl 63)
        val field = BitField64(value)

        // Check specific bits that should be true
        assertTrue(field[0], "Bit at index 0 should be true")
        assertTrue(field[5], "Bit at index 5 should be true")
        assertTrue(field[63], "Bit at index 63 should be true")

        // Check some bits that should be false
        assertFalse(field[1], "Bit at index 1 should be false")
        assertFalse(field[10], "Bit at index 10 should be false")
        assertFalse(field[62], "Bit at index 62 should be false")
    }

    /**
     * Tests the set operator for setting individual bits.
     */
    @Test
    fun testSetOperator() {
        var field = BitField64(0UL)

        // Set some bits to true
        field = field.set(3, true)
        field = field.set(7, true)
        field = field.set(63, true)

        // Verify the bits were set
        assertTrue(field[3], "Bit at index 3 should be true after setting")
        assertTrue(field[7], "Bit at index 7 should be true after setting")
        assertTrue(field[63], "Bit at index 63 should be true after setting")

        // Verify other bits remain unset
        assertFalse(field[0], "Bit at index 0 should remain false")
        assertFalse(field[4], "Bit at index 4 should remain false")

        // Expected value after setting bits 3, 7, and 63
        val expectedValue = (1UL shl 3) or (1UL shl 7) or (1UL shl 63)
        assertEquals(expectedValue, field.toULong(), "Value should reflect bits that were set")
    }

    /**
     * Tests setting a bit that was already set (should not change).
     */
    @Test
    fun testSettingAlreadySetBit() {
        val field = BitField64(1UL) // Bit 0 is already set
        val newField = field.set(0, true)

        assertTrue(newField[0], "Bit should still be set")
        assertEquals(1UL, newField.toULong(), "Value should remain unchanged when setting an already set bit")
    }

    /**
     * Tests unsetting a bit that was already unset (should not change).
     */
    @Test
    fun testClearingAlreadyClearedBit() {
        val field = BitField64(0UL) // All bits are cleared
        val newField = field.set(10, false)

        assertFalse(newField[10], "Bit should remain cleared")
        assertEquals(0UL, newField.toULong(), "Value should remain unchanged when clearing an already cleared bit")
    }

    /**
     * Tests toggling bits (setting true to false and false to true).
     */
    @Test
    fun testToggleBits() {
        var field = BitField64(0UL)

        // Set some bits
        field = field.set(1, true)
        field = field.set(32, true)

        // Now toggle them
        field = field.set(1, false)
        field = field.set(32, false)

        // And set some that were initially false
        field = field.set(5, true)
        field = field.set(63, true)

        // Verify the final state
        assertFalse(field[1], "Bit 1 should be toggled off")
        assertFalse(field[32], "Bit 32 should be toggled off")
        assertTrue(field[5], "Bit 5 should be toggled on")
        assertTrue(field[63], "Bit 63 should be toggled on")

        // Expected final value
        val expectedValue = (1UL shl 5) or (1UL shl 63)
        assertEquals(expectedValue, field.toULong(), "Final value should reflect all toggle operations")
    }

    /**
     * Tests the behavior at boundary indices (0 and 63).
     */
    @Test
    fun testBoundaryIndices() {
        var field = BitField64(0UL)

        // Set boundary bits
        field = field.set(0, true)
        field = field.set(63, true)

        // Verify they were set
        assertTrue(field[0], "Lowest bit (0) should be set")
        assertTrue(field[63], "Highest bit (63) should be set")

        // Expected value with first and last bits set
        val expectedValue = 1UL or (1UL shl 63)
        assertEquals(expectedValue, field.toULong(), "Value should have first and last bits set")

        // Now clear them
        field = field.set(0, false)
        field = field.set(63, false)

        // Verify they were cleared
        assertFalse(field[0], "Lowest bit (0) should be cleared")
        assertFalse(field[63], "Highest bit (63) should be cleared")

        assertEquals(0UL, field.toULong(), "Value should be 0 after clearing all set bits")
    }

    /**
     * Tests conversion between Long and ULong with the toLong and toULong methods.
     */
    @Test
    fun testConversions() {
        // Test with a value that has the sign bit set in Long representation
        val value = ULong.MAX_VALUE
        val field = BitField64(value)

        assertEquals(-1L, field.toLong(), "toLong() with all bits set should be -1")
        assertEquals(ULong.MAX_VALUE, field.toULong(), "toULong() should preserve the original value")
    }

    /**
     * Tests that BitField64 is immutable - operations should return new instances.
     */
    @Test
    fun testImmutability() {
        val field1 = BitField64(1UL)
        val field2 = field1.set(1, true)

        // The Original object should be unchanged
        assertEquals(1UL, field1.toULong(), "Original BitField64 should be unchanged")

        // New object should have the updated value
        assertEquals(3UL, field2.toULong(), "New BitField64 should have the updated value")

        // Should be different instances
        assertNotSame(field1, field2, "set() should return a new BitField64 instance")
    }

    /**
     * Tests that the value class is properly inlined by verifying its value equals behavior.
     */
    @Test
    fun testValueClassBehavior() {
        val field1 = BitField64(42UL)
        val field2 = BitField64(42UL)
        val field3 = BitField64(43UL)

        // Different instances with the same value should be equal
        assertEquals(field1, field2, "BitField64 instances with same value should be equal")
        assertNotEquals(field1, field3, "BitField64 instances with different values should not be equal")

        // HashCode should be determined by value
        assertEquals(field1.hashCode(), field2.hashCode(), "Hash codes should be equal for equal values")
    }

    /**
     * Tests for invalid index arguments (outside the 0-63 range).
     * This includes testing the expected exception behavior.
     */
    @Test
    fun testInvalidIndices() {
        val field = BitField64(0UL)

        // Test negative indices
        assertFailsWith<IndexOutOfBoundsException> {
            field[-1]
        }

        // Test indices greater than 63
        assertFailsWith<IndexOutOfBoundsException> {
            field[64]
        }

        // Test setting with invalid indices
        assertFailsWith<IndexOutOfBoundsException> {
            field[-1] = true
        }

        assertFailsWith<IndexOutOfBoundsException> {
            field[64] = false
        }
    }


    /**
     * Tests the behavior of BitField64 with all bits set to 0.
     * Test is making sure that NONE is all zeros.
     */
    @Test
    fun testBitFieldNoneIsAllZero() {
        val field = BitField64.NONE
        assertEquals(0UL, field.toULong(), "BitField64.NONE should be all zeros")
    }

    /**
     * Tests the behavior of BitField64 with all bits set to 1.
     * Test is making sure that ALL is all ones.
     */
    @Test
    fun testBitFieldAllIsAllOnes() {
        val field = BitField64.ALL
        assertEquals(0xFFFFFFFFFFFFFFFFUL, field.toULong(), "BitField64.ALL should be all ones")
    }
}