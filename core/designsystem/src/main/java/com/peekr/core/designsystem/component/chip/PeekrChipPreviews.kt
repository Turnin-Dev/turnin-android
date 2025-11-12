package com.peekr.core.designsystem.component.chip

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.peekr.core.designsystem.theme.PeekrAppTheme
import com.peekr.core.designsystem.util.icon.PeekrIcons
import com.peekr.core.designsystem.util.icon.Profile

@Preview(uiMode = Configuration.UI_MODE_NIGHT_NO)
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun PeekrChipPreview() {
    PeekrAppTheme {
        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            PeekrChip(text = "Label", onClick = {})
            PeekrChip(text = "Label", icon = PeekrIcons.Filled.Normal.Profile, onClick = {})
        }
    }
}
