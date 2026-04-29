package com.turnin.core.domain.model

/** 알림 유형 */
enum class NotificationType {
    /** 친구 요청 */
    FRIEND_REQUEST,

    /** 친구 수락 */
    FRIEND_ACCEPT,

    /** 친구의 새 키워드 */
    NEW_KEYWORD,

    /** 공지사항 */
    NOTICE,

    /** 이벤트 */
    EVENT,

    ;

    /** 브로드캐스트 알림 여부 */
    val isBroadcast: Boolean
        get() = this == NOTICE || this == EVENT

    /** 친구 관련 알림 여부 (친구 요청/수락) */
    val isFriendRelated: Boolean
        get() = this == FRIEND_REQUEST || this == FRIEND_ACCEPT
}
