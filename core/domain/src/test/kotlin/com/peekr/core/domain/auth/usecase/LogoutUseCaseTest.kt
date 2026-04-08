package com.peekr.core.domain.auth.usecase

import com.peekr.core.domain.auth.repository.AuthRepository
import com.peekr.core.domain.auth.social.SocialAuthManager
import com.peekr.core.domain.auth.social.SocialAuthManagerFactory
import com.peekr.core.domain.common.Result
import com.peekr.core.domain.common.error.CommonErrorType
import com.peekr.core.domain.model.SocialLoginProvider
import com.peekr.core.domain.notification.repository.NotificationRepository
import com.peekr.core.domain.util.DomainLogger
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.last
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class LogoutUseCaseTest {
    private val authRepository: AuthRepository = mockk()
    private val notificationRepository: NotificationRepository = mockk()
    private val socialAuthManagerFactory: SocialAuthManagerFactory = mockk()
    private val socialAuthManager: SocialAuthManager = mockk()
    private val logger: DomainLogger = mockk()
    private val usecase =
        LogoutUseCase(authRepository, notificationRepository, socialAuthManagerFactory, logger)

    @Before
    fun setUp() {
        coEvery { notificationRepository.getFcmToken() } returns TEST_FCM_TOKEN
        every { socialAuthManagerFactory.create(TestLoginProvider) } returns socialAuthManager
        coEvery { socialAuthManager.signOut() } returns Result.Success(Unit)
        coEvery { notificationRepository.unsubscribeFromTopic() } just Runs
        every { logger.d(any(), any(), any()) } just Runs
    }

    // ------------------------------ 로그인 프로바이더 조회 ------------------------------

    @Test
    fun `로그인 프로바이더가 없으면 LoginProviderNotFound 에러를 방출한다`() = runTest {
        // given
        coEvery { authRepository.getLoginType() } returns null

        // when
        val result = usecase().last()

        // then
        val error = result as Result.Error
        assertTrue(error.error is CommonErrorType.SocialAuth.LoginProviderNotFound)

        verify(exactly = 0) { authRepository.logout(any()) }
        coVerify(exactly = 0) { notificationRepository.unsubscribeFromTopic() }
        coVerify(exactly = 0) { socialAuthManager.signOut() }
    }

    // ------------------------------ 로그아웃 ------------------------------

    @Test
    fun `로그아웃 API 호출 실패 시 에러를 방출한다`() = runTest {
        // given
        val expectedError = CommonErrorType.Unexpected(null)
        coEvery { authRepository.getLoginType() } returns TestLoginProvider
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

        verify(exactly = 1) { authRepository.logout(any()) }
        coVerify(exactly = 0) { notificationRepository.unsubscribeFromTopic() }
        coVerify(exactly = 0) { socialAuthManager.signOut() }
    }

    @Test
    fun `로그아웃 성공 시 소셜 로그아웃도 수행하고 성공을 방출한다`() = runTest {
        // given
        coEvery { authRepository.getLoginType() } returns TestLoginProvider
        every {
            authRepository.logout(TEST_FCM_TOKEN)
        } returns flowOf(Result.Success(Unit))

        // when
        val result = usecase().last()

        // then
        assertTrue(result is Result.Success)
        verify(exactly = 1) { authRepository.logout(any()) }
        coVerify(exactly = 1) { notificationRepository.unsubscribeFromTopic() }
        coVerify(exactly = 1) { socialAuthManager.signOut() }
    }

    @Test
    fun `소셜 로그아웃 실패 시에도 성공을 방출한다`() = runTest {
        // given
        coEvery { authRepository.getLoginType() } returns TestLoginProvider
        every {
            authRepository.logout(TEST_FCM_TOKEN)
        } returns flowOf(Result.Success(Unit))
        coEvery { socialAuthManager.signOut() } throws Exception("소셜 로그아웃 실패")

        // when
        val result = usecase().last()

        // then
        assertTrue(result is Result.Success)
    }

    // ------------------------------ Loading ------------------------------

    @Test
    fun `로그아웃 시작 시 Loading을 방출한다`() = runTest {
        // given
        coEvery { authRepository.getLoginType() } returns TestLoginProvider
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
        coEvery { authRepository.getLoginType() } returns TestLoginProvider
        coEvery { notificationRepository.getFcmToken() } returns null
        every {
            authRepository.logout("")
        } returns flowOf(Result.Success(Unit))

        // when
        val result = usecase().last()

        // then
        verify(exactly = 1) { authRepository.logout("") }
        assertTrue(result is Result.Success)
    }

    companion object {
        private val TestLoginProvider = SocialLoginProvider.KAKAO
        private const val TEST_FCM_TOKEN = "test_fcm_token"
    }
}
