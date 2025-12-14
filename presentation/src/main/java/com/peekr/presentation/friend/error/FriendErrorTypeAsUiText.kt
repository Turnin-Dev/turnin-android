package com.peekr.presentation.friend.error

import com.peekr.core.presentation.ui.util.UiText
import com.peekr.domain.friend.error.FriendErrorType
import com.peekr.presentation.R

fun FriendErrorType.asUiText(): UiText = when (this) {
    is FriendErrorType.Unexpected -> UiText.StringResource(R.string.friend_error_unexpected)
    FriendErrorType.UserIdNotFound -> UiText.StringResource(R.string.friend_error_user_id_not_found)
}
