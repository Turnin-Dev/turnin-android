package com.peekr.core.data.source.network.api

import com.peekr.core.data.source.network.api.NetworkApiPath
import com.peekr.core.data.source.network.dto.userKeyword.request.CreateUserKeywordRequest
import com.peekr.core.data.source.network.dto.userKeyword.request.PatchDescriptionRequest
import com.peekr.core.data.source.network.dto.userKeyword.request.PatchOffsetRequest
import com.peekr.core.data.source.network.dto.userKeyword.response.DescriptionResponse
import com.peekr.core.data.source.network.dto.userKeyword.response.PatchDescriptionResponse
import com.peekr.core.data.source.network.dto.userKeyword.response.PatchOffsetResponse
import com.peekr.core.data.source.network.dto.userKeyword.response.UserKeywordResponse
import com.peekr.core.data.source.network.dto.userKeyword.response.UserKeywordsResponse
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

    /** 사용자 키워드 설명 조회 */
    @GET(NetworkApiPath.UserKeyword.DESCRIPTION)
    suspend fun getDescription(
        @Query("userKeywordId") userKeywordId: Long,
    ): Response<DescriptionResponse>

    /** 사용자 키워드 생성 */
    @POST(NetworkApiPath.UserKeyword.ROUTE)
    suspend fun createUserKeyword(
        @Body createUserKeywordRequest: CreateUserKeywordRequest,
    ): Response<UserKeywordResponse>

    /** 사용자 키워드 오프셋 수정 */
    @PATCH(NetworkApiPath.UserKeyword.PATCH_OFFSET)
    suspend fun patchOffset(
        @Query("userKeywordId") userKeywordId: Long,
        @Body patchOffsetRequest: PatchOffsetRequest,
    ): Response<PatchOffsetResponse>

    /** 사용자 키워드 설명 수정 */
    @PATCH(NetworkApiPath.UserKeyword.DESCRIPTION)
    suspend fun patchDescription(
        @Query("userKeywordId") userKeywordId: Long,
        @Body patchDescriptionRequest: PatchDescriptionRequest,
    ): Response<PatchDescriptionResponse>

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
