package com.turnin.core.domain.user.repository

import com.turnin.core.domain.common.Result
import com.turnin.core.domain.common.error.CommonErrorType
import com.turnin.core.domain.model.Introduce
import com.turnin.core.domain.model.UserId
import com.turnin.core.domain.user.model.CoreMyProfile
import com.turnin.core.domain.user.model.CoreUserProfile
import com.turnin.core.domain.user.model.User
import com.turnin.core.domain.user.model.UserPatch
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

/** 사용자 리포지토리 */
interface UserRepository {
    /**
     * 나의 프로필 전역 상태 (메모리 캐시)
     *
     * 앱 시작 시 DB를 구독하며, 변경사항이 자동으로 반영된다.
     *
     * - 현재 값만 필요한 경우: [StateFlow.value] 사용 (초기값 설정, null 체크, 값 추출 등)
     * - 변경사항 감지가 필요한 경우: Flow로 구독 (UseCase를 통해 map 후 노출 권장)
     *
     * **주의**: 앱 시작 직후 DB 읽기 완료 전에는 `null`일 수 있으므로 반드시 null 체크 및
     * 네트워크 fallback 로직을 작성해야 한다. 값 변경은 반드시 DB를 통해 수행한다. (SSOT 원칙)
     */
    val myProfile: StateFlow<CoreMyProfile?>

    /**
     * 나의 사용자 ID 조회
     */
    suspend fun getMyUserId(): UserId?

    /**
     * 사용자 조회
     *
     * @return [User]
     */
    fun getUser(): Flow<Result<User, CommonErrorType>>

    /**
     * 나의 프로필 정보를 조회해서 로컬 데이터에 업데이트한다.
     */
    fun getMyProfileRefresh(): Flow<Result<Unit, CommonErrorType>>

    /**
     * 사용자 프로필 조회
     *
     * @param userId 사용자 ID
     * @param forceRefresh 강제 새로고침 (캐시를 무효화하고 데이터를 새롭게 받아온다.)
     *
     * @return [CoreUserProfile]
     */
    fun getUserProfile(
        userId: UserId,
        forceRefresh: Boolean = false,
    ): Flow<Result<CoreUserProfile, CommonErrorType>>

    /**
     * 사용자 프로필을 캐시에서 조회한다.
     *
     * @param userId 사용자 ID
     */
    fun getCachedUserProfile(
        userId: UserId,
    ): CoreUserProfile?

    /**
     * 나의 프로필 수정
     *
     * @param patch 사용자 수정 요청
     */
    fun updateMyProfile(patch: UserPatch): Flow<Result<Unit, CommonErrorType>>

    /**
     * 나의 소개글 수정
     *
     * @param introduce 소개글
     */
    fun updateMyIntroduce(introduce: Introduce): Flow<Result<Unit, CommonErrorType>>
}
