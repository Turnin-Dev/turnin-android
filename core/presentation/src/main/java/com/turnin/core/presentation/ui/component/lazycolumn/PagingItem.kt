package com.turnin.core.presentation.ui.component.lazycolumn

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import com.turnin.core.presentation.common.util.rememberPagingMediatorRefreshing

/**
 * [LazyPagingItems]의 로딩 상태(초기 로딩, 추가 로딩, 에러)를
 * [LazyColumn] 등에서 통합 관리하기 위한 확장 함수
 *
 * @param pagingItems 페이징 아이템
 * @param key [LazyColumn] key
 * @param contentType [LazyColumn] contentType
 * @param isPagingMediatorRefreshing RemoteMediator 사용 시 [rememberPagingMediatorRefreshing]의
 *   반환값을 전달해야 한다. mediator.refresh가 끝난 뒤에도 itemSnapshotList가 아직 갱신되지 않은
 *   구간(stale 구간)을 로딩 중으로 취급해 이전 데이터가 잠깐 노출되는 것을 방지한다.
 *   RemoteMediator를 쓰지 않는 Pager라면 생략(기본값 false).
 * @param skeletonCount 표시할 스켈레톤 개수
 * @param skeleton 스켈레톤 뷰
 * @param initialError 초기 에러 뷰
 * @param footerError Footer 에러 뷰
 * @param refreshError Refresh 에러 뷰
 * @param emptyGuidance 빈 리스트인 경우 표시할 뷰
 * @param lastContent 더 이상 표시할 데이터가 없는 경우에 표시할 뷰
 * @param pagingContent 페이징 컨텐츠
 */
fun <T : Any> LazyListScope.pagingItem(
    pagingItems: LazyPagingItems<T>,
    key: ((Int) -> Any)? = null,
    contentType: (Int) -> Any? = { null },
    isPagingMediatorRefreshing: Boolean = false,
    skeletonCount: Int = 5,
    skeleton: @Composable (LazyItemScope.(Int) -> Unit),
    initialError: @Composable (LazyItemScope.(Throwable) -> Unit),
    footerError: @Composable (LazyItemScope.(Throwable) -> Unit),
    refreshError: @Composable (LazyItemScope.(Throwable) -> Unit)? = null,
    emptyGuidance: (@Composable (LazyItemScope.() -> Unit))? = null,
    lastContent: (@Composable (LazyItemScope.() -> Unit))? = null,
    pagingContent: @Composable (LazyItemScope.(Int) -> Unit),
) {
    val refreshState = pagingItems.loadState.refresh
    val appendState = pagingItems.loadState.append
    val sourceStates = pagingItems.loadState.source
    val itemCount = pagingItems.itemCount

    val isInitialState = sourceStates.refresh is LoadState.NotLoading &&
        !sourceStates.refresh.endOfPaginationReached

    val isRefreshLoading = refreshState is LoadState.Loading || isPagingMediatorRefreshing
    val isRefreshError = refreshState is LoadState.Error
    val isInitialError = refreshState is LoadState.Error && itemCount == 0
    val isAppendLoading = appendState is LoadState.Loading
    val isAppendError = appendState is LoadState.Error
    val isItemsExists = itemCount > 0
    val isItemsEmpty = isInitialState &&
        refreshState is LoadState.NotLoading &&
        appendState is LoadState.NotLoading &&
        itemCount == 0 &&
        !isPagingMediatorRefreshing
    val isEndOfReached = appendState is LoadState.NotLoading &&
        appendState.endOfPaginationReached

    when {
        // Refresh 상태 로딩
        isRefreshLoading -> {
            items(skeletonCount) {
                skeleton(it)
            }
        }

        // 초기 에러
        isInitialError -> {
            item {
                initialError(refreshState.error)
            }
        }

        // 페이징 아이템 존재
        isItemsExists -> {
            // Refresh 에러 및 초기 데이터 로드 에러
            if (isRefreshError) {
                item {
                    refreshError?.invoke(this, refreshState.error)
                }
            }

            // 페이징 아이템 정상 로드
            items(
                count = itemCount,
                key = key,
                contentType = contentType,
                itemContent = pagingContent,
            )

            item {
                // Append 상태 로딩
                if (isAppendLoading) {
                    skeleton(0)
                }
                // Append 에러
                if (isAppendError) {
                    footerError(appendState.error)
                }
            }

            // 리스트의 끝 도달
            if (isEndOfReached) {
                // 모든 데이터를 불러온 경우
                // (더 이상 로드할 데이터가 없거나 정상 상태 or 마지막 페이지인 상태)
                lastContent?.let { lc ->
                    item {
                        lc.invoke(this)
                    }
                }
            }
        }

        // 빈 리스트
        isItemsEmpty -> {
            // 페이징 아이템이 비어있는 경우 빈 화면 안내 뷰 표시
            emptyGuidance?.let {
                item {
                    Box(
                        modifier = Modifier.fillParentMaxSize(),
                        contentAlignment = Alignment.Center,
                    ) {
                        it()
                    }
                }
            }
        }
    }
}
