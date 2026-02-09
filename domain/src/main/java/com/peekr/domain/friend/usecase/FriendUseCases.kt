package com.peekr.domain.friend.usecase

import javax.inject.Inject

class FriendUseCases @Inject constructor(
    /** @see GetFriendsUseCase */
    val getFriends: GetFriendsUseCase,
    /** @see GetIncomingRequestsUseCase */
    val getIncomingRequests: GetIncomingRequestsUseCase,
    /** @see AcceptFriendRequestUseCase */
    val acceptFriendRequest: AcceptFriendRequestUseCase,
)
