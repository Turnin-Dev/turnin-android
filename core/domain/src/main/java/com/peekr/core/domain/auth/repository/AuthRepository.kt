package com.peekr.core.domain.auth.repository

import com.peekr.core.domain.auth.model.ExistsUser
import com.peekr.core.domain.auth.model.LoginCredentials
import com.peekr.core.domain.auth.model.LoginResult
import com.peekr.core.domain.auth.model.Register
import com.peekr.core.domain.auth.model.RegisterResult
import com.peekr.core.domain.common.Result
import com.peekr.core.domain.common.error.CommonErrorType
import com.peekr.core.domain.model.DisplayId
import com.peekr.core.domain.model.SocialLoginProvider
import kotlinx.coroutines.flow.Flow

/** Auth 리포지토리 */
interface AuthRepository {
    // TODO: 로그인 여부 조건 수정 예정

    /**
     * 로그인 여부를 확인한다.
     *
     * 로그인 성공 조건:
     * - userId, accessToken, refreshToken 모두 존재하며 정상적으로 복호화에 성공한 경우
     *
     * 로그인 실패 조건:
     * - 3개의 데이터 중 하나라도 없는 경우
     * - 암호화된 데이터를 복호화하는 과정에서 오류가 발생한 경우
     */
    suspend fun isLoggedIn(): Boolean

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
     * 로그인 타입 조회
     */
    suspend fun getLoginType(): SocialLoginProvider?

    /**
     * 로그아웃
     *
     * **로그아웃 시 모든 앱 데이터를 삭제하므로 유의해야 한다.**
     *
     * @param token 삭제할 FCM 토큰
     */
    fun logout(token: String): Flow<Result<Unit, CommonErrorType>>

    /**
     * 계정 삭제
     *
     * **계정 삭제 시 모든 앱 데이터를 삭제하므로 유의해야 한다.**
     */
    fun deleteAccount(): Flow<Result<Unit, CommonErrorType>>
}
