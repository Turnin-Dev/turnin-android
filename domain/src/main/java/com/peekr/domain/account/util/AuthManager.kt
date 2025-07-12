package com.peekr.domain.account.util

import com.peekr.domain.account.model.UserUID
import com.peekr.domain.shared.util.ErrorType
import com.peekr.domain.shared.util.Result

/** 소셜로그인 인증을 관리한다. */
interface AuthManager {
    /** 해당하는 소셜로그인 플랫폼에 로그인 한다. */
    suspend fun signIn(): Result<UserUID, ErrorType>

    /** 해당하는 소셜로그인 플랫폼을 로그아웃 한다. */
    suspend fun signOut(): Result<Unit, ErrorType>

    /** 해당하는 소셜로그인 플랫폼에서 계정을 삭제한다. */
    suspend fun deleteAccount(): Result<Unit, ErrorType>
}
