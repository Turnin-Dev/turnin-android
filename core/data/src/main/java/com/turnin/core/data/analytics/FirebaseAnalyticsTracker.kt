package com.turnin.core.data.analytics

import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.logEvent
import com.turnin.core.domain.util.analytics.AnalyticsEvent
import com.turnin.core.domain.util.analytics.AnalyticsTracker
import javax.inject.Inject

/**
 * Firebase Analytics를 이용한 Analytics Tracker
 */
class FirebaseAnalyticsTracker @Inject constructor(
    private val firebaseAnalytics: FirebaseAnalytics,
) : AnalyticsTracker {
    override fun logEvent(event: AnalyticsEvent) {
        when (event) {
            is AnalyticsEvent.SignUp -> {
                firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SIGN_UP) {
                    param(FirebaseAnalytics.Param.METHOD, event.method)
                }
            }

            is AnalyticsEvent.Login -> {
                firebaseAnalytics.logEvent(FirebaseAnalytics.Event.LOGIN) {
                    param(FirebaseAnalytics.Param.METHOD, event.method)
                }
            }

            is AnalyticsEvent.ScreenView -> {
                firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SCREEN_VIEW) {
                    param(FirebaseAnalytics.Param.SCREEN_NAME, event.screenName)
                }
            }

            is AnalyticsEvent.ScreenDwellTime -> {
                firebaseAnalytics.logEvent(EVENT_SCREEN_DWELL_TIME) {
                    param(FirebaseAnalytics.Param.SCREEN_NAME, event.screenName)
                    param(PARAM_DWELL_TIME, event.dwellTimeMs)
                }
            }
        }
    }

    companion object {
        private const val EVENT_SCREEN_DWELL_TIME = "screen_dwell_time"
        private const val PARAM_DWELL_TIME = "duration_ms"
    }
}
