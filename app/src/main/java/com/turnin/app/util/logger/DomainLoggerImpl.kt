package com.turnin.app.util.logger

import com.turnin.core.common.logger.AppLogger
import com.turnin.core.domain.util.DomainLogger
import javax.inject.Inject

class DomainLoggerImpl @Inject constructor() : DomainLogger {
    override fun d(tag: String, message: String) {
        AppLogger.d(tag, message)
    }

    override fun d(tag: String, throwable: Throwable, message: String) {
        AppLogger.d(tag, throwable, message)
    }

    override fun i(tag: String, message: String) {
        AppLogger.i(tag, message)
    }

    override fun w(tag: String, message: String) {
        AppLogger.w(tag, message)
    }

    override fun w(tag: String, throwable: Throwable, message: String) {
        AppLogger.w(tag, throwable, message)
    }

    override fun e(tag: String, message: String) {
        AppLogger.e(tag, message)
    }

    override fun e(tag: String, throwable: Throwable, message: String) {
        AppLogger.e(tag, throwable, message)
    }
}
