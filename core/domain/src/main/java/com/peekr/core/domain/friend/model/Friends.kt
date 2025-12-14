package com.peekr.core.domain.friend.model

/**
 * 친구 목록 모델
 *
 * @property pageNumber 페이지 번호
 * @property pageSize 페이지 크기
 * @property totalSize 모든 항목(친구) 개수
 * @property hasNext 다음 페이지 존재 여부
 * @property friends 친구 목록
 */
data class Friends(
    val pageNumber: Long,
    val pageSize: Int,
    val totalSize: Long,
    val hasNext: Boolean,
    val friends: List<FriendInfo>,
)
