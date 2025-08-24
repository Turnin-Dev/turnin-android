package com.peekr.designsystem.component.button

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.peekr.designsystem.util.icon.PeekrIconType

/**
 * PeekrButton 스타일(사이즈)
 *
 * @param height 버튼 높이
 * @param cornerRadius 코너 사이즈
 * @param iconSize 버튼 아이콘 사이즈
 * @param paddingValues 버튼 내부 패딩
 * @param innerPadding 아이콘과 텍스트 사이 간격
 */
enum class PeekrButtonStyle(
    val height: Dp,
    val cornerRadius: Dp,
    val iconSize: Dp,
    val paddingValues: PaddingValues,
    val innerPadding: Dp,
) {
    Large(
        height = 48.dp,
        cornerRadius = 10.dp,
        iconSize = 20.dp,
        paddingValues = PaddingValues(horizontal = 28.dp, vertical = 12.dp),
        innerPadding = 10.dp,
    ),
    Medium(
        height = 40.dp,
        cornerRadius = 8.dp,
        iconSize = 20.dp,
        paddingValues = PaddingValues(horizontal = 20.dp, vertical = 9.dp),
        innerPadding = 10.dp,
    ),
    Small(
        height = 32.dp,
        cornerRadius = 6.dp,
        iconSize = 16.dp,
        paddingValues = PaddingValues(horizontal = 14.dp, vertical = 7.dp),
        innerPadding = 8.dp,
    ),
    ExtraSmall(
        height = 25.dp,
        cornerRadius = 4.dp,
        iconSize = 13.dp,
        paddingValues = PaddingValues(horizontal = 10.dp, vertical = 5.dp),
        innerPadding = 5.dp,
    ),
}

/**
 * [CoreButton] 을 기반으로 한 SolidButton
 *
 * @param text 버튼 텍스트
 * @param style [PeekrButtonStyle]
 * @param modifier [Modifier]
 * @param icon 버튼 아이콘
 * @param enabled 버튼 활성화 여부
 * @param loading 로딩 여부
 * @param onClick 버튼 클릭 시
 */
@Composable
fun PeekrSolidButton(
    text: String,
    style: PeekrButtonStyle,
    modifier: Modifier = Modifier,
    icon: PeekrIconType? = null,
    enabled: Boolean = true,
    loading: Boolean = false,
    onClick: () -> Unit,
) {
    CoreButton(
        type = PeekrButtonType.Solid,
        style = style,
        text = text,
        modifier = modifier,
        icon = icon,
        enabled = enabled,
        loading = loading,
        onClick = onClick,
    )
}

/**
 * [CoreButton] 을 기반으로 한 OutlinedButton
 *
 * @param text 버튼 텍스트
 * @param style [PeekrButtonStyle]
 * @param modifier [Modifier]
 * @param icon 버튼 아이콘
 * @param enabled 버튼 활성화 여부
 * @param onClick 버튼 클릭 시
 */
@Composable
fun PeekrOutlinedButton(
    text: String,
    style: PeekrButtonStyle,
    modifier: Modifier = Modifier,
    icon: PeekrIconType? = null,
    enabled: Boolean = true,
    onClick: () -> Unit,
) {
    CoreButton(
        type = PeekrButtonType.Outlined,
        style = style,
        text = text,
        modifier = modifier,
        icon = icon,
        enabled = enabled,
        onClick = onClick,
    )
}
