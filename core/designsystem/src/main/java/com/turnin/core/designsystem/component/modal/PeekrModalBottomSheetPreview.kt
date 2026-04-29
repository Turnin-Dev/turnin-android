package com.turnin.core.designsystem.component.modal

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import com.turnin.core.designsystem.theme.PeekrAppTheme

@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
private fun PeekrModalBottomSheetPreview() {
    PeekrAppTheme {
        var showBottomSheet by remember { mutableStateOf(false) }
        val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

        if (showBottomSheet) {
            PeekrModalBottomSheet(
                sheetState = sheetState,
                onDismissRequest = {
                    if (!sheetState.isVisible) {
                        showBottomSheet = false
                    }
                },
            ) { contentModifier ->
                Box(
                    contentModifier
                        .fillMaxWidth()
                        .fillMaxHeight(0.7f)
                        .background(Color.Gray),
                )
            }
        }

        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Button(onClick = { showBottomSheet = true }) {
                Text(text = "Open")
            }
        }
    }
}
