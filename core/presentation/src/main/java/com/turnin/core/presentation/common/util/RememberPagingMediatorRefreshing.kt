package com.turnin.core.presentation.common.util

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import kotlinx.coroutines.delay

/**
 * ## 핵심 원리
 * RemoteMediator는 Room 트랜잭션 커밋 직후 `mediator.refresh = NotLoading`을 반환하지만,
 * Room invalidation → PagingSource 재쿼리 → itemSnapshotList 반영까지는 비동기 지연이 있다.
 * 이 갭 때문에 "NotLoading인데 아직 이전 데이터"인 프레임이 생기고, 그대로 그리면 이전 데이터가 잠깐 노출된다.
 * 이 함수는 mediator가 끝난 뒤 itemSnapshotList가 실제로 바뀔 때까지 `true`를 유지해 그 갭을 가려준다.
 *
 * ## ⚠️ RemoteMediator를 사용하는 Pager에서만 사용
 * RemoteMediator가 없으면 loadState.mediator가 항상 null이라 이 함수는 아무 효과가 없다(안전하지만 무의미).
 *
 * @param pagingItems RemoteMediator를 사용하는 Pager로부터 생성된 [LazyPagingItems]
 * @param timeoutMillis mediator refresh는 끝났지만 서버가 이전과 완전히 동일한 데이터/순서를
 *   반환해 [LazyPagingItems.itemSnapshotList] 레퍼런스가 바뀌지 않는 경우를 대비한
 *   안전장치. 이 시간이 지나면 강제로 `isRefreshing = false` 처리합니다.
 * @return mediator refresh 완료 + snapshot 실제 반영까지 모두 끝났을 때만 `false`,
 *   그 사이 구간(이전 데이터 노출 구간 포함)에는 `true`
 */
@Composable
fun <T : Any> rememberPagingMediatorRefreshing(
    pagingItems: LazyPagingItems<T>,
    timeoutMillis: Long = 3000L,
): Boolean {
    var isRefreshing by remember { mutableStateOf(false) }
    var pendingSnapshotUpdate by remember { mutableStateOf(false) }

    val mediatorRefresh = pagingItems.loadState.mediator?.refresh

    LaunchedEffect(mediatorRefresh) {
        when (mediatorRefresh) {
            is LoadState.Loading -> isRefreshing = true
            is LoadState.NotLoading -> if (isRefreshing) pendingSnapshotUpdate = true
            null -> isRefreshing = false
            else -> Unit
        }
    }

    LaunchedEffect(pagingItems.itemSnapshotList) {
        if (pendingSnapshotUpdate) {
            isRefreshing = false
            pendingSnapshotUpdate = false
        }
    }

    // 서버가 이전과 정확히 동일한 순서/데이터를 내려주면 snapshot 레퍼런스가
    // 안 바뀔 수도 있어서, 이 경우를 대비한 타임아웃 안전장치
    LaunchedEffect(pendingSnapshotUpdate) {
        if (pendingSnapshotUpdate) {
            delay(timeoutMillis)
            isRefreshing = false
            pendingSnapshotUpdate = false
        }
    }

    return isRefreshing
}
