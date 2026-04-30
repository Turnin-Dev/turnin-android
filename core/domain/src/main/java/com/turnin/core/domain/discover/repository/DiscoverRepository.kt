package com.turnin.core.domain.discover.repository

import androidx.paging.PagingData
import com.turnin.core.domain.discover.model.DiscoverContext
import com.turnin.core.domain.model.UserId
import kotlinx.coroutines.flow.Flow

/**
 * 탐색 리포지토리
 */
interface DiscoverRepository {
    /**
     * 탐색 컨텍스트 페이지네이션 조회
     *
     * 탐색 컨텍스트: 사용자 정보 일부 + 키워드 정보 일부
     *
     * @param userId 사용자 ID
     */
    fun getDiscoverContexts(
        userId: UserId,
    ): Flow<PagingData<DiscoverContext>>
}
