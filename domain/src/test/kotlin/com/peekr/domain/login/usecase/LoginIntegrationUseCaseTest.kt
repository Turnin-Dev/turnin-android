package com.peekr.domain.login.usecase

import com.peekr.core.domain.auth.SaveRefreshTokenUseCase
import com.peekr.core.domain.auth.model.JWTToken
import com.peekr.core.domain.auth.model.Login
import com.peekr.core.domain.auth.model.LoginResult
import com.peekr.core.domain.model.ProviderId
import com.peekr.core.domain.model.SocialLoginProvider
import com.peekr.core.domain.model.UserId
import com.peekr.core.domain.util.ErrorType
import com.peekr.core.domain.util.Result
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.last
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class LoginIntegrationUseCaseTest {
    private lateinit var loginIntegrationUseCase: LoginIntegrationUseCase
    private val loginUseCase: LoginUseCase = mockk()
    private val saveRefreshTokenUseCase: SaveRefreshTokenUseCase = mockk()

    @Before
    fun setUp() {
        loginIntegrationUseCase = LoginIntegrationUseCase(
            loginUseCase,
            saveRefreshTokenUseCase,
        )
    }

    @Test
    fun `로그인, 토큰 저장 UseCase가 전부 정상적으로 동작 시 true를 반환한다`() = runTest {
        // given
        every { loginUseCase(any()) } returns flowOf(Result.Success(MockLoginResult))
        every { saveRefreshTokenUseCase(any()) } returns flowOf(Result.Success(true))

        // when
        val result = loginIntegrationUseCase(MockLogin).last()

        // then
        assertTrue(result is Result.Success)
        assertTrue((result as Result.Success).data)

        verify { loginUseCase(MockLogin) }
        verify { saveRefreshTokenUseCase(MockJwtToken.refreshToken) }
    }

    @Test
    fun `로그인 UseCase에서 에러 발생 시 해당 에러를 반환하고 후속 UseCase는 실행되지 않는다`() = runTest {
        // given
        val expectedError = ErrorType.Auth.KakaoSignInError
        every { loginUseCase(any()) } returns flowOf(Result.Error(error = expectedError))

        // when
        val result = loginIntegrationUseCase(MockLogin).last()

        // then
        assertTrue(result is Result.Error)
        assertEquals(expectedError, (result as Result.Error).error)

        verify(exactly = 0) { saveRefreshTokenUseCase(any()) }
    }

    @Test
    fun `로그인 UseCase에서 에러 발생 시 해당 에러를 반환하고 토큰 저장 UseCase는 실행되지 않는다`() = runTest {
        // given
        val expectedError = ErrorType.Auth.LoginFailed
        every { loginUseCase(any()) } returns flowOf(Result.Error(error = expectedError))

        // when
        val result = loginIntegrationUseCase(MockLogin).last()

        // then
        assertTrue(result is Result.Error)
        assertEquals(expectedError, (result as Result.Error).error)

        verify { loginUseCase(MockLogin) }
        verify(exactly = 0) { saveRefreshTokenUseCase(any()) }
    }

    @Test
    fun `토큰 저장 UseCase에서 에러 발생 시 해당 에러를 반환한다`() = runTest {
        // given
        val expectedError = ErrorType.Auth.SaveTokenFailed
        every { loginUseCase(any()) } returns flowOf(Result.Success(MockLoginResult))
        every { saveRefreshTokenUseCase(any()) } returns flowOf(Result.Error(error = expectedError))

        // when
        val result = loginIntegrationUseCase(MockLogin).last()

        // then
        assertTrue(result is Result.Error)
        assertEquals(expectedError, (result as Result.Error).error)

        verify { loginUseCase(MockLogin) }
        verify { saveRefreshTokenUseCase(MockJwtToken.refreshToken) }
    }

    @Test
    fun `예외 발생 시 catch에서 LoginFailed 에러를 반환한다`() = runTest {
        // given - Flow 내에서 예외 발생
        every { loginUseCase(any()) } returns flow {
            throw RuntimeException("예상치 못한 에러")
        }

        // when
        val result = loginIntegrationUseCase(MockLogin).last()

        // then
        assertTrue(result is Result.Error)
        assertEquals(ErrorType.Auth.LoginFailed, (result as Result.Error).error)

        verify { loginUseCase(MockLogin) }
    }

    @Test
    fun `Flow가 여러 값을 방출할 때 마지막 값이 최종 결과가 된다`() = runTest {
        // given
        every { loginUseCase(any()) } returns flow {
            emit(Result.Loading)
            delay(10)
            emit(Result.Success(MockLoginResult))
        }
        every { saveRefreshTokenUseCase(any()) } returns flowOf(Result.Success(true))

        // when
        val results = loginIntegrationUseCase(MockLogin).toList()

        // then
        assertTrue(results.first() is Result.Loading)
        assertTrue(results.last() is Result.Success)
        assertTrue((results.last() as Result.Success).data)
    }

    companion object {
        internal val MockLogin = Login(SocialLoginProvider.GOOGLE, ProviderId("123"))
        private val MockUserId = UserId(1L)
        private val MockJwtToken = JWTToken("aaa.bbb.ccc", "rrr.bbb.ccc")
        private val MockLoginResult =
            LoginResult(MockUserId, MockJwtToken.accessToken, MockJwtToken.refreshToken)
    }
}
