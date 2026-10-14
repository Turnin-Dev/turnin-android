package com.turnin.domain.login.usecase

import com.turnin.core.domain.auth.model.LoginCredentials
import com.turnin.core.domain.auth.model.LoginResult
import com.turnin.core.domain.auth.repository.AuthRepository
import com.turnin.core.domain.common.Result
import com.turnin.core.domain.common.error.CommonErrorType
import com.turnin.core.domain.eventBus.AuthEventBus
import com.turnin.core.domain.model.ProviderId
import com.turnin.core.domain.model.SocialLoginProvider
import com.turnin.core.domain.model.UserId
import com.turnin.domain.login.error.LoginErrorType
import dev.mokkery.answering.returns
import dev.mokkery.every
import dev.mokkery.everySuspend
import dev.mokkery.mock
import dev.mokkery.verify.VerifyMode
import dev.mokkery.verifySuspend
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest

@OptIn(ExperimentalCoroutinesApi::class)
class LoginUseCaseTest {
    private val authRepository: AuthRepository = mock()
    private val authEventBus: AuthEventBus = mock()

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
        everySuspend { authEventBus.emitLogin() } returns Unit

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
        verifySuspend(VerifyMode.exactly(1)) { authEventBus.emitLogin() }
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
        verifySuspend(VerifyMode.exactly(0)) { authEventBus.emitLogin() }
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
        verifySuspend(VerifyMode.exactly(0)) { authEventBus.emitLogin() }
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
        verifySuspend(VerifyMode.exactly(0)) { authEventBus.emitLogin() }
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
