package com.turnin.core.presentation.common.analytics

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.withFrameNanos
import com.turnin.core.common.logger.AppLogger
import com.turnin.core.domain.util.analytics.TraceAttribute
import com.turnin.core.domain.util.analytics.TraceName

/**
 * 화면 렌더링 성능 측정
 *
 * 순수하게 "화면이 떠 있던 기술적 시간"만 측정
 */
@Composable
internal fun ScreenRenderTracer(screenName: String) {
    val tracer = LocalPerformanceTracer.current

    LaunchedEffect(screenName) {
        val trace = tracer.startTrace(TraceName.SCREEN_RENDER)
        val startedAt = System.currentTimeMillis()

        trace.putAttribute(TraceAttribute.SCREEN_NAME, screenName.take(100))

        AppLogger.d(TAG, "[Render] START name=$screenName")

        try {
            withFrameNanos { }
            val elapsed = System.currentTimeMillis() - startedAt
            AppLogger.d(TAG, "[Render] FIRST FRAME drawn name=$screenName elapsed=${elapsed}ms")
        } finally {
            trace.stop()
            AppLogger.d(TAG, "[Render] STOP name=$screenName (finally 실행됨)")
        }
    }
}

private const val TAG = "ScreenTracking"
