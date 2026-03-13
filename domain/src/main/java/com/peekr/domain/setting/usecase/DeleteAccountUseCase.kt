package com.peekr.domain.setting.usecase

import com.peekr.core.domain.auth.repository.AuthRepository
import com.peekr.core.domain.auth.social.SocialAuthManagerFactory
import com.peekr.core.domain.common.Result
import com.peekr.core.domain.common.error.CommonErrorType
import com.peekr.core.domain.common.error.mapError
import com.peekr.domain.setting.error.SettingErrorType
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow

/**
 * 계정 삭제
 *
 * @see invoke
 */
class DeleteAccountUseCase @Inject constructor(
    private val authRepository: AuthRepository,
    private val socialAuthManagerFactory: SocialAuthManagerFactory,
) {
    /**
     * 계정을 삭제한다.
     *
     * 자세한 내용은 기능명세서 `RQ-3`를 참고한다.
     */
    operator fun invoke(): Flow<Result<Unit, SettingErrorType>> = flow {
        emit(Result.Loading)

        // 1. 소셜 로그인 연동 해제
        val loginProvider = authRepository.getLoginType()
        if (loginProvider == null) {
            emit(Result.Error(SettingErrorType.CommonError(CommonErrorType.SocialAuth.LoginProviderNotFound)))
            return@flow
        }
        val socialAuthManager = socialAuthManagerFactory.create(loginProvider)
        val deleteResult = socialAuthManager.deleteAccount()

        if (deleteResult is Result.Error) {
            emit(
                deleteResult.mapError { commonError ->
                    SettingErrorType.CommonError(commonError)
                },
            )
            return@flow
        }

        // 2. 계정 삭제 API 호출 및 앱 데이터 정리
        emitAll(
            authRepository.deleteAccount()
                .mapError { commonError ->
                    SettingErrorType.CommonError(commonError)
                },
        )
    }
}
