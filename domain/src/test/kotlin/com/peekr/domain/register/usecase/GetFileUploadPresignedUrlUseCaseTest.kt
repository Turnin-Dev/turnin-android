package com.peekr.domain.register.usecase

import com.peekr.core.domain.file.FileErrorType
import com.peekr.core.domain.file.FileRepository
import com.peekr.core.domain.file.model.Mime
import com.peekr.core.domain.file.model.PresignedUrl
import com.peekr.core.domain.util.Result
import com.peekr.domain.register.error.RegisterErrorType
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.last
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class GetFileUploadPresignedUrlUseCaseTest {
    private val repository: FileRepository = mockk()
    private val usecase = GetFileUploadPresignedUrlUseCase(repository)

    @Test
    fun `사전 정의된 url 요청 성공 테스트`() = runTest {
        // given
        every {
            repository.getFileUploadPresignedUrl(any(), any())
        } returns flowOf(Result.Success(TestPresignedUrl))

        // when
        val result = usecase("fileName", Mime.IMAGE_JPEG).last()

        // then
        assertTrue(result is Result.Success)
        assertEquals(TestPresignedUrl, (result as Result.Success).data)
    }

    @Test
    fun `사전 정의된 url 요청 시 에러가 발생하면 정상적으로 에러를 반환한다`() = runTest {
        // given
        val expectedError = FileErrorType.Unexpected(null)
        every {
            repository.getFileUploadPresignedUrl(any(), any())
        } returns flowOf(Result.Error(expectedError))

        // when
        val result = usecase("fileName", Mime.IMAGE_JPEG).last()

        // then
        assertTrue(result is Result.Error)
        assertEquals(
            RegisterErrorType.Unexpected(null),
            (result as Result.Error).error,
        )
    }

    companion object {
        private val TestPresignedUrl = PresignedUrl("a", "b", 0)
    }
}
