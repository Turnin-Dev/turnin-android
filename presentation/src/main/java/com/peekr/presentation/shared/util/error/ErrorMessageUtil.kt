package com.peekr.presentation.shared.util.error

import com.peekr.domain.shared.util.ErrorCode
import com.peekr.domain.shared.util.ErrorType
import com.peekr.domain.shared.util.Result
import com.peekr.presentation.R
import com.peekr.presentation.shared.util.UiText

/**
 * [Result.Error]를 [UiText]타입으로 변환하여 표시한다.
 *
 * @param errorTypeFirst `true`인 경우 [ErrorType]를 먼저 표시하고, `false`인 경우 [ErrorCode]를 먼저 표시한다.
 */
fun Result.Error<ErrorType>.errorDisplay(errorTypeFirst: Boolean = true): UiText =
    if (errorTypeFirst) {
        when {
            this.error !is ErrorType.Unexpected -> this.error.asUiText()
            this.code != null -> this.code!!.asUiText()
            else -> UiText.StringResource(R.string.error_unexpected)
        }
    } else {
        when {
            this.code != null -> this.code!!.asUiText()
            this.error !is ErrorType.Unexpected -> this.error.asUiText()
            else -> UiText.StringResource(R.string.error_unexpected)
        }
    }
