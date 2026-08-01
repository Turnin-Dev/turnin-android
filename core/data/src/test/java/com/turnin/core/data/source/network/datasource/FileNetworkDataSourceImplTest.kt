package com.turnin.core.data.source.network.datasource

import com.squareup.moshi.JsonDataException
import com.turnin.core.data.ServerTestRule
import com.turnin.core.data.source.network.api.FileApi
import com.turnin.core.data.source.network.dto.file.response.PresignedUrlResponse
import com.turnin.core.data.source.network.error.NetworkErrorType
import com.turnin.core.data.source.network.util.NetworkResult
import com.turnin.core.domain.file.model.FileCategory
import io.mockk.coEvery
import io.mockk.mockk
import java.net.HttpURLConnection
import java.util.concurrent.TimeUnit
import kotlinx.coroutines.test.runTest
import okhttp3.OkHttpClient
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.RecordedRequest
import okhttp3.mockwebserver.SocketPolicy
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
    private lateinit var dataSource: FileNetworkDataSource
    private lateinit var testOkHttpClient: OkHttpClient

    @Before
    fun setUp() {
        testOkHttpClient = OkHttpClient.Builder().build()
        dataSource = FileNetworkDataSourceImpl(fileApi, testOkHttpClient)
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
        val result = dataSource.getFileUploadPresignedUrl(
            "my-image.jpg",
            "image/jpeg",
            FileCategory.PROFILE_IMAGE,
        )

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
        dataSource = FileNetworkDataSourceImpl(mockApi, testOkHttpClient)
        coEvery { mockApi.getFileUploadPresignedUrl(any(), any(), any()) } throws JsonDataException("smile")

        // when
        val result = dataSource.getFileUploadPresignedUrl("asd", "asd", FileCategory.PROFILE_IMAGE)

        // then
        assertTrue(result is NetworkResult.Error)
        assertEquals((result as NetworkResult.Error).error, NetworkErrorType.Exception.JsonData)
    }

    @Test
    fun `getFileUpdatePresignedUrl() 성공 테스트`() = runTest {
        // given
        testRule.server.enqueue(
            MockResponse().apply {
                setResponseCode(200)
                setBody(mockPresignedResponseJson)
            },
        )

        // when
        val result = dataSource.getFileUpdatePresignedUrl(
            "my-image.jpg",
            "image/jpeg",
            FileCategory.PROFILE_IMAGE,
        )

        // then
        assertTrue(result is NetworkResult.Success)
        assertEquals(
            mockPresignUrlResponse,
            (result as NetworkResult.Success).data,
        )
    }

    @Test
    fun `getFileUpdatePresignedUrl() 실패 테스트 (정의된 API 예외 발생)`() = runTest {
        // given
        val mockApi: FileApi = mockk()
        dataSource = FileNetworkDataSourceImpl(mockApi, testOkHttpClient)
        coEvery { mockApi.getFileUpdatePresignedUrl(any(), any(), any()) } throws JsonDataException("smile")

        // when
        val result = dataSource.getFileUpdatePresignedUrl("asd", "asd", FileCategory.PROFILE_IMAGE)

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
    fun `uploadFile() 실패 테스트 - 서버 에러 발생시 정상적으로 정의된 에러타입을 반환한다`() = runTest {
        // Given
        val fileContent = "test file content".toByteArray()
        val mimeType = "application/pdf"
        val presignedUrl = testRule.server.url("/upload").toString()

        testRule.server.enqueue(
            MockResponse().apply {
                setResponseCode(HttpURLConnection.HTTP_BAD_REQUEST)
            },
        )

        // When
        val result = dataSource.uploadFile(presignedUrl, fileContent, mimeType)

        // Then
        assertTrue(result is NetworkResult.Error)
        assertEquals(
            NetworkErrorType.Network.UploadFileFailed,
            (result as NetworkResult.Error).error,
        )
    }

    @Test
    fun `uploadFile() 실패 테스트 - 타임아웃 발생 시 TimeOut 에러를 반환한다`() = runTest {
        // given
        val fileContent = "test file content".toByteArray()
        val mimeType = "image/jpeg"
        val presignedUrl = testRule.server.url("/upload").toString()

        // 타임아웃을 짧게 설정한 클라이언트 생성
        val shortTimeoutClient = OkHttpClient.Builder()
            .connectTimeout(50, TimeUnit.MILLISECONDS)
            .readTimeout(50, TimeUnit.MILLISECONDS)
            .writeTimeout(50, TimeUnit.MILLISECONDS)
            .build()

        dataSource = FileNetworkDataSourceImpl(fileApi, shortTimeoutClient)

        testRule.server.enqueue(
            MockResponse().apply {
                socketPolicy = SocketPolicy.NO_RESPONSE
            },
        )

        // when
        val result = dataSource.uploadFile(presignedUrl, fileContent, mimeType)

        // then
        assertTrue(result is NetworkResult.Error)
        assertEquals(
            NetworkErrorType.Exception.TimeOut,
            (result as NetworkResult.Error).error,
        )
    }

    @Test
    fun `uploadFile() 실패 테스트 - callTimeout 초과 시 TimeOut 에러를 반환한다`() = runTest {
        // given
        val fileContent = "test file content".toByteArray()
        val mimeType = "image/jpeg"
        val presignedUrl = testRule.server.url("/upload").toString()

        // callTimeout만 짧게 설정한 클라이언트 생성 (connect/read/write는 넉넉하게 유지)
        val shortCallTimeoutClient = OkHttpClient.Builder()
            .callTimeout(50, TimeUnit.MILLISECONDS)
            .build()

        dataSource = FileNetworkDataSourceImpl(fileApi, shortCallTimeoutClient)

        testRule.server.enqueue(
            MockResponse().apply {
                socketPolicy = SocketPolicy.NO_RESPONSE
            },
        )

        // when
        val result = dataSource.uploadFile(presignedUrl, fileContent, mimeType)

        // then
        assertTrue(result is NetworkResult.Error)
        assertEquals(
            NetworkErrorType.Exception.TimeOut,
            (result as NetworkResult.Error).error,
        )
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
    fun `uploadFile() 실패 테스트 - 예외 발생 시 정상적으로 정의된 에러타입을 반환한다`() = runTest {
        // given
        val fileContent = "test file content".toByteArray()
        val mimeType = "text/plain"
        val presignedUrl = "http://invalid-url-that-will-fail"

        // when
        val result = dataSource.uploadFile(presignedUrl, fileContent, mimeType)

        // then
        assertTrue(result is NetworkResult.Error)
        assertEquals(
            NetworkErrorType.Network.UploadFileFailed,
            (result as NetworkResult.Error).error,
        )
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
