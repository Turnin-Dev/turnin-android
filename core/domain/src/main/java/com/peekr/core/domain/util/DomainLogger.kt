package com.peekr.core.domain.util

/**
 * 도메인 계층 전용 로거
 */
interface DomainLogger {
    fun d(tag: String, message: String)

    fun d(tag: String, throwable: Throwable, message: String)

    fun i(tag: String, message: String)

    fun w(tag: String, message: String)

    fun w(tag: String, throwable: Throwable, message: String)

    fun e(tag: String, message: String)

    fun e(tag: String, throwable: Throwable, message: String)
}
