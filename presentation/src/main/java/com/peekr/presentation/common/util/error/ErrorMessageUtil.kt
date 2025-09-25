package com.peekr.presentation.common.util.error

import com.peekr.core.domain.util.ErrorType
import com.peekr.core.domain.util.Result
import com.peekr.presentation.R
import com.peekr.presentation.common.util.UiText

/**
 * [Result.Error]를 [UiText]타입으로 변환하여 표시한다.
 *
 * @param errorTypeFirst `true`인 경우 [ErrorType]를 먼저 표시하고, `false`인 경우 [ErrorCode]를 먼저 표시한다.
 */
private fun Result.Error<ErrorType>.errorAsUiText(errorTypeFirst: Boolean = true): UiText {
    val codeAsUiText = code?.asUiText()
    val typeAsUiText = error.takeUnless { it is ErrorType.Unexpected }?.asUiText()
    return if (errorTypeFirst) {
        typeAsUiText ?: codeAsUiText ?: UiText.StringResource(R.string.error_unexpected)
    } else {
        codeAsUiText ?: typeAsUiText ?: UiText.StringResource(R.string.error_unexpected)
    }
}

/**
 * [Result.Error]를 [UiText]타입으로 변환한다.
 *
 * 1. [ErrorType]를 먼저 표시
 * 2. (1번이 null이면) [ErrorCode]를 표시
 *
 * @see errorAsUiText
 */
fun Result.Error<ErrorType>.asUiTextTypeFirst(): UiText = errorAsUiText(errorTypeFirst = true)

/**
 * [Result.Error]를 [UiText]타입으로 변환한다.
 *
 * 1. [ErrorCode]를 먼저 표시
 * 2. (1번이 null이면) [ErrorType]를 표시
 *
 * @see errorAsUiText
 */
fun Result.Error<ErrorType>.asUiTextCodeFirst(): UiText = errorAsUiText(errorTypeFirst = false)
