package com.turnin.core.data.source.network.datasource

import com.turnin.core.data.ServerTestRule
import com.turnin.core.data.source.network.api.PingPongApi
import com.turnin.core.data.source.network.dto.pingPong.request.CreatePingPongAnswerRequest
import com.turnin.core.data.source.network.dto.pingPong.request.CreatePingPongRequest
import com.turnin.core.data.source.network.dto.pingPong.response.PingPongAnswerResponse
import com.turnin.core.data.source.network.dto.pingPong.response.PingPongCursorPageResponse
import com.turnin.core.data.source.network.dto.pingPong.response.PingPongDetailResponse
import com.turnin.core.data.source.network.dto.pingPong.response.PingPongQuestionerResponse
import com.turnin.core.data.source.network.dto.pingPong.response.PingPongResponse
import com.turnin.core.data.source.network.error.NetworkErrorType
import com.turnin.core.data.source.network.util.NetworkResult
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import okhttp3.mockwebserver.MockResponse
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class PingPongNetworkDataSourceImplTest {
    @get:Rule
    val testRule = ServerTestRule()

    private val pingPongApi: PingPongApi
        get() = testRule.createNetworkApi<PingPongApi>(testRule.moshi)

    private lateinit var dataSource: PingPongNetworkDataSource

    @Before
    fun setUp() {
        dataSource = PingPongNetworkDataSourceImpl(pingPongApi)
    }

    @Test
    fun `핑퐁 목록 조회 - 성공 시 응답 바디를 반환한다`() = runTest {
        // given
        testRule.server.enqueue(
            MockResponse().apply {
                setResponseCode(200)
                setBody(testRule.encodeToJson(TestCursorPageResponse))
            },
        )

        // when
        val response = dataSource.getPingPongs(TEST_USER_KEYWORD_ID, null, TEST_PAGE_SIZE)

        // then
        val success = response as NetworkResult.Success
        assertEquals(TestCursorPageResponse, success.data)
    }

    @Test
    fun `핑퐁 목록 조회 - 경로와 쿼리 파라미터로 요청한다`() = runTest {
        // given
        testRule.server.enqueue(
            MockResponse().apply {
                setResponseCode(200)
                setBody(testRule.encodeToJson(TestCursorPageResponse))
            },
        )

        // when
        dataSource.getPingPongs(TEST_USER_KEYWORD_ID, TEST_CURSOR, TEST_PAGE_SIZE)

        // then
        val request = testRule.server.takeRequest()
        assertEquals("GET", request.method)
        assertEquals(
            "/api/v1/user-keyword/$TEST_USER_KEYWORD_ID/ping-pong?cursor=$TEST_CURSOR&size=$TEST_PAGE_SIZE",
            request.path,
        )
    }

    @Test
    fun `핑퐁 목록 조회 - 잘못된 응답 바디로 응답 시 JsonData 에러를 반환한다`() = runTest {
        // given
        testRule.server.enqueue(
            MockResponse().apply {
                setResponseCode(200)
                setBody(TestInvalidResponse)
            },
        )

        // when
        val response = dataSource.getPingPongs(TEST_USER_KEYWORD_ID, null, TEST_PAGE_SIZE)

        // then
        val error = response as NetworkResult.Error
        assertEquals(NetworkErrorType.Exception.JsonData, error.error)
    }

    @Test
    fun `핑퐁 목록 조회 - HTTP 상태코드 404 응답 시 서버 에러 코드를 함께 반환한다`() = runTest {
        // given
        testRule.server.enqueue(
            MockResponse().apply {
                setResponseCode(404)
                setBody(TestErrorResponse)
            },
        )

        // when
        val response = dataSource.getPingPongs(TEST_USER_KEYWORD_ID, null, TEST_PAGE_SIZE)

        // then
        val error = response as NetworkResult.Error
        assertEquals(404, error.status)
        assertEquals(TEST_ERROR_CODE, error.code)
    }

    @Test
    fun `핑퐁 생성 - 성공 시 생성된 핑퐁을 반환한다`() = runTest {
        // given
        testRule.server.enqueue(
            MockResponse().apply {
                setResponseCode(201)
                setBody(testRule.encodeToJson(TestPingPongResponse))
            },
        )

        // when
        val response = dataSource.createPingPong(TEST_USER_KEYWORD_ID, TEST_QUESTION)

        // then
        val success = response as NetworkResult.Success
        assertEquals(TestPingPongResponse, success.data)
    }

    @Test
    fun `핑퐁 생성 - 질문 내용을 요청 바디로 전송한다`() = runTest {
        // given
        testRule.server.enqueue(
            MockResponse().apply {
                setResponseCode(201)
                setBody(testRule.encodeToJson(TestPingPongResponse))
            },
        )

        // when
        dataSource.createPingPong(TEST_USER_KEYWORD_ID, TEST_QUESTION)

        // then
        val request = testRule.server.takeRequest()
        assertEquals("POST", request.method)
        assertEquals("/api/v1/user-keyword/$TEST_USER_KEYWORD_ID/ping-pong", request.path)
        assertEquals(
            CreatePingPongRequest(TEST_QUESTION),
            testRule.decodeFromJson<CreatePingPongRequest>(request.body.readUtf8()),
        )
    }

    @Test
    fun `핑퐁 생성 - 알 수 없는 예외 발생 시 Unexpected 에러를 반환한다`() = runTest {
        // given
        val mockApi: PingPongApi = mockk()
        val exception = Exception()
        dataSource = PingPongNetworkDataSourceImpl(mockApi)
        coEvery { mockApi.createPingPong(any(), any()) } throws exception

        // when
        val response = dataSource.createPingPong(TEST_USER_KEYWORD_ID, TEST_QUESTION)

        // then
        val error = response as NetworkResult.Error
        assertEquals(NetworkErrorType.Unexpected(exception), error.error)
    }

    @Test
    fun `핑퐁 답변 생성 - 성공 시 생성된 답변을 반환한다`() = runTest {
        // given
        testRule.server.enqueue(
            MockResponse().apply {
                setResponseCode(201)
                setBody(testRule.encodeToJson(TestAnswerResponse))
            },
        )

        // when
        val response = dataSource.createAnswer(TEST_PING_PONG_ID, TEST_ANSWER)

        // then
        val success = response as NetworkResult.Success
        assertEquals(TestAnswerResponse, success.data)
    }

    @Test
    fun `핑퐁 답변 생성 - 답변 내용을 요청 바디로 전송한다`() = runTest {
        // given
        testRule.server.enqueue(
            MockResponse().apply {
                setResponseCode(201)
                setBody(testRule.encodeToJson(TestAnswerResponse))
            },
        )

        // when
        dataSource.createAnswer(TEST_PING_PONG_ID, TEST_ANSWER)

        // then
        val request = testRule.server.takeRequest()
        assertEquals("POST", request.method)
        assertEquals("/api/v1/ping-pong/$TEST_PING_PONG_ID/answer", request.path)
        assertEquals(
            CreatePingPongAnswerRequest(TEST_ANSWER),
            testRule.decodeFromJson<CreatePingPongAnswerRequest>(request.body.readUtf8()),
        )
    }

    @Test
    fun `핑퐁 답변 생성 - HTTP 상태코드 409 응답 시 HttpError를 반환한다`() = runTest {
        // given
        testRule.server.enqueue(
            MockResponse().apply {
                setResponseCode(409)
            },
        )

        // when
        val response = dataSource.createAnswer(TEST_PING_PONG_ID, TEST_ANSWER)

        // then
        val error = response as NetworkResult.Error
        assertEquals(NetworkErrorType.Network.HttpError(409), error.error)
    }

    @Test
    fun `핑퐁 삭제 - 204 응답 시 성공을 반환한다`() = runTest {
        // given
        testRule.server.enqueue(
            MockResponse().apply {
                setResponseCode(204)
            },
        )

        // when
        val response = dataSource.deletePingPong(TEST_PING_PONG_ID)

        // then
        assertTrue(response is NetworkResult.Success)
    }

    @Test
    fun `핑퐁 삭제 - 핑퐁 ID 경로로 DELETE 요청한다`() = runTest {
        // given
        testRule.server.enqueue(
            MockResponse().apply {
                setResponseCode(204)
            },
        )

        // when
        dataSource.deletePingPong(TEST_PING_PONG_ID)

        // then
        val request = testRule.server.takeRequest()
        assertEquals("DELETE", request.method)
        assertEquals("/api/v1/ping-pong/$TEST_PING_PONG_ID", request.path)
    }

    @Test
    fun `핑퐁 삭제 - HTTP 상태코드 403 응답 시 HttpError를 반환한다`() = runTest {
        // given
        testRule.server.enqueue(
            MockResponse().apply {
                setResponseCode(403)
            },
        )

        // when
        val response = dataSource.deletePingPong(TEST_PING_PONG_ID)

        // then
        val error = response as NetworkResult.Error
        assertEquals(NetworkErrorType.Network.HttpError(403), error.error)
    }

    @Test
    fun `핑퐁 답변 삭제 - 204 응답 시 성공을 반환한다`() = runTest {
        // given
        testRule.server.enqueue(
            MockResponse().apply {
                setResponseCode(204)
            },
        )

        // when
        val response = dataSource.deletePingPongAnswer(TEST_PING_PONG_ID)

        // then
        assertTrue(response is NetworkResult.Success)
    }

    @Test
    fun `핑퐁 답변 삭제 - 답변 경로로 DELETE 요청한다`() = runTest {
        // given
        testRule.server.enqueue(
            MockResponse().apply {
                setResponseCode(204)
            },
        )

        // when
        dataSource.deletePingPongAnswer(TEST_PING_PONG_ID)

        // then
        val request = testRule.server.takeRequest()
        assertEquals("DELETE", request.method)
        assertEquals("/api/v1/ping-pong/$TEST_PING_PONG_ID/answer", request.path)
    }

    companion object {
        private const val TEST_USER_KEYWORD_ID = 10L
        private const val TEST_PING_PONG_ID = 1L
        private const val TEST_CURSOR = 5L
        private const val TEST_PAGE_SIZE = 20
        private const val TEST_QUESTION = "질문?"
        private const val TEST_ANSWER = "답변"
        private const val TEST_ERROR_CODE = "P001"

        private val TestPingPongResponse = PingPongResponse(
            id = TEST_PING_PONG_ID,
            userKeywordId = TEST_USER_KEYWORD_ID,
            questionerId = 2L,
            question = TEST_QUESTION,
            createdAt = 1_000L,
            updatedAt = 1_000L,
        )
        private val TestAnswerResponse = PingPongAnswerResponse(
            id = 3L,
            pingPongId = TEST_PING_PONG_ID,
            answer = TEST_ANSWER,
            createdAt = 2_000L,
            updatedAt = 2_000L,
        )
        private val TestCursorPageResponse = PingPongCursorPageResponse(
            items = listOf(
                PingPongDetailResponse(
                    pingPong = TestPingPongResponse,
                    questioner = PingPongQuestionerResponse(
                        userId = 2L,
                        userName = "질문자",
                        profileImageUrl = null,
                    ),
                    answer = null,
                ),
            ),
            nextCursor = null,
        )
        private val TestInvalidResponse =
            """
            {
                "what": "???"
            }
            """.trimIndent()
        private val TestErrorResponse =
            """
            {
                "code": "$TEST_ERROR_CODE",
                "message": "not found",
                "status": 404
            }
            """.trimIndent()
    }
}
