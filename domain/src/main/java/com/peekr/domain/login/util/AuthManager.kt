package com.peekr.domain.login.util

import com.peekr.core.domain.common.Result
import com.peekr.core.domain.model.ProviderId
import com.peekr.domain.login.error.LoginErrorType
import kotlinx.coroutines.flow.Flow

/** 소셜로그인 인증을 관리한다. */
interface AuthManager {
    /** 해당하는 소셜로그인 플랫폼에 로그인 한다. */
    fun signIn(): Flow<Result<ProviderId, LoginErrorType>>

    /** 해당하는 소셜로그인 플랫폼을 로그아웃 한다. */
    fun signOut(): Flow<Result<Unit, LoginErrorType>>

    /** 해당하는 소셜로그인 플랫폼에서 계정을 삭제한다. */
    fun deleteAccount(): Flow<Result<Unit, LoginErrorType>>
}
