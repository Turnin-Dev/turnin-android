package com.peekr.domain.friend.usecase

import androidx.paging.PagingData
import com.peekr.core.domain.friend.model.FriendInfo
import com.peekr.core.domain.friend.repository.FriendRepository
import com.peekr.core.domain.model.UserId
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow

/**
 * 친구 목록 페이지네이션 조회
 *
 * @see invoke
 */
class GetFriendsPaginationUseCase @Inject constructor(
    private val friendRepository: FriendRepository,
) {
    /**
     * 친구 목록을 페이지네이션으로 조회한다.
     *
     * @param userId 친구(사용자) ID
     */
    operator fun invoke(userId: Long): Flow<PagingData<FriendInfo>> =
        friendRepository.getFriends(UserId(userId))
}
