package com.peekr.core.designsystem.component.modal

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.peekr.core.designsystem.theme.PeekrAppTheme
import com.peekr.core.designsystem.theme.PeekrTheme
import com.peekr.core.designsystem.util.click.clickableSingleWithoutRipple

/**
 * Peekr Modal
 *
 * @param opened 모달 활성화 여부
 * @param modifier [Modifier]
 * @param onDismiss 모달이 사라질 때 수행할 작업 (자원 해제, 모달 표시 비활성화 등)
 * @param content 모달 컨텐츠
 */
@Composable
fun PeekrModal(
    opened: Boolean,
    modifier: Modifier = Modifier,
    onDismiss: () -> Unit,
    content: @Composable () -> Unit,
) {
    if (opened) {
        Box(
            modifier = modifier
                .fillMaxSize()
                .background(ScrimColor)
                .clickableSingleWithoutRipple(onClick = onDismiss),
            contentAlignment = Alignment.Center,
        ) {
            ContentFrame(
                modifier = Modifier
                    .clickableSingleWithoutRipple {}
                    .padding(20.dp),
                content = content,
            )
        }
    }
}

@Composable
private fun ContentFrame(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    Box(
        modifier = modifier
            .clip(ModalShape)
            .background(PeekrTheme.colorScheme.backgroundNormal, ModalShape)
            .padding(start = 20.dp, end = 20.dp, bottom = 20.dp, top = 30.dp),
        contentAlignment = Alignment.Center,
    ) {
        content()
    }
}

private val ModalShape = RoundedCornerShape(25.dp)
private val ScrimColor = Color(0xB3353535)

@Preview(showBackground = true)
@Composable
private fun PeekrModalPreview() {
    var opened by remember { mutableStateOf(false) }

    PeekrAppTheme {
        Box(Modifier.fillMaxSize()) {
            Button(onClick = { opened = true }) { Text("Open modal!") }

            PeekrModal(
                opened = opened,
                onDismiss = { opened = false },
            ) {
                Box(
                    Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .background(Color.LightGray),
                )
            }
        }
    }
}
