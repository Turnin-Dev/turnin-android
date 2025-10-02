package com.peekr.core.data.userKeyword.network

import com.peekr.core.data.network.NetworkApiPath
import com.peekr.core.data.userKeyword.network.request.CreateUserKeywordRequest
import com.peekr.core.data.userKeyword.network.request.PatchUserKeywordRequest
import com.peekr.core.data.userKeyword.network.response.UserKeywordResponse
import com.peekr.core.data.userKeyword.network.response.UserKeywordsResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Query

interface UserKeywordApi {
    /** 사용자 키워드 리스트 조회 */
    @GET(NetworkApiPath.UserKeyword.ROUTE)
    suspend fun getUserKeywords(): Response<UserKeywordsResponse>

    /** 사용자 키워드 생성 */
    @POST(NetworkApiPath.UserKeyword.ROUTE)
    suspend fun createUserKeyword(
        @Body createUserKeywordRequest: CreateUserKeywordRequest,
    ): Response<UserKeywordResponse>

    /**
     * 사용자 키워드 수정
     *
     * - 성공 시: HTTP 상태코드 `204` 반환
     * - 실패 시: HTTP 상태코드 `404` 반환
     */
    @PATCH(NetworkApiPath.UserKeyword.ROUTE)
    suspend fun patchUserKeyword(
        @Query("userKeywordId") userKeywordId: Long,
        @Body patchUserKeywordRequest: PatchUserKeywordRequest,
    ): Response<Unit>

    /**
     * 사용자 키워드 삭제
     *
     * - 성공 시: HTTP 상태코드 `204` 반환
     * - 실패 시: HTTP 상태코드 `404` 반환
     */
    @DELETE(NetworkApiPath.UserKeyword.ROUTE)
    suspend fun deleteUserKeyword(
        @Query("userKeywordId") userKeywordId: Long,
    ): Response<Unit>
}
