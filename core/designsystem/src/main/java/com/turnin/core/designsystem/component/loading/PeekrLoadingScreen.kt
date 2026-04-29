package com.turnin.core.designsystem.component.loading

import android.view.Window
import android.view.WindowManager
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
import androidx.compose.ui.window.DialogProperties
import androidx.compose.ui.window.DialogWindowProvider
import com.turnin.core.designsystem.theme.PeekrAppTheme

@Composable
fun PeekrLoadingScreen() {
    Dialog(
        onDismissRequest = {},
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
            dismissOnBackPress = false,
            dismissOnClickOutside = false,
        ),
    ) {
        val dialogWindow = getDialogWindow()
        SideEffect {
            dialogWindow?.let { window ->
                window.setDimAmount(0f)
                window.setWindowAnimations(-1)
                window.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
            }
        }

        Box(
            modifier = Modifier
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
            PeekrLoadingScreen()
            Text("Hello World", fontSize = 50.sp)
        }
    }
}
