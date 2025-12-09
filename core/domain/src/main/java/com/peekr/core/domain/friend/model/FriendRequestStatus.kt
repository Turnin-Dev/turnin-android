package com.peekr.core.domain.friend.model

/**
 * 친구 상태
 *
 * 친구 상태 클래스는 내부적으로만 사용하고 사용자에게 표시되지 않는다.
 */
enum class FriendRequestStatus {
    /** 친구 요청이 생성된 상태(승인 대기) */
    PENDING,

    /** 친구 요청이 수락된 상태 */
    ACCEPTED,

    /** 친구 요청이 거절(또는 취소)된 상태 */
    REJECTED,
}
