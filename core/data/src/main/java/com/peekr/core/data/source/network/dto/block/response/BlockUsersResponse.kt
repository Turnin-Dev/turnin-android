package com.peekr.core.data.source.network.dto.block.response

import com.peekr.core.data.paging.PagingDataHolder
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

/**
 * 차단 목록 응답 바디
 *
 * @property pageNumber 페이지 번호
 * @property pageSize 페이지 크기
 * @property hasNext 다음 페이지 존재 여부
 * @property list 차단 목록
 */
@JsonClass(generateAdapter = true)
data class BlockUsersResponse(
    val pageNumber: Long,
    val pageSize: Int,
    override val hasNext: Boolean,
    @Json(name = "blockUsers")
    override val list: List<BlockUserResponse>,
) : PagingDataHolder<BlockUserResponse>
