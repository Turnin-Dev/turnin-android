package com.peekr.domain.account.usecase.register

import com.peekr.domain.account.model.Mime
import com.peekr.domain.account.model.PresignedUrl
import com.peekr.domain.common.util.ErrorType
import com.peekr.domain.common.util.Result
import io.mockk.every
import io.mockk.mockk
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.last
import kotlinx.coroutines.test.runTest
import org.junit.Test

class GetFileUrlUseCaseTest {
    private val getFileUploadPresignedUrlUseCase: GetFileUploadPresignedUrlUseCase = mockk()
    private val uploadFileUseCase: UploadFileUseCase = mockk()
    private val usecase = GetFileUrlUseCase(getFileUploadPresignedUrlUseCase, uploadFileUseCase)

    @Test
    fun `파일 업로드 후 정상적으로 파일의 url을 반환한다`() = runTest {
        // given
        every {
            getFileUploadPresignedUrlUseCase(any(), any())
        } returns flowOf(Result.Success(TestPresignedUrl))
        every {
            uploadFileUseCase(any(), any(), any(), any())
        } returns flowOf(Result.Success(TEST_FILE_URL))

        // when
        val result = usecase(TestByteArray, "name", Mime.IMAGE_JPEG).last()

        // then
        assert(result is Result.Success)
        assertEquals(TEST_FILE_URL, (result as Result.Success).data)
    }

    @Test
    fun `파일 업로드 시 에러가 발생하면 정상적으로 에러를 반환한다`() = runTest {
        // given
        val expectedError = ErrorType.Exception.IO
        every {
            getFileUploadPresignedUrlUseCase(any(), any())
        } returns flowOf(Result.Success(TestPresignedUrl))
        every {
            uploadFileUseCase(any(), any(), any(), any())
        } returns flowOf(Result.Error(expectedError))

        // when
        val result = usecase(TestByteArray, "name", Mime.IMAGE_JPEG).last()

        // then
        assert(result is Result.Error)
        assertEquals(expectedError, (result as Result.Error).error)
    }

    @Test
    fun `파일 업로드 시 사전 정의된 url을 가져올 때 에러가 발생하면 정상적으로 에러를 반환한다`() = runTest {
        // given
        val expectedError = ErrorType.Exception.IO
        every {
            getFileUploadPresignedUrlUseCase(any(), any())
        } returns flowOf(Result.Error(expectedError))
        every {
            uploadFileUseCase(any(), any(), any(), any())
        } returns flowOf(Result.Success(TEST_FILE_URL))

        // when
        val result = usecase(TestByteArray, "name", Mime.IMAGE_JPEG).last()

        // then
        assert(result is Result.Error)
        assertEquals(expectedError, (result as Result.Error).error)
    }

    companion object {
        private val TestPresignedUrl = PresignedUrl("", "", 0)
        private const val TEST_FILE_URL = "file-url"
        private val TestByteArray = "123".toByteArray()
    }
}
