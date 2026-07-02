package com.turnin.presentation.discover.view

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.turnin.core.designsystem.component.avatar.TurninAvatar
import com.turnin.core.designsystem.theme.TurninAppTheme
import com.turnin.core.designsystem.theme.TurninTheme
import com.turnin.core.designsystem.util.click.clickableSingle
import com.turnin.core.presentation.ui.util.PreviewLightDarkWithBackground
import com.turnin.presentation.discover.model.UiDiscoverUser

/**
 * 탐색 화면에서 사용하는 사용자 칩
 *
 * @param modifier [Modifier]
 * @param isSelected 선택 여부
 * @param userChipInfo 사용자 칩 정보
 * @param onClick 칩 클릭 시 콜백
 */
@Composable
internal fun UserChip(
    modifier: Modifier = Modifier,
    isSelected: Boolean,
    userChipInfo: UiDiscoverUser,
    onClick: () -> Unit,
) {
    Box(
        modifier = modifier
            .clip(Shape)
            .background(
                if (isSelected) {
                    TurninTheme.colorScheme.textNormal
                } else {
                    TurninTheme.colorScheme.backgroundNormal
                },
            )
            .border(
                width = 0.5.dp,
                color = TurninTheme.colorScheme.textNormal,
                shape = Shape,
            )
            .clickableSingle(onClick = onClick),
    ) {
        Row(
            modifier = Modifier.padding(InnerPadding),
            horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            TurninAvatar(
                modifier = Modifier.size(AvatarSize),
                model = userChipInfo.profileImageUrl,
                contentDescription = null,
            )
            Text(
                text = userChipInfo.userName,
                style = TurninTheme.typography.caption1,
                fontWeight = FontWeight.Medium,
                color = if (isSelected) {
                    TurninTheme.colorScheme.backgroundNormal
                } else {
                    TurninTheme.colorScheme.textNormal
                },
            )
        }
    }
}

private val Shape = RoundedCornerShape(100.dp)
private val InnerPadding = PaddingValues(
    start = 4.dp,
    end = 8.dp,
    top = 4.dp,
    bottom = 4.dp,
)
private val AvatarSize = 32.dp

@PreviewLightDarkWithBackground
@Composable
private fun UserChipPreview() {
    TurninAppTheme {
        var isSelected by remember { mutableStateOf(false) }

        UserChip(
            isSelected = isSelected,
            userChipInfo = UiDiscoverUser(1L, "홍길동", "did", null),
            onClick = { isSelected = !isSelected },
        )
    }
}
