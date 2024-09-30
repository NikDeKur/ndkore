package dev.nikdekur.ndkore.test

import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertTrue
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.measureTime

class CoroutinesTest {

    @Test
    fun testRealDelay1() = runTest(timeout = 500.milliseconds) {
        val time = measureTime {
            realDelay(350)
        }

        assertTrue(time >= 350.milliseconds)
    }


    @Test
    fun testRealDelay2() = runTest(timeout = 500.milliseconds) {
        val time = measureTime {
            realDelay(150)
        }

        assertTrue(time >= 150.milliseconds)
    }
}