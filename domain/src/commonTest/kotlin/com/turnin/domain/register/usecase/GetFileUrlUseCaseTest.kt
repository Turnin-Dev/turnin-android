package com.turnin.domain.register.usecase

import com.turnin.core.domain.common.Result
import com.turnin.core.domain.common.error.CommonErrorType
import com.turnin.core.domain.file.FileRepository
import com.turnin.core.domain.file.model.FileCategory
import com.turnin.core.domain.file.model.Mime
import com.turnin.core.domain.file.model.PresignedUrl
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

class GetFileUrlUseCaseTest {
    private val fileRepository: FileRepository = mock()
    private val usecase = GetFileUrlUseCase(
        getFileUploadPresignedUrlUseCase = GetFileUploadPresignedUrlUseCase(fileRepository),
        uploadFileUseCase = UploadFileUseCase(fileRepository),
    )

    @Test
    fun `파일 업로드 후 정상적으로 파일의 url을 반환한다`() = runTest {
        // given
        every {
            fileRepository.getFileUploadPresignedUrl(any(), any(), any())
        } returns flowOf(Result.Success(TestPresignedUrl))
        every {
            fileRepository.uploadFile(any(), any(), any(), any(), any())
        } returns flowOf(Result.Success(TEST_FILE_URL))

        // when
        val result = usecase(TestByteArray, "name", Mime.IMAGE_JPEG, FileCategory.PROFILE_IMAGE).last()

        // then
        assertTrue(result is Result.Success)
        assertEquals(TEST_FILE_URL, (result as Result.Success).data)
    }

    @Test
    fun `파일 업로드 시 에러가 발생하면 정상적으로 에러를 반환한다`() = runTest {
        // given
        val commonError = CommonErrorType.Unexpected(null)
        every {
            fileRepository.getFileUploadPresignedUrl(any(), any(), any())
        } returns flowOf(Result.Success(TestPresignedUrl))
        every {
            fileRepository.uploadFile(any(), any(), any(), any(), any())
        } returns flowOf(Result.Error(commonError))

        // when
        val result = usecase(TestByteArray, "name", Mime.IMAGE_JPEG, FileCategory.PROFILE_IMAGE).last()

        // then
        assertTrue(result is Result.Error)
        assertEquals(RegisterErrorType.CommonError(commonError), (result as Result.Error).error)
    }

    @Test
    fun `파일 업로드 시 사전 정의된 url을 가져올 때 에러가 발생하면 정상적으로 에러를 반환한다`() = runTest {
        // given
        val commonError = CommonErrorType.Unexpected(null)
        every {
            fileRepository.getFileUploadPresignedUrl(any(), any(), any())
        } returns flowOf(Result.Error(commonError))
        every {
            fileRepository.uploadFile(any(), any(), any(), any(), any())
        } returns flowOf(Result.Success(TEST_FILE_URL))

        // when
        val result = usecase(TestByteArray, "name", Mime.IMAGE_JPEG, FileCategory.PROFILE_IMAGE).last()

        // then
        assertTrue(result is Result.Error)
        assertEquals(RegisterErrorType.CommonError(commonError), (result as Result.Error).error)
    }

    companion object {
        private val TestPresignedUrl = PresignedUrl("", "", 0)
        private const val TEST_FILE_URL = "file-url"
        private val TestByteArray = "123".encodeToByteArray()
    }
}
