package com.peekr.core.domain.auth.repository

import com.peekr.core.domain.auth.model.ExistsUser
import com.peekr.core.domain.auth.model.LoginCredentials
import com.peekr.core.domain.auth.model.LoginResult
import com.peekr.core.domain.auth.model.Register
import com.peekr.core.domain.auth.model.RegisterResult
import com.peekr.core.domain.common.Result
import com.peekr.core.domain.common.error.CommonErrorType
import com.peekr.core.domain.model.DisplayId
import kotlinx.coroutines.flow.Flow

/** Auth 리포지토리 */
interface AuthRepository {
    /** 로그인 */
    fun login(loginCredentials: LoginCredentials): Flow<Result<LoginResult, CommonErrorType>>

    /** 사용자 존재 여부 확인 */
    fun existsUser(existsUser: ExistsUser): Flow<Result<Boolean, CommonErrorType>>

    /** 사용자 표시 ID 존재 여부 확인 */
    fun existsDisplayId(displayId: DisplayId): Flow<Result<Boolean, CommonErrorType>>

    /**
     * 회원가입
     *
     * @param register 회원가입을 위한 정보
     */
    fun register(register: Register): Flow<Result<RegisterResult, CommonErrorType>>

    /**
     * 토큰 저장
     *
     * @param accessToken 액세스 토큰
     * @param refreshToken 리프레쉬 토큰
     */
    fun saveTokens(
        accessToken: String,
        refreshToken: String,
    ): Flow<Result<Unit, CommonErrorType>>

    /**
     * 자원 정리
     *
     * 로컬 데이터를 전부 삭제한다.
     */
    suspend fun cleanUp()
}
