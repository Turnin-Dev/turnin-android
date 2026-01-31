package com.peekr.core.presentation.ui.component.lazycolumn

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.runtime.Composable
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems

/**
 * [LazyPagingItems]의 로딩 상태(초기 로딩, 추가 로딩, 에러)를
 * [LazyColumn] 등에서 통합 관리하기 위한 확장 함수
 *
 * @param pagingItems 페이징 아이템
 * @param key [LazyColumn] key
 * @param contentType [LazyColumn] contentType
 * @param skeletonCount 표시할 스켈레톤 개수
 * @param skeleton 스켈레톤 뷰
 * @param initialError 초기 에러 뷰
 * @param footerError Footer 에러 뷰
 * @param lastContent 더 이상 표시할 데이터가 없는 경우에 표시할 뷰
 * @param pagingContent 페이징 컨텐츠
 */
fun <T : Any> LazyListScope.pagingItem(
    pagingItems: LazyPagingItems<T>,
    key: ((Int) -> Any)? = null,
    contentType: (Int) -> Any? = { null },
    skeletonCount: Int = 5,
    skeleton: @Composable (LazyItemScope.(Int) -> Unit),
    initialError: @Composable (LazyItemScope.() -> Unit),
    footerError: @Composable (LazyItemScope.() -> Unit),
    lastContent: (@Composable (LazyItemScope.() -> Unit))? = null,
    pagingContent: @Composable (LazyItemScope.(Int) -> Unit),
) {
    val refreshState = pagingItems.loadState.refresh
    val appendState = pagingItems.loadState.append

    when {
        // 1. 데이터가 하나도 없는 초기 로딩 중일 때 스켈레톤 표시
        refreshState is LoadState.Loading && pagingItems.itemCount == 0 -> {
            items(skeletonCount) {
                skeleton(it)
            }
        }

        // 2. 초기 데이터 로드 시도 중 에러 발생 시 에러 뷰 표시
        refreshState is LoadState.Error && pagingItems.itemCount == 0 -> {
            item {
                initialError()
            }
        }

        // 3. 데이터가 존재하며 정상적으로 표시 가능한 경우
        else -> {
            // 페이징 아이템 렌더링
            items(
                count = pagingItems.itemCount,
                key = key,
                contentType = contentType,
                itemContent = pagingContent,
            )

            // 하단 추가 로드 상태(Append) 상태 처리
            when (appendState) {
                // 다음 페이지 로드
                LoadState.Loading -> {
                    item {
                        skeleton(0)
                    }
                }

                // 다음 페이지 로드 시 에러
                is LoadState.Error -> {
                    item {
                        footerError()
                    }
                }

                // 모든 데이터를 불러온 경우
                // (더 이상 로드할 데이터가 없거나 정상 상태 or 마지막 페이지인 상태)
                is LoadState.NotLoading -> {
                    lastContent?.let { lc ->
                        item {
                            lc.invoke(this)
                        }
                    }
                }
            }
        }
    }
}
