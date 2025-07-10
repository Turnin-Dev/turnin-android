package com.peekr.domain.repository

import com.peekr.domain.model.account.Login
import com.peekr.domain.util.ErrorType
import com.peekr.domain.util.Result
import kotlinx.coroutines.flow.Flow

/** 계정 관련 리포지토리 */
interface AccountRepository {
    /** 로그인 */
    suspend fun login(login: Login): Flow<Result<Boolean, ErrorType>>
}
