package com.turnin.core.data.source.network.datasource

import com.turnin.core.data.ServerTestRule
import com.turnin.core.data.source.network.api.AnnouncementApi
import com.turnin.core.data.source.network.util.NetworkResult
import com.turnin.core.domain.model.AnnouncementId
import kotlinx.coroutines.test.runTest
import okhttp3.mockwebserver.MockResponse
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class AnnouncementNetworkDataSourceImplTest {
    @get:Rule
    val testRule = ServerTestRule()

    private val announcementApi: AnnouncementApi
        get() = testRule.createNetworkApi<AnnouncementApi>(testRule.moshi)

    private lateinit var dataSource: AnnouncementNetworkDataSource

    @Before
    fun setUp() {
        dataSource = AnnouncementNetworkDataSourceImpl(announcementApi)
    }

    @Test
    fun `getAnnouncements() 성공 테스트`() = runTest {
        // given
        testRule.server.enqueue(
            MockResponse().apply {
                setResponseCode(200)
                setBody(mockAnnouncementsResponseJson)
            },
        )

        // when
        val result = dataSource.getAnnouncements()

        // then
        assertTrue(result is NetworkResult.Success)
        val data = (result as NetworkResult.Success).data
        assertEquals(1, data.size)
        assertEquals("테스트 공지", data[0].title)
    }

    @Test
    fun `markAsRead() 성공 테스트`() = runTest {
        // given
        testRule.server.enqueue(
            MockResponse().apply {
                setResponseCode(200)
                setBody("{}")
            },
        )

        // when
        val result = dataSource.markAsRead(AnnouncementId(1L))

        // then
        assertTrue(result is NetworkResult.Success)
    }

    companion object {
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
    }
}
