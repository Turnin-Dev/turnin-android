package com.peekr.core.domain.auth.usecase

import com.peekr.core.domain.auth.repository.AuthRepository
import com.peekr.core.domain.auth.social.SocialAuthManagerFactory
import com.peekr.core.domain.common.Result
import com.peekr.core.domain.common.coroutine.runCatchingSafe
import com.peekr.core.domain.common.error.CommonErrorType
import com.peekr.core.domain.notification.repository.NotificationRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow

/**
 * 로그아웃
 *
 * @see invoke
 */
class LogoutUseCase @Inject constructor(
    private val authRepository: AuthRepository,
    private val notificationRepository: NotificationRepository,
    private val socialAuthManagerFactory: SocialAuthManagerFactory,
) {
    /**
     * 로그아웃을 수행한다.
     * (로그아웃 수행 시에는 로컬 데이터를 전부 삭제한다.)
     *
     * 로그아웃 과정에서는 소셜 로그아웃 실패 시에도 사용자 경험을 위해 정상 과정으로 간주한다.
     *
     * 자세한 내용은 기능명세서 `RQ-2`를 참고한다.
     */
    operator fun invoke(): Flow<Result<Unit, CommonErrorType>> = flow {
        emit(Result.Loading)

        // 0. 데이터 준비
        val loginProvider = authRepository.getLoginType()
        if (loginProvider == null) {
            emit(Result.Error(CommonErrorType.SocialAuth.LoginProviderNotFound))
            return@flow
        }

        // 1. 로그아웃
        val fcmToken = notificationRepository.getFcmToken() ?: ""
        val logoutResult = authRepository.logout(fcmToken)
            .filter { it !is Result.Loading }
            .first()
        if (logoutResult is Result.Error) {
            emit(logoutResult)
            return@flow
        }

        // 2. 알림 구독 해제
        notificationRepository.unsubscribeFromTopic()

        // 3. 소셜 로그인 연동 해제
        val socialAuthManager = socialAuthManagerFactory.create(loginProvider)

        // 로그아웃 단계에서는 소셜 로그아웃 에러 처리를 직접적으로 하지 않고,
        // 사용자 경험을 더 우선시한다.
        runCatchingSafe { socialAuthManager.signOut() }

        emit(Result.Success(Unit))
    }
}
