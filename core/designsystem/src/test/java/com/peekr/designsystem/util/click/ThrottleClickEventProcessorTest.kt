package com.peekr.designsystem.util.click

import com.peekr.core.designsystem.util.click.ThrottleClickEventProcessor
import org.junit.Assert.assertEquals
import org.junit.Test

class ThrottleClickEventProcessorTest {
    @Test
    fun `processEvent should execute immediately on first click(즉시 실행 테스트)`() {
        // Given
        val processor = ThrottleClickEventProcessor(300L)
        var counter = 0

        // When
        processor.processEvent { counter++ }

        // Then
        assertEquals(1, counter) // 즉시 실행되어야 함
    }

    @Test
    fun `processEvent should respect throttle time(쓰로틀 기능 테스트)`() {
        // Given
        val processor = ThrottleClickEventProcessor(300L)
        var counterA = 0
        var counterB = 0

        // When - 첫 번째 클릭 (항상 실행됨)
        processor.processEvent { counterA++ }

        // 바로 두 번째 클릭 (무시되어야 함)
        processor.processEvent { counterB++ }

        // Then
        assertEquals(1, counterA) // 첫 번째 클릭은 실행됨
        assertEquals(0, counterB) // 두 번째 클릭은 무시됨 (throttle 시간 내)
    }

    @Test
    fun `processEvent should handle multiple events correctly(여러 이벤트 처리 테스트)`() {
        // Given
        val processor = ThrottleClickEventProcessor(100L) // 테스트용으로 짧은 시간 사용
        var clickCount = 0

        // When
        processor.processEvent { clickCount++ } // 첫 번째 클릭 (실행됨)
        assertEquals(1, clickCount)

        // 쓰로틀 시간을 강제로 지나게 하기 위해 약간의 시간을 기다림
        try {
            Thread.sleep(150) // 100ms보다 충분히 긴 시간 대기
        } catch (e: InterruptedException) {
            // 예외 처리
        }

        // 쓰로틀 시간이 지난 후 클릭 (실행되어야 함)
        processor.processEvent { clickCount++ }

        // Then
        assertEquals(2, clickCount) // 두 번의 클릭이 모두 처리되어야 함
    }

    @Test
    fun `processEvent should work with custom throttle time(커스텀 쓰로틀 시간 테스트)`() {
        // Given
        val customThrottleTime = 50L // 매우 짧은 쓰로틀 시간
        val processor = ThrottleClickEventProcessor(customThrottleTime)
        var clickCount = 0

        // When
        processor.processEvent { clickCount++ } // 첫 번째 클릭

        // 커스텀 쓰로틀 시간을 강제로 지나게 함
        try {
            Thread.sleep(60) // 50ms보다 약간 긴 시간 대기
        } catch (e: InterruptedException) {
            // 예외 처리
        }

        // 충분한 시간이 지난 후 클릭
        processor.processEvent { clickCount++ }

        // Then
        assertEquals(2, clickCount) // 커스텀 쓰로틀 시간 이후 클릭은 실행되어야 함
    }
}
