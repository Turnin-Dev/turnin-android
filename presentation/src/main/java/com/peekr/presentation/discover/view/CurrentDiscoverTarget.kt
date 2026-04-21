package com.peekr.presentation.discover.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.peekr.core.designsystem.component.avatar.PeekrAvatar
import com.peekr.core.designsystem.theme.PeekrAppTheme
import com.peekr.core.designsystem.theme.PeekrTheme
import com.peekr.core.designsystem.util.PeekrShadowType
import com.peekr.core.designsystem.util.click.clickableSingleWithoutRipple
import com.peekr.core.designsystem.util.peekrShadow
import com.peekr.core.presentation.ui.util.PreviewLightDarkWithBackground
import com.peekr.presentation.discover.model.UiDiscoverContext
import com.peekr.presentation.discover.model.UiDiscoverKeyword

/**
 * 현재 탐색 대상
 *
 * @param modifier [Modifier]
 * @param discoverContext 탐색 컨텍스트 [UiDiscoverContext]
 * @param onUserClick 사용자 클릭 시 콜백
 * @param onKeywordClick 키워드 클릭 시 콜백
 */
@Composable
internal fun CurrentDiscoverTarget(
    modifier: Modifier = Modifier,
    discoverContext: UiDiscoverContext,
    onUserClick: () -> Unit,
    onKeywordClick: (UiDiscoverKeyword) -> Unit,
) {
    Column(
        modifier = modifier
            .peekrShadow(
                type = PeekrShadowType.Custom(
                    blur = 6.dp,
                    lightColor = Color(0xFF1C1B1B).copy(alpha = 0.1f),
                    darkColor = Color(0xFF000000).copy(alpha = 0.3f),
                ),
                shape = OuterShape,
            )
            .clip(OuterShape)
            .background(PeekrTheme.colorScheme.backgroundNormal)
            .padding(vertical = OuterPadding),
        verticalArrangement = Arrangement.spacedBy(20.dp),
    ) {
        // 사용자 정보
        UserInfo(
            modifier = Modifier
                .clickableSingleWithoutRipple { onUserClick() }
                .align(Alignment.Start)
                .padding(horizontal = OuterPadding),
            userName = discoverContext.user.userName,
            displayId = discoverContext.user.displayId,
            profileImageUrl = discoverContext.user.profileImageUrl,
        )

        // 키워드
        if (discoverContext.keywords.isNotEmpty()) {
            KeywordsFlowView(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                keywords = discoverContext.keywords,
                onClick = { onKeywordClick(it) },
                contentPadding = PaddingValues(horizontal = OuterPadding),
            )
        }
    }
}

/**
 * 사용자 정보 영역
 *
 * @param modifier [Modifier]
 * @param userName 사용자명
 * @param displayId 사용자 표시 ID
 * @param profileImageUrl 프로필 사진 url
 */
@Composable
private fun UserInfo(
    modifier: Modifier = Modifier,
    userName: String,
    displayId: String,
    profileImageUrl: String?,
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(24.dp, alignment = Alignment.Start),
    ) {
        // 프로필 사진
        PeekrAvatar(
            modifier = Modifier.size(AvatarSize),
            model = profileImageUrl,
            contentDescription = null,
        )
        // 이름, ID
        Column(
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.spacedBy(2.dp),
        ) {
            Text(
                text = userName,
                style = PeekrTheme.typography.body1,
                fontWeight = FontWeight.SemiBold,
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

private val AvatarSize = 64.dp
private val OuterPadding = 20.dp
private val OuterShape = RoundedCornerShape(48.dp)

// ------------------------------ Preview ------------------------------

@PreviewLightDarkWithBackground
@Composable
private fun CurrentTargetUserPreview() {
    PeekrAppTheme {
        CurrentDiscoverTarget(
            modifier = Modifier.fillMaxWidth(),
            discoverContext = UiDiscoverContext.sample.copy(
                keywords = listOf(
                    UiDiscoverKeyword(1L, 1L, "아주 긴 키워드 테스트 아주 긴 키워드 테스트"),
                    UiDiscoverKeyword(2L, 2L, "Confidence"),
                    UiDiscoverKeyword(3L, 3L, "Mechanical Keyboards"),
                    UiDiscoverKeyword(4L, 4L, "Software Engineering"),
                    UiDiscoverKeyword(5L, 5L, "키워드 123456789"),
                ),
            ),
            onUserClick = {},
            onKeywordClick = {},
        )
    }
}

@PreviewLightDarkWithBackground
@Composable
private fun CurrentTargetUserEmptyPreview() {
    PeekrAppTheme {
        CurrentDiscoverTarget(
            modifier = Modifier.fillMaxWidth(),
            discoverContext = UiDiscoverContext.sample.copy(keywords = emptyList()),
            onUserClick = {},
            onKeywordClick = {},
        )
    }
}
