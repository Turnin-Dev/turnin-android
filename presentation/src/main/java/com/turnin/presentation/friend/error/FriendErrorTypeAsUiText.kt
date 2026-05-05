package com.turnin.presentation.friend.error

import com.turnin.core.presentation.common.error.asUiText
import com.turnin.core.presentation.ui.util.UiText
import com.turnin.core.presentation.ui.util.UiText.StringResource
import com.turnin.domain.friend.error.FriendErrorType
import com.turnin.presentation.R

fun FriendErrorType.asUiText(): UiText = when (this) {
    is FriendErrorType.Unexpected -> StringResource(R.string.friend_error_unexpected)
    FriendErrorType.UserIdNotFound -> StringResource(R.string.friend_error_user_id_not_found)
    FriendErrorType.MyUserIdNotFound -> StringResource(R.string.friend_error_my_user_id_not_found)
    FriendErrorType.UserNotFound -> StringResource(R.string.friend_error_user_not_found)
    FriendErrorType.AlreadyProceed -> StringResource(R.string.friend_error_already_proceed)
    FriendErrorType.NotSameRequesterIdAndMyId -> StringResource(R.string.friend_error_not_same_requester_id_and_my_id)
    is FriendErrorType.CommonError -> this.error.asUiText()
}
