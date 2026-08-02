package com.turnin.domain.home.usecase

import androidx.paging.PagingData
import com.turnin.core.domain.feed.model.Feed
import com.turnin.core.domain.feed.model.FeedType
import com.turnin.core.domain.feed.repository.FeedRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow

/**
 * 유형별 피드 조회
 *
 * @see invoke
 */
class GetFeedsUseCase @Inject constructor(
    private val feedRepository: FeedRepository,
) {
    /**
     * 유형별로 피드를 조회한다. (커서 기반 페이지네이션)
     *
     * @param type 피드 유형 [FeedType]
     */
    operator fun invoke(type: FeedType): Flow<PagingData<Feed>> =
        feedRepository.getFeeds(type)
}
