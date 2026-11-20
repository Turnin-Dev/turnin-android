package com.turnin.domain.pingPong.usecase

import androidx.paging.PagingData
import com.turnin.core.domain.model.UserKeywordId
import com.turnin.core.domain.pingPong.model.PingPongDetail
import com.turnin.core.domain.pingPong.repository.PingPongRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow

/**
 * 핑퐁 질문/답변 목록 조회
 *
 * @see invoke
 */
class GetPingPongsUseCase @Inject constructor(
    private val pingPongRepository: PingPongRepository,
) {
    /**
     * 핑퐁 질문/답변 목록을 조회한다.
     *
     * @param userKeywordId 사용자 키워드 ID
     */
    operator fun invoke(userKeywordId: Long): Flow<PagingData<PingPongDetail>> =
        pingPongRepository.getPingPongs(UserKeywordId(userKeywordId))
}
