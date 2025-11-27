package com.peekr.core.data.repository

import com.peekr.core.data.source.network.datasource.FileNetworkDataSource
import com.peekr.core.data.source.network.dto.file.response.PresignedUrlResponse
import com.peekr.core.data.source.network.error.NetworkErrorType
import com.peekr.core.data.source.network.error.toCommonErrorType
import com.peekr.core.data.source.network.util.NetworkResult
import com.peekr.core.domain.common.Result
import com.peekr.core.domain.file.FileRepository
import com.peekr.core.domain.file.model.Mime
import com.peekr.core.domain.file.model.PresignedUrl
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.last
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Test

class FileRepositoryImplTest {
    private val dataSource: FileNetworkDataSource = mockk()

    @OptIn(ExperimentalCoroutinesApi::class)
    private val dispatcher = UnconfinedTestDispatcher()
    private val repository: FileRepository =
        FileRepositoryImpl(dataSource, dispatcher)

    @Test
    fun `getFileUploadPresignedUrl() 성공 테스트`() =
        runTest {
            // given
            coEvery {
                dataSource.getFileUploadPresignedUrl(any(), any())
            } returns NetworkResult.Success(mockPresignedUrlResponse)

            // when
            val result = repository.getFileUploadPresignedUrl("a.jpg", Mime.IMAGE_JPEG).last()

            // then
            Assert.assertTrue(result is Result.Success)
            Assert.assertEquals(mockPresignedUrl, (result as Result.Success).data)
        }

    @Test
    fun `getFileUploadPresignedUrl() 실패 테스트 - 데이터 소스에서 에러 방출 시 Error를 반환한다`() =
        runTest {
            // given
            val expected = NetworkErrorType.Network.Conflict
            coEvery {
                dataSource.getFileUploadPresignedUrl(any(), any())
            } returns NetworkResult.Error(error = expected, message = mockErrorMessage)

            // when
            val result = repository.getFileUploadPresignedUrl("a.jpg", Mime.IMAGE_JPEG).last()

            // then
            Assert.assertTrue(result is Result.Error)
            Assert.assertEquals(
                expected.toCommonErrorType(),
                (result as Result.Error).error,
            )
            Assert.assertEquals(result.message, mockErrorMessage)
        }

    companion object {
        private val mockErrorMessage = "error world!"
        private val mockPresignedUrlResponse = PresignedUrlResponse("example.com", "PUT", 600)
        private val mockPresignedUrl = PresignedUrl("example.com", "PUT", 600)
    }
}
