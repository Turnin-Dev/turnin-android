package com.peekr.core.domain.auth.usecase

import com.peekr.core.domain.auth.repository.AuthRepository
import com.peekr.core.domain.common.CommonErrorType
import com.peekr.core.domain.common.Result
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class CheckLoggedInUseCase @Inject constructor(
    private val authRepository: AuthRepository,
) {
    operator fun invoke(): Flow<Result<Boolean, CommonErrorType>> = flow {
        // 1) 로컬 저장소에 사용자 ID, 액세스 토큰, 리프레쉬 토큰이 있는지 확인한다.
        // 없으면, false
        // 2) 액세스 토큰으로 인증한다.
        // 성공 하면, true
        // 3) 만약 실패 시, 토큰 재발급 과정을 거친다.
        // 실패 시, false
        // 4) 성공 하면 액세스 토큰 재발급 및 저장 후 true
    }
}
