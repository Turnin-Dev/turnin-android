package com.turnin.core.presentation.ui.component.card

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.turnin.core.designsystem.component.avatar.PeekrAvatar
import com.turnin.core.designsystem.theme.PeekrAppTheme
import com.turnin.core.designsystem.theme.PeekrTheme
import com.turnin.core.presentation.ui.util.PreviewLightDarkWithBackground

/**
 * [ProfileCard]에서 사용하는 디자인 토큰 값 모음
 */
object ProfileCardTokens {
    val AvatarSize = 58.dp
}

/**
 * 프로필 카드
 *
 * 프로필 카드에는 프로필 사진, 사용자 명, 사용자 표시 ID가 표시되며 가로로 긴 카드 형태이다.
 *
 * 친구 목록 처럼 사용자의 프로필을 간단하게 리스트 형태로 표시해야 하는 경우 사용한다.
 *
 * @param modifier [Modifier]
 * @param profileImageUrl 친구 프로필 사진 url
 * @param name 친구 이름
 * @param displayId 친구 사용자 표시 ID
 */
@Composable
fun ProfileCard(
    modifier: Modifier = Modifier,
    profileImageUrl: String?,
    name: String,
    displayId: String,
) {
    Row(
        modifier = modifier.padding(vertical = 10.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp, Alignment.Start),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        PeekrAvatar(
            modifier = Modifier.size(ProfileCardTokens.AvatarSize),
            model = profileImageUrl,
            contentDescription = name,
        )
        Column(
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.spacedBy(2.dp, alignment = Alignment.CenterVertically),
        ) {
            Text(
                text = name,
                style = PeekrTheme.typography.body3,
                fontWeight = FontWeight.Bold,
                color = PeekrTheme.colorScheme.textNormal,
            )
            Text(
                text = displayId,
                style = PeekrTheme.typography.body4,
                fontWeight = FontWeight.Normal,
                color = PeekrTheme.colorScheme.textAssist,
            )
        }
    }
}

@PreviewLightDarkWithBackground
@Composable
private fun ProfileCardPreview() {
    PeekrAppTheme {
        ProfileCard(
            modifier = Modifier.fillMaxWidth(),
            profileImageUrl = null,
            displayId = "Display ID",
            name = "Username",
        )
    }
}
