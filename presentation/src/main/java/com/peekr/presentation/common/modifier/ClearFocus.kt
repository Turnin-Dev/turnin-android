package com.peekr.presentation.common.modifier

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager

/**
 * 이 기능을 적용 후 적용한 영역을 탭할 시 포커싱이 해제된다.
 *
 * @param onClear 포커싱 해제 후 추가로 수행할 작업
 */
@Composable
fun Modifier.clearFocus(onClear: (() -> Unit)? = null): Modifier {
    val focusManager = LocalFocusManager.current
    return this.pointerInput(Unit) {
        detectTapGestures(
            onTap = {
                focusManager.clearFocus()
                onClear?.invoke()
            },
        )
    }
}
