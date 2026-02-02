package com.peekr.presentation.friend.state

sealed interface FriendEffect {
    data object NavigateToMyProfile : FriendEffect

    data class NavigateToUserProfile(
        val userId: Long,
    ) : FriendEffect
}
