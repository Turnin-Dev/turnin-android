package com.peekr.domain.account.usecase

import com.peekr.domain.account.model.Login
import com.peekr.domain.account.model.LoginWithExistsUser
import com.peekr.domain.account.model.SocialLoginProvider
import com.peekr.domain.account.model.UserUID
import com.peekr.domain.account.repository.AccountRepository
import com.peekr.domain.account.usecase.login.GetLoginIfUserExistsUseCase
import com.peekr.domain.account.usecase.login.SocialLoginUseCase
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

class GetLoginIfUserExistsUseCaseTest {
    private lateinit var getLoginIfUserExistsUseCase: GetLoginIfUserExistsUseCase
    private val socialLoginUseCase: SocialLoginUseCase = mockk()
    private val accountRepository: AccountRepository = mockk()

    @Before
    fun setUp() {
        getLoginIfUserExistsUseCase =
            GetLoginIfUserExistsUseCase(socialLoginUseCase, accountRepository)
    }

    @Test
    fun `정상적으로 작동하는 경우 LoginWithExistsUser를 반환한다`() = runTest {
        // given
        every { socialLoginUseCase(any()) } returns flowOf(Result.Success(MockLogin))
        every { accountRepository.existsUser(any()) } returns flowOf(Result.Success(true))

        // when
        val result = getLoginIfUserExistsUseCase(SocialLoginProvider.GOOGLE).last()

        // then
        assertTrue(result is Result.Success)
        assertEquals((result as Result.Success).data, LoginWithExistsUser(MockLogin, true))

        verify { socialLoginUseCase(any()) }
        verify { accountRepository.existsUser(any()) }
    }

    @Test
    fun `UseCase에서 에러 발생 시 해당 에러를 반환하고 후속 UseCase는 실행되지 않는다`() = runTest {
        // given
        val expectedError = ErrorType.Auth.Cancellation
        every { socialLoginUseCase(any()) } returns flowOf(Result.Error(error = expectedError))

        // when
        val result = getLoginIfUserExistsUseCase(SocialLoginProvider.GOOGLE).last()

        // then
        assertTrue(result is Result.Error)
        assertEquals(expectedError, (result as Result.Error).error)

        verify(exactly = 0) { accountRepository.existsUser(any()) }
    }

    @Test
    fun `예외 발생 시 catch에서 LoginFailed 에러를 반환한다`() = runTest {
        // given
        every { socialLoginUseCase(any()) } returns flow {
            throw RuntimeException()
        }

        // when
        val result = getLoginIfUserExistsUseCase(SocialLoginProvider.GOOGLE).last()

        // then
        assertTrue(result is Result.Error)
        assertEquals(ErrorType.Auth.LoginFailed, (result as Result.Error).error)
    }

    @Test
    fun `Flow가 여러 값을 방출할 때 마지막 값이 최종 결과가 된다`() = runTest {
        // given
        val expectedLoginWithExistsUser = LoginWithExistsUser(MockLogin, true)
        every { socialLoginUseCase(any()) } returns flow {
            emit(Result.Loading)
            delay(10)
            emit(Result.Success(MockLogin))
        }
        every { accountRepository.existsUser(any()) } returns flowOf(Result.Success(true))

        // when
        val results = getLoginIfUserExistsUseCase(SocialLoginProvider.GOOGLE).toList()

        // then
        assertEquals(3, results.size)
        assertTrue(results[0] is Result.Loading)
        assertTrue(results[1] is Result.Loading) // .onStart 추가했기 때문에
        assertTrue(results[2] is Result.Success)
        assertEquals(expectedLoginWithExistsUser, (results[2] as Result.Success).data)
    }

    companion object {
        private val MockLogin = Login(SocialLoginProvider.GOOGLE, UserUID("123"))
    }
}
