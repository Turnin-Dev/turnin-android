package com.turnin.core.data.source.network.api

import com.turnin.core.data.source.network.dto.block.request.BlockRequest
import com.turnin.core.data.source.network.dto.block.response.BlockReasonResponse
import com.turnin.core.data.source.network.dto.block.response.BlockedUserCursorPageResponse
import com.turnin.core.data.source.network.retrofit.Cacheable
import com.turnin.core.data.source.network.retrofit.HttpCacheDuration
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

/**
 * 차단 API
 */
interface BlockApi {
    /**
     * 차단 사용자 목록 조회 (페이지네이션)
     */
    @GET(NetworkApiPath.Block.ROUTE)
    suspend fun getBlockedUsers(
        @Query("cursor") cursor: Long?,
        @Query("size") size: Int,
    ): Response<BlockedUserCursorPageResponse>

    /**
     * 차단 사유 목록 조회
     */
    @Cacheable(maxAge = HttpCacheDuration.ONE_WEEK)
    @GET(NetworkApiPath.Block.REASON)
    suspend fun getBlockReasons(): Response<List<BlockReasonResponse>>

    /**
     * 차단 생성
     */
    @POST(NetworkApiPath.Block.ROUTE)
    suspend fun createBlock(
        @Body blockRequest: BlockRequest,
    ): Response<Unit>

    /**
     * 차단 삭제
     */
    @DELETE(NetworkApiPath.Block.ROUTE)
    suspend fun deleteBlock(
        @Query("blockId") blockId: Long,
    ): Response<Unit>
}
