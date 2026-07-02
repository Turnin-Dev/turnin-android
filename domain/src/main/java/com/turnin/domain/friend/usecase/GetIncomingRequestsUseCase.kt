package com.turnin.domain.friend.usecase

import androidx.paging.PagingData
import com.turnin.core.domain.friend.model.IncomingRequest
import com.turnin.core.domain.friend.repository.FriendRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow

/**
 * 나에게 들어오는 친구 요청 목록 조회 (페이지네이션)
 *
 * @see invoke
 */
class GetIncomingRequestsUseCase @Inject constructor(
    private val friendRepository: FriendRepository,
) {
    /**
     * 나에게 들어오는 친구 요청 목록 조회 (페이지네이션)
     */
    operator fun invoke(): Flow<PagingData<IncomingRequest>> =
        friendRepository.getIncomingRequests()
}
