package com.peekr.presentation.block.error

import com.peekr.core.presentation.common.error.asUiText
import com.peekr.core.presentation.ui.util.UiText
import com.peekr.core.presentation.ui.util.UiText.StringResource
import com.peekr.domain.block.error.BlockErrorType
import com.peekr.presentation.R

fun BlockErrorType.asUiText(): UiText = when (this) {
    BlockErrorType.RequesterIdBlockerIdNotSame -> StringResource(R.string.block_error_requester_id_blocker_id_not_same)
    is BlockErrorType.Unexpected -> StringResource(R.string.block_error_unexpected)
    is BlockErrorType.CommonError -> this.error.asUiText()
}
