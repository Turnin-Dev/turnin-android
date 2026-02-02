package com.peekr.presentation.discover.view

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
import com.peekr.core.designsystem.component.avatar.PeekrAvatar
import com.peekr.core.designsystem.theme.PeekrAppTheme
import com.peekr.core.designsystem.theme.PeekrTheme
import com.peekr.core.designsystem.util.click.clickableSingle
import com.peekr.core.presentation.ui.util.PreviewLightDarkWithBackground
import com.peekr.presentation.discover.model.UiHistoryUser

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
    userChipInfo: UiHistoryUser,
    onClick: () -> Unit,
) {
    Box(
        modifier = modifier
            .clip(Shape)
            .background(
                if (isSelected) {
                    PeekrTheme.colorScheme.textNormal
                } else {
                    PeekrTheme.colorScheme.backgroundNormal
                },
            )
            .border(
                width = 0.35.dp,
                color = PeekrTheme.colorScheme.textNormal,
                shape = Shape,
            )
            .clickableSingle(onClick = onClick),
    ) {
        Row(
            modifier = Modifier.padding(InnerPadding),
            horizontalArrangement = Arrangement.spacedBy(5.dp, Alignment.CenterHorizontally),
        ) {
            PeekrAvatar(
                modifier = Modifier.size(AvatarSize),
                model = userChipInfo.profileImageUrl,
                contentDescription = null,
            )
            Text(
                text = userChipInfo.userName,
                style = PeekrTheme.typography.caption3,
                fontWeight = FontWeight.Normal,
                color = if (isSelected) {
                    PeekrTheme.colorScheme.backgroundNormal
                } else {
                    PeekrTheme.colorScheme.textNormal
                },
            )
        }
    }
}

private val Shape = RoundedCornerShape(100.dp)
private val InnerPadding = PaddingValues(
    start = 2.dp,
    end = 6.dp,
    top = 2.dp,
    bottom = 2.dp,
)
private val AvatarSize = 18.dp

@PreviewLightDarkWithBackground
@Composable
private fun UserChipPreview() {
    PeekrAppTheme {
        var isSelected by remember { mutableStateOf(false) }

        UserChip(
            isSelected = isSelected,
            userChipInfo = UiHistoryUser(1L, "홍길동", null),
            onClick = { isSelected = !isSelected },
        )
    }
}
