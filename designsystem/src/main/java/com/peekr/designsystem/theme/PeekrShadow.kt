package com.peekr.designsystem.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.peekr.designsystem.util.dropShadow

// PeekrShadow 를 적용하기 위해 모아놓은 DropShadow 파라미터 모음
private data class PeekrShadowToken(
    val color: Color = Color.Black,
    val borderRadius: Dp = 0.dp,
    val offsetX: Dp = 0.dp,
    val offsetY: Dp = 0.dp,
    val blur: Dp = 0.dp,
    val spread: Dp = 0.dp,
)

// PeekrShadowType 를 통해 PeekrShadowToken 를 가져온다.
private fun PeekrShadowType.getPeekrShadowToken(
    darkMode: Boolean,
): PeekrShadowToken = when (this) {
    PeekrShadowType.Normal -> {
        PeekrShadowToken(
            color = if (darkMode) Color.White.copy(alpha = 0.25f) else Color.Black.copy(alpha = 0.25f),
            blur = 3.dp,
        )
    }
}

/** PeekrShadow 타입 */
enum class PeekrShadowType {
    Normal,
}

/**
 * PeekrShadow
 * @param type [PeekrShadowType]
 */
fun Modifier.peekrShadow(type: PeekrShadowType): Modifier = composed {
    val token = type.getPeekrShadowToken(isSystemInDarkTheme())

    this.then(
        Modifier.dropShadow(
            color = token.color,
            blur = token.blur,
        ),
    )
}
