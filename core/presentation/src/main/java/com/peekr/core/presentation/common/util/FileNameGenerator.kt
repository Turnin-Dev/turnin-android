package com.peekr.core.presentation.common.util

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.UUID

/** 파일 이름 생성기 */
object FileNameGenerator {
    private const val DATE_TIME_FORMAT = "yyMMddHHmm"

    /**
     * 파일 이름을 생성한다.
     *
     * @param username 사용자 이름
     */
    fun generate(username: String): String {
        val now = LocalDateTime.now()
        val formatter = DateTimeFormatter.ofPattern(DATE_TIME_FORMAT)
        val date = now.format(formatter)
        val uuid = UUID.randomUUID().toString().take(8)
        return "$username-$date-$uuid"
    }
}
