package com.peekr.core.designsystem.component.loading

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.peekr.core.designsystem.theme.PeekrAppTheme
import com.peekr.core.designsystem.theme.PeekrTheme

@Composable
fun PeekrLoadingScreen(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .background(ModalBackgroundColor)
            .zIndex(10f)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = { },
            ),
    ) {
        CircularProgressIndicator(
            modifier = Modifier
                .align(Alignment.Center)
                .size(50.dp),
            color = PeekrTheme.colorScheme.primary,
        )
    }
}

private val ModalBackgroundColor = Color(0xFF353535).copy(0.7f)

@Preview(showBackground = true)
@Composable
private fun PeekrLoadingScreenPreview() {
    PeekrAppTheme {
        Box(Modifier.fillMaxSize()) {
            PeekrLoadingScreen(Modifier.fillMaxSize())
            Text("Hello World", fontSize = 50.sp)
        }
    }
}
