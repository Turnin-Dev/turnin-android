package com.peekr.domain.account.usecase

import com.peekr.domain.account.model.JWTToken
import com.peekr.domain.account.model.Login
import com.peekr.domain.account.model.SocialLoginProvider
import com.peekr.domain.account.model.UserUID
import com.peekr.domain.shared.util.ErrorType
import com.peekr.domain.shared.util.Result
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
    private val socialLoginUseCase: SocialLoginUseCase = mockk()
    private val loginUseCase: LoginUseCase = mockk()
    private val saveRefreshTokenUseCase: SaveRefreshTokenUseCase = mockk()

    @Before
    fun setUp() {
        loginIntegrationUseCase = LoginIntegrationUseCase(
            socialLoginUseCase,
            loginUseCase,
            saveRefreshTokenUseCase,
        )
    }

    @Test
    fun `소셜로그인, 로그인, 토큰 저장 UseCase가 전부 정상적으로 동작 시 true를 반환한다`() = runTest {
        // given
        every { socialLoginUseCase(any()) } returns flowOf(Result.Success(MockLogin))
        every { loginUseCase(any()) } returns flowOf(Result.Success(MockJwtToken))
        every { saveRefreshTokenUseCase(any()) } returns flowOf(Result.Success(true))

        // when
        val result = loginIntegrationUseCase(SocialLoginProvider.Google).last()

        // then
        assertTrue(result is Result.Success)
        assertTrue((result as Result.Success).data)

        verify { socialLoginUseCase(SocialLoginProvider.Google) }
        verify { loginUseCase(MockLogin) }
        verify { saveRefreshTokenUseCase(MockJwtToken.refreshToken) }
    }

    @Test
    fun `소셜로그인 UseCase에서 에러 발생 시 해당 에러를 반환하고 후속 UseCase는 실행되지 않는다`() = runTest {
        // given
        val expectedError = ErrorType.Auth.KakaoSignInError
        every { socialLoginUseCase(any()) } returns flowOf(Result.Error(error = expectedError))

        // when
        val result = loginIntegrationUseCase(SocialLoginProvider.Google).last()

        // then
        assertTrue(result is Result.Error)
        assertEquals(expectedError, (result as Result.Error).error)

        verify { socialLoginUseCase(SocialLoginProvider.Google) }
        verify(exactly = 0) { loginUseCase(any()) }
        verify(exactly = 0) { saveRefreshTokenUseCase(any()) }
    }

    @Test
    fun `로그인 UseCase에서 에러 발생 시 해당 에러를 반환하고 토큰 저장 UseCase는 실행되지 않는다`() = runTest {
        // given
        val expectedError = ErrorType.Auth.LoginFailed
        every { socialLoginUseCase(any()) } returns flowOf(Result.Success(MockLogin))
        every { loginUseCase(any()) } returns flowOf(Result.Error(error = expectedError))

        // when
        val result = loginIntegrationUseCase(SocialLoginProvider.Google).last()

        // then
        assertTrue(result is Result.Error)
        assertEquals(expectedError, (result as Result.Error).error)

        verify { socialLoginUseCase(SocialLoginProvider.Google) }
        verify { loginUseCase(MockLogin) }
        verify(exactly = 0) { saveRefreshTokenUseCase(any()) }
    }

    @Test
    fun `토큰 저장 UseCase에서 에러 발생 시 해당 에러를 반환한다`() = runTest {
        // given
        val expectedError = ErrorType.Auth.SaveTokenFailed
        every { socialLoginUseCase(any()) } returns flowOf(Result.Success(MockLogin))
        every { loginUseCase(any()) } returns flowOf(Result.Success(MockJwtToken))
        every { saveRefreshTokenUseCase(any()) } returns flowOf(Result.Error(error = expectedError))

        // when
        val result = loginIntegrationUseCase(SocialLoginProvider.Google).last()

        // then
        assertTrue(result is Result.Error)
        assertEquals(expectedError, (result as Result.Error).error)

        verify { socialLoginUseCase(SocialLoginProvider.Google) }
        verify { loginUseCase(MockLogin) }
        verify { saveRefreshTokenUseCase(MockJwtToken.refreshToken) }
    }

    @Test
    fun `소셜로그인 UseCase에서 Loading 상태 방출 시 Loading을 반환한다`() = runTest {
        // given
        every { socialLoginUseCase(any()) } returns flowOf(Result.Loading)

        // when
        val result = loginIntegrationUseCase(SocialLoginProvider.Google).last()

        // then
        assertTrue(result is Result.Loading)

        verify { socialLoginUseCase(SocialLoginProvider.Google) }
        verify(exactly = 0) { loginUseCase(any()) }
        verify(exactly = 0) { saveRefreshTokenUseCase(any()) }
    }

    @Test
    fun `예외 발생 시 catch에서 LoginFailed 에러를 반환한다`() = runTest {
        // given - Flow 내에서 예외 발생
        every { socialLoginUseCase(any()) } returns flow {
            throw RuntimeException("예상치 못한 에러")
        }

        // when
        val result = loginIntegrationUseCase(SocialLoginProvider.Google).last()

        // then
        assertTrue(result is Result.Error)
        assertEquals(ErrorType.Auth.LoginFailed, (result as Result.Error).error)

        verify { socialLoginUseCase(SocialLoginProvider.Google) }
    }

    @Test
    fun `Flow가 여러 값을 방출할 때 마지막 값이 최종 결과가 된다`() = runTest {
        // given
        every { socialLoginUseCase(any()) } returns flow {
            emit(Result.Loading)
            delay(10)
            emit(Result.Success(MockLogin))
        }
        every { loginUseCase(any()) } returns flowOf(Result.Success(MockJwtToken))
        every { saveRefreshTokenUseCase(any()) } returns flowOf(Result.Success(true))

        // when
        val results = loginIntegrationUseCase(SocialLoginProvider.Google).toList()

        // then
        assertEquals(2, results.size)
        assertTrue(results[0] is Result.Loading)
        assertTrue(results[1] is Result.Success)
        assertTrue((results[1] as Result.Success).data)
    }

    companion object {
        private val MockLogin = Login(SocialLoginProvider.Google, UserUID("123"))
        private val MockJwtToken = JWTToken("aaa.bbb.ccc", "rrr.bbb.ccc")
    }
}
