package com.peekr.domain.friend.usecase

import javax.inject.Inject

class FriendUseCases @Inject constructor(
    /**
     * 나의 사용자 ID 조회
     */
    val getMyUserId: GetMyUserIdUseCase,
    /**
     * 친구 목록 조회
     * @see GetFriendsUseCase
     */
    val getFriends: GetFriendsUseCase,
    /**
     * 친구 요청 목록 조회
     * @see GetIncomingRequestsUseCase
     */
    val getIncomingRequests: GetIncomingRequestsUseCase,
    /**
     * 친구 요청 수락
     * @see AcceptFriendRequestUseCase
     */
    val acceptFriendRequest: AcceptFriendRequestUseCase,
)
