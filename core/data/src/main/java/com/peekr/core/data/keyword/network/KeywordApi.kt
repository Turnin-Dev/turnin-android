package com.peekr.core.data.keyword.network

import com.peekr.core.data.keyword.network.request.CreateKeywordRequest
import com.peekr.core.data.keyword.network.response.KeywordResponse
import com.peekr.core.data.network.NetworkApiPath
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

/** Keyword API (인증 요청 API) */
interface KeywordApi {
    /** 키워드 ID로 키워드 조회 */
    @GET("${NetworkApiPath.Keyword.ID}/{keywordId}")
    suspend fun getKeywordById(
        @Path("keywordId") keywordId: Long,
    ): Response<KeywordResponse>

    /** 키워드 명으로 키워드 조회 */
    @GET("${NetworkApiPath.Keyword.NAME}/{keywordName}")
    suspend fun getKeywordByName(
        @Path("keywordName") keywordName: String,
    ): Response<KeywordResponse>

    /** 키워드 생성 */
    @POST(NetworkApiPath.Keyword.ROUTE)
    suspend fun createKeyword(
        @Body createKeywordRequest: CreateKeywordRequest,
    ): Response<KeywordResponse>
}
