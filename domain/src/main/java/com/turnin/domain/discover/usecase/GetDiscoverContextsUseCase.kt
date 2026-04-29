package com.turnin.domain.discover.usecase

import androidx.paging.PagingData
import com.turnin.core.domain.discover.model.DiscoverContext
import com.turnin.core.domain.discover.repository.DiscoverRepository
import com.turnin.core.domain.model.UserId
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow

/**
 * 탐색 컨텍스트 리스트 조회
 *
 * @see invoke
 */
class GetDiscoverContextsUseCase @Inject constructor(
    private val discoverRepository: DiscoverRepository,
) {
    /**
     * 탐색 컨텍스트 리스트를 조회한다. (커서 페이지네이션)
     */
    operator fun invoke(userId: Long): Flow<PagingData<DiscoverContext>> = flow {
        val userIdVO = UserId(userId)
        emitAll(discoverRepository.getDiscoverContexts(userIdVO))
    }
}
