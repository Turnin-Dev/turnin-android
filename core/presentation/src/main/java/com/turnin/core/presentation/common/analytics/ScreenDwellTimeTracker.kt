package com.turnin.core.presentation.common.analytics

import androidx.activity.compose.LocalActivity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.turnin.core.common.logger.AppLogger
import com.turnin.core.domain.util.analytics.AnalyticsEvent

/**
 * 화면 체류시간(dwell time) 추적
 *
 * - 백그라운드로 나가있던 시간은 제외하고, 실제로 화면을 보고 있던 시간만 누적
 * - 여러 번 나눠서 전송하지 않고, 화면을 완전히 떠날 때(onDispose) 딱 1회만 전송
 */
@Composable
internal fun ScreenDwellTimeTracker(screenName: String) {
    val lifecycleOwner = LocalLifecycleOwner.current
    val activity = LocalActivity.current
    val analyticsTracker = LocalAnalyticsTracker.current

    // 지금까지 누적된 '포그라운드로 보고 있던' 시간
    val accumulatedMs = rememberSaveable { mutableLongStateOf(0L) }
    // 현재 포그라운드 구간이 시작된 시각
    val segmentStartMs = rememberSaveable { mutableLongStateOf(0L) }

    DisposableEffect(screenName, lifecycleOwner) {
        // 화면 진입 시점 = 첫 포그라운드 구간 시작
        segmentStartMs.longValue = System.currentTimeMillis()

        AppLogger.d(
            TAG,
            "[Dwell] ENTER " +
                "screenName=$screenName " +
                "accumulated=${accumulatedMs.longValue}ms " +
                "(rememberSaveable 복원값 확인용)",
        )

        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_STOP -> {
                    // 백그라운드로 나간 경우, 이번 구간 시간만큼 누적하고 중단
                    accumulatedMs.longValue += System.currentTimeMillis() - segmentStartMs.longValue
                    AppLogger.d(
                        TAG,
                        "[Dwell] ON_STOP  " +
                            "screenName=$screenName " +
                            "accumulated=${accumulatedMs.longValue}ms",
                    )
                }

                Lifecycle.Event.ON_START -> {
                    // 포그라운드로 복귀하는 경우, 새 구간 시작 시각만 갱신 (전송 X)
                    segmentStartMs.longValue = System.currentTimeMillis()
                    AppLogger.d(
                        TAG,
                        "[Dwell] ON_START " +
                            "screenName=$screenName " +
                            "(segment 재시작, 아직 전송 X)",
                    )
                }

                else -> {}
            }
        }

        lifecycleOwner.lifecycle.addObserver(observer)

        onDispose {
            // 회전(설정 변경)으로 인한 dispose인지 확인
            // -> true면 진짜 이탈이 아니므로 전송하지 않고 값만 다음 컴포지션으로 넘김
            // (rememberSaveable이 복원해줌)
            val isRotating = activity?.isChangingConfigurations == true

            // 화면을 완전히 떠나는 시점 = 마지막 구간 누적 + 최종 전송 (1회만)
            accumulatedMs.longValue += System.currentTimeMillis() - segmentStartMs.longValue

            if (isRotating) {
                AppLogger.d(
                    TAG,
                    "[Dwell] DISPOSE(회전) " +
                        "screenName=$screenName " +
                        "accumulated=${accumulatedMs.longValue}ms -> 전송 SKIP, 값 유지",
                )
            } else {
                AppLogger.d(
                    TAG,
                    "[Dwell] DISPOSE(진짜 이탈) " +
                        "screenName=$screenName " +
                        "final=${accumulatedMs.longValue}ms -> 이벤트 전송",
                )

                analyticsTracker.logEvent(
                    AnalyticsEvent.ScreenDwellTime(screenName, accumulatedMs.longValue),
                )

                // 다음 방문을 위해 리셋
                accumulatedMs.longValue = 0L
                segmentStartMs.longValue = 0L
            }

            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }
}

private const val TAG = "ScreenTracking"
