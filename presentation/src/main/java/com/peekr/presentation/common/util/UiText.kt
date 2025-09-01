package com.peekr.presentation.common.util

import android.content.Context
import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

/**
 * UI 에서 유연하게 사용할 수 있는 Text 타입
 *
 * - [DynamicString]을 사용해서 동적으로 문자열 값을 담을 수 있다.
 * - [StringResource]를 사용해서 문자열 리소스를 담을 수 있다.
 */
sealed class UiText {
    /** 직접 입력한 문자열 값을 래핑한다. */
    data class DynamicString(val value: String) : UiText()

    /** 문자열 리소스를 래핑한다. */
    class StringResource(
        @StringRes val id: Int,
    ) : UiText()

    /** [Composable]에서 사용 가능한 String 변환 함수이다. */
    @Composable
    fun asString(): String = when (this) {
        is DynamicString -> value
        is StringResource -> LocalContext.current.getString(id)
    }

    /** [Context]를 통해 일반 함수에서 사용 가능한 String 변환 함수이다. */
    fun asString(context: Context): String = when (this) {
        is DynamicString -> value
        is StringResource -> context.getString(id)
    }
}
