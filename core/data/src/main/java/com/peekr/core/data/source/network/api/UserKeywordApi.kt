package com.peekr.core.data.source.network.api

import com.peekr.core.data.source.network.dto.userKeyword.request.CreateUserKeywordRequest
import com.peekr.core.data.source.network.dto.userKeyword.request.PatchDescriptionRequest
import com.peekr.core.data.source.network.dto.userKeyword.response.DescriptionResponse
import com.peekr.core.data.source.network.dto.userKeyword.response.PatchDescriptionResponse
import com.peekr.core.data.source.network.dto.userKeyword.response.UserKeywordDetailResponse
import com.peekr.core.data.source.network.dto.userKeyword.response.UserKeywordResponse
import com.peekr.core.data.source.network.dto.userKeyword.response.UserKeywordsResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface UserKeywordApi {
    /** 사용자 키워드 리스트 조회 */
    @Deprecated("삭제 예정 - 사용자 키워드 상세 정보 리스트 조회를 대신 사용한다.")
    @GET(NetworkApiPath.UserKeyword.ROUTE)
    suspend fun getUserKeywords(
        @Query("userId") userId: Long,
    ): Response<UserKeywordsResponse>

    /**
     * 사용자 키워드 상세 정보 조회
     *
     * @param userKeywordId 사용자 키워드 ID
     * @param withUserInfo 사용자 정보 포함 여부
     */
    @GET(NetworkApiPath.UserKeyword.DETAIL)
    suspend fun getDetail(
        @Path("userKeywordId") userKeywordId: Long,
        @Query("withUserInfo") withUserInfo: Boolean,
    ): Response<UserKeywordDetailResponse>

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
