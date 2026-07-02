package com.turnin.domain.register.usecase

import com.turnin.core.domain.common.Result
import com.turnin.core.domain.common.error.CommonErrorType
import com.turnin.core.domain.file.FileRepository
import com.turnin.core.domain.file.model.FileCategory
import com.turnin.core.domain.file.model.Mime
import com.turnin.core.domain.file.model.PresignedUrl
import com.turnin.domain.register.error.RegisterErrorType
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.last
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

class GetFileUploadPresignedUrlUseCaseTest {
    private val repository: FileRepository = mockk()
    private val usecase = GetFileUploadPresignedUrlUseCase(repository)

    @Test
    fun `사전 정의된 url 요청 성공 테스트`() = runTest {
        // given
        every {
            repository.getFileUploadPresignedUrl(any(), any(), any())
        } returns flowOf(Result.Success(TestPresignedUrl))

        // when
        val result = usecase("fileName", Mime.IMAGE_JPEG, FileCategory.PROFILE_IMAGE).last()

        // then
        val success = (result as Result.Success)
        assertEquals(TestPresignedUrl, success.data)
    }

    @Test
    fun `사전 정의된 url 요청 시 에러가 발생하면 정상적으로 에러를 반환한다`() = runTest {
        // given
        val expectedError = CommonErrorType.Unexpected(null)
        every {
            repository.getFileUploadPresignedUrl(any(), any(), any())
        } returns flowOf(Result.Error(expectedError))

        // when
        val result = usecase("fileName", Mime.IMAGE_JPEG, FileCategory.PROFILE_IMAGE).last()

        // then
        val errorResult = result as Result.Error
        assertEquals(
            RegisterErrorType.CommonError(expectedError),
            errorResult.error,
        )
    }

    companion object {
        private val TestPresignedUrl = PresignedUrl("a", "b", 0)
    }
}
