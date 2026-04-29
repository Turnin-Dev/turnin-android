package com.turnin.domain.friend.usecase

import com.turnin.core.domain.common.Result
import com.turnin.core.domain.common.error.CommonErrorType
import com.turnin.core.domain.common.error.mapError
import com.turnin.core.domain.friend.model.FriendRequestStatus
import com.turnin.core.domain.friend.model.PatchFriendStatus
import com.turnin.core.domain.friend.repository.FriendRepository
import com.turnin.core.domain.model.UserId
import com.turnin.domain.friend.error.FriendErrorType
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow

/**
 * 친구 요청 수락
 *
 * @see invoke
 */
class AcceptFriendRequestUseCase @Inject constructor(
    private val friendRepository: FriendRepository,
) {
    /**
     * 친구 요청을 수락한다.
     *
     * @param myUserId 나의 사용자 ID (친구 요청을 받은 사용자는 무조건 '나'이기 때문)
     * @param targetUserId 친구 요청한 사용자 ID
     */
    operator fun invoke(
        myUserId: Long,
        targetUserId: Long,
    ): Flow<Result<Unit, FriendErrorType>> = flow {
        val myUserIdVO = UserId(myUserId)
        val targetUserIdVO = UserId(targetUserId)
        val patch = PatchFriendStatus(
            requesterId = myUserIdVO,
            receiverId = targetUserIdVO,
            requestStatus = FriendRequestStatus.ACCEPTED,
        )

        emitAll(
            friendRepository.updateFriendStatus(patch)
                .mapError { commonError ->
                    when (commonError) {
                        CommonErrorType.Network.Forbidden -> FriendErrorType.NotSameRequesterIdAndMyId
                        CommonErrorType.Network.NotFound -> FriendErrorType.AlreadyProceedOrUserNotFound
                        else -> FriendErrorType.CommonError(commonError)
                    }
                },
        )
    }
}
