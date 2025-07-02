package dev.nikdekur.ndkore.reflect

import kotlinx.serialization.Serializable
import kotlin.test.*

/**
 * Abstract test class for ReflectMethod implementations.
 * Extend this class and implement [createReflectMethod] to test your implementation.
 */
abstract class ReflectMethodTest {

    /**
     * Create an instance of ReflectMethod implementation to test.
     */
    abstract fun createReflectMethod(): ReflectMethod

    private val reflectMethod by lazy { createReflectMethod() }


    @Serializable
    data class UserData(val name: String, val age: Int)


    @Serializable
    data class NestedData(val user: UserData, val isActive: Boolean)


    @Serializable
    data class NullableData(val value: String?)


    @Serializable
    class TestClass {
        val publicField = "public"
        private val privateField = "private"

        fun getName(): String = "methodName"
        fun getAge(): Int = 25
        fun isActive(): Boolean = true
        fun getValue(): String? = null
    }


    @Test
    fun testFindSimpleField() {
        val data = UserData("John", 20)

        assertEquals("John", reflectMethod.findValue(data, "name"))
        assertEquals(20, reflectMethod.findValue(data, "age"))
    }

    @Test
    fun testFindNonExistentField() {
        val data = UserData("John", 20)

        assertEquals(ReflectMethod.NotFound, reflectMethod.findValue(data, "nonExistent"))
        assertEquals(ReflectMethod.NotFound, reflectMethod.findValue(data, ""))
    }

    @Test
    fun testFindInNestedObject() {
        val userData = UserData("Alice", 30)
        val nestedData = NestedData(userData, true)

        assertEquals(userData, reflectMethod.findValue(nestedData, "user"))
        assertEquals(true, reflectMethod.findValue(nestedData, "isActive"))
    }

    @Test
    fun testFindNullValue() {
        val data = NullableData(null)

        // Should return null (the actual value), not NotFound
        assertNull(reflectMethod.findValue(data, "value"))
        assertNotEquals(ReflectMethod.NotFound, reflectMethod.findValue(data, "value"))
    }

    @Test
    fun testFindInMap() {
        val map = mapOf(
            "key1" to "value1",
            "key2" to 42,
            "key3" to null
        )

        assertEquals("value1", reflectMethod.findValue(map, "key1"))
        assertEquals(42, reflectMethod.findValue(map, "key2"))
        assertNull(reflectMethod.findValue(map, "key3"))
        assertEquals(ReflectMethod.NotFound, reflectMethod.findValue(map, "nonExistent"))
    }

    @Test
    fun testFindInNestedMap() {
        val nestedMap = mapOf(
            "level1" to mapOf(
                "level2" to "deepValue"
            )
        )

        // Test direct access to nested map
        val level1Result = reflectMethod.findValue(nestedMap, "level1")
        assertTrue(level1Result is Map<*, *>)
    }

    @Test
    fun testFindInList() {
        val list = listOf("first", "second", "third")

        // Test index access
        assertEquals("first", reflectMethod.findValue(list, "0"))
        assertEquals("second", reflectMethod.findValue(list, "1"))
        assertEquals("third", reflectMethod.findValue(list, "2"))
        assertEquals(ReflectMethod.NotFound, reflectMethod.findValue(list, "10"))
        assertEquals(ReflectMethod.NotFound, reflectMethod.findValue(list, "notAnIndex"))
    }


    @Test
    fun testFindMethodResults() {
        if (!reflectMethod.supportMethodCalling) {
            // Skip this test if method calling is not supported
            return
        }

        val testObj = TestClass()

        // Test direct method calls
        assertEquals("methodName", reflectMethod.findValue(testObj, "getName"))
        assertEquals(25, reflectMethod.findValue(testObj, "getAge"))
        assertEquals(true, reflectMethod.findValue(testObj, "isActive"))
        assertNull(reflectMethod.findValue(testObj, "getValue"))

        // Test camelCase property access (if supported)
        val nameResult = reflectMethod.findValue(testObj, "name")
        // This might return "methodName" if getter conversion is supported
        assertTrue(nameResult == "methodName" || nameResult == ReflectMethod.NotFound)
    }

    @Test
    fun testFindFields() {
        val testObj = TestClass()

        assertEquals("public", reflectMethod.findValue(testObj, "publicField"))

        // Private field access depends on implementation
        val privateResult = reflectMethod.findValue(testObj, "privateField")
        assertTrue(privateResult == "private" || privateResult == ReflectMethod.NotFound)
    }

    @Test
    fun testFindWithDifferentTypes() {
        if (!reflectMethod.supportMethodCalling) {
            // Skip this test if method calling is not supported
            return
        }

        val stringObj = "test"

        assertEquals(4, reflectMethod.findValue(stringObj, "length"))
    }

    @Test
    fun testFindWithEmptyCollections() {
        val emptyList = emptyList<String>()
        val emptyMap = emptyMap<String, Any>()

        assertEquals(ReflectMethod.NotFound, reflectMethod.findValue(emptyList, "0"))
        assertEquals(ReflectMethod.NotFound, reflectMethod.findValue(emptyList, "name"))
        assertEquals(ReflectMethod.NotFound, reflectMethod.findValue(emptyMap, "key"))
    }

    @Test
    fun testFindWithComplexNesting() {
        val complexData = mapOf(
            "users" to listOf(
                UserData("Alice", 25),
                UserData("Bob", 30)
            ),
            "settings" to mapOf(
                "theme" to "dark",
                "notifications" to true
            )
        )

        val usersResult = reflectMethod.findValue(complexData, "users")
        assertTrue(usersResult is List<*>)

        val settingsResult = reflectMethod.findValue(complexData, "settings")
        assertTrue(settingsResult is Map<*, *>)

        // Test finding in nested list
        val userNameResult = reflectMethod.findValue(usersResult, "name")
        assertEquals("Alice", userNameResult)
    }

    @Test
    fun testEdgeCases() {
        // Test with various name formats
        val data = UserData("Test", 1)
        assertEquals(ReflectMethod.NotFound, reflectMethod.findValue(data, "NAME"))
        assertEquals("Test", reflectMethod.findValue(data, "name"))
    }

    @Test
    fun testConsistentNotFoundBehavior() {
        val data = UserData("John", 20)

        // NotFound should be the same object instance
        val result1 = reflectMethod.findValue(data, "nonExistent1")
        val result2 = reflectMethod.findValue(data, "nonExistent2")

        assertEquals(ReflectMethod.NotFound, result1)
        assertEquals(ReflectMethod.NotFound, result2)
        assertSame(result1, result2) // Should be the same object instance
    }

    @Test
    fun testSpecialCharactersInNames() {
        val map = mapOf(
            "key-with-dashes" to "value1",
            "key_with_underscores" to "value2",
            "key with spaces" to "value3"
        )

        assertEquals("value1", reflectMethod.findValue(map, "key-with-dashes"))
        assertEquals("value2", reflectMethod.findValue(map, "key_with_underscores"))
        assertEquals("value3", reflectMethod.findValue(map, "key with spaces"))
    }
}