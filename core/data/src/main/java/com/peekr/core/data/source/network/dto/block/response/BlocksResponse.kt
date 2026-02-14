package com.peekr.core.data.source.network.dto.block.response

import com.peekr.core.data.paging.PagingDataHolder
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

/**
 * 차단 목록 응답 바디
 *
 * @property pageNumber 페이지 번호
 * @property pageSize 페이지 크기
 * @property totalSize 모든 항목(친구) 개수
 * @property hasNext 다음 페이지 존재 여부
 * @property list 차단 목록
 */
@JsonClass(generateAdapter = true)
data class BlocksResponse(
    val pageNumber: Long,
    val pageSize: Int,
    val totalSize: Long,
    override val hasNext: Boolean,
    @Json(name = "blocks")
    override val list: List<BlockResponse>,
) : PagingDataHolder<BlockResponse>
