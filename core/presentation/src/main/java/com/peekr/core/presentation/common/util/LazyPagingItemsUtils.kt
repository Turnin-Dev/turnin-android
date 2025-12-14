package com.peekr.core.presentation.common.util

import androidx.paging.compose.LazyPagingItems

/**
 * 목록의 끝 도달 여부를 반환한다.
 *
 * [androidx.paging.LoadState]가 `append`인 경우에만 사용한다.
 */
fun LazyPagingItems<*>.isEndOfPage(): Boolean =
    this.loadState.append.endOfPaginationReached &&
        this.itemCount > 0
