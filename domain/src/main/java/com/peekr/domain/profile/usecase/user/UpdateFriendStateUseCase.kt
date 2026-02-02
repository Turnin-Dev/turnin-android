package com.peekr.domain.profile.usecase.user

import com.peekr.core.domain.common.Result
import com.peekr.core.domain.common.coroutine.mapSuccess
import com.peekr.core.domain.common.error.CommonErrorType
import com.peekr.core.domain.common.error.mapError
import com.peekr.core.domain.friend.model.AddFriend
import com.peekr.core.domain.friend.model.DeleteFriend
import com.peekr.core.domain.friend.model.FriendRequestStatus
import com.peekr.core.domain.friend.model.FriendStatus
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
class UpdateFriendStateUseCase @Inject constructor(
    private val userRepository: UserRepository,
    private val friendRepository: FriendRepository,
) {
    /**
     * 현재 상태에 따라 친구 관계 상태를 업데이트한다.
     *
     * @param receiverId 친구 관계 상태가 업데이트될 대상의 사용자 ID
     * @param currentFriendStatus 현재 친구 관계 상태
     *
     * @return 업데이트 후 변경된 친구 상태를 반환한다
     */
    operator fun invoke(
        receiverId: Long,
        currentFriendStatus: FriendStatus,
    ): Flow<Result<FriendStatus, ProfileErrorType>> = flow {
        userRepository.getMyUserId()?.let { userId ->
            val receiverIdVO = UserId(receiverId)

            when (currentFriendStatus) {
                FriendStatus.NOTHING -> {
                    // 1)
                    // 현재 상태: 아무 것도 아닌 상태
                    // 상태 변경 시: 친구 추가 진행
                    emitAll(
                        addFriendFlow(
                            requesterId = userId,
                            receiverId = receiverIdVO,
                        ).mapSuccess {
                            currentFriendStatus.toggle()
                        },
                    )
                }

                FriendStatus.FRIENDS -> {
                    // 2)
                    // 현재 상태: 친구인 상태
                    // 상태 변경 시: 친구 삭제 진행
                    emitAll(
                        deleteFriendFlow(
                            requesterId = userId,
                            receiverId = receiverIdVO,
                        ).mapSuccess {
                            currentFriendStatus.toggle()
                        },
                    )
                }

                FriendStatus.REQUESTED -> {
                    // 3)
                    // 현재 상태: 친구 요청을 보낸 상태
                    // 상태 변경 시: 친구 삭제 진행 (= 친구 요청 삭제)
                    emitAll(
                        deleteFriendFlow(
                            requesterId = userId,
                            receiverId = receiverIdVO,
                        ).mapSuccess {
                            currentFriendStatus.toggle()
                        },
                    )
                }

                FriendStatus.RECEIVED -> {
                    // 4.)
                    // 현재 상태: 친구 요청을 받은 상태
                    // 상태 변경 시: 친구 요청 수락
                    emitAll(
                        updateFriendshipStatusFlow(
                            requesterId = userId,
                            receiverId = receiverIdVO,
                            friendRequestStatus = FriendRequestStatus.ACCEPTED,
                        ).mapSuccess {
                            currentFriendStatus.toggle()
                        },
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
                            when (result.error) {
                                CommonErrorType.Network.Conflict -> {
                                    Result.Error(ProfileErrorType.AlreadyFriendsOrRequested)
                                }

                                else -> Result.Error(ProfileErrorType.CommonError(result.error))
                            }
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
                    when (commonError) {
                        CommonErrorType.Network.NotFound -> {
                            ProfileErrorType.AlreadyProcessed
                        }

                        else -> ProfileErrorType.CommonError(commonError)
                    }
                },
        )
    }

    // 친구 관계 상태 업데이트 Flow
    private fun updateFriendshipStatusFlow(
        requesterId: UserId,
        receiverId: UserId,
        friendRequestStatus: FriendRequestStatus,
    ): Flow<Result<Unit, ProfileErrorType>> = flow {
        val patchFriendStatus = PatchFriendStatus(
            requesterId = requesterId,
            receiverId = receiverId,
            requestStatus = friendRequestStatus,
        )
        emitAll(
            friendRepository
                .updateFriendStatus(patchFriendStatus)
                .mapError { commonError ->
                    when (commonError) {
                        CommonErrorType.Network.NotFound -> {
                            ProfileErrorType.AlreadyProcessed
                        }

                        else -> ProfileErrorType.CommonError(commonError)
                    }
                },
        )
    }
}
