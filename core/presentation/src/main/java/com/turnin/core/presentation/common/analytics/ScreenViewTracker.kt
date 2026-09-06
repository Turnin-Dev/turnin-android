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
