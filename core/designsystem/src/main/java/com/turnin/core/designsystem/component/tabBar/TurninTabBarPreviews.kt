package com.turnin.core.designsystem.component.tabBar

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import com.turnin.core.designsystem.theme.TurninAppTheme

@Preview
@Composable
private fun TurninTabBarPreview() {
    TurninAppTheme {
        TurninTabBar(
            tabs = listOf("Label(Left)", "Label(Right)"),
            pageContent = { page ->
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center,
                ) {
                    Text("$page", fontSize = 50.sp)
                }
            },
        )
    }
}

@Preview
@Composable
private fun TurninTabBarPreview2() {
    TurninAppTheme {
        TurninTabBar(
            tabs = listOf("Label"),
            pageContent = { page ->
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center,
                ) {
                    Text("$page", fontSize = 50.sp)
                }
            },
        )
    }
}
