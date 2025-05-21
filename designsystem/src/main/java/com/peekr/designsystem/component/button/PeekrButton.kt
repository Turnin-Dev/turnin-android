package com.peekr.designsystem.component.button

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.peekr.designsystem.theme.PeekrTheme
import com.peekr.designsystem.util.click.ClickEventProcessor
import com.peekr.designsystem.util.click.ThrottleClickEventProcessor
import com.peekr.designsystem.util.click.getThrottle
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
 * @param onClick 버튼 클릭 시
 */
@Composable
fun PeekrSolidButton(
    text: String,
    style: PeekrButtonStyle,
    modifier: Modifier = Modifier,
    icon: PeekrIconType? = null,
    enabled: Boolean = true,
    onClick: () -> Unit,
) {
    CoreButton(
        type = PeekrButtonType.Solid,
        style = style,
        text = text,
        modifier = modifier,
        icon = icon,
        enabled = enabled,
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

/** PeekrButton 타입 */
private enum class PeekrButtonType {
    Solid,
    Outlined,
}

@Composable
private fun PeekrButtonStyle.textStyle(): TextStyle = when (this) {
    PeekrButtonStyle.Large -> PeekrTheme.typography.body1.copy(fontWeight = FontWeight.SemiBold)
    PeekrButtonStyle.Medium -> PeekrTheme.typography.body2.copy(fontWeight = FontWeight.SemiBold)
    PeekrButtonStyle.Small -> PeekrTheme.typography.label1.copy(fontWeight = FontWeight.Medium)
    PeekrButtonStyle.ExtraSmall -> PeekrTheme.typography.label2.copy(fontWeight = FontWeight.Medium)
}

@Composable
private fun PeekrButtonType.buttonColors(): ButtonColors = when (this) {
    PeekrButtonType.Solid -> {
        ButtonDefaults.buttonColors(
            containerColor = PeekrTheme.colorScheme.primary,
            contentColor = PeekrTheme.colorScheme.staticWhite,
            disabledContainerColor = PeekrTheme.colorScheme.interactionDisable,
            disabledContentColor = PeekrTheme.colorScheme.staticWhite,
        )
    }

    PeekrButtonType.Outlined -> {
        ButtonDefaults.buttonColors(
            containerColor = Color.Transparent,
            contentColor = PeekrTheme.colorScheme.primary,
            disabledContainerColor = Color.Transparent,
            disabledContentColor = PeekrTheme.colorScheme.interactionDisable,
        )
    }
}

@Composable
private fun PeekrButtonType.borderStroke(enabled: Boolean): BorderStroke? = when (this) {
    PeekrButtonType.Solid -> null
    PeekrButtonType.Outlined -> BorderStroke(
        width = 1.dp,
        color = if (enabled) {
            PeekrTheme.colorScheme.primary
        } else {
            PeekrTheme.colorScheme.interactionDisable
        },
    )
}

/**
 * Peekr Core Button
 *
 * @param type [PeekrButtonType]
 * @param style [PeekrButtonStyle]
 * @param text 버튼 텍스트
 * @param modifier [Modifier]
 * @param icon 버튼 아이콘
 * @param enabled 버튼 활성화 여부
 * @param onClick 버튼 클릭 시
 */
@Composable
private fun CoreButton(
    type: PeekrButtonType,
    style: PeekrButtonStyle,
    text: String,
    modifier: Modifier = Modifier,
    icon: PeekrIconType? = null,
    enabled: Boolean = true,
    onClick: () -> Unit,
) {
    val throttleClickEventProcessor = remember {
        ClickEventProcessor.getThrottle(ThrottleClickEventProcessor.THROTTLE_TIME_MS)
    }

    Button(
        onClick = { throttleClickEventProcessor.processEvent(onClick) },
        modifier = modifier.height(style.height),
        enabled = enabled,
        colors = type.buttonColors(),
        border = type.borderStroke(enabled),
        contentPadding = style.paddingValues,
        shape = RoundedCornerShape(style.cornerRadius),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(style.innerPadding),
        ) {
            icon?.let {
                Icon(
                    modifier = Modifier.size(style.iconSize),
                    imageVector = icon.imageVector,
                    contentDescription = text,
                )
            }
            Text(
                text = text,
                style = style.textStyle(),
            )
        }
    }
}
