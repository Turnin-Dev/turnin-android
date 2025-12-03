package com.peekr.core.data.source.network.datasource

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
     *
     * @return [UserProfileResponse]
     */
    suspend fun getUserProfile(userId: UserId): NetworkResult<UserProfileResponse>

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
