package com.turnin.core.domain.util.analytics

/**
 * 애널리틱스 이벤트 기록을 위한 인터페이스
 */
interface AnalyticsTracker {
    fun logEvent(event: AnalyticsEvent)
}
