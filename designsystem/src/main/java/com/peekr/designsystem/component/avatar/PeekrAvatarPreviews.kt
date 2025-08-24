package com.peekr.designsystem.component.avatar

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.peekr.designsystem.theme.PeekrAppTheme

@Preview(showBackground = true)
@Composable
private fun Avatar_Size() {
    PeekrAppTheme {
        Column(
            modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            repeat(150) {
                PeekrAvatar(
                    modifier = Modifier.size((it + 50).dp),
                    model = if (it % 2 == 0) photos[0] else photos[1],
                    contentDescription = null,
                    onClick = { },
                )
            }
        }
    }
}

private val photos = listOf(
    "img_url",
    null,
)
