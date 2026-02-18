package com.peekr.core.data.repository

import androidx.paging.testing.asSnapshot
import com.peekr.core.data.MockLog
import com.peekr.core.data.paging.PagingApiCallException
import com.peekr.core.data.source.network.datasource.DiscoverNetworkDataSource
import com.peekr.core.data.source.network.dto.discover.response.DiscoverContextCursorPageResponse
import com.peekr.core.data.source.network.dto.discover.response.DiscoverContextResponse
import com.peekr.core.data.source.network.dto.discover.response.DiscoverKeywordResponse
import com.peekr.core.data.source.network.dto.discover.response.DiscoverUserResponse
import com.peekr.core.data.source.network.dto.discover.response.toDomainModel
import com.peekr.core.data.source.network.util.NetworkResult
import com.peekr.core.domain.discover.model.DiscoverPagingTokens
import com.peekr.core.domain.model.UserId
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class DiscoverRepositoryImplTest {
    private val dataSource: DiscoverNetworkDataSource = mockk()
    private val repository = DiscoverRepositoryImpl(dataSource)

    @Before
    fun setUp() {
        MockLog.mock()
    }

    @After
    fun tearDown() {
        MockLog.cleanUp()
    }

    @Test
    fun `탐색 컨텍스트 조회 - 성공 테스트`() = runTest {
        // given: 2페이지까지 페이지네이션 가능한 테스트 데이터 설정
        val pageSize = 5
        val expectedCursorPage1 = createCursorPageResponse(nextCursor = 5L, pageSize)
        val expectedCursorPage2 = createCursorPageResponse(nextCursor = null, pageSize)

        coEvery {
            dataSource.getDiscoverContexts(any(), any(), any())
        } answers {
            val cursor = secondArg<Long?>()
            when (cursor) {
                null -> NetworkResult.Success(expectedCursorPage1)
                5L -> NetworkResult.Success(expectedCursorPage2)
                else -> NetworkResult.Success(
                    DiscoverContextCursorPageResponse(items = emptyList(), nextCursor = null),
                )
            }
        }

        // when
        val discoverContexts = repository.getDiscoverContexts(UserId(1L)).asSnapshot()

        // then
        // 2개의 페이지만 테스트했으므로 개수는 (페이지 사이즈 * 2)이어야 한다.
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

    companion object {
        /**
         * 테스트용 커서 페이지 응답 바디 생성기
         *
         * 목록 데이터는 중요하지 않고 다음 커서를 직접 설정해서 테스트를 진행한다.
         */
        private fun createCursorPageResponse(
            nextCursor: Long?,
            pageSize: Int,
        ): DiscoverContextCursorPageResponse = DiscoverContextCursorPageResponse(
            items = List(pageSize) {
                DiscoverContextResponse(
                    user = DiscoverUserResponse(
                        userId = it.toLong(),
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
