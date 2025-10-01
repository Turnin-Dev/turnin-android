package com.peekr.core.data.file.network

import com.peekr.core.common.logger.AppLogger
import com.peekr.core.data.ServerTestRule
import com.peekr.core.data.file.network.response.PresignedUrlResponse
import com.peekr.core.data.network.util.NetworkErrorType
import com.peekr.core.data.network.util.NetworkResult
import com.squareup.moshi.JsonDataException
import io.mockk.coEvery
import io.mockk.mockk
import io.mockk.verify
import java.net.HttpURLConnection
import kotlinx.coroutines.test.runTest
import okhttp3.OkHttpClient
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.RecordedRequest
import org.junit.Assert.assertArrayEquals
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class FileNetworkDataSourceImplTest {
    @get:Rule
    val testRule = ServerTestRule()
    private val fileApi: FileApi
        get() = testRule.createNetworkApi<FileApi>(testRule.moshi)
    private lateinit var dataSource: FileDataSource
    private lateinit var testOkHttpClient: OkHttpClient

    @Before
    fun setUp() {
        testOkHttpClient = OkHttpClient.Builder().build()
        dataSource = FileNetworkDataSource(fileApi, testOkHttpClient)
    }

    @Test
    fun `getFileUploadPresignedUrl() 성공 테스트`() = runTest {
        // given
        testRule.server.enqueue(
            MockResponse().apply {
                setResponseCode(200)
                setBody(mockPresignedResponseJson)
            },
        )

        // when
        val result = dataSource.getFileUploadPresignedUrl("my-image.jpg", "image/jpeg")

        // then
        assertTrue(result is NetworkResult.Success)
        assertEquals(
            mockPresignUrlResponse,
            (result as NetworkResult.Success).data,
        )
    }

    @Test
    fun `getFileUploadPresignedUrl() 실패 테스트 (정의된 API 예외 발생)`() = runTest {
        // given
        val mockApi: FileApi = mockk()
        dataSource = FileNetworkDataSource(mockApi, testOkHttpClient)
        coEvery { mockApi.getFileUploadPresignedUrl(any(), any()) } throws JsonDataException("smile")

        // when
        val result = dataSource.getFileUploadPresignedUrl("asd", "asd")

        // then
        assertTrue(result is NetworkResult.Error)
        assertEquals((result as NetworkResult.Error).error, NetworkErrorType.Exception.JsonData)
    }

    @Test
    fun `uploadFile() 성공 테스트`() = runTest {
        // given
        val fileContent = "test file content".toByteArray()
        val mimeType = "image/jpeg"
        val presignedUrl = testRule.server.url("/upload").toString()

        testRule.server.enqueue(MockResponse().setResponseCode(HttpURLConnection.HTTP_OK))

        // when
        val result = dataSource.uploadFile(presignedUrl, fileContent, mimeType)

        // then
        assertTrue(result is NetworkResult.Success)
        assertEquals(true, (result as NetworkResult.Success).data)

        // verify request
        val recordedRequest: RecordedRequest = testRule.server.takeRequest()
        assertEquals("PUT", recordedRequest.method)
        assertEquals("image/jpeg", recordedRequest.getHeader("Content-Type"))
        assertArrayEquals(fileContent, recordedRequest.body.readByteArray())
    }

    @Test
    fun `uploadFile() 실패 테스트- 서버 에러 발생시 false를 반환한다`() = runTest {
        // Given
        val fileContent = "test file content".toByteArray()
        val mimeType = "application/pdf"
        val presignedUrl = testRule.server.url("/upload").toString()

        testRule.server.enqueue(MockResponse().setResponseCode(HttpURLConnection.HTTP_BAD_REQUEST))

        // When
        val result = dataSource.uploadFile(presignedUrl, fileContent, mimeType)

        // Then
        assertTrue(result is NetworkResult.Success)
        assertEquals(false, (result as NetworkResult.Success).data)
    }

    @Test
    fun `uploadFile() 실패 테스트 - 잘못된 mime 입력 시 Error를 반환한다`() = runTest {
        // Given
        val fileContent = "test file content".toByteArray()
        val invalidMimeType = "invalid/mime/type"
        val presignedUrl = testRule.server.url("/upload").toString()

        // When
        val result = dataSource.uploadFile(presignedUrl, fileContent, invalidMimeType)

        // Then
        assertTrue(result is NetworkResult.Error)
        assertEquals(
            NetworkErrorType.Network.InvalidFileType,
            (result as NetworkResult.Error).error,
        )

        // Verify no request was made
        assertEquals(0, testRule.server.requestCount)
    }

    @Test
    fun `uploadFile() 실패 테스트 - 예외 발생 시 에러 로그를 수행한다`() = runTest {
        // Given
        val fileContent = "test file content".toByteArray()
        val mimeType = "text/plain"
        val presignedUrl = "http://invalid-url-that-will-fail"

        // When & Then
        val exception = runCatching {
            dataSource.uploadFile(presignedUrl, fileContent, mimeType)
        }.exceptionOrNull()
        assertTrue(exception is Throwable)

        // verify
        verify(exactly = 1) { AppLogger.e(any<String>(), any<Throwable>(), any<String>()) }
    }

    companion object {
        private const val MOCK_PRESIGNED_URL = "https://example-storage.com/objects/my-image.jpg"
        private const val MOCK_METHOD = "PUT"
        private const val MOCK_SECONDS = 600
        private val mockPresignedResponseJson =
            """
            {
              "presignedUrl": "$MOCK_PRESIGNED_URL",
              "method": "$MOCK_METHOD",
              "expiresInSeconds": $MOCK_SECONDS
            }
            """.trimIndent()
        private val mockPresignUrlResponse = PresignedUrlResponse(
            MOCK_PRESIGNED_URL,
            MOCK_METHOD,
            MOCK_SECONDS,
        )
    }
}
