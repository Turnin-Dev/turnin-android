package com.peekr.presentation.shared.modifier

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.error
import androidx.compose.ui.semantics.semantics

/**
 * 접근성 강화를 위한 Modifier 확장함수
 *
 *
 *
 * @param errorMessage 에러 메시지 (스크린리더에서 오류 인지를 돕기 위해 적용)
 */
@Composable
fun Modifier.accessibility(
    errorMessage: String,
): Modifier = this.semantics {
    this.error(errorMessage)
}
