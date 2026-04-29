package com.turnin.presentation.friend.state

import com.turnin.core.presentation.common.navigation.args.UserProfileArgs

sealed interface FriendEffect {
    data object NavigateToMyProfile : FriendEffect

    data class NavigateToUserProfile(
        val args: UserProfileArgs,
    ) : FriendEffect
}
