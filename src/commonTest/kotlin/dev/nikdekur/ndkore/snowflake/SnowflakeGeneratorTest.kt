package dev.nikdekur.ndkore.snowflake

import co.touchlab.stately.collections.ConcurrentMutableSet
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlin.test.*


class FakeClock(var currentMillis: Long) : Clock {
    override fun now(): Instant = Instant.fromEpochMilliseconds(currentMillis)
}

class SnowflakeGeneratorTest {

    @Test
    fun happyPathGeneratesValidSnowflake() {
        val fakeClock: Clock = FakeClock(1000)
        val generator = SnowflakeGenerator(
            clock = fakeClock,
            version = 1u,
            datacenterId = 2u,
            workerId = 3u,
            processId = 4u,
            defaultIncrement = 0u
        )
        val snowflake = generator.generate()
        assertEquals(1, snowflake.version)
        assertEquals(1000u, snowflake.timestampMs)
        assertEquals(2u, snowflake.datacenterId)
        assertEquals(3u, snowflake.workerId)
        assertEquals(4u, snowflake.processId)
        assertEquals(0u, snowflake.increment)
    }

    @Test
    fun sameTimestampIncreasesIncrement() {
        val fakeClock = FakeClock(2000)
        val generator = SnowflakeGenerator(
            clock = fakeClock,
            version = 1u,
            datacenterId = 10u,
            workerId = 20u,
            processId = 30u,
            defaultIncrement = 0u
        )
        val first = generator.generate()
        val second = generator.generate()
        assertEquals(first.timestampMs, second.timestampMs)
        assertEquals(first.increment + 1u, second.increment)
    }

    @Test
    fun clockMovingBackwardRaisesException() {
        val fakeClock = FakeClock(3000)
        val generator = SnowflakeGenerator(
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
        val generator = SnowflakeGenerator(
            clock = fakeClock,
            version = 1u,
            datacenterId = 10u,
            workerId = 20u,
            processId = 30u,
            defaultIncrement = SnowflakeV2.MAX_INCREMENT
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
        val generator = SnowflakeGenerator(
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

        assertEquals(5000u, last.timestampMs)
        assertEquals(5001u, second.timestampMs)
        assertEquals(0u, second.increment)
    }

    @Test
    fun boundaryValues() {
        val fakeClock = FakeClock(10000)
        val generator = SnowflakeGenerator(
            clock = fakeClock,
            version = SnowflakeV2.MAX_VERSION,
            datacenterId = SnowflakeV2.MAX_DATACENTER_ID,
            workerId = SnowflakeV2.MAX_WORKER_ID,
            processId = SnowflakeV2.MAX_PROCESS_ID,
            defaultIncrement = SnowflakeV2.MAX_INCREMENT - 1u
        )

        val snowflake = generator.generate()

        assertEquals(SnowflakeV2.MAX_VERSION.toInt(), snowflake.version)
        assertEquals(10000u, snowflake.timestampMs)
        assertEquals(SnowflakeV2.MAX_DATACENTER_ID, snowflake.datacenterId)
        assertEquals(SnowflakeV2.MAX_WORKER_ID, snowflake.workerId)
        assertEquals(SnowflakeV2.MAX_PROCESS_ID, snowflake.processId)
        assertEquals(SnowflakeV2.MAX_INCREMENT - 1u, snowflake.increment)
    }

    @Test
    fun serializedAndDeserializedPreservesAllValues() {
        val fakeClock = FakeClock(15000)
        val generator = SnowflakeGenerator(
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
        val deserialized = SnowflakeV2(data1, data2)

        assertEquals(original.version, deserialized.version)
        assertEquals(original.timestampMs, deserialized.timestampMs)
        assertEquals(original.datacenterId, deserialized.datacenterId)
        assertEquals(original.workerId, deserialized.workerId)
        assertEquals(original.processId, deserialized.processId)
        assertEquals(original.increment, deserialized.increment)
    }

    @Test
    fun uniquenessAcrossInstances() {
        val fakeClock = FakeClock(20000)

        val generator1 = SnowflakeGenerator(
            clock = fakeClock,
            version = 1u,
            datacenterId = 1u,
            workerId = 1u,
            processId = 1u,
            defaultIncrement = 0u
        )

        val generator2 = SnowflakeGenerator(
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
        assertEquals(id1.timestampMs, id2.timestampMs)
        assertEquals(id1.datacenterId, id2.datacenterId)
        assertNotEquals(id1.workerId, id2.workerId)
        assertEquals(id1.processId, id2.processId)
    }

    @Test
    fun stressTestSameTimestamp() {
        val fakeClock = FakeClock(30000)
        val generator = SnowflakeGenerator(
            clock = fakeClock,
            version = 1u,
            datacenterId = 5u,
            workerId = 10u,
            processId = 15u,
            defaultIncrement = 0u
        )

        val ids = mutableSetOf<SnowflakeV2>()
        val count = 1000 // Generate 1000 IDs with the same timestamp

        repeat(count) {
            val id = generator.generate()
            assertTrue(ids.add(id), "Generated ID should be unique")
            assertEquals(30000u, id.timestampMs)
            assertEquals(it.toULong(), id.increment)
        }

        assertEquals(count, ids.size)
    }

    @Test
    fun stressTestIncrementingTimestamp() {
        val fakeClock = FakeClock(40000)
        val generator = SnowflakeGenerator(
            clock = fakeClock,
            version = 2u,
            datacenterId = 5u,
            workerId = 10u,
            processId = 15u,
            defaultIncrement = 0u
        )

        val ids = mutableSetOf<SnowflakeV2>()
        val count = 1000

        repeat(count) {
            fakeClock.currentMillis = 40000L + it
            val id = generator.generate()
            assertTrue(ids.add(id), "Generated ID should be unique")
            assertEquals((40000 + it).toULong(), id.timestampMs)
            assertEquals(0u, id.increment)
        }

        assertEquals(count, ids.size)
    }

    @Test
    fun mixedTimestampAndIncrementStressTest() {
        val fakeClock = FakeClock(50000)
        val generator = SnowflakeGenerator(
            clock = fakeClock,
            version = 3u,
            datacenterId = 7u,
            workerId = 8u,
            processId = 9u,
            defaultIncrement = 0u
        )

        val ids = mutableSetOf<SnowflakeV2>()
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
        SnowflakeGenerator(
            clock = fakeClock,
            version = 0u,
            datacenterId = 0u,
            workerId = 0u,
            processId = 0u,
            defaultIncrement = 0u
        )

        SnowflakeGenerator(
            clock = fakeClock,
            version = SnowflakeV2.MAX_VERSION,
            datacenterId = SnowflakeV2.MAX_DATACENTER_ID,
            workerId = SnowflakeV2.MAX_WORKER_ID,
            processId = SnowflakeV2.MAX_PROCESS_ID,
            defaultIncrement = SnowflakeV2.MAX_INCREMENT - 1u
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

        val snowflake = SnowflakeV2(
            version = version,
            timestamp = timestamp,
            datacenterId = datacenterId,
            workerId = workerId,
            processId = processId,
            increment = increment
        )

        assertEquals(version.toInt(), snowflake.version)
        assertEquals(timestamp, snowflake.timestampMs)
        assertEquals(datacenterId, snowflake.datacenterId)
        assertEquals(workerId, snowflake.workerId)
        assertEquals(processId, snowflake.processId)
        assertEquals(increment, snowflake.increment)
    }

    @Test
    fun parallelGenerationTest() = runTest {
        val fakeClock = FakeClock(90000)
        val generator = SnowflakeGenerator(
            clock = fakeClock,
            version = 1u,
            datacenterId = 1u,
            workerId = 1u,
            processId = 1u,
            defaultIncrement = 0u
        )

        val ids = ConcurrentMutableSet<SnowflakeV2>()
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