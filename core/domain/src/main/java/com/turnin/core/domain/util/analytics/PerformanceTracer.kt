package com.turnin.core.domain.util.analytics

/**
 * 성능 측정 및 기록을 위한 인터페이스
 */
interface PerformanceTracer {
    /**
     * 성능 측정 시작
     * @param name trace 이름
     * @return [PerformanceTrace]
     */
    fun startTrace(name: String): PerformanceTrace
}

/**
 * 진행 중인 하나의 trace를 나타내는 핸들
 *
 * start-stop 사이의 계약을 명시적으로 드러낸다.
 */
interface PerformanceTrace {
    /**
     * 속성 추가
     */
    fun putAttribute(key: String, value: String)

    /**
     * 성능 측정 중단
     */
    fun stop()
}
