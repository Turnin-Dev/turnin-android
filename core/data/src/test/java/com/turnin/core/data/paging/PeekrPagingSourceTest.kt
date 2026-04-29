package com.turnin.core.data.paging

import androidx.paging.PagingSource.LoadParams
import androidx.paging.PagingSource.LoadResult
import androidx.paging.PagingState
import com.turnin.core.data.source.network.error.NetworkErrorType
import com.turnin.core.data.source.network.error.toCommonErrorType
import com.turnin.core.data.source.network.util.NetworkResult
import com.turnin.core.domain.common.error.PagingApiCallException
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

private data class Item(val id: Int, val name: String) : Any()

private class ItemListResponse(
    override val hasNext: Boolean,
    override val list: List<Item>,
) : PagingDataHolder<Item>

private fun interface ApiCallMock {
    suspend fun call(page: Long): NetworkResult<ItemListResponse>
}

private fun createItems(startId: Int, count: Int): List<Item> =
    (startId until startId + count).map { id ->
        Item(id = id, name = "Item $id")
    }

class PeekrPagingSourceTest {
    private val mockApiCall: ApiCallMock = mockk()

    private fun createPagingSource(): PeekrPagingSource<Item, ItemListResponse> =
        PeekrPagingSource(
            apiCall = { page -> mockApiCall.call(page) },
        )

    @Test
    fun `최초 로드 (Initial Load) 성공 테스트`() = runTest {
        // given
        val items = createItems(1, PAGE_SIZE)
        val response = ItemListResponse(
            list = items,
            hasNext = true,
        )
        val expectedResult = LoadResult.Page(
            data = items,
            prevKey = null,
            nextKey = PeekrPagingSource.START_PAGE_INDEX + 1,
        )

        // 1(초기) 페이지 요청 시 성공 응답을 반환하도록 설정
        coEvery {
            mockApiCall.call(PeekrPagingSource.START_PAGE_INDEX)
        } returns NetworkResult.Success(response)

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
        val currentPage = 3L
        val startIndex = (currentPage - 1) * PAGE_SIZE + 1
        val items = createItems(startIndex.toInt(), PAGE_SIZE)
        val response = ItemListResponse(
            list = items,
            hasNext = true,
        )
        val expectedResult = LoadResult.Page(
            data = items,
            prevKey = currentPage - 1,
            nextKey = currentPage + 1,
        )

        // 3 페이지 요청 시 성공 응답을 반환하도록 설정
        coEvery {
            mockApiCall.call(currentPage)
        } returns NetworkResult.Success(response)

        // when
        val pagingSource = createPagingSource()
        val actualResult = pagingSource.load(
            LoadParams.Append(
                key = currentPage,
                loadSize = PAGE_SIZE,
                placeholdersEnabled = false,
            ),
        )

        // then
        assertEquals(expectedResult, actualResult)
    }

    @Test
    fun `마지막 페이지 로드 성공 (데이터 없음) 테스트`() = runTest {
        // given
        val currentPage = 5L
        val response = ItemListResponse(list = emptyList(), hasNext = false)
        val expectedResult = LoadResult.Page(
            data = emptyList(),
            prevKey = currentPage - 1,
            nextKey = null, // 데이터가 없으므로 nextKey는 null
        )

        // 5 페이지 요청 시 빈 리스트 응답을 반환하도록 설정
        coEvery {
            mockApiCall.call(currentPage)
        } returns NetworkResult.Success(response)

        // when
        val pagingSource = createPagingSource()
        val actualResult = pagingSource.load(
            LoadParams.Append(
                key = currentPage,
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
        val networkError = NetworkErrorType.Unexpected(null)
        val errorCode = "E001"
        val errorMessage = "Internal Server Error"

        // NetworkResult.Error 반환하도록 설정
        coEvery { mockApiCall.call(any()) } returns
            NetworkResult.Error(
                error = networkError,
                code = errorCode,
                message = errorMessage,
            )

        // when
        val pagingSource = createPagingSource()
        val result = pagingSource.load(
            LoadParams.Refresh(
                key = null,
                loadSize = PAGE_SIZE,
                placeholdersEnabled = false,
            ),
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
            LoadParams.Refresh(
                key = null,
                loadSize = PAGE_SIZE,
                placeholdersEnabled = false,
            ),
        )

        // then
        val error = result as LoadResult.Error
        assertTrue(error.throwable is PagingApiCallException)
    }

    @Test
    fun `getRefreshKey 로직 테스트`() {
        val pagingSource = createPagingSource()

        // PagingState 객체 생성 (일반적으로 테스트하기 까다로우므로 간단한 데이터로 구성)
        val pagingState = PagingState(
            pages = listOf(
                LoadResult.Page(
                    data = createItems(1, 10),
                    prevKey = null,
                    nextKey = 2L,
                ),
                LoadResult.Page(
                    data = createItems(11, 10),
                    prevKey = 1L,
                    nextKey = 3L,
                ),
            ),
            anchorPosition = 15, // anchorPosition이 15이고, pageSize가 10이라면 두 번째 페이지(key=2) 중간에 있다고 가정
            config = androidx.paging.PagingConfig(pageSize = PAGE_SIZE),
            leadingPlaceholderCount = 0,
        )

        /*
         * anchorPosition 15는
         * 1페이지 (0~9)와 2페이지 (10~19) 중 2페이지에 해당.
         * closestPageToPosition(15)는 key가 2L인 페이지를 반환한다.
         * prevKey (2-1=1) + 1 = 2L
         */
        val refreshKey = pagingSource.getRefreshKey(pagingState)

        // 2페이지를 리프레시 키로 예상
        assertEquals(2L, refreshKey)
    }

    companion object {
        private const val PAGE_SIZE = 10
    }
}
