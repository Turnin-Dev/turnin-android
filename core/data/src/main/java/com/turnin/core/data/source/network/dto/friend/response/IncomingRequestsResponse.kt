package com.turnin.core.data.source.network.dto.friend.response

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import com.turnin.core.data.paging.PagingDataHolder

/**
 * 나에게 들어온 친구 요청 목록 응답 바디
 *
 * @property pageNumber 페이지 번호
 * @property pageSize 페이지 크기
 * @property totalSize 모든 항목(요청자) 개수
 * @property hasNext 다음 페이지 존재 여부
 * @property list 친구 요청 목록
 */
@JsonClass(generateAdapter = true)
data class IncomingRequestsResponse(
    val pageNumber: Long,
    val pageSize: Int,
    val totalSize: Long,
    override val hasNext: Boolean,
    @Json(name = "requesters")
    override val list: List<IncomingRequestResponse>,
) : PagingDataHolder<IncomingRequestResponse>
