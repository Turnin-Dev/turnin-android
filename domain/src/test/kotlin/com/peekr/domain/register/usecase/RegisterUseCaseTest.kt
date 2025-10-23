package com.peekr.domain.register.usecase

import com.peekr.core.domain.auth.model.JWTToken
import com.peekr.core.domain.auth.model.Register
import com.peekr.core.domain.auth.model.RegisterResult
import com.peekr.core.domain.auth.repository.AuthRepository
import com.peekr.core.domain.file.model.Mime
import com.peekr.core.domain.model.DisplayId
import com.peekr.core.domain.model.Introduce
import com.peekr.core.domain.model.Name
import com.peekr.core.domain.model.ProviderId
import com.peekr.core.domain.model.SocialLoginProvider
import com.peekr.core.domain.model.UserId
import com.peekr.core.domain.util.Result
import com.peekr.domain.register.error.RegisterErrorType
import com.peekr.domain.register.model.ImageFileDetail
import io.mockk.every
import io.mockk.mockk
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.last
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertTrue
import org.junit.Test

class RegisterUseCaseTest {
    private val authRepository: AuthRepository = mockk()
    private val getFileUrlUseCase: GetFileUrlUseCase = mockk()
    private val usecase = RegisterUseCase(authRepository, getFileUrlUseCase)

    @Test
    fun `업로드한 파일 url로 회원가입을 정상적으로 진행한다`() = runTest {
        // given
        every {
            authRepository.register(TestRegister)
        } returns flowOf(Result.Success(TestRegisterResult))
        every {
            getFileUrlUseCase(any(), any(), any())
        } returns flowOf(Result.Success(TEST_FILE_URL))

        // when
        val result = usecase(
            provider = TestProvider,
            providerId = TestProviderId,
            displayId = TestDisplayId,
            name = TestName,
            imageFileDetail = TestImageFileDetail,
            introduce = TestIntroduce,
        ).last()

        // then
        assertTrue(result is Result.Success)
        assertEquals(TestRegisterResult, (result as Result.Success).data)
    }

    @Test
    fun `업로드한 파일 url이 null이어도 회원가입을 정상적으로 진행한다`() = runTest {
        // given
        every {
            authRepository.register(TestRegisterWithNullFile)
        } returns flowOf(Result.Success(TestRegisterResult))
        every {
            getFileUrlUseCase(any(), any(), any())
        } returns flowOf(Result.Success(TEST_FILE_URL))

        // when
        val result = usecase(
            provider = TestProvider,
            providerId = TestProviderId,
            displayId = TestDisplayId,
            name = TestName,
            imageFileDetail = null,
            introduce = TestIntroduce,
        ).last()

        // then
        assertTrue(result is Result.Success)
        assertEquals(TestRegisterResult, (result as Result.Success).data)
    }

    @Test
    fun `업로드한 파일 url을 받아올 때 에러 발생 시 정상적으로 에러를 반환한다`() = runTest {
        // given
        val expectedError = RegisterErrorType.Unexpected(null)
        every {
            authRepository.register(TestRegister)
        } returns flowOf(Result.Success(TestRegisterResult))
        every {
            getFileUrlUseCase(any(), any(), any())
        } returns flowOf(Result.Error(expectedError))

        // when
        val result = usecase(
            provider = TestProvider,
            providerId = TestProviderId,
            displayId = TestDisplayId,
            name = TestName,
            imageFileDetail = TestImageFileDetail,
            introduce = TestIntroduce,
        ).last()

        // then
        assertTrue(result is Result.Error)
        assertEquals(expectedError, (result as Result.Error).error)
    }

    companion object {
        private val TestUserId = UserId(1L)
        private val TestProvider = SocialLoginProvider.GOOGLE
        private val TestProviderId = ProviderId("google123")
        private val TestDisplayId = DisplayId("abc")
        private val TestName = Name("honggd")
        private val TestIntroduce = Introduce("hello!")
        private val TestJWTToken = JWTToken("a", "b")
        private const val TEST_FILE_URL = "https://example.com/test.jpg"
        private val TestImageFileDetail = ImageFileDetail("123".toByteArray(), "", Mime.IMAGE_JPEG)
        private val TestRegister = Register(
            provider = TestProvider,
            providerId = TestProviderId,
            displayId = TestDisplayId,
            name = TestName,
            profileImageUrl = TEST_FILE_URL,
            introduce = TestIntroduce,
        )
        private val TestRegisterWithNullFile = Register(
            provider = TestProvider,
            providerId = TestProviderId,
            displayId = TestDisplayId,
            name = TestName,
            profileImageUrl = null,
            introduce = TestIntroduce,
        )
        private val TestRegisterResult =
            RegisterResult(TestUserId, TestJWTToken.accessToken, TestJWTToken.refreshToken)
    }
}
