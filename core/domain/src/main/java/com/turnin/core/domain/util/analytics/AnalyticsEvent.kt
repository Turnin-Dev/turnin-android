package com.turnin.core.domain.util.analytics

/**
 * 애널리틱스 이벤트 목록
 */
sealed interface AnalyticsEvent {
    /**
     * 회원가입 성공 기록 추적
     * @param method 가입 수단 (ex. google, kakao)
     */
    data class SignUp(val method: String) : AnalyticsEvent

    /**
     * 로그인 성공 기록 추적
     * @param method 로그인 수단(ex. google, kakao)
     */
    data class Login(val method: String) : AnalyticsEvent

    /**
     * 화면 방문 (조회 수) 기록 추적
     * @param screenName 화면 이름
     */
    data class ScreenView(val screenName: String) : AnalyticsEvent

    /**
     * 화면 체류시간(dwell time) 추적
     * @param screenName 화면 이름
     * @param dwellTimeMs 체류 시간(ms)
     */
    data class ScreenDwellTime(
        val screenName: String,
        val dwellTimeMs: Long,
    ) : AnalyticsEvent
}
