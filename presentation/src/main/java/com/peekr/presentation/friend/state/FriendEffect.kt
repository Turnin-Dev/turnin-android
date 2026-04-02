package com.peekr.presentation.friend.state

import com.peekr.core.presentation.common.navigation.args.UserProfileArgs

sealed interface FriendEffect {
    data object NavigateToMyProfile : FriendEffect

    data class NavigateToUserProfile(
        val args: UserProfileArgs,
    ) : FriendEffect
}
