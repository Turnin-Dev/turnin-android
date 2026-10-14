package com.turnin.domain.setting.usecase

import com.turnin.core.domain.auth.repository.AuthRepository
import com.turnin.core.domain.auth.social.SocialAuthManager
import com.turnin.core.domain.auth.social.SocialAuthManagerFactory
import com.turnin.core.domain.common.Result
import com.turnin.core.domain.common.error.CommonErrorType
import com.turnin.core.domain.model.SocialLoginProvider
import com.turnin.core.domain.notification.repository.NotificationRepository
import com.turnin.core.domain.util.DomainLogger
import com.turnin.domain.setting.error.SettingErrorType
import dev.mokkery.answering.returns
import dev.mokkery.every
import dev.mokkery.everySuspend
import dev.mokkery.matcher.any
import dev.mokkery.mock
import dev.mokkery.verify
import dev.mokkery.verify.VerifyMode
import dev.mokkery.verifySuspend
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.last
import kotlinx.coroutines.test.runTest

class DeleteAccountUseCaseTest {
    private val authRepository: AuthRepository = mock()
    private val notificationRepository: NotificationRepository = mock()
    private val socialAuthManagerFactory: SocialAuthManagerFactory = mock()
    private val socialAuthManager: SocialAuthManager = mock()
    private val logger: DomainLogger = mock()
    private val usecase =
        DeleteAccountUseCase(authRepository, notificationRepository, socialAuthManagerFactory, logger)

    @BeforeTest
    fun setUp() {
        everySuspend { authRepository.getLoginType() } returns SocialLoginProvider.KAKAO
        every { authRepository.deleteAccount() } returns flowOf(Result.Success(Unit))
        every { socialAuthManagerFactory.create(any()) } returns socialAuthManager
        everySuspend { socialAuthManager.deleteAccount() } returns Result.Success(Unit)
        everySuspend { notificationRepository.unsubscribeFromTopic() } returns Unit
        every { logger.e(any(), any(), any()) } returns Unit
    }

    @Test
    fun `계정 삭제 성공 테스트`() = runTest {
        val result = usecase().last()
        assertTrue(result is Result.Success)

        verifySuspend(VerifyMode.exactly(1)) { notificationRepository.unsubscribeFromTopic() }
        verify(VerifyMode.exactly(1)) { authRepository.deleteAccount() }
        verifySuspend(VerifyMode.exactly(1)) { socialAuthManager.deleteAccount() }
    }

    @Test
    fun `현재 로그인 타입을 조회할 수 없는 경우 에러를 방출한다`() = runTest {
        // given
        everySuspend { authRepository.getLoginType() } returns null

        // when
        val result = usecase().last()

        // then
        val error = result as Result.Error
        assertEquals(
            SettingErrorType.CommonError(CommonErrorType.SocialAuth.LoginProviderNotFound),
            error.error,
        )

        // 이 후 로직은 실행되지 않아야 한다.
        verifySuspend(VerifyMode.exactly(0)) { notificationRepository.unsubscribeFromTopic() }
        verify(VerifyMode.exactly(0)) { authRepository.deleteAccount() }
        verifySuspend(VerifyMode.exactly(0)) { socialAuthManager.deleteAccount() }
    }

    @Test
    fun `소셜 계정 삭제 시 에러가 발생해도 그대로 계정 삭제를 진행한다`() = runTest {
        // given
        val expectedError = CommonErrorType.Unexpected(null)
        everySuspend { socialAuthManager.deleteAccount() } returns Result.Error(expectedError)

        // when
        val result = usecase().last()

        // then
        assertTrue(result is Result.Success)

        verify(VerifyMode.exactly(1)) { authRepository.deleteAccount() }
        verifySuspend(VerifyMode.exactly(1)) { socialAuthManager.deleteAccount() }
        verifySuspend(VerifyMode.exactly(1)) { notificationRepository.unsubscribeFromTopic() }
    }

    @Test
    fun `계정 삭제 API 호출 시 에러가 발생하면 그대로 에러를 방출한다`() = runTest {
        // given
        val expectedError = CommonErrorType.Unexpected(null)
        every { authRepository.deleteAccount() } returns flowOf(Result.Error(expectedError))

        // when
        val result = usecase().last()

        // then
        val actualError = (result as Result.Error).error
        assertEquals(
            SettingErrorType.CommonError(expectedError),
            actualError,
        )

        verifySuspend(VerifyMode.exactly(0)) { socialAuthManager.deleteAccount() }
        verifySuspend(VerifyMode.exactly(0)) { notificationRepository.unsubscribeFromTopic() }
    }
}
