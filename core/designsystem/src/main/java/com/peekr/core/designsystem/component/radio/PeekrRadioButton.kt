package com.peekr.core.designsystem.component.radio

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.peekr.core.designsystem.theme.PeekrAppTheme
import com.peekr.core.designsystem.theme.PeekrTheme
import com.peekr.core.designsystem.util.click.clickableSingle

/**
 * Radio Button
 *
 * @param modifier [Modifier]
 */
@Composable
fun PeekrRadioButton(
    modifier: Modifier = Modifier,
    selected: Boolean,
    onClick: () -> Unit,
) {
    // 테두리
    Box(
        modifier = modifier
            .clip(CircleShape)
            .border(
                width = 2.dp,
                shape = CircleShape,
                color = if (selected) {
                    PeekrTheme.colorScheme.primary
                } else {
                    PeekrTheme.colorScheme.interactionDisable
                },
            )
            .clickableSingle(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        if (selected) {
            // 가운데 원
            Box(
                Modifier
                    .fillMaxSize(0.5f)
                    .clip(CircleShape)
                    .background(PeekrTheme.colorScheme.primary, CircleShape),
            )
        }
    }
}

@Preview
@Composable
private fun PeekrRadioButtonPreview() {
    var selected by remember { mutableStateOf(false) }

    PeekrAppTheme {
        PeekrRadioButton(
            modifier = Modifier.size(20.dp),
            selected = selected,
            onClick = { selected = !selected },
        )
    }
}
