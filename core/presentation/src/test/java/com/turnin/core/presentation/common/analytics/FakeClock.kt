package com.turnin.core.presentation.common.analytics

/**
 * 완전히 통제 가능한 가짜 시계.
 * Robolectric의 ShadowSystemClock에 의존하지 않으므로,
 * 버전/LooperMode와 무관하게 항상 동일하게 동작한다.
 */
class FakeClock(private var currentMs: Long = 0L) {
    fun now(): Long = currentMs

    fun advance(ms: Long) {
        currentMs += ms
    }
}
