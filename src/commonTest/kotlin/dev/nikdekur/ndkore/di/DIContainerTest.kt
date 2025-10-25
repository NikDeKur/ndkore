package dev.nikdekur.ndkore.di

import kotlin.test.*

/**
 * Abstract test class for DIContainer implementations.
 * 
 * This class provides comprehensive test coverage for all DIContainer interface methods,
 * ensuring consistent behavior across different implementations.
 * 
 * Subclasses must implement [createContainer] to provide the specific container instance to test.
 */
abstract class DIContainerTest {

    /**
     * Creates a fresh container instance for testing.
     * Each test method will call this to get a clean container state.
     */
    protected abstract fun createContainer(): DIContainer

    // Test data classes and interfaces
    interface TestService
    interface AnotherTestService
    interface ThirdTestService

    class TestServiceImpl : TestService
    class AnotherTestServiceImpl : AnotherTestService, ThirdTestService
    class ConcreteService

    enum class TestQualifier : Qualifier {
        PRIMARY, SECONDARY;

        override val value: QualifierValue
            get() = name
    }

    @Test
    fun `add should register service without binding`() {
        val container = createContainer()
        val service = TestServiceImpl()
        val definition = Definition(service)

        container.add(definition)

        val retrieved = container.getOrNull(TestServiceImpl::class)
        assertSame(service, retrieved)
    }

    @Test
    fun `add should register service with single interface binding`() {
        val container = createContainer()
        val service = TestServiceImpl()
        val definition = service bind TestService::class

        container.add(definition)

        val retrieved = container.getOrNull(TestService::class)
        assertSame(service, retrieved)
    }

    @Test
    fun `add should register service with multiple interface bindings`() {
        val container = createContainer()
        val service = AnotherTestServiceImpl()
        val definition = service.binds(AnotherTestService::class, ThirdTestService::class)

        container.add(definition)

        val retrievedAsAnother = container.getOrNull(AnotherTestService::class)
        val retrievedAsThird = container.getOrNull(ThirdTestService::class)

        assertSame(service, retrievedAsAnother)
        assertSame(service, retrievedAsThird)
    }

    @Test
    fun `add should register service with qualifier`() {
        val container = createContainer()
        val service = TestServiceImpl()
        val definition = service bind TestService::class qualify TestQualifier.PRIMARY

        container.add(definition)

        val retrieved = container.getOrNull(TestService::class, TestQualifier.PRIMARY.qualifier)
        assertSame(service, retrieved)
    }

    @Test
    fun `add should register service with string qualifier`() {
        val container = createContainer()
        val service = TestServiceImpl()
        val definition = service bind TestService::class qualify "custom-qualifier"

        container.add(definition)

        val retrieved = container.getOrNull(TestService::class, "custom-qualifier".qualifier)
        assertSame(service, retrieved)
    }

    @Test
    fun `add should register multiple services with different qualifiers`() {
        val container = createContainer()
        val primaryService = TestServiceImpl()
        val secondaryService = TestServiceImpl()

        container.add(primaryService bind TestService::class qualify TestQualifier.PRIMARY)
        container.add(secondaryService bind TestService::class qualify TestQualifier.SECONDARY)

        val retrievedPrimary = container.getOrNull(TestService::class, TestQualifier.PRIMARY.qualifier)
        val retrievedSecondary = container.getOrNull(TestService::class, TestQualifier.SECONDARY.qualifier)

        assertSame(primaryService, retrievedPrimary)
        assertSame(secondaryService, retrievedSecondary)
        assertNotSame(primaryService, secondaryService)
    }

    @Test
    fun `add should handle complex definition with binding and qualifier`() {
        val container = createContainer()
        val service = AnotherTestServiceImpl()
        val definition = service
            .bind(AnotherTestService::class)
            .bind(ThirdTestService::class)
            .qualify(TestQualifier.PRIMARY)

        container.add(definition)

        val retrievedAsAnother = container.getOrNull(AnotherTestService::class, TestQualifier.PRIMARY.qualifier)
        val retrievedAsThird = container.getOrNull(ThirdTestService::class, TestQualifier.PRIMARY.qualifier)

        assertSame(service, retrievedAsAnother)
        assertSame(service, retrievedAsThird)
    }

    @Test
    fun `getOrNull should return service when exists without qualifier`() {
        val container = createContainer()
        val service = TestServiceImpl()
        container.add(service bind TestService::class)

        val retrieved = container.getOrNull(TestService::class)

        assertSame(service, retrieved)
    }

    @Test
    fun `getOrNull should return service when exists with empty qualifier`() {
        val container = createContainer()
        val service = TestServiceImpl()
        container.add(service bind TestService::class)

        val retrieved = container.getOrNull(TestService::class, Qualifier.Empty)

        assertSame(service, retrieved)
    }

    @Test
    fun `getOrNull should return service when exists with specific qualifier`() {
        val container = createContainer()
        val service = TestServiceImpl()
        container.add(service bind TestService::class qualify TestQualifier.PRIMARY)

        val retrieved = container.getOrNull(TestService::class, TestQualifier.PRIMARY.qualifier)

        assertSame(service, retrieved)
    }

    @Test
    fun `getOrNull should return null when service does not exist`() {
        val container = createContainer()

        val retrieved = container.getOrNull(TestService::class)

        assertNull(retrieved)
    }

    @Test
    fun `getOrNull should return null when service exists but with different qualifier`() {
        val container = createContainer()
        val service = TestServiceImpl()
        container.add(service bind TestService::class qualify TestQualifier.PRIMARY)

        val retrieved = container.getOrNull(TestService::class, TestQualifier.SECONDARY.qualifier)

        assertNull(retrieved)
    }

    @Test
    fun `getOrNull should return null when service exists without qualifier but requested with qualifier`() {
        val container = createContainer()
        val service = TestServiceImpl()
        container.add(service bind TestService::class)

        val retrieved = container.getOrNull(TestService::class, TestQualifier.PRIMARY.qualifier)

        assertNull(retrieved)
    }

    @Test
    fun `getOrNull should return null when service exists with qualifier but requested without qualifier`() {
        val container = createContainer()
        val service = TestServiceImpl()
        container.add(service bind TestService::class qualify TestQualifier.PRIMARY)

        val retrieved = container.getOrNull(TestService::class)

        assertNull(retrieved)
    }

    @Test
    fun `get should return service when exists without qualifier`() {
        val container = createContainer()
        val service = TestServiceImpl()
        container.add(service bind TestService::class)

        val retrieved = container.get(TestService::class)

        assertSame(service, retrieved)
    }

    @Test
    fun `get should return service when exists with qualifier`() {
        val container = createContainer()
        val service = TestServiceImpl()
        container.add(service bind TestService::class qualify TestQualifier.PRIMARY)

        val retrieved = container.get(TestService::class, TestQualifier.PRIMARY.qualifier)

        assertSame(service, retrieved)
    }

    @Test
    fun `get should throw DependencyNotFoundException when service does not exist`() {
        val container = createContainer()

        val exception = assertFailsWith<DependencyNotFoundException> {
            container.get(TestService::class)
        }

        // Verify exception contains relevant information
        assertEquals(exception.message?.contains("TestService"), true)
    }

    @Test
    fun `get should throw DependencyNotFoundException when service exists but with different qualifier`() {
        val container = createContainer()
        val service = TestServiceImpl()
        container.add(service bind TestService::class qualify TestQualifier.PRIMARY)

        val exception = assertFailsWith<DependencyNotFoundException> {
            container.get(TestService::class, TestQualifier.SECONDARY.qualifier)
        }

        assertEquals(exception.message?.contains("TestService"), true)
    }

    @Test
    fun `get should throw DependencyNotFoundException when service exists without qualifier but requested with qualifier`() {
        val container = createContainer()
        val service = TestServiceImpl()
        container.add(service bind TestService::class)

        assertFailsWith<DependencyNotFoundException> {
            container.get(TestService::class, TestQualifier.PRIMARY.qualifier)
        }
    }

    @Test
    fun `get should throw DependencyNotFoundException when service exists with qualifier but requested without qualifier`() {
        val container = createContainer()
        val service = TestServiceImpl()
        container.add(service bind TestService::class qualify TestQualifier.PRIMARY)

        assertFailsWith<DependencyNotFoundException> {
            container.get(TestService::class)
        }
    }

    @Test
    fun `container should handle inheritance hierarchy correctly`() {
        val container = createContainer()
        val service = TestServiceImpl()
        container.add(service bind TestService::class)

        // Should be able to retrieve as interface
        val retrievedAsInterface = container.getOrNull(TestService::class)
        assertSame(service, retrievedAsInterface)

        // Should also be retrievable as concrete class if registered
        container.add(Definition(service))
        val retrievedAsConcrete = container.getOrNull(TestServiceImpl::class)
        assertSame(service, retrievedAsConcrete)
    }

    @Test
    fun `container should handle same service registered multiple times with different qualifiers`() {
        val container = createContainer()
        val service = TestServiceImpl()

        container.add(service bind TestService::class qualify TestQualifier.PRIMARY)
        container.add(service bind TestService::class qualify TestQualifier.SECONDARY)

        val primaryRetrieved = container.getOrNull(TestService::class, TestQualifier.PRIMARY.qualifier)
        val secondaryRetrieved = container.getOrNull(TestService::class, TestQualifier.SECONDARY.qualifier)

        assertSame(service, primaryRetrieved)
        assertSame(service, secondaryRetrieved)
    }

    @Test
    fun `container should handle overriding service registration`() {
        val container = createContainer()
        val firstService = TestServiceImpl()
        val secondService = TestServiceImpl()

        container.add(firstService bind TestService::class)
        container.add(secondService bind TestService::class) // Override

        val retrieved = container.getOrNull(TestService::class)

        // The behavior may vary by implementation, but it should return one of them consistently
        assertNotNull(retrieved)
        assertTrue(retrieved === firstService || retrieved === secondService)
    }

    @Test
    fun `container should handle empty qualifier correctly`() {
        val container = createContainer()
        val service = TestServiceImpl()
        container.add(service bind TestService::class qualify Qualifier.Empty)

        val retrievedWithEmpty = container.getOrNull(TestService::class, Qualifier.Empty)
        val retrievedWithoutQualifier = container.getOrNull(TestService::class)

        assertSame(service, retrievedWithEmpty)
        assertSame(service, retrievedWithoutQualifier)
    }

    @Test
    fun `container should handle string qualifier conversion correctly`() {
        val container = createContainer()
        val service = TestServiceImpl()
        val qualifierString = "test-qualifier"

        container.add(service bind TestService::class qualify qualifierString)

        val retrieved = container.getOrNull(TestService::class, qualifierString.qualifier)

        assertSame(service, retrieved)
    }

    @Test
    fun `container should distinguish between different string qualifiers`() {
        val container = createContainer()
        val service1 = TestServiceImpl()
        val service2 = TestServiceImpl()

        container.add(service1 bind TestService::class qualify "qualifier1")
        container.add(service2 bind TestService::class qualify "qualifier2")

        val retrieved1 = container.getOrNull(TestService::class, "qualifier1".qualifier)
        val retrieved2 = container.getOrNull(TestService::class, "qualifier2".qualifier)

        assertSame(service1, retrieved1)
        assertSame(service2, retrieved2)
    }
}