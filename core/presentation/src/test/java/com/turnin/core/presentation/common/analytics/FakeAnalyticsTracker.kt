package com.turnin.core.presentation.common.analytics

import com.turnin.core.domain.util.analytics.AnalyticsEvent
import com.turnin.core.domain.util.analytics.AnalyticsTracker

/**
 * 테스트 더블: 실제 AnalyticsTracker 구현 대신 이벤트를 리스트에 기록만 함.
 */
class FakeAnalyticsTracker : AnalyticsTracker {
    val events = mutableListOf<AnalyticsEvent>()

    override fun logEvent(event: AnalyticsEvent) {
        events += event
    }
}
