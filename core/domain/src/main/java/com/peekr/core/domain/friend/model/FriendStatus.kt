package com.peekr.core.domain.friend.model

/**
 * 친구 상태
 */
enum class FriendStatus {
    /** 아무 관계도 아닌 상태 */
    NOTHING,

    /** 친구 관계인 상태 */
    FRIENDS,

    /** 친구 요청 상태 */
    REQUESTED,

    /** 친구 요청을 받은 상태 */
    RECEIVED,
    ;

    fun toggle(): FriendStatus =
        when (this) {
            NOTHING -> REQUESTED
            FRIENDS -> NOTHING
            REQUESTED -> NOTHING
            RECEIVED -> FRIENDS
        }
}
