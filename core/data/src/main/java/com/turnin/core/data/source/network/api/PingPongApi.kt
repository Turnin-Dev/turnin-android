package com.turnin.core.data.source.network.api

import com.turnin.core.data.source.network.dto.pingPong.request.CreatePingPongAnswerRequest
import com.turnin.core.data.source.network.dto.pingPong.request.CreatePingPongRequest
import com.turnin.core.data.source.network.dto.pingPong.response.PingPongAnswerResponse
import com.turnin.core.data.source.network.dto.pingPong.response.PingPongCursorPageResponse
import com.turnin.core.data.source.network.dto.pingPong.response.PingPongResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface PingPongApi {
    /**
     * 핑퐁 질문/답변 목록 조회
     */
    @GET(NetworkApiPath.UserKeyword.PingPong.ROUTE)
    suspend fun getPingPongs(
        @Path("userKeywordId") userKeywordId: Long,
        @Query("cursor") cursor: Long?,
        @Query("size") size: Int,
    ): Response<PingPongCursorPageResponse>

    /**
     * 핑퐁 생성
     *
     * Rate Limit (동일 사용자 + 동일 게시물 기준): 1 req / 5s, 5 req / 60s
     */
    @POST(NetworkApiPath.UserKeyword.PingPong.ROUTE)
    suspend fun createPingPong(
        @Path("userKeywordId") userKeywordId: Long,
        @Body createPingPongRequest: CreatePingPongRequest,
    ): Response<PingPongResponse>

    /**
     * 핑퐁 답변 생성
     */
    @POST(NetworkApiPath.PingPong.ANSWER)
    suspend fun createAnswer(
        @Path("pingPongId") pingPongId: Long,
        @Body createAnswerRequest: CreatePingPongAnswerRequest,
    ): Response<PingPongAnswerResponse>

    /**
     * 핑퐁 삭제
     */
    @DELETE(NetworkApiPath.PingPong.ROUTE)
    suspend fun deletePingPong(
        @Path("pingPongId") pingPongId: Long,
    ): Response<Unit>

    /**
     * 핑퐁 답변 삭제
     */
    @DELETE(NetworkApiPath.PingPong.ANSWER)
    suspend fun deletePingPongAnswer(
        @Path("pingPongId") pingPongId: Long,
    ): Response<Unit>
}
