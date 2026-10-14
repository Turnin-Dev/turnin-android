package com.turnin.core.data.paging

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingConfig
import androidx.paging.PagingSource
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.turnin.core.data.MainDispatcherRule
import com.turnin.core.data.source.local.database.TurninDatabase
import com.turnin.core.data.source.local.database.entity.FeedEntity
import com.turnin.core.data.source.local.database.entity.FeedRemoteKeyEntity
import com.turnin.core.data.source.network.datasource.FeedNetworkDataSource
import com.turnin.core.data.source.network.dto.feed.FeedCursorPageResponse
import com.turnin.core.data.source.network.dto.feed.FeedResponse
import com.turnin.core.data.source.network.error.NetworkErrorType
import com.turnin.core.data.source.network.util.NetworkResult
import com.turnin.core.domain.common.error.PagingApiCallException
import com.turnin.core.domain.feed.model.FeedType
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
@OptIn(
    ExperimentalPagingApi::class,
    ExperimentalCoroutinesApi::class,
)
class FeedRemoteMediatorTest {
    @get:Rule
    var mainDispatcherRule = MainDispatcherRule()

    private lateinit var database: TurninDatabase
    private lateinit var dataSource: FeedNetworkDataSource
    private lateinit var remoteMediator: FeedRemoteMediator

    private val feedType = FeedType.ALL
    private val pagingConfig = PagingConfig(pageSize = 2)

    @Before
    fun setup() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            TurninDatabase::class.java,
        ).allowMainThreadQueries().build()

        dataSource = mockk()

        remoteMediator = FeedRemoteMediator(
            feedType = feedType,
            feedNetworkDataSource = dataSource,
            database = database,
        )
    }

    @After
    fun tearDown() {
        database.close()
    }

    /**
     * state.lastItemOrNull()이 마지막 페이지의 마지막 아이템을 반환하도록
     * PagingState를 구성하는 헬퍼
     */
    private fun pagingStateWithLastItem(vararg items: FeedEntity): PagingState<Int, FeedEntity> = PagingState(
        pages = listOf(
            PagingSource.LoadResult.Page(
                data = items.toList(),
                prevKey = null,
                nextKey = null,
            ),
        ),
        anchorPosition = 0,
        config = pagingConfig,
        leadingPlaceholderCount = 0,
    )

    private fun emptyPagingState(): PagingState<Int, FeedEntity> = PagingState(
        pages = listOf(),
        anchorPosition = null,
        config = pagingConfig,
        leadingPlaceholderCount = 0,
    )

    private fun createFeedEntity(id: Long, order: Int = 0): FeedEntity = FeedEntity(
        type = feedType,
        userKeywordId = id,
        userId = id,
        userName = "user$id",
        profileImageUrl = null,
        keywordId = id,
        keyword = "keyword$id",
        description = "description$id",
        createdAt = 0L,
        sortOrder = order,
    )

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
        )

    // ---------------------------------------------------------------
    // REFRESH
    // ---------------------------------------------------------------

    @Test
    fun `REFRESH 성공 시 아이템과 RemoteKey가 저장된다`() = runTest {
        // given
        val response = FeedCursorPageResponse(
            items = List(2) { createFeedResponse(it + 1L) },
            nextCursor = "first-cursor",
        )
        coEvery { dataSource.getFeeds(any(), any(), any()) } returns
            NetworkResult.Success(response)

        // when
        val result = remoteMediator.load(
            loadType = LoadType.REFRESH,
            state = emptyPagingState(),
        )

        // then
        assertTrue(result is RemoteMediator.MediatorResult.Success)
        assertFalse((result as RemoteMediator.MediatorResult.Success).endOfPaginationReached)

        val dbItems = database.feedDao().getAll(feedType).first()
        assertEquals(2, dbItems.size)

        val remoteKey = database.feedRemoteKeyDao().getRemoteKeyByType(feedType)
        assertEquals("first-cursor", remoteKey?.cursor)
    }

    @Test
    fun `REFRESH 응답의 nextCursor가 null이면 RemoteKey가 저장되지 않고 endOfPaginationReached는 true다`() =
        runTest {
            // given
            val response = FeedCursorPageResponse(
                items = List(2) { createFeedResponse(it + 1L) },
                nextCursor = null,
            )
            coEvery { dataSource.getFeeds(any(), any(), any()) } returns
                NetworkResult.Success(response)

            // when
            val result = remoteMediator.load(
                loadType = LoadType.REFRESH,
                state = emptyPagingState(),
            )

            // then
            assertTrue(result is RemoteMediator.MediatorResult.Success)
            assertTrue((result as RemoteMediator.MediatorResult.Success).endOfPaginationReached)

            val dbItems = database.feedDao().getAll(feedType).first()
            assertEquals(2, dbItems.size)

            val remoteKey = database.feedRemoteKeyDao().getRemoteKeyByType(feedType)
            assertNull(remoteKey)
        }

    @Test
    fun `REFRESH 시 기존 캐시와 RemoteKey를 먼저 삭제한다`() = runTest {
        // given: DB에 이미 이전 데이터/키가 남아있는 상태
        database.feedDao().upsertAll(listOf(createFeedEntity(id = 999L, order = 0)))
        database.feedRemoteKeyDao().upsert(FeedRemoteKeyEntity(feedType, "stale-cursor"))

        val response = FeedCursorPageResponse(
            items = List(1) { createFeedResponse(1L) },
            nextCursor = "new-cursor",
        )
        coEvery { dataSource.getFeeds(any(), any(), any()) } returns
            NetworkResult.Success(response)

        // when
        remoteMediator.load(
            loadType = LoadType.REFRESH,
            state = emptyPagingState(),
        )

        // then: 기존 데이터는 사라지고 새 데이터만 남는다
        val dbItems = database.feedDao().getAll(feedType).first()
        assertEquals(1, dbItems.size)
        assertEquals(1L, dbItems.first().userKeywordId)

        val remoteKey = database.feedRemoteKeyDao().getRemoteKeyByType(feedType)
        assertEquals("new-cursor", remoteKey?.cursor)
    }

    @Test
    fun `REFRESH 응답의 items가 비어있으면 저장 없이 endOfPaginationReached는 true다`() = runTest {
        // given
        val response = FeedCursorPageResponse(items = emptyList(), nextCursor = "ignored-cursor")
        coEvery { dataSource.getFeeds(any(), any(), any()) } returns
            NetworkResult.Success(response)

        // when
        val result = remoteMediator.load(
            loadType = LoadType.REFRESH,
            state = emptyPagingState(),
        )

        // then
        assertTrue(result is RemoteMediator.MediatorResult.Success)
        assertTrue((result as RemoteMediator.MediatorResult.Success).endOfPaginationReached)

        val dbItems = database.feedDao().getAll(feedType).first()
        assertTrue(dbItems.isEmpty())

        // items가 비어있으면 RemoteKey 저장 로직 자체를 타지 않음
        val remoteKey = database.feedRemoteKeyDao().getRemoteKeyByType(feedType)
        assertNull(remoteKey)
    }

    // ---------------------------------------------------------------
    // APPEND
    // ---------------------------------------------------------------

    @Test
    fun `APPEND 성공 시 기존 데이터에 이어서 저장되고 sortOrder가 증가한다`() = runTest {
        // given: 이미 REFRESH로 2건이 저장되어 있고 RemoteKey도 있는 상태
        val existingEntities = List(2) { createFeedEntity(id = it + 1L, order = it) }
        database.feedDao().upsertAll(existingEntities)
        database.feedRemoteKeyDao().upsert(FeedRemoteKeyEntity(feedType, "first-cursor"))

        val response = FeedCursorPageResponse(
            items = List(2) { createFeedResponse(it + 3L) },
            nextCursor = "second-cursor",
        )
        coEvery { dataSource.getFeeds(any(), any(), any()) } returns
            NetworkResult.Success(response)

        // when: 마지막으로 로드된 아이템(userKeywordId=2)을 state에 반영해 APPEND 호출
        val result = remoteMediator.load(
            loadType = LoadType.APPEND,
            state = pagingStateWithLastItem(existingEntities.last()),
        )

        // then
        assertTrue(result is RemoteMediator.MediatorResult.Success)
        assertFalse((result as RemoteMediator.MediatorResult.Success).endOfPaginationReached)

        val dbItems = database.feedDao().getAll(feedType).first()
        assertEquals(4, dbItems.size)

        // 새로 추가된 아이템들의 sortOrder가 기존 countByType(=2) 이후로 이어지는지 확인
        val newItems = dbItems.filter { it.userKeywordId in listOf(3L, 4L) }
            .sortedBy { it.sortOrder }
        assertEquals(listOf(2, 3), newItems.map { it.sortOrder })

        val remoteKey = database.feedRemoteKeyDao().getRemoteKeyByType(feedType)
        assertEquals("second-cursor", remoteKey?.cursor)
    }

    @Test
    fun `APPEND 시 마지막 아이템이 없으면 API 호출 없이 endOfPaginationReached는 false다`() = runTest {
        // when: state에 아이템이 하나도 없는 상태 (REFRESH 전이거나 진행 중)
        val result = remoteMediator.load(
            loadType = LoadType.APPEND,
            state = emptyPagingState(),
        )

        // then
        assertTrue(result is RemoteMediator.MediatorResult.Success)
        assertFalse((result as RemoteMediator.MediatorResult.Success).endOfPaginationReached)
        coVerify(exactly = 0) { dataSource.getFeeds(any(), any(), any()) }
    }

    @Test
    fun `APPEND 시 RemoteKey가 없으면 API 호출 없이 endOfPaginationReached는 true다`() = runTest {
        // given: 아이템은 있지만 RemoteKey는 저장되어 있지 않음
        val existingEntity = createFeedEntity(id = 1L, order = 0)
        database.feedDao().upsertAll(listOf(existingEntity))

        // when
        val result = remoteMediator.load(
            loadType = LoadType.APPEND,
            state = pagingStateWithLastItem(existingEntity),
        )

        // then
        assertTrue(result is RemoteMediator.MediatorResult.Success)
        assertTrue((result as RemoteMediator.MediatorResult.Success).endOfPaginationReached)
        coVerify(exactly = 0) { dataSource.getFeeds(any(), any(), any()) }
    }

    @Test
    fun `APPEND를 연속으로 두 번 호출해도 sortOrder가 누적되어 이어진다`() = runTest {
        // given: REFRESH로 2건(sortOrder 0,1) 저장됨
        val initial = List(2) { createFeedEntity(id = it + 1L, order = it) }
        database.feedDao().upsertAll(initial)
        database.feedRemoteKeyDao().upsert(FeedRemoteKeyEntity(feedType, "cursor-1"))

        val secondPage = FeedCursorPageResponse(
            items = List(2) { createFeedResponse(it + 3L) }, // id 3,4
            nextCursor = "cursor-2",
        )
        val thirdPage = FeedCursorPageResponse(
            items = List(2) { createFeedResponse(it + 5L) }, // id 5,6
            nextCursor = null,
        )
        coEvery { dataSource.getFeeds(any(), any(), any()) } returnsMany listOf(
            NetworkResult.Success(secondPage),
            NetworkResult.Success(thirdPage),
        )

        // when: 첫 번째 APPEND (기존 마지막 아이템 = id 2)
        remoteMediator.load(
            loadType = LoadType.APPEND,
            state = pagingStateWithLastItem(initial.last()),
        )

        val afterFirstAppend = database.feedDao().getAll(feedType).first()
        val lastAfterFirstAppend = afterFirstAppend.maxBy { it.sortOrder }

        // 두 번째 APPEND (기존 마지막 아이템 = id 4, sortOrder 3)
        val result = remoteMediator.load(
            loadType = LoadType.APPEND,
            state = pagingStateWithLastItem(lastAfterFirstAppend),
        )

        // then
        assertTrue(result is RemoteMediator.MediatorResult.Success)
        assertTrue((result as RemoteMediator.MediatorResult.Success).endOfPaginationReached)

        val finalItems = database.feedDao().getAll(feedType).first()
        assertEquals(6, finalItems.size)

        val idsSortedByOrder = finalItems.sortedBy { it.sortOrder }.map { it.userKeywordId }
        assertEquals(listOf(1L, 2L, 3L, 4L, 5L, 6L), idsSortedByOrder)

        val remoteKey = database.feedRemoteKeyDao().getRemoteKeyByType(feedType)
        assertNull(remoteKey) // 마지막 페이지 nextCursor가 null이므로 제거됨
    }

    // ---------------------------------------------------------------
    // PREPEND
    // ---------------------------------------------------------------

    @Test
    fun `PREPEND는 항상 endOfPaginationReached true를 반환하고 API를 호출하지 않는다`() = runTest {
        // when
        val result = remoteMediator.load(
            loadType = LoadType.PREPEND,
            state = emptyPagingState(),
        )

        // then
        assertTrue(result is RemoteMediator.MediatorResult.Success)
        assertTrue((result as RemoteMediator.MediatorResult.Success).endOfPaginationReached)
        coVerify(exactly = 0) { dataSource.getFeeds(any(), any(), any()) }
    }

    // ---------------------------------------------------------------
    // 에러 처리
    // ---------------------------------------------------------------

    @Test
    fun `네트워크 응답이 Error면 MediatorResult Error를 반환하고 DB는 변경되지 않는다`() = runTest {
        // given
        coEvery { dataSource.getFeeds(any(), any(), any()) } returns
            NetworkResult.Error(error = NetworkErrorType.Unexpected(null), message = "network error")

        // when
        val result = remoteMediator.load(
            loadType = LoadType.REFRESH,
            state = emptyPagingState(),
        )

        // then
        assertTrue(result is RemoteMediator.MediatorResult.Error)
        assertTrue((result as RemoteMediator.MediatorResult.Error).throwable is PagingApiCallException)

        val dbItems = database.feedDao().getAll(feedType).first()
        assertTrue(dbItems.isEmpty())
    }

    @Test
    fun `dataSource에서 예외가 발생하면 MediatorResult Error로 감싸 반환한다`() = runTest {
        // given
        coEvery { dataSource.getFeeds(any(), any(), any()) } throws IllegalStateException("boom")

        // when
        val result = remoteMediator.load(
            loadType = LoadType.REFRESH,
            state = emptyPagingState(),
        )

        // then
        assertTrue(result is RemoteMediator.MediatorResult.Error)
        assertTrue((result as RemoteMediator.MediatorResult.Error).throwable is IllegalStateException)
    }
}
