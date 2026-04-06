package com.peekr.domain.login.usecase

import com.peekr.core.domain.auth.model.LoginCredentials
import com.peekr.core.domain.auth.model.LoginResult
import com.peekr.core.domain.auth.repository.AuthRepository
import com.peekr.core.domain.common.Result
import com.peekr.core.domain.common.error.CommonErrorType
import com.peekr.core.domain.eventBus.AuthEventBus
import com.peekr.core.domain.model.ProviderId
import com.peekr.core.domain.model.SocialLoginProvider
import com.peekr.core.domain.model.UserId
import com.peekr.domain.login.error.LoginErrorType
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class LoginUseCaseTest {
    private val authRepository: AuthRepository = mockk()
    private val authEventBus: AuthEventBus = mockk()

    private val usecase = LoginUseCase(
        authRepository = authRepository,
        authEventBus = authEventBus,
    )

    private val loginCredentials = LoginCredentials(
        provider = SocialLoginProvider.KAKAO,
        providerId = ProviderId("test_provider_id"),
    )
    private val loginResult = LoginResult(
        userId = UserId(1L),
        accessToken = "access_token",
        refreshToken = "refresh_token",
    )

    @Test
    fun `로그인 성공 시 토큰 저장 후 로그인 이벤트를 발행하고 Unit을 반환한다`() = runTest {
        // given
        every { authRepository.login(loginCredentials) } returns flowOf(
            Result.Loading,
            Result.Success(loginResult),
        )
        every { authRepository.saveTokens(loginResult.accessToken, loginResult.refreshToken) } returns flowOf(
            Result.Success(Unit),
        )
        coEvery { authEventBus.emitLogin() } just Runs

        // when
        val results = usecase(loginCredentials).toList()

        // then
        assertEquals(
            listOf(
                Result.Loading,
                Result.Success(Unit),
            ),
            results,
        )
        coVerify(exactly = 1) { authEventBus.emitLogin() }
    }

    @Test
    fun `로그인 실패 시 CommonError를 반환한다`() = runTest {
        // given
        val commonError = CommonErrorType.Unexpected(null)
        every { authRepository.login(loginCredentials) } returns flowOf(
            Result.Loading,
            Result.Error(commonError),
        )

        // when
        val results = usecase(loginCredentials).toList()

        // then
        assertEquals(
            listOf(
                Result.Loading,
                Result.Error(LoginErrorType.CommonError(commonError)),
            ),
            results,
        )
        coVerify(exactly = 0) { authEventBus.emitLogin() }
    }

    @Test
    fun `토큰 저장 실패 시 CommonError를 반환한다`() = runTest {
        // given
        val commonError = CommonErrorType.Unexpected(null)
        every { authRepository.login(loginCredentials) } returns flowOf(
            Result.Loading,
            Result.Success(loginResult),
        )
        every { authRepository.saveTokens(loginResult.accessToken, loginResult.refreshToken) } returns flowOf(
            Result.Error(commonError),
        )

        // when
        val results = usecase(loginCredentials).toList()

        // then
        assertEquals(
            listOf(
                Result.Loading,
                Result.Error(LoginErrorType.CommonError(commonError)),
            ),
            results,
        )
        coVerify(exactly = 0) { authEventBus.emitLogin() }
    }

    @Test
    fun `토큰 저장 실패 시 로그인 이벤트를 발행하지 않는다`() = runTest {
        // given
        val commonError = CommonErrorType.Unexpected(null)
        every { authRepository.login(loginCredentials) } returns flowOf(
            Result.Loading,
            Result.Success(loginResult),
        )
        every { authRepository.saveTokens(loginResult.accessToken, loginResult.refreshToken) } returns flowOf(
            Result.Error(commonError),
        )

        // when
        usecase(loginCredentials).toList()

        // then
        coVerify(exactly = 0) { authEventBus.emitLogin() }
    }

    @Test
    fun `예외 발생 시 LoginFailed Error를 반환한다`() = runTest {
        // given
        val errorMessage = "Unexpected error"
        every { authRepository.login(loginCredentials) } returns flow {
            throw RuntimeException(errorMessage)
        }

        // when
        val results = usecase(loginCredentials).toList()

        // then
        assertEquals(
            listOf(
                Result.Error(LoginErrorType.LoginFailed, errorMessage),
            ),
            results,
        )
    }
}
