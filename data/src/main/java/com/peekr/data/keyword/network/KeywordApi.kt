package com.peekr.data.keyword.network

import com.peekr.core.data.network.NetworkApiPath
import com.peekr.data.keyword.model.request.CreateKeywordRequest
import com.peekr.data.keyword.model.response.KeywordResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

/** Keyword API (인증 요청 API) */
interface KeywordApi {
    /** 키워드 조회 */
    @GET("${NetworkApiPath.Keyword.ROUTE}/{keywordId}")
    suspend fun getKeyword(
        @Path("keywordId") keywordId: Long,
    ): Response<KeywordResponse>

    /** 키워드 생성 */
    @POST(NetworkApiPath.Keyword.ROUTE)
    suspend fun createKeyword(
        @Body createKeywordRequest: CreateKeywordRequest,
    ): Response<KeywordResponse>
}
