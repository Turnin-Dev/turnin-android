package com.peekr.domain.login.util

import com.peekr.core.domain.model.ProviderId
import com.peekr.core.domain.util.ErrorType
import com.peekr.core.domain.util.Result
import kotlinx.coroutines.flow.Flow

/** 소셜로그인 인증을 관리한다. */
interface AuthManager {
    /** 해당하는 소셜로그인 플랫폼에 로그인 한다. */
    fun signIn(): Flow<Result<ProviderId, ErrorType>>

    /** 해당하는 소셜로그인 플랫폼을 로그아웃 한다. */
    fun signOut(): Flow<Result<Unit, ErrorType>>

    /** 해당하는 소셜로그인 플랫폼에서 계정을 삭제한다. */
    fun deleteAccount(): Flow<Result<Unit, ErrorType>>
}
