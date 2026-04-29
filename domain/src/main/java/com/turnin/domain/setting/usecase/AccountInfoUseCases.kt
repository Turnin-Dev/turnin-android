package com.turnin.domain.setting.usecase

import javax.inject.Inject

class AccountInfoUseCases @Inject constructor(
    /**
     * 계정 정보 업데이트
     *
     * @see UpdateAccountInfoUseCase
     */
    val updateAccountInfo: UpdateAccountInfoUseCase,
    // ------------------------------ Validation ------------------------------
    val validateDisplayId: ValidateDisplayIdRemoteUseCase,
    val validateName: ValidationNameFormatUseCase,
    val validateIntroduce: ValidationIntroduceFormatUseCase,
)
