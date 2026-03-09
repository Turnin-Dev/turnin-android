package com.peekr.core.designsystem.component.modal

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.peekr.core.designsystem.theme.PeekrAppTheme

@Preview(showBackground = true)
@Composable
private fun PeekrModalWrapperPreview() {
    var isOpen by remember { mutableStateOf(false) }

    PeekrAppTheme {
        Box(Modifier.fillMaxSize()) {
            Button(onClick = { isOpen = true }) { Text("Open modal!") }

            PeekrModalWrapper(isOpen = isOpen, onDismissRequest = { isOpen = false }) {
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
