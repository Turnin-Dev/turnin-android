package com.turnin.core.data.repository

import android.content.Context
import androidx.paging.testing.asSnapshot
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.turnin.core.data.MainDispatcherRule
import com.turnin.core.data.MockLog
import com.turnin.core.data.source.local.database.TurninDatabase
import com.turnin.core.data.source.network.datasource.FeedNetworkDataSource
import com.turnin.core.data.source.network.dto.feed.FeedCursorPageResponse
import com.turnin.core.data.source.network.dto.feed.FeedCursorResponse
import com.turnin.core.data.source.network.dto.feed.FeedResponse
import com.turnin.core.data.source.network.dto.feed.toEntity
import com.turnin.core.data.source.network.error.NetworkErrorType
import com.turnin.core.data.source.network.util.NetworkResult
import com.turnin.core.domain.common.error.PagingApiCallException
import com.turnin.core.domain.feed.repository.FeedRepository
import io.mockk.clearMocks
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertNotNull
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertFalse
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

/**
 * FeedRepositoryImpl, FeedRemoteMediator 테스트
 */
@RunWith(RobolectricTestRunner::class)
@OptIn(ExperimentalCoroutinesApi::class)
class FeedRepositoryImplTest {
    @get:Rule
    var mainDispatcherRule = MainDispatcherRule()

    private val dataSource: FeedNetworkDataSource = mockk()
    private lateinit var database: TurninDatabase
    private lateinit var repository: FeedRepository

    @Before
    fun setUp() {
        MockLog.mock()

        val context = ApplicationProvider.getApplicationContext<Context>()
        database = Room.inMemoryDatabaseBuilder(context, TurninDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        repository = FeedRepositoryImpl(dataSource, database)
    }

    @After
    fun teardown() {
        database.clearAllTables()
        database.close()

        MockLog.cleanUp()

        clearMocks(dataSource)
    }

    @Test
    fun `피드 조회 시 RemoteMediator를 통해 데이터가 DB에 저장되고 도메인 모델로 반환된다`() = runTest {
        // given: Mock 데이터 준비
        val feedResponses = List(2) {
            val id = it + 1L
            createFeedResponse(id)
        }
        val networkResponse = FeedCursorPageResponse(
            items = feedResponses,
            nextCursor = null,
        )

        coEvery {
            dataSource.getFeeds(any(), any(), any(), any())
        } returns NetworkResult.Success(networkResponse)

        // when
        // Repository 호출 및 데이터 수집
        // PagingData는 수집하기 전까지 내부 로직이 돌아가지 않으므로 collect 처리
        val pagingDataFlow = repository.getFeeds()
        val snapshot = pagingDataFlow.asSnapshot {
            scrollTo(index = 1)
        }

        advanceUntilIdle()

        // then
        // 네트워크 응답이 도메인 모델(Feed)로 잘 변환되었는지 확인
        assertEquals(2, snapshot.size)
        // DB에서 내림차순 정렬이기에 2가 먼저 조회
        assertEquals(2L, snapshot[0].userKeywordId.value)

        coVerify(exactly = 1) { dataSource.getFeeds(null, null, null, any()) }
    }

    @Test
    fun `피드를 두 번째 페이지까지 조회 했을 때 정상적으로 조회된다`() = runTest {
        // given: 총 2페이지 분량의 데이터를 생성하고 마지막 페이지의 커서 값은 null로 설정
        val itemCount = 4
        val totalFeedResponses = List(itemCount) {
            val id = it + 1L
            createFeedResponse(id)
        }
        val firstFeedResponse = totalFeedResponses.take(2)
        val secondFeedResponse = totalFeedResponses.takeLast(2)
        val firstNetworkResponse = FeedCursorPageResponse(
            items = firstFeedResponse,
            nextCursor = FeedCursorResponse(
                score = firstFeedResponse.last().score,
                createdAt = firstFeedResponse.last().createdAt,
                userKeywordId = firstFeedResponse.last().userKeywordId,
            ),
        )
        val secondNetworkResponse = FeedCursorPageResponse(
            items = secondFeedResponse,
            nextCursor = null,
        )

        coEvery {
            dataSource.getFeeds(any(), any(), any(), any())
        } returnsMany listOf(
            NetworkResult.Success(firstNetworkResponse),
            NetworkResult.Success(secondNetworkResponse),
        )

        // when
        // Repository 호출 및 데이터 수집
        // PagingData는 수집하기 전까지 내부 로직이 돌아가지 않으므로 collect 처리
        val pagingDataFlow = repository.getFeeds()
        val snapshot = pagingDataFlow.asSnapshot()
        advanceUntilIdle()

        // then
        // 네트워크 응답이 도메인 모델(Feed)로 잘 변환되었는지 확인
        assertEquals(itemCount, snapshot.size)
        assertEquals(itemCount.toLong(), snapshot[0].userKeywordId.value)

        // DB에 실제로 저장되었는지 확인 (RemoteMediator 동작 확인)
        val dbItems = database.feedDao().getAll().first()
        assertEquals(4, dbItems.size)

        // RemoteKey가 성공적으로 저장되었는지 확인
        val cursor = firstNetworkResponse.nextCursor
        val remoteKey = database.feedRemoteKeyDao().getRemoteKey()
        assertNotNull(remoteKey)
        assertEquals(cursor?.score, remoteKey?.cursorScore)

        coVerify(exactly = 2) { dataSource.getFeeds(any(), any(), any(), any()) }
    }

    @Test
    fun `피드 조회 시 네트워크 에러가 발생하면 에러 상태를 반환한다`() = runTest {
        // given: 네트워크 에러 응답 설정
        coEvery {
            dataSource.getFeeds(any(), any(), any(), any())
        } returns NetworkResult.Error(
            error = NetworkErrorType.Unexpected(null),
            message = "서버 연결 실패",
        )

        // when, then: asSnapshot은 에러 발생 시 특정 예외를 던짐
        val exception = runCatching {
            repository.getFeeds().asSnapshot()
        }.exceptionOrNull()
        assertNotNull(exception)
        assertTrue(exception is PagingApiCallException)

        // DB는 비어있어야 함
        val dbItems = database.feedDao().getAll().first()
        assertTrue(dbItems.isEmpty())
    }

    @Test
    fun `새로고침 시 기존 DB 데이터가 삭제되고 새로운 데이터로 교체된다`() = runTest {
        // given
        // 초기 데이터 저장 (ID 1, 2)
        val initialItems = List(2) { createFeedResponse(it + 1L) }
        database.feedDao().upsertAll(initialItems.map { it.toEntity() })

        // 새로운 REFRESH 데이터 준비 (ID 10, 11)
        val refreshItems = List(2) { createFeedResponse(it + 10L) }
        val networkResponse = FeedCursorPageResponse(
            items = refreshItems,
            nextCursor = null,
        )
        coEvery { dataSource.getFeeds(any(), any(), any(), any()) } returns NetworkResult.Success(networkResponse)

        // when: getFeeds 실행 (기본적으로 첫 로드는 REFRESH)
        repository.getFeeds().asSnapshot()

        // then: DB에는 새로운 데이터(ID 10, 11)만 있어야 함
        val dbItems = database.feedDao().getAll().first()
        assertEquals(2, dbItems.size)
        assertTrue(dbItems.any { it.userKeywordId == 10L })
        assertFalse(dbItems.any { it.userKeywordId == 1L }) // 이전 데이터는 삭제되어야 함
    }

    @Test
    fun `중복된 ID를 가진 데이터가 들어오면 새로운 데이터로 덮어쓴다`() = runTest {
        // given: 동일한 ID(1L)를 가진 두 가지 버전의 데이터 준비 (item1은 이미 DB에 있다고 가정)
        val item1 = createFeedResponse(1L).copy(description = "Old")
        database.feedDao().upsertAll(listOf(item1.toEntity()))
        val item2 = createFeedResponse(1L).copy(description = "New")
        coEvery {
            dataSource.getFeeds(any(), any(), any(), any())
        } returns NetworkResult.Success(FeedCursorPageResponse(listOf(item2), null))

        // when: 데이터 로드
        repository.getFeeds().asSnapshot()

        // then: DB에는 1개의 데이터만 존재해야 하며, 내용은 최신(New)이어야 함
        val dbItems = database.feedDao().getAll().first()
        assertEquals(1, dbItems.size)
        assertEquals("New", dbItems[0].description)
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
