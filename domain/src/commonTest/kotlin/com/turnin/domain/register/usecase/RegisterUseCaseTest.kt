package com.turnin.domain.register.usecase

import com.turnin.core.domain.auth.model.JWTToken
import com.turnin.core.domain.auth.model.Register
import com.turnin.core.domain.auth.model.RegisterResult
import com.turnin.core.domain.auth.repository.AuthRepository
import com.turnin.core.domain.common.Result
import com.turnin.core.domain.common.error.CommonErrorType
import com.turnin.core.domain.file.FileRepository
import com.turnin.core.domain.file.model.ImageFileDetail
import com.turnin.core.domain.file.model.Mime
import com.turnin.core.domain.file.model.PresignedUrl
import com.turnin.core.domain.model.DisplayId
import com.turnin.core.domain.model.Introduce
import com.turnin.core.domain.model.Name
import com.turnin.core.domain.model.ProviderId
import com.turnin.core.domain.model.SocialLoginProvider
import com.turnin.core.domain.model.UserId
import com.turnin.domain.register.error.RegisterErrorType
import dev.mokkery.answering.returns
import dev.mokkery.every
import dev.mokkery.matcher.any
import dev.mokkery.mock
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.last
import kotlinx.coroutines.test.runTest

class RegisterUseCaseTest {
    private val authRepository: AuthRepository = mock()
    private val fileRepository: FileRepository = mock()
    private val usecase = RegisterUseCase(
        authRepository = authRepository,
        getFileUrlUseCase = GetFileUrlUseCase(
            getFileUploadPresignedUrlUseCase = GetFileUploadPresignedUrlUseCase(fileRepository),
            uploadFileUseCase = UploadFileUseCase(fileRepository),
        ),
    )

    private fun stubFileUpload(uploadResult: Result<String?, CommonErrorType>) {
        every {
            fileRepository.getFileUploadPresignedUrl(any(), any(), any())
        } returns flowOf(Result.Success(TestPresignedUrl))
        every {
            fileRepository.uploadFile(any(), any(), any(), any(), any())
        } returns flowOf(uploadResult)
    }

    @Test
    fun `업로드한 파일 url로 회원가입을 정상적으로 진행한다`() = runTest {
        // given
        every {
            authRepository.register(TestRegister)
        } returns flowOf(Result.Success(TestRegisterResult))
        stubFileUpload(Result.Success(TEST_FILE_URL))

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
        stubFileUpload(Result.Success(TEST_FILE_URL))

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
        val commonError = CommonErrorType.Unexpected(null)
        every {
            authRepository.register(TestRegister)
        } returns flowOf(Result.Success(TestRegisterResult))
        stubFileUpload(Result.Error(commonError))

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
        assertEquals(RegisterErrorType.CommonError(commonError), (result as Result.Error).error)
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
        private val TestPresignedUrl = PresignedUrl("", "", 0)
        private val TestImageFileDetail = ImageFileDetail.create("123".encodeToByteArray(), "", Mime.IMAGE_JPEG)
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
