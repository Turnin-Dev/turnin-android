package com.turnin.core.domain.file

import kotlin.time.Clock
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.format
import kotlinx.datetime.toLocalDateTime

/** 파일 이름 생성기 */
object FileNameGenerator {
    /** yyMMddHHmm */
    private val dateTimeFormat = LocalDateTime.Format {
        yearTwoDigits(baseYear = 2000)
        monthNumber()
        day()
        hour()
        minute()
    }

    /**
     * 파일 이름을 생성한다.
     *
     * @param username 사용자 이름
     */
    @OptIn(ExperimentalUuidApi::class)
    fun generate(username: String): String {
        val now = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
        val date = now.format(dateTimeFormat)
        val uuid = Uuid.random().toString().take(8)
        return "$username-$date-$uuid"
    }
}
