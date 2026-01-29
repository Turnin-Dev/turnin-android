package com.peekr.core.common.util

import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId

// fun Long.toRelativeTime(): String {
//    // 1. 서버에서 받은 시간(밀리초)을 현재 시스템 시간대의 LocalDateTime으로 변환
//    val serverDateTime = LocalDateTime.ofInstant(
//        Instant.ofEpochMilli(this),
//        ZoneId.systemDefault(),
//    )
//    val now = LocalDateTime.now(ZoneId.systemDefault())
//
//    // 2. 시간 차이 계산
//    val seconds = ChronoUnit.SECONDS.between(serverDateTime, now)
//    val minutes = ChronoUnit.MINUTES.between(serverDateTime, now)
//    val hours = ChronoUnit.HOURS.between(serverDateTime, now)
//    val days = ChronoUnit.DAYS.between(serverDateTime, now)
//
//    // 3. 조건별 문구 반환
//    return when {
//        seconds < 60 -> "방금 전"
//        minutes < 60 -> "${minutes}분 전"
//        hours < 24 -> "${hours}시간 전"
//        days < 7 -> "${days}일 전"
//        else -> {
//            // 일주일이 지나면 날짜를 표시 (예: 2026.01.01)
//            val year = serverDateTime.year
//            val month = serverDateTime.monthValue.toString().padStart(2, '0')
//            val day = serverDateTime.dayOfMonth.toString().padStart(2, '0')
//
//            "$year.$month.$day"
//        }
//    }
// }

/**
 * [Long] 타입의 시간을 상대 시간의 [String] 타입으로 변환한다.
 *
 * @param isMillis 밀리초 여부
 */
fun Long.toRelativeTime(isMillis: Boolean): String {
    val receivedTime = if (isMillis) this else this * 1000
    val now = System.currentTimeMillis()
    val diffMillis = now - receivedTime

    val seconds = diffMillis / 1000
    val minutes = seconds / 60
    val hours = minutes / 60
    val days = hours / 24

    return when {
        seconds < 60 -> "방금 전"
        minutes < 60 -> "${minutes}분 전"
        hours < 24 -> "${hours}시간 전"
        days < 7 -> "${days}일 전"
        else -> {
            // 일주일이 지나면 날짜를 표시 (사용자의 로컬 시간대로 변환)
            val localDateTime = LocalDateTime.ofInstant(
                Instant.ofEpochMilli(receivedTime),
                ZoneId.systemDefault(),
            )
            val year = localDateTime.year
            val month = localDateTime.monthValue.toString().padStart(2, '0')
            val day = localDateTime.dayOfMonth.toString().padStart(2, '0')

            "$year.$month.$day"
        }
    }
}
