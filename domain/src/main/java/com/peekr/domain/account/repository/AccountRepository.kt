package com.peekr.domain.account.repository

import com.peekr.domain.account.model.ExistsUser
import com.peekr.domain.account.model.JWTToken
import com.peekr.domain.account.model.Login
import com.peekr.domain.shared.util.ErrorType
import com.peekr.domain.shared.util.Result
import kotlinx.coroutines.flow.Flow

/** 계정 관련 리포지토리 */
interface AccountRepository {
    /** 로그인 */
    fun login(login: Login): Flow<Result<JWTToken, ErrorType>>

    /** 사용자 존재 여부 확인 */
    fun existsUser(existsUser: ExistsUser): Flow<Result<Boolean, ErrorType>>
}
