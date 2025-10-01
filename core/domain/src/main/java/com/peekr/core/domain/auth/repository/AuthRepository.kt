package com.peekr.core.domain.auth.repository

import com.peekr.core.domain.auth.model.ExistsUser
import com.peekr.core.domain.auth.model.JWTToken
import com.peekr.core.domain.auth.model.Login
import com.peekr.core.domain.auth.model.Register
import com.peekr.core.domain.model.DisplayId
import com.peekr.core.domain.util.ErrorType
import com.peekr.core.domain.util.Result
import kotlinx.coroutines.flow.Flow

/** Auth 리포지토리 */
interface AuthRepository {
    /** 로그인 */
    fun login(login: Login): Flow<Result<JWTToken, ErrorType>>

    /** 사용자 존재 여부 확인 */
    fun existsUser(existsUser: ExistsUser): Flow<Result<Boolean, ErrorType>>

    /** 사용자 표시 ID 존재 여부 확인 */
    fun existsDisplayId(displayId: DisplayId): Flow<Result<Boolean, ErrorType>>

    /**
     * 회원가입
     *
     * @param register 회원가입을 위한 정보
     */
    fun register(register: Register): Flow<Result<JWTToken, ErrorType>>

    /**
     * 리프레쉬 토큰 저장
     *
     * @param token 리프레쉬 토큰
     */
    fun saveRefreshToken(token: String): Flow<Result<Unit, ErrorType>>
}
