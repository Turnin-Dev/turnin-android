package com.peekr.domain.register.usecase

import com.peekr.core.domain.auth.SaveRefreshTokenUseCase
import com.peekr.core.domain.auth.model.JWTToken
import com.peekr.core.domain.auth.model.RegisterResult
import com.peekr.core.domain.model.DisplayId
import com.peekr.core.domain.model.Introduce
import com.peekr.core.domain.model.Name
import com.peekr.core.domain.model.ProviderId
import com.peekr.core.domain.model.SocialLoginProvider
import com.peekr.core.domain.model.UserId
import com.peekr.core.domain.util.ErrorType
import com.peekr.core.domain.util.Result
import io.mockk.every
import io.mockk.mockk
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.last
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertTrue
import org.junit.Test

class RegisterIntegrationUseCaseTest {
    private val registerUseCase: RegisterUseCase = mockk()
    private val saveRefreshTokenUseCase: SaveRefreshTokenUseCase = mockk()
    private val usecase = RegisterIntegrationUseCase(registerUseCase, saveRefreshTokenUseCase)

    @Test
    fun `회원가입 성공 테스트`() = runTest {
        // given
        every {
            registerUseCase(TestProvider, TestProviderId, TestDisplayId, TestName, null, TestIntroduce)
        } returns flowOf(Result.Success(TestRegisterResult))
        every {
            saveRefreshTokenUseCase(any())
        } returns flowOf(Result.Success(true))

        // when
        val result = usecase(
            provider = TestProvider,
            providerId = TestProviderId.uid,
            displayId = TestDisplayId.value,
            name = TestName.value,
            imageFileDetail = null,
            introduce = TestIntroduce.value,
        ).last()

        // then
        assertTrue(result is Result.Success)
        assertTrue((result as Result.Success).data)
    }

    @Test
    fun `회원가입시 register 유스케이스에서 에러 발생 시 정상적으로 에러를 반환한다`() = runTest {
        // given
        val expectedError = ErrorType.Exception.IO
        every {
            registerUseCase(TestProvider, TestProviderId, TestDisplayId, TestName, null, TestIntroduce)
        } returns flowOf(Result.Success(TestRegisterResult))
        every {
            saveRefreshTokenUseCase(any())
        } returns flowOf(Result.Error(expectedError))

        // when
        val result = usecase(
            provider = TestProvider,
            providerId = TestProviderId.uid,
            displayId = TestDisplayId.value,
            name = TestName.value,
            imageFileDetail = null,
            introduce = TestIntroduce.value,
        ).last()

        // then
        assertTrue(result is Result.Error)
        assertEquals(expectedError, (result as Result.Error).error)
    }

    @Test
    fun `회원가입시 리프레쉬 토큰을 저장하는 과정에서 에러 발생 시 정상적으로 에러를 반환한다`() = runTest {
        // given
        val expectedError = ErrorType.Exception.IO
        every {
            registerUseCase(TestProvider, TestProviderId, TestDisplayId, TestName, null, TestIntroduce)
        } returns flowOf(Result.Error(expectedError))
        every {
            saveRefreshTokenUseCase(any())
        } returns flowOf(Result.Success(true))

        // when
        val result = usecase(
            provider = TestProvider,
            providerId = TestProviderId.uid,
            displayId = TestDisplayId.value,
            name = TestName.value,
            imageFileDetail = null,
            introduce = TestIntroduce.value,
        ).last()

        // then
        assertTrue(result is Result.Error)
        assertEquals(expectedError, (result as Result.Error).error)
    }

    @Test
    fun `회원가입시 VO 객체 유효성 검사 과정에서 실패 시 정상적으로 에러를 반환한다`() = runTest {
        // given
        every {
            registerUseCase(TestProvider, TestProviderId, TestDisplayId, TestName, null, TestIntroduce)
        } returns flowOf(Result.Success(TestRegisterResult))
        every {
            saveRefreshTokenUseCase(any())
        } returns flowOf(Result.Success(true))

        // when
        val result = usecase(
            provider = TestProvider,
            providerId = TestProviderId.uid,
            displayId = "!!!",
            name = TestName.value,
            imageFileDetail = null,
            introduce = TestIntroduce.value,
        ).last()

        // then
        assertTrue(result is Result.Error)
        assertTrue((result as Result.Error).error is ErrorType.Unexpected)
    }

    companion object {
        private val TestUserId = UserId(1L)
        private val TestProvider = SocialLoginProvider.GOOGLE
        private val TestProviderId = ProviderId("google123")
        private val TestDisplayId = DisplayId("abc")
        private val TestName = Name("honggd")
        private val TestIntroduce = Introduce("hello!")
        private val TestJWTToken = JWTToken("a", "b")
        private val TestRegisterResult =
            RegisterResult(TestUserId, TestJWTToken.accessToken, TestJWTToken.refreshToken)
    }
}
