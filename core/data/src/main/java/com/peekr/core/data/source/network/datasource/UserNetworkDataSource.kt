package com.peekr.core.data.source.network.datasource

import com.peekr.core.data.source.network.dto.common.UserKeywordDetailResponse
import com.peekr.core.data.source.network.dto.user.request.IntroducePatchRequest
import com.peekr.core.data.source.network.dto.user.request.UserPatchRequest
import com.peekr.core.data.source.network.dto.user.response.MyProfileResponse
import com.peekr.core.data.source.network.dto.user.response.UserProfileResponse
import com.peekr.core.data.source.network.dto.user.response.UserResponse
import com.peekr.core.data.source.network.util.NetworkResult
import com.peekr.core.domain.model.UserId

/** 사용자 데이터 소스 */
interface UserNetworkDataSource {
    /**
     * 사용자 조회
     *
     * @return [UserResponse]
     */
    suspend fun getUser(): NetworkResult<UserResponse>

    /**
     * 나의 프로필 조회
     *
     * @return [MyProfileResponse]
     */
    suspend fun getMyProfile(): NetworkResult<MyProfileResponse>

    /**
     * 사용자 프로필 조회
     *
     * @param userId 사용자 ID
     * @param includeBlocked 조회 시 차단 사용자 포함 여부
     * (`true`면 차단 사용자까지 함께 조회하고 `false`면 제외하고 조회한다.)
     *
     * @return [UserProfileResponse]
     */
    suspend fun getUserProfile(
        userId: UserId,
        includeBlocked: Boolean,
    ): NetworkResult<UserProfileResponse>

    /**
     * 나의 키워드 상세 정보 리스트 조회
     */
    suspend fun getMyKeywords(): NetworkResult<List<UserKeywordDetailResponse>>

    /**
     * 사용자의 키워드 상세 정보 리스트 조회
     *
     * @param userId 사용자 ID
     */
    suspend fun getUserKeywords(userId: Long): NetworkResult<List<UserKeywordDetailResponse>>

    /**
     * 사용자 수정
     *
     * @param patch 사용자 수정 요청 바디
     */
    suspend fun updateUser(patch: UserPatchRequest): NetworkResult<Unit>

    /**
     * 사용자 소개글 수정
     *
     * @param patch 사용자 소개글 수정 요청 바디
     */
    suspend fun updateIntroduce(patch: IntroducePatchRequest): NetworkResult<Unit>
}
