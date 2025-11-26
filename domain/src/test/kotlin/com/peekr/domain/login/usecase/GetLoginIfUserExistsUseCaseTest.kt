package com.peekr.domain.login.usecase

import com.peekr.core.domain.auth.model.LoginCredentials
import com.peekr.core.domain.auth.repository.AuthRepository
import com.peekr.core.domain.common.Result
import com.peekr.core.domain.model.ProviderId
import com.peekr.core.domain.model.SocialLoginProvider
import com.peekr.domain.login.error.LoginErrorType
import com.peekr.domain.login.model.LoginWithExistsUser
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

class GetLoginIfUserExistsUseCaseTest {
    private lateinit var getExistingLoginCredentialsUseCase: GetExistingLoginCredentialsUseCase
    private val socialLoginUseCase: SocialLoginUseCase = mockk()
    private val authRepository: AuthRepository = mockk()

    @Before
    fun setUp() {
        getExistingLoginCredentialsUseCase =
            GetExistingLoginCredentialsUseCase(socialLoginUseCase, authRepository)
    }

    @Test
    fun `정상적으로 작동하는 경우 LoginWithExistsUser를 반환한다`() = runTest {
        // given
        every { socialLoginUseCase(any()) } returns flowOf(Result.Success(MockLoginCredentials))
        every { authRepository.existsUser(any()) } returns flowOf(Result.Success(true))

        // when
        val result = getExistingLoginCredentialsUseCase(SocialLoginProvider.GOOGLE).last()

        // then
        assertTrue(result is Result.Success)
        assertEquals(LoginWithExistsUser(MockLoginCredentials, true), (result as Result.Success).data)

        verify { socialLoginUseCase(any()) }
        verify { authRepository.existsUser(any()) }
    }

    @Test
    fun `UseCase에서 에러 발생 시 해당 에러를 반환하고 후속 UseCase는 실행되지 않는다`() = runTest {
        // given
        val expectedError = LoginErrorType.Unexpected(null)
        every { socialLoginUseCase(any()) } returns flowOf(Result.Error(error = expectedError))

        // when
        val result = getExistingLoginCredentialsUseCase(SocialLoginProvider.GOOGLE).last()

        // then
        assertTrue(result is Result.Error)
        assertEquals(expectedError, (result as Result.Error).error)

        verify(exactly = 0) { authRepository.existsUser(any()) }
    }

    @Test
    fun `예외 발생 시 catch에서 LoginFailed 에러를 반환한다`() = runTest {
        // given
        every { socialLoginUseCase(any()) } returns flow {
            throw RuntimeException()
        }

        // when
        val result = getExistingLoginCredentialsUseCase(SocialLoginProvider.GOOGLE).last()

        // then
        assertTrue(result is Result.Error)
        assertEquals(LoginErrorType.LoginFailed, (result as Result.Error).error)
    }

    @Test
    fun `Flow가 여러 값을 방출할 때 마지막 값이 최종 결과가 된다`() = runTest {
        // given
        val expectedLoginWithExistsUser = LoginWithExistsUser(MockLoginCredentials, true)
        every { socialLoginUseCase(any()) } returns flow {
            emit(Result.Loading)
            delay(10)
            emit(Result.Success(MockLoginCredentials))
        }
        every { authRepository.existsUser(any()) } returns flowOf(Result.Success(true))

        // when
        val results = getExistingLoginCredentialsUseCase(SocialLoginProvider.GOOGLE).toList()

        // then
        // 최소 1회 이상의 Loading 방출과 최종 Success 만 검증
        assertTrue(results.any { it is Result.Loading })
        val last = results.last()
        assertTrue(last is Result.Success)
        assertEquals(expectedLoginWithExistsUser, (last as Result.Success).data)
    }

    companion object {
        private val MockLoginCredentials = LoginCredentials(SocialLoginProvider.GOOGLE, ProviderId("123"))
    }
}
