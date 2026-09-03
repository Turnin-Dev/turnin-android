package com.turnin.core.presentation.common.analytics

import androidx.compose.runtime.staticCompositionLocalOf
import com.turnin.core.domain.util.analytics.AnalyticsTracker

val LocalAnalyticsTracker = staticCompositionLocalOf<AnalyticsTracker> {
    error("AnalyticsTracker가 제공되지 않았습니다. (CompositionLocalProvider 확인 필요)")
}
