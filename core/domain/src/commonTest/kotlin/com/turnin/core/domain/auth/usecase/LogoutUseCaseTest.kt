package com.turnin.core.domain.auth.usecase

import com.turnin.core.domain.auth.repository.AuthRepository
import com.turnin.core.domain.auth.social.SocialAuthManager
import com.turnin.core.domain.auth.social.SocialAuthManagerFactory
import com.turnin.core.domain.common.Result
import com.turnin.core.domain.common.error.CommonErrorType
import com.turnin.core.domain.model.SocialLoginProvider
import com.turnin.core.domain.notification.repository.NotificationRepository
import com.turnin.core.domain.util.DomainLogger
import dev.mokkery.answering.returns
import dev.mokkery.answering.throws
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
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest

class LogoutUseCaseTest {
    private val authRepository: AuthRepository = mock()
    private val notificationRepository: NotificationRepository = mock()
    private val socialAuthManagerFactory: SocialAuthManagerFactory = mock()
    private val socialAuthManager: SocialAuthManager = mock()
    private val logger: DomainLogger = mock()
    private val usecase =
        LogoutUseCase(authRepository, notificationRepository, socialAuthManagerFactory, logger)

    @BeforeTest
    fun setUp() {
        everySuspend { notificationRepository.getFcmToken() } returns TEST_FCM_TOKEN
        every { socialAuthManagerFactory.create(TestLoginProvider) } returns socialAuthManager
        everySuspend { socialAuthManager.signOut() } returns Result.Success(Unit)
        everySuspend { notificationRepository.unsubscribeFromTopic() } returns Unit
        every { logger.e(any(), any(), any()) } returns Unit
    }

    // ------------------------------ 로그인 프로바이더 조회 ------------------------------

    @Test
    fun `로그인 프로바이더가 없으면 LoginProviderNotFound 에러를 방출한다`() = runTest {
        // given
        everySuspend { authRepository.getLoginType() } returns null

        // when
        val result = usecase().last()

        // then
        val error = result as Result.Error
        assertTrue(error.error is CommonErrorType.SocialAuth.LoginProviderNotFound)

        verify(VerifyMode.exactly(0)) { authRepository.logout(any()) }
        verifySuspend(VerifyMode.exactly(0)) { notificationRepository.unsubscribeFromTopic() }
        verifySuspend(VerifyMode.exactly(0)) { socialAuthManager.signOut() }
    }

    // ------------------------------ 로그아웃 ------------------------------

    @Test
    fun `로그아웃 API 호출 실패 시 에러를 방출한다`() = runTest {
        // given
        val expectedError = CommonErrorType.Unexpected(null)
        everySuspend { authRepository.getLoginType() } returns TestLoginProvider
        every {
            authRepository.logout(TEST_FCM_TOKEN)
        } returns flowOf(
            Result.Loading,
            Result.Error(expectedError),
        )

        // when
        val result = usecase().last()

        // then
        val error = result as Result.Error
        assertEquals(expectedError, error.error)

        verify(VerifyMode.exactly(1)) { authRepository.logout(any()) }
        verifySuspend(VerifyMode.exactly(0)) { notificationRepository.unsubscribeFromTopic() }
        verifySuspend(VerifyMode.exactly(0)) { socialAuthManager.signOut() }
    }

    @Test
    fun `로그아웃 성공 시 소셜 로그아웃도 수행하고 성공을 방출한다`() = runTest {
        // given
        everySuspend { authRepository.getLoginType() } returns TestLoginProvider
        every {
            authRepository.logout(TEST_FCM_TOKEN)
        } returns flowOf(Result.Success(Unit))

        // when
        val result = usecase().last()

        // then
        assertTrue(result is Result.Success)
        verify(VerifyMode.exactly(1)) { authRepository.logout(any()) }
        verifySuspend(VerifyMode.exactly(1)) { notificationRepository.unsubscribeFromTopic() }
        verifySuspend(VerifyMode.exactly(1)) { socialAuthManager.signOut() }
    }

    @Test
    fun `소셜 로그아웃 실패 시에도 성공을 방출한다`() = runTest {
        // given
        everySuspend { authRepository.getLoginType() } returns TestLoginProvider
        every {
            authRepository.logout(TEST_FCM_TOKEN)
        } returns flowOf(Result.Success(Unit))
        everySuspend { socialAuthManager.signOut() } throws Exception("소셜 로그아웃 실패")

        // when
        val result = usecase().last()

        // then
        assertTrue(result is Result.Success)
    }

    // ------------------------------ Loading ------------------------------

    @Test
    fun `로그아웃 시작 시 Loading을 방출한다`() = runTest {
        // given
        everySuspend { authRepository.getLoginType() } returns TestLoginProvider
        every {
            authRepository.logout(TEST_FCM_TOKEN)
        } returns flowOf(Result.Success(Unit))

        // when
        val results = usecase().toList()

        // then
        assertTrue(results.first() is Result.Loading)
    }

    // ------------------------------ 빈 문자열 처리 ------------------------------
    @Test
    fun `FCM 토큰이 null이면 빈 문자열로 폴백하여 로그아웃을 진행한다`() = runTest {
        // given
        everySuspend { authRepository.getLoginType() } returns TestLoginProvider
        everySuspend { notificationRepository.getFcmToken() } returns null
        every {
            authRepository.logout("")
        } returns flowOf(Result.Success(Unit))

        // when
        val result = usecase().last()

        // then
        verify(VerifyMode.exactly(1)) { authRepository.logout("") }
        assertTrue(result is Result.Success)
    }

    companion object {
        private val TestLoginProvider = SocialLoginProvider.KAKAO
        private const val TEST_FCM_TOKEN = "test_fcm_token"
    }
}
