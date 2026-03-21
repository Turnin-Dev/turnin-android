package com.peekr.core.common.fcm

/** FCM data 필드 키 상수 (백엔드 스펙이랑 통일) */
object FcmDataKey {
    /** 알림 제목 */
    const val TITLE = "title"

    /** 알림 본문 */
    const val BODY = "body"

    /** 알림 유형 (ex. FRIEND_REQUEST, NEW_KEYWORD 등) */
    const val NOTI_TYPE = "noti_type"

    /** 참조 리소스 타입 (ex. USER, POST 등) */
    const val REF_TYPE = "ref_type"

    /** 참조 리소스 ID */
    const val REF_ID = "ref_id"
}
