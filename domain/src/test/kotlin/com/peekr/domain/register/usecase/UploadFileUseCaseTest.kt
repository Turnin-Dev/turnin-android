package com.peekr.domain.register.usecase

import com.peekr.core.domain.file.FileRepository
import com.peekr.core.domain.file.model.Mime
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

class UploadFileUseCaseTest {
    private val repository: FileRepository = mockk()
    private val usecase = UploadFileUseCase(repository)

    @Test
    fun `파일을 업로드하고 파일의 url을 정상적으로 반환한다`() = runTest {
        // given
        every {
            repository.uploadFile(any(), any(), any(), any())
        } returns flowOf(Result.Success(TEST_FILE_URL))

        // when
        val result = usecase(TEST_PRESIGNED_URL, TestFile, TEST_FILE_NAME, TestMime).last()

        // then
        assertTrue(result is Result.Success)
        assertEquals(TEST_FILE_URL, (result as Result.Success).data)
    }

    @Test
    fun `파일 업로드 시 에러가 발생하면 정상적으로 에러를 반환한다`() = runTest {
        // given
        val expectedError = ErrorType.Network.ClientError
        every {
            repository.uploadFile(any(), any(), any(), any())
        } returns flowOf(Result.Error(expectedError))

        // when
        val result = usecase(TEST_PRESIGNED_URL, TestFile, TEST_FILE_NAME, TestMime).last()

        // then
        assertTrue(result is Result.Error)
        assertEquals(expectedError, (result as Result.Error).error)
    }

    companion object {
        private const val TEST_PRESIGNED_URL = "asd"
        private val TestFile = "123".toByteArray()
        private const val TEST_FILE_NAME = "test.jpg"
        private val TestMime = Mime.IMAGE_JPEG
        private const val TEST_FILE_URL = "https://example.com/test.jpg"
    }
}
