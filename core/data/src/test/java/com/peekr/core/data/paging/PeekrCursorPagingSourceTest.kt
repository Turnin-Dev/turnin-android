package com.peekr.core.data.paging

import androidx.paging.PagingSource.LoadParams
import androidx.paging.PagingSource.LoadResult
import androidx.paging.PagingState
import com.peekr.core.data.source.network.error.NetworkErrorType
import com.peekr.core.data.source.network.error.toCommonErrorType
import com.peekr.core.data.source.network.util.CursorPageResponse
import com.peekr.core.data.source.network.util.NetworkResult
import com.peekr.core.domain.common.error.PagingApiCallException
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

private data class TestItem(val id: Int, val name: String)

private class ItemCursorResponse(
    override val items: List<TestItem>,
    override val nextCursor: Long?,
) : CursorPageResponse<TestItem, Long>

private fun interface CursorApiCallMock {
    suspend fun call(cursor: Long?): NetworkResult<ItemCursorResponse>
}

class PeekrCursorPagingSourceTest {
    private val mockApiCall: CursorApiCallMock = mockk()

    private fun createPagingSource(): PeekrCursorPagingSource<Long, TestItem> =
        PeekrCursorPagingSource(
            apiCall = { cursor -> mockApiCall.call(cursor) },
        )

    private fun createItems(startId: Int, count: Int): List<TestItem> =
        (startId until startId + count).map { id ->
            TestItem(id = id, name = "Item $id")
        }

    @Test
    fun `최초 로드 (Initial Load) 성공 테스트`() = runTest {
        // given
        val items = createItems(1, PAGE_SIZE)
        val nextCursor = 11L
        val response = ItemCursorResponse(
            items = items,
            nextCursor = nextCursor,
        )
        val expectedResult = LoadResult.Page(
            data = items,
            prevKey = null,
            nextKey = nextCursor,
        )

        // 커서가 null일 때 최초 로드 성공 응답 설정
        coEvery { mockApiCall.call(null) } returns NetworkResult.Success(response)

        // when
        val pagingSource = createPagingSource()
        val actualResult = pagingSource.load(
            LoadParams.Refresh(
                key = null,
                loadSize = PAGE_SIZE,
                placeholdersEnabled = false,
            ),
        )

        // then
        assertEquals(expectedResult, actualResult)
    }

    @Test
    fun `다음 페이지 로드 (Append Load) 성공 테스트`() = runTest {
        // given
        val currentCursor = 11L
        val nextCursor = 21L
        val items = createItems(11, PAGE_SIZE)
        val response = ItemCursorResponse(
            items = items,
            nextCursor = nextCursor,
        )
        val expectedResult = LoadResult.Page(
            data = items,
            prevKey = null,
            nextKey = nextCursor,
        )

        // 특정 커서 전달 시 성공 응답 설정
        coEvery { mockApiCall.call(currentCursor) } returns NetworkResult.Success(response)

        // when
        val pagingSource = createPagingSource()
        val actualResult = pagingSource.load(
            LoadParams.Append(
                key = currentCursor,
                loadSize = PAGE_SIZE,
                placeholdersEnabled = false,
            ),
        )

        // then
        assertEquals(expectedResult, actualResult)
    }

    @Test
    fun `마지막 페이지 로드 (다음 커서 없음) 테스트`() = runTest {
        // given
        val currentCursor = 41L
        val items = createItems(41, 5) // 마지막 5개 아이템
        val response = ItemCursorResponse(
            items = items,
            nextCursor = null, // 다음 페이지 없음
        )
        val expectedResult = LoadResult.Page(
            data = items,
            prevKey = null,
            nextKey = null,
        )

        coEvery { mockApiCall.call(currentCursor) } returns NetworkResult.Success(response)

        // when: nextKey가 null
        val pagingSource = createPagingSource()
        val actualResult = pagingSource.load(
            LoadParams.Append(
                key = currentCursor,
                loadSize = PAGE_SIZE,
                placeholdersEnabled = false,
            ),
        )

        // then
        assertEquals(expectedResult, actualResult)
    }

    @Test
    fun `API Error 발생 시 PagingApiCallException 반환 테스트`() = runTest {
        // given
        val networkError = NetworkErrorType.Network.HttpError(404)
        coEvery { mockApiCall.call(any()) } returns NetworkResult.Error(networkError)

        // when
        val pagingSource = createPagingSource()
        val result = pagingSource.load(
            LoadParams.Refresh(key = null, loadSize = PAGE_SIZE, placeholdersEnabled = false),
        )

        // then
        // PagingApiCallException이 포함되었는지 확인
        val exception = (result as LoadResult.Error).throwable
        assertTrue(exception is PagingApiCallException)
        // 예외 내부 데이터 검증
        val pagingException = exception as PagingApiCallException
        assertEquals(networkError.toCommonErrorType(), pagingException.error)
    }

    @Test
    fun `Exception 발생 시 예외 처리 테스트`() = runTest {
        // given
        coEvery { mockApiCall.call(any()) } throws IllegalStateException("Unexpected error")

        // when
        val pagingSource = createPagingSource()
        val result = pagingSource.load(
            LoadParams.Refresh(key = null, loadSize = PAGE_SIZE, placeholdersEnabled = false),
        )

        // then
        val error = result as LoadResult.Error
        assertTrue(error.throwable is PagingApiCallException)
    }

    @Test
    fun `getRefreshKey는 항상 null을 반환해야 한다`() {
        // given
        val pagingSource = createPagingSource()
        val pagingState = PagingState<Long, TestItem>(
            pages = emptyList(),
            anchorPosition = 1,
            config = androidx.paging.PagingConfig(pageSize = PAGE_SIZE),
            leadingPlaceholderCount = 0,
        )

        // when
        val refreshKey = pagingSource.getRefreshKey(pagingState)

        // then
        assertEquals(null, refreshKey)
    }

    companion object {
        private const val PAGE_SIZE = 10
    }
}
