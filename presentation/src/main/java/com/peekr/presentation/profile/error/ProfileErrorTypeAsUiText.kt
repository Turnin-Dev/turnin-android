package com.peekr.presentation.profile.error

import com.peekr.core.presentation.common.error.asUiText
import com.peekr.core.presentation.ui.util.UiText
import com.peekr.core.presentation.ui.util.UiText.StringResource
import com.peekr.domain.profile.error.ProfileErrorType
import com.peekr.presentation.R

internal fun ProfileErrorType.asUiText(): UiText = when (this) {
    is ProfileErrorType.CommonError -> this.error.asUiText()
    is ProfileErrorType.ValidationError -> this.error.asUiText()
    ProfileErrorType.MyUserIdNotFound -> StringResource(R.string.profile_error_my_user_id_not_found)
    ProfileErrorType.ProfileLoadFailed -> StringResource(R.string.profile_error_profile_load_failed)
    ProfileErrorType.KeywordsLoadFailed -> StringResource(R.string.profile_error_keywords_load_failed)
    ProfileErrorType.UpdateFriendStatusFailed -> StringResource(R.string.profile_error_update_friend_status_failed)
    ProfileErrorType.AlreadyFriendsOrRequested -> StringResource(R.string.profile_error_already_friends_or_requested)
    ProfileErrorType.AlreadyProcessed -> StringResource(R.string.profile_error_already_proceed)

    is ProfileErrorType.Unexpected -> StringResource(R.string.profile_error_unexpected)
}
