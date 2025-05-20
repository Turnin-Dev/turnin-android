package com.peekr.designsystem.util.click

/**
 * 클릭 후 특정 시간 동안 다음 클릭을 방지하는 Throttle 클릭 이벤트 프로세서
 *
 * Throttle 은 사용자가 버튼을 빠르게 여러 번 눌러도 지정된 시간 동안 한 번만 동작 하도록 제한합니다.
 */
class ThrottleClickEventProcessor(
    private val throttleTimeMs: Long = THROTTLE_TIME_MS,
) : ClickEventProcessor {
    private val now: Long
        get() = System.currentTimeMillis()

    private var lastEventTimeMs: Long = 0

    override fun processEvent(event: () -> Unit) {
        if (now - lastEventTimeMs >= throttleTimeMs) {
            event.invoke()
        }
        lastEventTimeMs = now
    }

    companion object {
        const val THROTTLE_TIME_MS = 300L
    }
}

fun ClickEventProcessor.Companion.getThrottle(throttleTimeMs: Long): ClickEventProcessor =
    ThrottleClickEventProcessor(throttleTimeMs)
