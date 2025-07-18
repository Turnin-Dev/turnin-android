package com.peekr.domain.account.repository

import com.peekr.domain.account.model.JWTToken
import com.peekr.domain.account.model.Login
import com.peekr.domain.shared.util.ErrorType
import com.peekr.domain.shared.util.Result
import kotlinx.coroutines.flow.Flow

/** 계정 관련 리포지토리 */
interface AccountRepository {
    /**
 * 사용자의 로그인 정보를 받아 JWT 토큰 발급 결과를 비동기적으로 반환합니다.
 *
 * @param login 로그인에 필요한 사용자 정보.
 * @return 성공 시 JWT 토큰, 실패 시 오류 유형을 포함하는 Result를 Flow로 반환합니다.
 */
    fun login(login: Login): Flow<Result<JWTToken, ErrorType>>
}
