package com.turnin.core.data.source.network.datasource

import com.turnin.core.data.source.network.error.NetworkErrorType
import com.turnin.core.data.source.network.ktor.createHttpClient
import com.turnin.core.data.source.network.util.NetworkResult
import com.turnin.core.domain.model.AnnouncementId
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.MockRequestHandleScope
import io.ktor.client.engine.mock.respond
import io.ktor.client.request.HttpRequestData
import io.ktor.client.request.HttpResponseData
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlinx.coroutines.test.runTest

class AnnouncementNetworkDataSourceImplTest {
    private val requests = mutableListOf<HttpRequestData>()

    private fun dataSourceRespondingWith(
        vararg responses: MockRequestHandleScope.() -> HttpResponseData,
    ): AnnouncementNetworkDataSource {
        val engine = MockEngine { request ->
            requests += request
            responses[(requests.size - 1).coerceAtMost(responses.lastIndex)]()
        }
        return AnnouncementNetworkDataSourceImpl(createHttpClient(engine, BASE_URL))
    }

    @Test
    fun `getAnnouncements 성공 시 공지 목록을 파싱해 반환한다`() = runTest {
        // given
        val dataSource = dataSourceRespondingWith({ respondJson(mockAnnouncementsResponseJson) })

        // when
        val result = dataSource.getAnnouncements()

        // then
        assertTrue(result is NetworkResult.Success)
        val data = (result as NetworkResult.Success).data
        assertEquals(1, data.size)
        assertEquals("테스트 공지", data[0].title)
        assertEquals(HttpMethod.Get, requests.single().method)
        assertEquals("/api/v1/announcement", requests.single().url.encodedPath)
    }

    @Test
    fun `markAsRead 성공 시 공지 ID를 경로에 담아 POST 요청한다`() = runTest {
        // given
        val dataSource = dataSourceRespondingWith({ respondJson("{}") })

        // when
        val result = dataSource.markAsRead(AnnouncementId(1L))

        // then
        assertTrue(result is NetworkResult.Success)
        assertEquals(HttpMethod.Post, requests.single().method)
        assertEquals("/api/v1/announcement/read/1", requests.single().url.encodedPath)
    }

    @Test
    fun `서버 에러 응답 시 상태 코드와 에러 바디를 담아 HttpError를 반환한다`() = runTest {
        // given
        val dataSource = dataSourceRespondingWith({
            respondJson(mockErrorResponseJson, HttpStatusCode.NotFound)
        })

        // when
        val result = dataSource.getAnnouncements()

        // then
        assertTrue(result is NetworkResult.Error)
        result as NetworkResult.Error
        assertEquals(NetworkErrorType.Network.HttpError(404), result.error)
        assertEquals(404, result.status)
        assertEquals("ANNOUNCEMENT_NOT_FOUND", result.code)
        assertEquals("공지를 찾을 수 없습니다.", result.message)
        assertEquals(1, requests.size)
    }

    @Test
    fun `재시도 가능한 서버 에러는 재시도 후 성공 응답을 반환한다`() = runTest {
        // given
        val dataSource = dataSourceRespondingWith(
            { respondJson("{}", HttpStatusCode.ServiceUnavailable) },
            { respondJson(mockAnnouncementsResponseJson) },
        )

        // when
        val result = dataSource.getAnnouncements()

        // then
        assertTrue(result is NetworkResult.Success)
        assertEquals(2, requests.size)
    }

    @Test
    fun `응답 JSON 형식이 맞지 않으면 JsonData 에러를 반환한다`() = runTest {
        // given
        val dataSource = dataSourceRespondingWith({ respondJson("""{"invalid": true}""") })

        // when
        val result = dataSource.getAnnouncements()

        // then
        assertTrue(result is NetworkResult.Error)
        assertEquals(NetworkErrorType.Exception.JsonData, (result as NetworkResult.Error).error)
    }

    private fun MockRequestHandleScope.respondJson(
        content: String,
        status: HttpStatusCode = HttpStatusCode.OK,
    ): HttpResponseData = respond(
        content = content,
        status = status,
        headers = headersOf(HttpHeaders.ContentType, "application/json"),
    )

    companion object {
        private const val BASE_URL = "https://turnin.test"

        private val mockAnnouncementsResponseJson =
            """
            [
              {
                "id": 1,
                "title": "테스트 공지",
                "content": "공지 내용입니다.",
                "targetAudience": "ALL",
                "isRead": false,
                "createdAt": 1715820000000
              }
            ]
            """.trimIndent()

        private val mockErrorResponseJson =
            """
            {
              "code": "ANNOUNCEMENT_NOT_FOUND",
              "message": "공지를 찾을 수 없습니다.",
              "status": 404
            }
            """.trimIndent()
    }
}
