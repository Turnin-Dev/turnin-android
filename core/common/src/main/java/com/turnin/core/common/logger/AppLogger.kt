package com.turnin.core.common.logger

import com.turnin.core.common.BuildConfig
import timber.log.Timber

/**
 * 앱 내 전역적으로 사용할 로거
 *
 * 반드시, [initLogger]를 호출 후 사용해야 한다.
 */
object AppLogger {
    const val DEFAULT_TAG = "TurninAppLogger"

    /**
     * 로거 초기화
     */
    fun initLogger() {
        if (Timber.forest().isNotEmpty()) return
        if (BuildConfig.DEBUG) {
            Timber.plant(DebugTree())
        } else {
            Timber.plant(ReleaseTree())
        }
        i(DEFAULT_TAG, "Turnin app logger initialization successful")
    }

    fun d(tag: String = DEFAULT_TAG, message: String) {
        Timber.tag(tag).d(message)
    }

    fun d(tag: String = DEFAULT_TAG, throwable: Throwable, message: String) {
        Timber.tag(tag).d(throwable, message)
    }

    fun i(tag: String = DEFAULT_TAG, message: String) {
        Timber.tag(tag).i(message)
    }

    fun w(tag: String = DEFAULT_TAG, message: String) {
        Timber.tag(tag).w(message)
    }

    fun w(tag: String = DEFAULT_TAG, throwable: Throwable, message: String) {
        Timber.tag(tag).w(throwable, message)
    }

    fun e(tag: String = DEFAULT_TAG, message: String) {
        Timber.tag(tag).e(message)
    }

    fun e(tag: String = DEFAULT_TAG, throwable: Throwable, message: String) {
        Timber.tag(tag).e(throwable, message)
    }

    /**
     * 실행 시간을 로깅한다. 기본적으로 디버그 로깅이다.
     *
     * @param tag 로그 태그
     * @param action 수행할 작업
     */
    inline fun <T> logTime(tag: String = DEFAULT_TAG, action: () -> T): T {
        val startTime = System.currentTimeMillis()
        return try {
            action()
        } finally {
            val elapsed = System.currentTimeMillis() - startTime
            d(tag, "Execution time: ${elapsed}ms")
        }
    }
}
