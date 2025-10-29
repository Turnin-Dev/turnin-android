package com.peekr.core.designsystem.component.loading

import android.view.Window
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogWindowProvider
import androidx.compose.ui.zIndex
import com.peekr.core.designsystem.theme.PeekrAppTheme

@Composable
fun PeekrLoadingScreen(modifier: Modifier = Modifier) {
    Dialog(onDismissRequest = {}) {
        val dialogWindow = getDialogWindow()
        SideEffect {
            dialogWindow.let { window ->
                window?.setDimAmount(0f)
                window?.setWindowAnimations(-1)
            }
        }

        Box(
            modifier = modifier
                .zIndex(10f)
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = { },
                ),
        ) {
            PeekrLoadingIndicator(
                Modifier
                    .align(Alignment.Center)
                    .size(30.dp),
            )
        }
    }
}

@ReadOnlyComposable
@Composable
private fun getDialogWindow(): Window? = (LocalView.current.parent as? DialogWindowProvider)?.window

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
