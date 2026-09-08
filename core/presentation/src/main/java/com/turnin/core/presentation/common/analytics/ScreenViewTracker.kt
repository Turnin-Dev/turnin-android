package com.turnin.core.presentation.common.analytics

import androidx.activity.compose.LocalActivity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import com.turnin.core.common.logger.AppLogger
import com.turnin.core.domain.util.analytics.AnalyticsEvent

/**
 * 화면 방문 (조회 수) 기록 추적
 */
@Composable
internal fun ScreenViewTracker(screenName: String) {
    val analyticsTracker = LocalAnalyticsTracker.current
    val activity = LocalActivity.current
    var isRotated by rememberSaveable(screenName) { mutableStateOf(false) }

    LaunchedEffect(screenName) {
        if (!isRotated) {
            AppLogger.d(TAG, "[ScreenView] SCREEN_VIEW fired -> screenName=$screenName")
            analyticsTracker.logEvent(AnalyticsEvent.ScreenView(screenName))
        }
        // 회전으로 인한 일회성 스킵 신호. 소비 후 반드시 리셋해야
        // 재방문 시에도 정상 이벤트 발행됨.
        // 이 리셋 구문이 없다면 화면 회전 후 영원한 기록 누락 버그가 발생함.
        // (isRotated는 Bundle이 아닌 NavBackStackEntry 범위 내에 저장됨)
        // 상세: docs/notes/remember-saveable-lifecycle.md
        isRotated = false
    }

    DisposableEffect(screenName) {
        onDispose {
            if (activity?.isChangingConfigurations == true) {
                isRotated = true
            }
        }
    }
}

private const val TAG = "ScreenTracking"
