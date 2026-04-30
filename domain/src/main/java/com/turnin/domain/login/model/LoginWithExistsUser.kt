package com.turnin.domain.login.model

import com.turnin.core.domain.auth.model.LoginCredentials

/**
 * 로그인 정보와 이미 존재하는 사용자인지에 대한 데이터를 담고있다.
 *
 * @property loginCredentials 로그인에 필요한 정보
 * @property isExistsUser 이미 존재하는 사용자인지에 대한 여부
 */
data class LoginWithExistsUser(
    val loginCredentials: LoginCredentials,
    val isExistsUser: Boolean,
)
