package com.peekr.designsystem.util.click

import com.peekr.core.designsystem.util.click.DebounceClickEventProcessor
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class DebounceClickEventProcessorTest {
    @Test
    fun `processEvent should execute the event after debounce time(기본 기능 테스트)`() = runTest {
        // Given
        val testScope = TestScope(testScheduler)
        val processor = DebounceClickEventProcessor(testScope, 200L)
        var counter = 0

        // When
        processor.processEvent { counter++ }

        // Then
        assertEquals(0, counter) // Not executed immediately
        advanceTimeBy(100L)
        assertEquals(0, counter) // Still not executed
        advanceTimeBy(101L) // Total 201ms > 200ms debounce time
        assertEquals(1, counter) // Executed after debounce time
    }

    @Test
    fun `processEvent should cancel previous event and only execute last one(다중 클릭 테스트)`() =
        runTest {
            // Given
            val testScope = TestScope(testScheduler)
            val processor = DebounceClickEventProcessor(testScope, 200L)
            var counter = 0
            var lastValue = ""

            // When - simulate multiple rapid clicks
            processor.processEvent {
                counter++
                lastValue = "First"
            }
            advanceTimeBy(100L) // 100ms passed

            processor.processEvent {
                counter++
                lastValue = "Second"
            }
            advanceTimeBy(100L) // 200ms since second click (first one should be canceled)

            // Then
            assertEquals(0, counter) // Nothing executed yet
            advanceTimeBy(101L) // Now enough time has passed for second click
            assertEquals(1, counter) // Only the second event should be executed
            assertEquals("Second", lastValue) // Check that second event was executed
        }

    @Test
    fun `processEvent should work with custom debounce time(커스텀 디바운스 시간 테스트)`() = runTest {
        // Given
        val testScope = TestScope(testScheduler)
        val customDebounceTime = 500L
        val processor = DebounceClickEventProcessor(testScope, customDebounceTime)
        var counter = 0

        // When
        processor.processEvent { counter++ }

        // Then
        assertEquals(0, counter)
        advanceTimeBy(499L)
        assertEquals(0, counter) // Still not executed
        advanceTimeBy(2L) // Total 501ms > 500ms debounce time
        assertEquals(1, counter) // Now executed
    }
}
