package com.peekr.core.domain.auth.social

import com.peekr.core.domain.common.Result
import com.peekr.core.domain.common.error.CommonErrorType
import com.peekr.core.domain.model.ProviderId
import kotlinx.coroutines.flow.Flow

/** 소셜로그인 인증을 관리한다. */
interface SocialAuthManager {
    /** 해당하는 소셜로그인 플랫폼에 로그인 한다. */
    fun signIn(): Flow<Result<ProviderId, CommonErrorType>>

    /** 해당하는 소셜로그인 플랫폼을 로그아웃 한다. */
    suspend fun signOut(): Result<Unit, CommonErrorType>

    /** 해당하는 소셜로그인 플랫폼에서 계정을 삭제한다. */
    suspend fun deleteAccount(): Result<Unit, CommonErrorType>
}
