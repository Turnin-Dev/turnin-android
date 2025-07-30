package com.peekr.presentation.shared.util

import com.peekr.domain.shared.util.ErrorType
import com.peekr.domain.shared.util.Result
import com.peekr.presentation.R

/**
 * ErrorType의 에러 메시지를 먼저 표시한다.
 *
 * - [includeUnexpected]가 `false`인 경우, [ErrorType.Unexpected.cause]의 메시지를 표시하지 않고
 * - [includeUnexpected]가 `true`인 경우, [ErrorType.Unexpected.cause]의 메시지를 표시한다.
 *
 * @param includeUnexpected [ErrorType.Unexpected]의 포함 여부를 나타낸다.
 */
fun Result.Error<ErrorType>.errorTypeFirst(includeUnexpected: Boolean = false): UiText =
    if (includeUnexpected) {
        when {
            this.error !is ErrorType.Unexpected -> this.error.asUiText()
            this.message != null -> UiText.DynamicString(this.message!!)
            else -> UiText.StringResource(R.string.error_unexpected)
        }
    } else {
        when {
            this.error !is ErrorType.Unexpected -> this.error.asUiText()
            else -> UiText.StringResource(R.string.error_unexpected)
        }
    }
