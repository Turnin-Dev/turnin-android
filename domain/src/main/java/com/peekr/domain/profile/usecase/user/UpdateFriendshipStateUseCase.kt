package com.peekr.domain.profile.usecase.user

import com.peekr.core.domain.common.Result
import com.peekr.core.domain.common.error.mapError
import com.peekr.core.domain.friend.model.AddFriend
import com.peekr.core.domain.friend.model.DeleteFriend
import com.peekr.core.domain.friend.model.FriendStatus
import com.peekr.core.domain.friend.model.FriendshipStatus
import com.peekr.core.domain.friend.model.PatchFriendStatus
import com.peekr.core.domain.friend.repository.FriendRepository
import com.peekr.core.domain.model.UserId
import com.peekr.core.domain.user.repository.UserRepository
import com.peekr.domain.profile.error.ProfileErrorType
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map

/**
 * 친구 관계 상태 업데이트
 *
 * @see invoke
 */
class UpdateFriendshipStateUseCase @Inject constructor(
    private val userRepository: UserRepository,
    private val friendRepository: FriendRepository,
) {
    /**
     * 현재 상태에 따라 친구 관계 상태를 업데이트한다.
     *
     * @param receiverId 친구 관계 상태가 업데이트될 대상의 사용자 ID
     * @param currentFriendshipStatus 현재 친구 관계 상태
     */
    operator fun invoke(
        receiverId: Long,
        currentFriendshipStatus: FriendshipStatus,
    ): Flow<Result<Unit, ProfileErrorType>> = flow {
        userRepository.getUserId()?.let { userId ->
            val receiverIdVO = UserId(receiverId)

            when (currentFriendshipStatus) {
                FriendshipStatus.NOTHING -> {
                    // 1)
                    // 현재 상태: 아무 것도 아닌 상태
                    // 상태 변경 시: 친구 추가 진행
                    emitAll(
                        addFriendFlow(
                            requesterId = userId,
                            receiverId = receiverIdVO,
                        ),
                    )
                }

                FriendshipStatus.FRIENDS -> {
                    // 2)
                    // 현재 상태: 친구인 상태
                    // 상태 변경 시: 친구 삭제 진행
                    emitAll(
                        deleteFriendFlow(
                            requesterId = userId,
                            receiverId = receiverIdVO,
                        ),
                    )
                }

                FriendshipStatus.REQUESTED -> {
                    // 3)
                    // 현재 상태: 친구 요청을 보낸 상태
                    // 상태 변경 시: 친구 삭제 진행 (= 친구 요청 삭제)
                    emitAll(
                        deleteFriendFlow(
                            requesterId = userId,
                            receiverId = receiverIdVO,
                        ),
                    )
                }

                FriendshipStatus.RECEIVED -> {
                    // 4.)
                    // 현재 상태: 친구 요청을 받은 상태
                    // 상태 변경 시: 친구 요청 수락
                    emitAll(
                        updateFriendshipStatusFlow(
                            requesterId = userId,
                            receiverId = receiverIdVO,
                            friendStatus = FriendStatus.ACCEPTED,
                        ),
                    )
                }
            }
        } ?: emit(Result.Error(ProfileErrorType.MyUserIdNotFound))
    }

    // 친구 추가 Flow
    private fun addFriendFlow(
        requesterId: UserId,
        receiverId: UserId,
    ): Flow<Result<Unit, ProfileErrorType>> = flow {
        val addFriend = AddFriend(requesterId, receiverId)
        emitAll(
            friendRepository
                .addFriend(addFriend)
                .map { result ->
                    when (result) {
                        Result.Loading -> Result.Loading
                        is Result.Error -> {
                            Result.Error(ProfileErrorType.CommonError(result.error))
                        }

                        is Result.Success -> {
                            Result.Success(Unit)
                        }
                    }
                },
        )
    }

    // 친구 삭제 Flow
    private fun deleteFriendFlow(
        requesterId: UserId,
        receiverId: UserId,
    ): Flow<Result<Unit, ProfileErrorType>> = flow {
        val deleteFriend = DeleteFriend(requesterId, receiverId)
        emitAll(
            friendRepository
                .deleteFriend(deleteFriend)
                .mapError { commonError ->
                    ProfileErrorType.CommonError(commonError)
                },
        )
    }

    // 친구 관계 상태 업데이트 Flow
    private fun updateFriendshipStatusFlow(
        requesterId: UserId,
        receiverId: UserId,
        friendStatus: FriendStatus,
    ): Flow<Result<Unit, ProfileErrorType>> = flow {
        val patchFriendStatus = PatchFriendStatus(
            requesterId = requesterId,
            receiverId = receiverId,
            status = friendStatus,
        )
        emitAll(
            friendRepository
                .updateFriendStatus(patchFriendStatus)
                .mapError { commonError ->
                    ProfileErrorType.CommonError(commonError)
                },
        )
    }
}
