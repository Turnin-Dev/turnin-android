package com.peekr.presentation.friend.error

import com.peekr.core.presentation.common.error.asUiText
import com.peekr.core.presentation.ui.util.UiText
import com.peekr.core.presentation.ui.util.UiText.StringResource
import com.peekr.domain.friend.error.FriendErrorType
import com.peekr.presentation.R

fun FriendErrorType.asUiText(): UiText = when (this) {
    is FriendErrorType.Unexpected -> StringResource(R.string.friend_error_unexpected)
    FriendErrorType.UserIdNotFound -> StringResource(R.string.friend_error_user_id_not_found)
    FriendErrorType.AlreadyProceedOrUserNotFound -> StringResource(R.string.friend_error_already_proceed)
    FriendErrorType.NotSameRequesterIdAndMyId -> StringResource(R.string.friend_error_not_same_requester_id_and_my_id)
    is FriendErrorType.CommonError -> this.error.asUiText()
}
