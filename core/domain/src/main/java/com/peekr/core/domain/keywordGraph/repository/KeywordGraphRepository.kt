package com.peekr.core.domain.keywordGraph.repository

import androidx.paging.PagingData
import com.peekr.core.domain.keywordGraph.model.NodeContext
import com.peekr.core.domain.model.UserId
import kotlinx.coroutines.flow.Flow

/**
 * 키워드 그래프 리포지토리
 */
interface KeywordGraphRepository {
    /**
     * [NodeContext]를 커서 기반 페이지네이션으로 조회한다.
     *
     * @param userId 조회할 사용자 ID
     * @param cursor 커서
     * @param size 페이지 사이즈
     *
     * @return [PagingData], [NodeContext]
     *
     * @see NodeContext
     */
    fun getNodeContexts(
        userId: UserId,
        cursor: Long?,
        size: Int,
    ): Flow<PagingData<NodeContext>>
}
