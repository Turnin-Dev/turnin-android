package com.peekr.core.data.source.network.api

import com.peekr.core.data.source.network.dto.common.UserKeywordDetailResponse
import com.peekr.core.data.source.network.dto.user.request.IntroducePatchRequest
import com.peekr.core.data.source.network.dto.user.request.UserPatchRequest
import com.peekr.core.data.source.network.dto.user.response.MyProfileResponse
import com.peekr.core.data.source.network.dto.user.response.UserProfileResponse
import com.peekr.core.data.source.network.dto.user.response.UserResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.Path
import retrofit2.http.Query

/** User Network API */
interface UserApi {
    /** 사용자 조회 */
    @GET(NetworkApiPath.User.ROUTE)
    suspend fun getUser(): Response<UserResponse>

    /** 나의 프로필 조회 */
    @GET(NetworkApiPath.User.MY_PROFILE)
    suspend fun getMyProfile(): Response<MyProfileResponse>

    /** 사용자 프로필 조회 */
    @GET(NetworkApiPath.User.USER_PROFILE)
    suspend fun getUserProfile(
        @Path("userId") userId: Long,
        @Query("includeBlocked") includeBlocked: Boolean,
    ): Response<UserProfileResponse>

    /** 나의 키워드 상세 정보 리스트 조회 */
    @GET(NetworkApiPath.User.MY_KEYWORDS)
    suspend fun getMyKeywords(): Response<List<UserKeywordDetailResponse>>

    /** 사용자 키워드 상세 정보 리스트 조회 */
    @GET(NetworkApiPath.User.USER_KEYWORDS)
    suspend fun getUserKeywords(
        @Path("userId") userId: Long,
    ): Response<List<UserKeywordDetailResponse>>

    /** 사용자 수정 */
    @PATCH(NetworkApiPath.User.ROUTE)
    suspend fun updateUser(
        @Body userPatchRequest: UserPatchRequest,
    ): Response<Unit>

    /** 사용자 소개글 수정 */
    @PATCH(NetworkApiPath.User.INTRODUCE)
    suspend fun updateIntroduce(
        @Body introducePathRequest: IntroducePatchRequest,
    ): Response<Unit>
}
