@file:OptIn(ExperimentalTime::class)

package dev.nikdekur.ndkore.snowstar

import co.touchlab.stately.collections.ConcurrentMutableSet
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runTest
import kotlin.test.*
import kotlin.time.Clock
import kotlin.time.ExperimentalTime
import kotlin.time.Instant


class FakeClock(var currentMillis: Long) : Clock {
    override fun now(): Instant = Instant.fromEpochMilliseconds(currentMillis)
}

class SnowstarGeneratorTest {

    @Test
    fun happyPathGeneratesValidSnowstar() {
        val fakeClock: Clock = FakeClock(1000)
        val generator = SnowstarGenerator(
            clock = fakeClock,
            version = 1u,
            datacenterId = 2u,
            workerId = 3u,
            processId = 4u,
            defaultIncrement = 0u
        )
        val snowstar = generator.generate()
        assertEquals(1, snowstar.version)
        assertEquals(1000u, snowstar.data1)
        assertEquals(2u, snowstar.datacenterId)
        assertEquals(3u, snowstar.workerId)
        assertEquals(4u, snowstar.processId)
        assertEquals(0u, snowstar.increment)
    }

    @Test
    fun sameTimestampIncreasesIncrement() {
        val fakeClock = FakeClock(2000)
        val generator = SnowstarGenerator(
            clock = fakeClock,
            version = 1u,
            datacenterId = 10u,
            workerId = 20u,
            processId = 30u,
            defaultIncrement = 0u
        )
        val first = generator.generate()
        val second = generator.generate()
        assertEquals(first.data1, second.data1)
        assertEquals(first.increment + 1u, second.increment)
    }

    @Test
    fun clockMovingBackwardRaisesException() {
        val fakeClock = FakeClock(3000)
        val generator = SnowstarGenerator(
            clock = fakeClock,
            version = 1u,
            datacenterId = 10u,
            workerId = 20u,
            processId = 30u,
            defaultIncrement = 0u
        )
        generator.generate()
        fakeClock.currentMillis = 2900
        val exception = assertFailsWith<IllegalStateException> {
            generator.generate()
        }
        assertTrue(exception.message!!.contains("Clock moved backwards"))
    }

    @Test
    fun incrementOverflowRaisesException() {
        val fakeClock: Clock = FakeClock(4000)
        val generator = SnowstarGenerator(
            clock = fakeClock,
            version = 1u,
            datacenterId = 10u,
            workerId = 20u,
            processId = 30u,
            defaultIncrement = Snowstar.MAX_INCREMENT
        )
        val exception = assertFailsWith<IllegalStateException> {
            generator.generate()
        }
        assertTrue(exception.message!!.contains("Increment overflow"))
    }

    // Additional tests

    @Test
    fun timestampAdvancesResetsIncrement() {
        val fakeClock = FakeClock(5000)
        val generator = SnowstarGenerator(
            clock = fakeClock,
            version = 1u,
            datacenterId = 10u,
            workerId = 20u,
            processId = 30u,
            defaultIncrement = 0u
        )
        repeat(5) {
            generator.generate()
        }
        val last = generator.generate()
        assertEquals(5u, last.increment)

        fakeClock.currentMillis = 5001
        val second = generator.generate()

        assertEquals(5000u, last.data1)
        assertEquals(5001u, second.data1)
        assertEquals(0u, second.increment)
    }

    @Test
    fun boundaryValues() {
        val fakeClock = FakeClock(10000)
        val generator = SnowstarGenerator(
            clock = fakeClock,
            version = Snowstar.MAX_VERSION,
            datacenterId = Snowstar.MAX_DATACENTER_ID,
            workerId = Snowstar.MAX_WORKER_ID,
            processId = Snowstar.MAX_PROCESS_ID,
            defaultIncrement = Snowstar.MAX_INCREMENT - 1u
        )

        val snowstar = generator.generate()

        assertEquals(Snowstar.MAX_VERSION.toInt(), snowstar.version)
        assertEquals(10000u, snowstar.data1)
        assertEquals(Snowstar.MAX_DATACENTER_ID, snowstar.datacenterId)
        assertEquals(Snowstar.MAX_WORKER_ID, snowstar.workerId)
        assertEquals(Snowstar.MAX_PROCESS_ID, snowstar.processId)
        assertEquals(Snowstar.MAX_INCREMENT - 1u, snowstar.increment)
    }

    @Test
    fun serializedAndDeserializedPreservesAllValues() {
        val fakeClock = FakeClock(15000)
        val generator = SnowstarGenerator(
            clock = fakeClock,
            version = 5u,
            datacenterId = 123u,
            workerId = 456u,
            processId = 789u,
            defaultIncrement = 12345u
        )

        val original = generator.generate()
        // Convert to raw data
        val data1 = original.data1
        val data2 = original.data2

        // Create new instance from raw data
        val deserialized = Snowstar(data1, data2)

        assertEquals(original.version, deserialized.version)
        assertEquals(original.data1, deserialized.data1)
        assertEquals(original.datacenterId, deserialized.datacenterId)
        assertEquals(original.workerId, deserialized.workerId)
        assertEquals(original.processId, deserialized.processId)
        assertEquals(original.increment, deserialized.increment)
    }

    @Test
    fun uniquenessAcrossInstances() {
        val fakeClock = FakeClock(20000)

        val generator1 = SnowstarGenerator(
            clock = fakeClock,
            version = 1u,
            datacenterId = 1u,
            workerId = 1u,
            processId = 1u,
            defaultIncrement = 0u
        )

        val generator2 = SnowstarGenerator(
            clock = fakeClock,
            version = 1u,
            datacenterId = 1u,
            workerId = 2u,
            processId = 1u,
            defaultIncrement = 0u
        )

        val id1 = generator1.generate()
        val id2 = generator2.generate()

        assertNotEquals(id1, id2)
        assertEquals(id1.data1, id2.data1)
        assertEquals(id1.datacenterId, id2.datacenterId)
        assertNotEquals(id1.workerId, id2.workerId)
        assertEquals(id1.processId, id2.processId)
    }

    @Test
    fun stressTestSameTimestamp() {
        val fakeClock = FakeClock(30000)
        val generator = SnowstarGenerator(
            clock = fakeClock,
            version = 1u,
            datacenterId = 5u,
            workerId = 10u,
            processId = 15u,
            defaultIncrement = 0u
        )

        val ids = mutableSetOf<Snowstar>()
        val count = 1000 // Generate 1000 IDs with the same timestamp

        repeat(count) {
            val id = generator.generate()
            assertTrue(ids.add(id), "Generated ID should be unique")
            assertEquals(30000u, id.data1)
            assertEquals(it.toULong(), id.increment)
        }

        assertEquals(count, ids.size)
    }

    @Test
    fun stressTestIncrementingTimestamp() {
        val fakeClock = FakeClock(40000)
        val generator = SnowstarGenerator(
            clock = fakeClock,
            version = 2u,
            datacenterId = 5u,
            workerId = 10u,
            processId = 15u,
            defaultIncrement = 0u
        )

        val ids = mutableSetOf<Snowstar>()
        val count = 1000

        repeat(count) {
            fakeClock.currentMillis = 40000L + it
            val id = generator.generate()
            assertTrue(ids.add(id), "Generated ID should be unique")
            assertEquals((40000 + it).toULong(), id.data1)
            assertEquals(0u, id.increment)
        }

        assertEquals(count, ids.size)
    }

    @Test
    fun mixedTimestampAndIncrementStressTest() {
        val fakeClock = FakeClock(50000)
        val generator = SnowstarGenerator(
            clock = fakeClock,
            version = 3u,
            datacenterId = 7u,
            workerId = 8u,
            processId = 9u,
            defaultIncrement = 0u
        )

        val ids = mutableSetOf<Snowstar>()
        val count = 2000

        repeat(count) {
            // Every 10 iterations, advance the clock
            if (it % 10 == 0 && it > 0) {
                fakeClock.currentMillis += 1
            }

            val id = generator.generate()
            assertTrue(ids.add(id), "Generated ID should be unique")
        }

        assertEquals(count, ids.size)
    }


    @Test
    fun validationOfParameters() {
        val fakeClock = FakeClock(70000)

        // These should not throw exceptions
        SnowstarGenerator(
            clock = fakeClock,
            version = 0u,
            datacenterId = 0u,
            workerId = 0u,
            processId = 0u,
            defaultIncrement = 0u
        )

        SnowstarGenerator(
            clock = fakeClock,
            version = Snowstar.MAX_VERSION,
            datacenterId = Snowstar.MAX_DATACENTER_ID,
            workerId = Snowstar.MAX_WORKER_ID,
            processId = Snowstar.MAX_PROCESS_ID,
            defaultIncrement = Snowstar.MAX_INCREMENT - 1u
        )
    }

    @Test
    fun bitmaskOperationsCorrectness() {
        // Test specific bit patterns to ensure bit operations are working correctly
        val version: ULong = 0xFu // All 4 bits set
        val timestamp = 0x0123456789ABCDEFu // A pattern to test bit shifting
        val datacenterId: ULong = 0x3FFu // All 10 bits set
        val workerId: ULong = 0x2AAu // 10101010 pattern (10 bits)
        val processId: ULong = 0x155u // 01010101 pattern (10 bits)
        val increment: ULong = 0x3FFFFFFFu // All 30 bits set

        val snowstar = Snowstar(
            version = version,
            timestamp = timestamp,
            datacenterId = datacenterId,
            workerId = workerId,
            processId = processId,
            increment = increment
        )

        assertEquals(version.toInt(), snowstar.version)
        assertEquals(timestamp, snowstar.data1)
        assertEquals(datacenterId, snowstar.datacenterId)
        assertEquals(workerId, snowstar.workerId)
        assertEquals(processId, snowstar.processId)
        assertEquals(increment, snowstar.increment)
    }

    @Test
    fun parallelGenerationTest() = runTest {
        val fakeClock = FakeClock(90000)
        val generator = SnowstarGenerator(
            clock = fakeClock,
            version = 1u,
            datacenterId = 1u,
            workerId = 1u,
            processId = 1u,
            defaultIncrement = 0u
        )

        val ids = ConcurrentMutableSet<Snowstar>()
        val count = 1000
        val coroutines = 10

        coroutineScope {
            repeat(coroutines) { coroutineId ->
                launch {
                    repeat(count) {
                        val id = generator.generate()
                        assertTrue(ids.add(id), "ID collision detected in parallel generation")
                    }
                }
            }
        }

        assertEquals(coroutines * count, ids.size)
    }
}