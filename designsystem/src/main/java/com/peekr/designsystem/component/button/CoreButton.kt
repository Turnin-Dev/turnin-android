package com.peekr.designsystem.component.button

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.ui.unit.dp
import com.peekr.designsystem.theme.PeekrTheme
import com.peekr.designsystem.util.click.ClickEventProcessor
import com.peekr.designsystem.util.click.ThrottleClickEventProcessor
import com.peekr.designsystem.util.click.getThrottle
import com.peekr.designsystem.util.icon.PeekrIconType

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
internal fun CoreButton(
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

/** PeekrButton 타입 */
internal enum class PeekrButtonType {
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
