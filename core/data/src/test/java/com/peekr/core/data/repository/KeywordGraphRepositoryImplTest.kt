package com.peekr.core.data.repository

import androidx.paging.testing.asSnapshot
import com.peekr.core.data.MockLog
import com.peekr.core.data.paging.PagingApiCallException
import com.peekr.core.data.source.network.datasource.KeywordGraphNetworkDataSource
import com.peekr.core.data.source.network.dto.keywordGraph.response.KeywordNodeResponse
import com.peekr.core.data.source.network.dto.keywordGraph.response.NodeContextCursorPageResponse
import com.peekr.core.data.source.network.dto.keywordGraph.response.NodeContextResponse
import com.peekr.core.data.source.network.dto.keywordGraph.response.UserNodeResponse
import com.peekr.core.data.source.network.dto.keywordGraph.response.toDomainModel
import com.peekr.core.data.source.network.util.NetworkResult
import com.peekr.core.domain.keywordGraph.model.KeywordGraphPagingTokens
import com.peekr.core.domain.model.UserId
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

/**
 * 키워드 그래프 리포지토리 + 페이징 테스트가 포함
 */
class KeywordGraphRepositoryImplTest {
    private val dataSource: KeywordGraphNetworkDataSource = mockk()
    private val repository = KeywordGraphRepositoryImpl(dataSource)

    @Before
    fun setUp() {
        MockLog.mock()
    }

    @After
    fun tearDown() {
        MockLog.cleanUp()
    }

    @Test
    fun `노드 컨텍스트 조회 - 성공 테스트`() = runTest {
        // given: 2페이지까지 페이지네이션 가능한 테스트 데이터 설정
        val pageSize = KeywordGraphPagingTokens.PAGE_SIZE
        val expectedCursorPage1 = createNodeContextCursorPageResponse(
            cursor = null,
            pageSize = pageSize,
        )
        val expectedCursorPage2 = createNodeContextCursorPageResponse(
            cursor = 2,
            pageSize = pageSize,
        )

        coEvery {
            dataSource.getNodeContexts(any(), null, pageSize)
        } returns NetworkResult.Success(expectedCursorPage1)
        coEvery {
            dataSource.getNodeContexts(any(), 1, pageSize)
        } returns NetworkResult.Success(expectedCursorPage2)

        // when
        val nodeContexts = repository.getNodeContexts(UserId(1L)).asSnapshot()

        // then
        // 2개의 페이지만 테스트했으므로 개수는 (페이지 사이즈 * 2)이어야 한다.
        assertEquals(pageSize * 2, nodeContexts.size)
        assertEquals(expectedCursorPage1.items.first().toDomainModel(), nodeContexts.first())
    }

    @Test
    fun `노드 컨텍스트 조회 - 데이터 소스에서 예외 발생 시 정의된 예외를 반환한다`() = runTest {
        // given
        val pageSize = KeywordGraphPagingTokens.PAGE_SIZE
        coEvery {
            dataSource.getNodeContexts(any(), null, pageSize)
        } throws Exception()

        // when
        val exception = runCatching {
            repository.getNodeContexts(UserId(1L)).asSnapshot()
        }.exceptionOrNull()

        // then
        assertTrue(exception is PagingApiCallException)
    }

    companion object {
        /**
         * 테스트용 커서 페이지 응답 바디 생성기
         *
         * 단순하게 [cursor]가 1씩 증가하도록 구성
         */
        private fun createNodeContextCursorPageResponse(
            cursor: Long?,
            pageSize: Int,
        ): NodeContextCursorPageResponse =
            NodeContextCursorPageResponse(
                items = List(pageSize) {
                    NodeContextResponse(
                        userNode = UserNodeResponse(
                            userId = it.toLong(),
                            userName = "name",
                            profileImageUrl = null,
                        ),
                        keywordNodes = listOf(
                            KeywordNodeResponse(
                                keywordId = 1L,
                                keywordName = "keyword",
                                userKeywordId = 1L,
                            ),
                        ),
                    )
                },
                nextCursor = cursor?.let { it + 1 } ?: 1,
            )
    }
}
