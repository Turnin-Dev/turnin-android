package com.turnin.presentation.block.error

import com.turnin.core.presentation.common.error.asUiText
import com.turnin.core.presentation.ui.util.UiText
import com.turnin.core.presentation.ui.util.UiText.StringResource
import com.turnin.domain.block.error.BlockErrorType
import com.turnin.presentation.R

fun BlockErrorType.asUiText(): UiText = when (this) {
    BlockErrorType.MissingBlockTarget -> StringResource(R.string.block_error_missing_block_target)
    BlockErrorType.RequesterIdBlockerIdNotSame -> StringResource(R.string.block_error_requester_id_blocker_id_not_same)
    is BlockErrorType.Unexpected -> StringResource(R.string.block_error_unexpected)
    is BlockErrorType.CommonError -> this.error.asUiText()
}
