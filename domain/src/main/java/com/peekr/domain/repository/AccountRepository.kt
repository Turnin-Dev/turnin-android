package com.peekr.domain.repository

import com.peekr.domain.model.account.Login

/** 계정 관련 리포지토리 */
interface AccountRepository {
    suspend fun login(login: Login)
}
