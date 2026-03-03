package com.peekr.domain.setting.usecase

import javax.inject.Inject

class SettingUseCases @Inject constructor(
    /**
     * 로그아웃
     *
     * @see LogoutUseCase
     */
    val logoutUseCase: LogoutUseCase,
    /**
     * 계정 삭제
     *
     * @see deleteAccountUseCase
     */
    val deleteAccountUseCase: DeleteAccountUseCase,
    /**
     * 계정 정보 조회
     *
     * @see GetAccountInfoUseCase
     */
    val getAccountInfo: GetAccountInfoUseCase,
)
