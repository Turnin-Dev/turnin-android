package com.peekr.core.data.repository

import android.content.Context
import androidx.paging.testing.asSnapshot
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.peekr.core.data.MainDispatcherRule
import com.peekr.core.data.MockLog
import com.peekr.core.data.source.local.database.PeekrDatabase
import com.peekr.core.data.source.network.datasource.FeedNetworkDataSource
import com.peekr.core.data.source.network.dto.feed.FeedCursorPageResponse
import com.peekr.core.data.source.network.dto.feed.FeedCursorResponse
import com.peekr.core.data.source.network.dto.feed.FeedResponse
import com.peekr.core.data.source.network.util.NetworkResult
import com.peekr.core.domain.feed.repository.FeedRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertNotNull
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
@OptIn(ExperimentalCoroutinesApi::class)
class FeedRepositoryImplTest {
    @get:Rule
    var mainDispatcherRule = MainDispatcherRule()

    private val dataSource: FeedNetworkDataSource = mockk()
    private lateinit var database: PeekrDatabase
    private lateinit var repository: FeedRepository

    @Before
    fun setUp() {
        MockLog.mock()

        val context = ApplicationProvider.getApplicationContext<Context>()
        database = Room.inMemoryDatabaseBuilder(context, PeekrDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        repository = FeedRepositoryImpl(dataSource, database)
    }

    @After
    fun teardown() {
        MockLog.cleanUp()
        database.close()
    }

    @Test
    fun `피드 조회 시 RemoteMediator를 통해 데이터가 DB에 저장되고 도메인 모델로 반환된다`() = runTest {
        // given: Mock 데이터 준비
        val feedResponses = List(2) {
            val id = it + 1L
            createFeedResponse(id)
        }
        val mockNetworkResponse = FeedCursorPageResponse(
            items = feedResponses,
            nextCursor = FeedCursorResponse(
                score = feedResponses.last().score,
                createdAt = feedResponses.last().createdAt,
                userKeywordId = feedResponses.last().userKeywordId,
            ),
        )

        coEvery {
            dataSource.getFeeds(any(), any(), any(), any())
        } returns NetworkResult.Success(mockNetworkResponse)

        // when
        // Repository 호출 및 데이터 수집
        // PagingData는 수집하기 전까지 내부 로직이 돌아가지 않으므로 collect 처리
        val pagingDataFlow = repository.getFeeds()
        val snapshot = pagingDataFlow.asSnapshot()

        // then
        // 네트워크 응답이 도메인 모델(Feed)로 잘 변환되었는지 확인
        assertEquals(2, snapshot.size)
        assertEquals(1L, snapshot[0].userKeywordId.value)

        // DB에 실제로 저장되었는지 확인 (RemoteMediator 동작 확인)
        val dbItems = database.feedDao().getAll().first()
        assertEquals(2, dbItems.size)

        // RemoteKey가 성공적으로 저장되었는지 확인
        val lastItem = feedResponses.last() // 마지막 아이템
        val remoteKey = database.feedRemoteKeyDao().getById(lastItem.userKeywordId)
        assertNotNull(remoteKey)
        assertEquals(lastItem.score, remoteKey?.cursorScore)

        coVerify(exactly = 1) { dataSource.getFeeds(null, null, null, any()) }
    }

    private fun createFeedResponse(id: Long) =
        FeedResponse(
            userKeywordId = id,
            userId = id,
            userName = "name",
            profileImageUrl = "image",
            keywordId = id,
            keyword = "keyword",
            description = "desc",
            createdAt = 1000L,
            score = 50.0,
            similarity = 0.7,
        )
}
