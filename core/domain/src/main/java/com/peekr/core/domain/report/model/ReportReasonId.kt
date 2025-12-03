package com.peekr.core.domain.report.model

@JvmInline
value class ReportReasonId private constructor(val value: Long) {
    /** 신고 사유 ID */
    companion object {
        operator fun invoke(value: Long): ReportReasonId = ReportReasonId(value)
    }

    init {
        validate()
    }

    fun validate() {
    }
}
