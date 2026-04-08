package com.peekr.domain.setting.usecase

import com.peekr.core.domain.auth.repository.AuthRepository
import com.peekr.core.domain.auth.social.SocialAuthManagerFactory
import com.peekr.core.domain.common.Result
import com.peekr.core.domain.common.coroutine.runCatchingSafe
import com.peekr.core.domain.common.error.CommonErrorType
import com.peekr.core.domain.common.error.mapError
import com.peekr.core.domain.notification.repository.NotificationRepository
import com.peekr.core.domain.util.DomainLogger
import com.peekr.core.domain.util.catchAndLog
import com.peekr.domain.setting.error.SettingErrorType
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.lastOrNull

/**
 * 계정 삭제
 *
 * @see invoke
 */
class DeleteAccountUseCase @Inject constructor(
    private val authRepository: AuthRepository,
    private val notificationRepository: NotificationRepository,
    private val socialAuthManagerFactory: SocialAuthManagerFactory,
    private val logger: DomainLogger,
) {
    private val tag = this::class.java.simpleName

    /**
     * 계정을 삭제한다.
     *
     * 자세한 내용은 기능명세서 `RQ-3`를 참고한다.
     */
    operator fun invoke(): Flow<Result<Unit, SettingErrorType>> = flow {
        emit(Result.Loading)

        // 0. 데이터 준비
        val loginProvider = authRepository.getLoginType()
        if (loginProvider == null) {
            emit(Result.Error(SettingErrorType.CommonError(CommonErrorType.SocialAuth.LoginProviderNotFound)))
            return@flow
        }

        // 1. 계정 삭제 API 호출 및 앱 데이터 정리
        val deleteAccountResult = authRepository.deleteAccount()
            .mapError { commonError ->
                SettingErrorType.CommonError(commonError)
            }
            .lastOrNull()

        when (deleteAccountResult) {
            is Result.Success -> Unit
            is Result.Error -> {
                emit(deleteAccountResult)
                return@flow
            }

            else -> {
                emit(Result.Error(SettingErrorType.Unexpected(null)))
                return@flow
            }
        }

        // 2. 알림 구독 해제
        notificationRepository.unsubscribeFromTopic()

        // 3. 소셜 로그인 연동 해제 (해당 단계가 실패해도 계정은 이미 삭제된 상태)
        val socialAuthManager = socialAuthManagerFactory.create(loginProvider)
        runCatchingSafe { socialAuthManager.deleteAccount() }

        emit(Result.Success(Unit))
    }
        .catchAndLog(logger, tag) { e ->
            emit(Result.Error(SettingErrorType.Unexpected(e)))
        }
}
