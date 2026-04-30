package com.turnin.domain.friend.usecase

import androidx.paging.PagingData
import com.turnin.core.domain.friend.model.FriendInfo
import com.turnin.core.domain.friend.repository.FriendRepository
import com.turnin.core.domain.model.UserId
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow

/**
 * 친구 목록 조회 (페이지네이션)
 *
 * @see invoke
 */
class GetFriendsUseCase @Inject constructor(
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
