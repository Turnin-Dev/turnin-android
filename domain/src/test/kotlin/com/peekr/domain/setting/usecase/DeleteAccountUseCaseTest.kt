package com.peekr.domain.setting.usecase

import com.peekr.core.domain.auth.repository.AuthRepository
import com.peekr.core.domain.auth.social.SocialAuthManager
import com.peekr.core.domain.auth.social.SocialAuthManagerFactory
import com.peekr.core.domain.common.Result
import com.peekr.core.domain.common.error.CommonErrorType
import com.peekr.core.domain.model.SocialLoginProvider
import com.peekr.domain.setting.error.SettingErrorType
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.last
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class DeleteAccountUseCaseTest {
    private val authRepository: AuthRepository = mockk()
    private val socialAuthManagerFactory: SocialAuthManagerFactory = mockk()
    private val socialAuthManager: SocialAuthManager = mockk()
    private val usecase = DeleteAccountUseCase(authRepository, socialAuthManagerFactory)

    @Before
    fun setUp() {
        coEvery { authRepository.getLoginType() } returns SocialLoginProvider.KAKAO
        every { authRepository.deleteAccount() } returns flowOf(Result.Success(Unit))
        every { socialAuthManagerFactory.create(any()) } returns socialAuthManager
        coEvery { socialAuthManager.deleteAccount() } returns Result.Success(Unit)
    }

    @Test
    fun `계정 삭제 성공 테스트`() = runTest {
        val result = usecase().last()
        assertTrue(result is Result.Success)
    }

    @Test
    fun `현재 로그인 타입을 조회할 수 없는 경우 에러를 방출한다`() = runTest {
        // given
        coEvery { authRepository.getLoginType() } returns null

        // when
        val result = usecase().last()

        // then
        val error = result as Result.Error
        assertEquals(SettingErrorType.LoginProviderNotFound, error.error)

        // 이 후 로직은 실행되지 않아야 한다.
        coVerify(exactly = 0) { authRepository.deleteAccount() }
        coVerify(exactly = 0) { socialAuthManager.deleteAccount() }
    }

    @Test
    fun `소셜 계정 삭제 시 에러가 발생하면 그대로 에러를 방출한다`() = runTest {
        // given
        val expectedError = CommonErrorType.Unexpected(null)
        coEvery { socialAuthManager.deleteAccount() } returns Result.Error(expectedError)

        // when
        val result = usecase().last()

        // then
        val actualError = (result as Result.Error).error
        assertEquals(
            SettingErrorType.CommonError(expectedError),
            actualError,
        )

        coVerify(exactly = 0) { authRepository.deleteAccount() }
    }

    @Test
    fun `계정 삭제 API 호출 시 에러가 발생하면 그대로 에러를 방출한다`() = runTest {
        // given
        val expectedError = CommonErrorType.Unexpected(null)
        coEvery { authRepository.deleteAccount() } returns flowOf(Result.Error(expectedError))

        // when
        val result = usecase().last()

        // then
        val actualError = (result as Result.Error).error
        assertEquals(
            SettingErrorType.CommonError(expectedError),
            actualError,
        )
    }
}
