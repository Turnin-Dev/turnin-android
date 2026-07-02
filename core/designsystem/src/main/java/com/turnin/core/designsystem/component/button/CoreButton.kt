package com.turnin.core.designsystem.component.button

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.turnin.core.designsystem.theme.TurninTheme
import com.turnin.core.designsystem.util.click.ClickEventProcessor
import com.turnin.core.designsystem.util.click.ThrottleClickEventProcessor
import com.turnin.core.designsystem.util.click.getThrottle
import com.turnin.core.designsystem.util.icon.TurninIconType

/** TurninButton 타입 */
internal enum class TurninButtonType {
    Solid,
    Outlined,
    Negative,
}

/**
 * Turnin Core Button
 *
 * @param type [TurninButtonType]
 * @param style [TurninButtonStyle]
 * @param text 버튼 텍스트
 * @param modifier [Modifier]
 * @param icon 버튼 아이콘
 * @param enabled 버튼 활성화 여부
 * @param loading 로딩 여부
 * @param onClick 버튼 클릭 시
 */
@Composable
internal fun CoreButton(
    type: TurninButtonType,
    style: TurninButtonStyle,
    text: String,
    modifier: Modifier = Modifier,
    icon: TurninIconType? = null,
    enabled: Boolean = true,
    loading: Boolean = false,
    onClick: () -> Unit,
) {
    val throttleClickEventProcessor = remember {
        ClickEventProcessor.getThrottle(ThrottleClickEventProcessor.THROTTLE_TIME_MS)
    }

    Button(
        onClick = { if (!loading) throttleClickEventProcessor.processEvent(onClick) },
        modifier = modifier.heightIn(min = style.height),
        enabled = enabled,
        colors = type.buttonColors(),
        border = type.borderStroke(enabled),
        contentPadding = style.paddingValues,
        shape = RoundedCornerShape(style.cornerRadius),
    ) {
        Box(contentAlignment = Alignment.Center) {
            Row(
                modifier = Modifier.alpha(if (loading) 0f else 1f),
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

            if (loading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(style.iconSize),
                    strokeWidth = 3.dp,
                    color = type.buttonColors().contentColor,
                )
            }
        }
    }
}

@Composable
private fun TurninButtonStyle.textStyle(): TextStyle = when (this) {
    TurninButtonStyle.Large -> TurninTheme.typography.body1.copy(fontWeight = FontWeight.SemiBold)
    TurninButtonStyle.Medium -> TurninTheme.typography.body2.copy(fontWeight = FontWeight.SemiBold)
    TurninButtonStyle.Small -> TurninTheme.typography.label1.copy(fontWeight = FontWeight.Medium)
    TurninButtonStyle.Tiny -> TurninTheme.typography.label2.copy(fontWeight = FontWeight.Medium)
}

@Composable
private fun TurninButtonType.buttonColors(): ButtonColors = when (this) {
    TurninButtonType.Solid -> {
        ButtonDefaults.buttonColors(
            containerColor = TurninTheme.colorScheme.primary,
            contentColor = TurninTheme.colorScheme.staticWhite,
            disabledContainerColor = TurninTheme.colorScheme.interactionDisable,
            disabledContentColor = TurninTheme.colorScheme.staticWhite,
        )
    }

    TurninButtonType.Outlined -> {
        ButtonDefaults.buttonColors(
            containerColor = Color.Transparent,
            contentColor = TurninTheme.colorScheme.primary,
            disabledContainerColor = Color.Transparent,
            disabledContentColor = TurninTheme.colorScheme.interactionDisable,
        )
    }

    TurninButtonType.Negative -> {
        ButtonDefaults.buttonColors(
            containerColor = TurninTheme.colorScheme.interactionDisable,
            contentColor = TurninTheme.colorScheme.staticWhite,
            disabledContainerColor = TurninTheme.colorScheme.interactionDisable,
            disabledContentColor = TurninTheme.colorScheme.staticWhite,
        )
    }
}

@Composable
private fun TurninButtonType.borderStroke(enabled: Boolean): BorderStroke? = when (this) {
    TurninButtonType.Solid -> null
    TurninButtonType.Outlined -> BorderStroke(
        width = 1.dp,
        color = if (enabled) {
            TurninTheme.colorScheme.primary
        } else {
            TurninTheme.colorScheme.interactionDisable
        },
    )

    TurninButtonType.Negative -> null
}
