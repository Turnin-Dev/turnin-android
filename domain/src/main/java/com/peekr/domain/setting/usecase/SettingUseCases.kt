package com.peekr.domain.setting.usecase

import com.peekr.core.domain.auth.usecase.LogoutUseCase
import javax.inject.Inject

class SettingUseCases @Inject constructor(
    /**
     * 로그아웃
     *
     * @see com.peekr.core.domain.auth.usecase.LogoutUseCase
     */
    val logout: LogoutUseCase,
    /**
     * 계정 삭제
     *
     * @see DeleteAccountUseCase
     */
    val deleteAccount: DeleteAccountUseCase,
    /**
     * 계정 정보 조회
     *
     * @see GetAccountInfoUseCase
     */
    val getAccountInfo: GetAccountInfoUseCase,
)
