package com.peekr.presentation.friend.state

sealed interface FriendEffect {
    data class NavigateToUserProfile(
        val userId: Long,
    ) : FriendEffect
}
