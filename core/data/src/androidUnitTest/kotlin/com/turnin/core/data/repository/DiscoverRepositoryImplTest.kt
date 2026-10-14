package com.turnin.core.data.repository

import androidx.paging.testing.asSnapshot
import com.turnin.core.data.FakeMemoryCache
import com.turnin.core.data.MockLog
import com.turnin.core.data.source.network.datasource.DiscoverNetworkDataSource
import com.turnin.core.data.source.network.dto.discover.response.DiscoverContextCursorPageResponse
import com.turnin.core.data.source.network.dto.discover.response.DiscoverContextResponse
import com.turnin.core.data.source.network.dto.discover.response.DiscoverKeywordResponse
import com.turnin.core.data.source.network.dto.discover.response.DiscoverUserResponse
import com.turnin.core.data.source.network.dto.discover.response.toDomainModel
import com.turnin.core.data.source.network.util.NetworkResult
import com.turnin.core.domain.common.error.PagingApiCallException
import com.turnin.core.domain.discover.model.DiscoverCacheKey
import com.turnin.core.domain.discover.model.DiscoverPagingTokens
import com.turnin.core.domain.model.UserId
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class DiscoverRepositoryImplTest {
    private val dataSource: DiscoverNetworkDataSource = mockk()
    private val memoryCache = FakeMemoryCache<DiscoverCacheKey, DiscoverContextCursorPageResponse>()
    private val repository = DiscoverRepositoryImpl(dataSource, memoryCache)

    @Before
    fun setUp() {
        MockLog.mock()
        memoryCache.clear()
    }

    @After
    fun tearDown() {
        MockLog.cleanUp()
    }

    @Test
    fun `탐색 컨텍스트 조회 - 성공 테스트`() = runTest {
        // given: 2페이지까지 페이지네이션 가능한 테스트 데이터 설정
        val pageSize = DiscoverPagingTokens.PAGE_SIZE
        val expectedCursorPage1 = createCursorPageResponse("cursor-5", pageSize, startUserId = 1L)
        val expectedCursorPage2 = createCursorPageResponse(null, pageSize, startUserId = pageSize + 1L)

        coEvery {
            dataSource.getDiscoverContexts(any(), any(), any())
        } answers {
            val cursor = secondArg<String?>()
            when (cursor) {
                null -> NetworkResult.Success(expectedCursorPage1)
                "cursor-5" -> NetworkResult.Success(expectedCursorPage2)
                else -> NetworkResult.Success(
                    DiscoverContextCursorPageResponse(items = emptyList(), nextCursor = null),
                )
            }
        }

        // when
        val discoverContexts = repository.getDiscoverContexts(UserId(1L)).asSnapshot()

        // then
        assertEquals(pageSize * 2, discoverContexts.size)
        assertEquals(expectedCursorPage1.items.first().toDomainModel(), discoverContexts.first())
    }

    @Test
    fun `탐색 컨텍스트 조회 - 데이터 소스에서 예외 발생 시 정의된 예외를 반환한다`() = runTest {
        // given
        val pageSize = DiscoverPagingTokens.PAGE_SIZE
        coEvery {
            dataSource.getDiscoverContexts(any(), null, pageSize)
        } throws Exception()

        // when
        val exception = runCatching {
            repository.getDiscoverContexts(UserId(1L)).asSnapshot()
        }.exceptionOrNull()

        // then
        assertTrue(exception is PagingApiCallException)
    }

    @Test
    fun `탐색 컨텍스트 조회 - 중복된 사용자 조회 시 제외하고 반환한다`() = runTest {
        // given: 중복된 2개의 페이지 생성 (모든 사용자가 중복)
        val pageSize = DiscoverPagingTokens.PAGE_SIZE
        val expectedCursorPage1 = createCursorPageResponse("cursor-5", pageSize, startUserId = 1L)
        val expectedCursorPage2 = createCursorPageResponse(null, pageSize, startUserId = 1L)

        coEvery {
            dataSource.getDiscoverContexts(any(), any(), any())
        } answers {
            val cursor = secondArg<String?>()
            when (cursor) {
                null -> NetworkResult.Success(expectedCursorPage1)
                "cursor-5" -> NetworkResult.Success(expectedCursorPage2)
                else -> NetworkResult.Success(
                    DiscoverContextCursorPageResponse(items = emptyList(), nextCursor = null),
                )
            }
        }

        // when
        val discoverContexts = repository.getDiscoverContexts(UserId(1L)).asSnapshot()

        // then: 중복된 사용자는 제외하고 반환한다
        assertEquals(pageSize, discoverContexts.size)
        coVerify { dataSource.getDiscoverContexts(any(), "cursor-5", pageSize) }
        assertEquals(
            (1L..pageSize).toList(),
            discoverContexts.map { it.user.userId.value },
        )
    }

    @Test
    fun `캐시 히트 - 1페이지 캐시가 있으면 네트워크 요청 없이 반환한다`() = runTest {
        // given
        val pageSize = DiscoverPagingTokens.PAGE_SIZE
        val userId = UserId(1L)
        val cachedPage1 = createCursorPageResponse(nextCursor = null, pageSize)
        memoryCache[DiscoverCacheKey(userId, null)] = cachedPage1

        // when
        repository.getDiscoverContexts(userId).asSnapshot()

        // then
        coVerify(exactly = 0) { dataSource.getDiscoverContexts(any(), any(), any()) }
    }

    @Test
    fun `캐시 저장 - 1페이지 응답을 캐시에 저장한다`() = runTest {
        // given
        val pageSize = DiscoverPagingTokens.PAGE_SIZE
        val userId = UserId(1L)
        val page1Response = createCursorPageResponse(nextCursor = null, pageSize)

        coEvery {
            dataSource.getDiscoverContexts(userId.value, null, pageSize)
        } returns NetworkResult.Success(page1Response)

        // when
        repository.getDiscoverContexts(userId).asSnapshot()

        // then
        assertEquals(page1Response, memoryCache[DiscoverCacheKey(userId, null)])
    }

    @Test
    fun `캐시 저장 - 2페이지 응답을 캐시에 저장한다`() = runTest {
        // given
        val pageSize = DiscoverPagingTokens.PAGE_SIZE
        val userId = UserId(1L)
        val page1Response = createCursorPageResponse(nextCursor = "cursor-5", pageSize)
        val page2Response = createCursorPageResponse(nextCursor = null, pageSize)

        coEvery {
            dataSource.getDiscoverContexts(any(), any(), any())
        } answers {
            when (secondArg<String?>()) {
                null -> NetworkResult.Success(page1Response)
                "cursor-5" -> NetworkResult.Success(page2Response)
                else -> NetworkResult.Success(
                    DiscoverContextCursorPageResponse(items = emptyList(), nextCursor = null),
                )
            }
        }

        // when
        repository.getDiscoverContexts(userId).asSnapshot()

        // then
        assertEquals(page2Response, memoryCache[DiscoverCacheKey(userId, "cursor-5")])
    }

    @Test
    fun `캐시 저장 - 3페이지 이상은 캐시에 저장하지 않는다`() = runTest {
        // given
        val pageSize = DiscoverPagingTokens.PAGE_SIZE
        val userId = UserId(1L)
        val page1Response = createCursorPageResponse(nextCursor = "cursor-5", pageSize)
        val page2Response = createCursorPageResponse(nextCursor = "cursor-10", pageSize)
        val page3Response = createCursorPageResponse(nextCursor = null, pageSize)

        coEvery {
            dataSource.getDiscoverContexts(any(), any(), any())
        } answers {
            when (secondArg<String?>()) {
                null -> NetworkResult.Success(page1Response)
                "cursor-5" -> NetworkResult.Success(page2Response)
                "cursor-10" -> NetworkResult.Success(page3Response)
                else -> NetworkResult.Success(
                    DiscoverContextCursorPageResponse(items = emptyList(), nextCursor = null),
                )
            }
        }

        // when
        repository.getDiscoverContexts(userId).asSnapshot()

        // then
        assertNull(memoryCache[DiscoverCacheKey(userId, "cursor-10")])
    }

    @Test
    fun `캐시 무효화 - 1페이지와 2페이지 캐시를 삭제한다`() {
        // given
        val userId = UserId(1L)
        val page1Response = createCursorPageResponse(nextCursor = "cursor-5", pageSize = 5)
        val page2Response = createCursorPageResponse(nextCursor = null, pageSize = 5)
        memoryCache[DiscoverCacheKey(userId, null)] = page1Response
        memoryCache[DiscoverCacheKey(userId, "cursor-5")] = page2Response

        // when
        repository.invalidateCache(userId)

        // then
        assertNull(memoryCache[DiscoverCacheKey(userId, null)])
        assertNull(memoryCache[DiscoverCacheKey(userId, "cursor-5")])
    }

    @Test
    fun `캐시 무효화 - 1페이지 캐시가 없으면 2페이지 캐시는 삭제되지 않는다`() {
        // given
        val userId = UserId(1L)
        val page2Response = createCursorPageResponse(nextCursor = null, pageSize = 5)
        memoryCache[DiscoverCacheKey(userId, "cursor-5")] = page2Response

        // when
        repository.invalidateCache(userId)

        // then: 1페이지 캐시가 없으면 2페이지 커서를 알 수 없으므로 2페이지 캐시는 남아있어야 한다
        assertNotNull(memoryCache[DiscoverCacheKey(userId, "cursor-5")])
    }

    companion object {
        /**
         * 테스트용 커서 페이지 응답 바디 생성기
         *
         * 목록 데이터는 중요하지 않고 다음 커서를 직접 설정해서 테스트를 진행한다.
         */
        private fun createCursorPageResponse(
            nextCursor: String?,
            pageSize: Int,
            startUserId: Long = 1L,
        ): DiscoverContextCursorPageResponse = DiscoverContextCursorPageResponse(
            items = List(pageSize) {
                DiscoverContextResponse(
                    user = DiscoverUserResponse(
                        userId = it.toLong() + startUserId,
                        userName = "name$it",
                        displayId = "did$it",
                        profileImageUrl = null,
                    ),
                    keywords = listOf(
                        DiscoverKeywordResponse(
                            keywordId = 1L,
                            keywordName = "keyword",
                            userKeywordId = 1L,
                        ),
                    ),
                )
            },
            nextCursor = nextCursor,
        )
    }
}
