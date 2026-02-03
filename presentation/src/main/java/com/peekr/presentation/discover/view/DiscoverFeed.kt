package com.peekr.presentation.discover.view

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.peekr.core.designsystem.component.avatar.PeekrAvatar
import com.peekr.core.designsystem.component.chip.PeekrChip
import com.peekr.core.designsystem.theme.PeekrAppTheme
import com.peekr.core.designsystem.theme.PeekrTheme
import com.peekr.core.designsystem.util.click.clickableSingle
import com.peekr.core.designsystem.util.token.ScreenTokens
import com.peekr.core.presentation.ui.util.PreviewLightDarkWithBackground
import com.peekr.presentation.discover.model.UiDiscoverContext
import com.peekr.presentation.discover.model.UiDiscoverKeyword

/**
 * 탐색 피드 뷰
 *
 * @param modifier [Modifier]
 * @param discoverContext 탐색 컨텍스트 [UiDiscoverContext]
 * @param onFeedClick 피드 클릭 시 콜백
 * @param onUserClick 사용자 클릭 시 콜백
 * @param onKeywordClick 키워드 클릭 시 콜백
 */
@Composable
internal fun DiscoverFeed(
    modifier: Modifier = Modifier,
    discoverContext: UiDiscoverContext,
    onFeedClick: () -> Unit,
    onUserClick: () -> Unit,
    onKeywordClick: (UiDiscoverKeyword) -> Unit,
) {
    Column(
        modifier = modifier
            .clickableSingle(onClick = onFeedClick)
            .padding(vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        UserInfo(
            modifier = Modifier
                .clickableSingle(onClick = onUserClick)
                .padding(horizontal = ScreenTokens.HorizontalPadding),
            userName = discoverContext.userName,
            displayId = discoverContext.displayId,
            profileImageUrl = discoverContext.profileImageUrl,
        )
        Keywords(
            modifier = Modifier.fillMaxWidth(),
            keywords = discoverContext.keywords,
            onClick = { keyword ->
                onKeywordClick(keyword)
            },
        )
    }
}

/**
 * 사용자 정보 영역
 *
 * @param modifier [Modifier]
 * @param userName 사용자 명
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
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        PeekrAvatar(
            modifier = Modifier.size(35.dp),
            model = profileImageUrl,
            contentDescription = userName,
        )
        Column(horizontalAlignment = Alignment.Start) {
            Text(
                text = userName,
                style = PeekrTheme.typography.label2,
                fontWeight = FontWeight.Bold,
                color = PeekrTheme.colorScheme.textNormal,
            )
            Text(
                text = displayId,
                style = PeekrTheme.typography.label3,
                fontWeight = FontWeight.Normal,
                color = PeekrTheme.colorScheme.textAssist,
            )
        }
    }
}

/**
 * 사용자 키워드 리스트 영역
 *
 * @param modifier [Modifier]
 * @param keywords 사용자 키워드 리스트 [UiDiscoverKeyword]
 * @param onClick 키워드 클릭 시 콜백
 */
@Composable
private fun Keywords(
    modifier: Modifier = Modifier,
    keywords: List<UiDiscoverKeyword>,
    onClick: (UiDiscoverKeyword) -> Unit,
) {
    LazyRow(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically,
        contentPadding = PaddingValues(horizontal = ScreenTokens.HorizontalPadding),
    ) {
        items(
            items = keywords,
            key = { it.userKeywordId },
        ) { keyword ->
            PeekrChip(
                text = keyword.keywordName,
                onClick = { onClick(keyword) },
                color = PeekrTheme.colorScheme.componentKeywordBG,
            )
        }
    }
}

@PreviewLightDarkWithBackground
@Composable
private fun DiscoverFeedPreview() {
    PeekrAppTheme {
        DiscoverFeed(
            discoverContext = UiDiscoverContext.sample,
            onFeedClick = {},
            onUserClick = {},
            onKeywordClick = {},
        )
    }
}
