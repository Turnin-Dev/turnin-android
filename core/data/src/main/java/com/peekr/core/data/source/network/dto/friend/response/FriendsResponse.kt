package com.peekr.core.data.source.network.dto.friend.response

import com.peekr.core.data.paging.PagingDataHolder
import com.peekr.core.domain.friend.model.Friends
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

/**
 * 친구 목록 응답 바디
 *
 * @property pageNumber 페이지 번호
 * @property pageSize 페이지 크기
 * @property totalSize 모든 항목(친구) 개수
 * @property hasNext 다음 페이지 존재 여부
 * @property list 친구 목록
 */
@JsonClass(generateAdapter = true)
data class FriendsResponse(
    val pageNumber: Long,
    val pageSize: Int,
    val totalSize: Long,
    override val hasNext: Boolean,
    @Json(name = "friends")
    override val list: List<FriendInfoResponse>,
) : PagingDataHolder<FriendInfoResponse>

fun FriendsResponse.toDomainModel(): Friends =
    Friends(
        pageNumber = pageNumber,
        pageSize = pageSize,
        totalSize = totalSize,
        hasNext = hasNext,
        friends = list.map { it.toDomainModel() },
    )
