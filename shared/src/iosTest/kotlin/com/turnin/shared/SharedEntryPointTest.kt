package com.turnin.shared

import com.turnin.shared.core.auth.AuthFacade
import com.turnin.shared.data.core.network.auth.TokenStorage
import com.turnin.shared.presentation.feature.announcement.AnnouncementErrorReason
import com.turnin.shared.presentation.feature.announcement.AnnouncementFacade
import com.turnin.shared.presentation.feature.announcement.AnnouncementsState
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.client.request.HttpRequestData
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import kotlin.test.AfterTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.last
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest

@OptIn(ExperimentalCoroutinesApi::class)
class SharedEntryPointTest {
    private val tokenStorage = FakeTokenStorage()
    private val requests = mutableListOf<HttpRequestData>()

    private val auth: AuthFacade get() = SharedEntryPoint.resolve()
    private val announcements: AnnouncementFacade get() = SharedEntryPoint.resolve()

    @AfterTest
    fun tearDown() = SharedEntryPoint.reset()

    private fun start(status: HttpStatusCode = HttpStatusCode.OK, body: String = ANNOUNCEMENTS_JSON) {
        val engine = MockEngine { request ->
            requests += request
            respond(body, status, headersOf(HttpHeaders.ContentType, "application/json"))
        }
        SharedEntryPoint.start(baseUrl = "https://turnin.test", engine = engine, tokenStorage = tokenStorage)
    }

    @Test
    fun `저장한 토큰으로 공지 목록을 조회해 Swift용 모델로 반환한다`() = runTest {
        start()
        auth.saveTokens(accessToken = "access", refreshToken = "refresh")

        val states = announcements.announcements().toList()

        assertEquals(AnnouncementsState.Loading, states.first())
        val items = (states.last() as AnnouncementsState.Success).items
        assertEquals(1L, items.single().id)
        assertEquals("공지", items.single().title)
        assertEquals("Bearer access", requests.single().headers[HttpHeaders.Authorization])
    }

    @Test
    fun `토큰 갱신에 실패하면 UNAUTHORIZED로 반환하고 인증 실패 이벤트를 보낸다`() = runTest {
        start(status = HttpStatusCode.Unauthorized, body = "")
        auth.saveTokens(accessToken = "expired", refreshToken = "expired")
        val authFailures = mutableListOf<Unit>()
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            auth.authFailures.collect { authFailures += it }
        }

        val state = announcements.announcements().last()

        assertEquals(AnnouncementsState.Failure(AnnouncementErrorReason.UNAUTHORIZED), state)
        assertTrue(requests.any { it.url.encodedPath == REFRESH_PATH })
        assertFalse(auth.hasTokens())
        assertEquals(1, authFailures.size)
    }

    @Test
    fun `읽음 처리 성공 시 null을 반환한다`() = runTest {
        start(body = "{}")
        auth.saveTokens(accessToken = "access", refreshToken = "refresh")

        val reason = announcements.markAsRead(announcementId = 1L)

        assertNull(reason)
        assertEquals("/api/v1/announcement/read/1", requests.single().url.encodedPath)
    }

    @Test
    fun `토큰을 삭제하면 다음 요청부터 토큰 없이 보낸다`() = runTest {
        start()
        auth.saveTokens(accessToken = "access", refreshToken = "refresh")
        announcements.announcements().last()

        auth.clearTokens()
        announcements.announcements().last()

        assertFalse(auth.hasTokens())
        assertNull(requests.last().headers[HttpHeaders.Authorization])
    }

    private class FakeTokenStorage : TokenStorage {
        private var tokens: Pair<String, String>? = null

        override suspend fun getAccessToken(): String? = tokens?.first

        override suspend fun getRefreshToken(): String? = tokens?.second

        override suspend fun saveTokens(accessToken: String, refreshToken: String) {
            tokens = accessToken to refreshToken
        }

        override suspend fun clear() {
            tokens = null
        }
    }

    companion object {
        private const val REFRESH_PATH = "/api/v1/auth/refresh"
        private const val ANNOUNCEMENTS_JSON =
            """[{"id":1,"title":"공지","content":"내용","targetAudience":"ALL","isRead":false,"createdAt":0}]"""
    }
}
