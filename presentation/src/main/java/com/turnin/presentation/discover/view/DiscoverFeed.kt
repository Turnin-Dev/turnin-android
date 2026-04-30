package com.turnin.presentation.discover.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.turnin.core.designsystem.component.avatar.TurninAvatar
import com.turnin.core.designsystem.component.skeleton.SkeletonBox
import com.turnin.core.designsystem.theme.TurninAppTheme
import com.turnin.core.designsystem.theme.TurninTheme
import com.turnin.core.designsystem.util.click.clickableSingle
import com.turnin.core.designsystem.util.click.clickableSingleWithoutRipple
import com.turnin.core.presentation.ui.util.PreviewLightDarkWithBackground
import com.turnin.presentation.discover.model.UiDiscoverContext
import com.turnin.presentation.discover.model.UiDiscoverKeyword

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
    selected: Boolean,
    onFeedClick: () -> Unit,
    onUserClick: () -> Unit,
    onKeywordClick: (UiDiscoverKeyword) -> Unit,
) {
    Column(
        modifier = modifier
            .clip(OuterShape)
            .background(
                color = if (selected) {
                    TurninTheme.colorScheme.interactionClick
                } else {
                    Color.Transparent
                },
                shape = OuterShape,
            )
            .clickableSingle(onClick = onFeedClick)
            .padding(vertical = OuterPadding),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        UserInfo(
            modifier = Modifier
                .clickableSingleWithoutRipple(onClick = onUserClick)
                .padding(horizontal = OuterPadding),
            userName = discoverContext.user.userName,
            displayId = discoverContext.user.displayId,
            profileImageUrl = discoverContext.user.profileImageUrl,
        )
        if (discoverContext.keywords.isNotEmpty()) {
            KeywordsFlowView(
                modifier = Modifier.fillMaxWidth(),
                keywords = discoverContext.keywords,
                onClick = { keyword ->
                    onKeywordClick(keyword)
                },
                contentPadding = PaddingValues(horizontal = OuterPadding),
            )
        }
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
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp, alignment = Alignment.Start),
    ) {
        // 프로필 사진
        TurninAvatar(
            modifier = Modifier.size(AvatarSize),
            model = profileImageUrl,
            contentDescription = null,
        )

        // 이름, ID
        Column(
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.Center,
        ) {
            Text(
                text = userName,
                style = TurninTheme.typography.body2,
                fontWeight = FontWeight.SemiBold,
                color = TurninTheme.colorScheme.textNormal,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )
            Text(
                text = displayId,
                style = TurninTheme.typography.body3,
                fontWeight = FontWeight.Normal,
                color = TurninTheme.colorScheme.textAssist,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}

/**
 * 탐색 피드 스켈레톤
 */
@Composable
internal fun DiscoverFeedSkeleton() {
    Column(
        modifier = Modifier.padding(OuterPadding),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        // 사용자 정보
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp, alignment = Alignment.Start),
        ) {
            // 프로필 사진
            SkeletonBox(Modifier.size(AvatarSize), CircleShape)

            // 이름, ID
            Column(
                horizontalAlignment = Alignment.Start,
                verticalArrangement = Arrangement.Center,
            ) {
                SkeletonBox(Modifier.size(84.dp, 18.dp))
                Spacer(Modifier.height(6.dp))
                SkeletonBox(Modifier.size(103.dp, 18.dp))
            }
        }

        // 키워드 목록
        Row {
            SkeletonBox(Modifier.size(125.dp, 30.dp), RoundedCornerShape(500.dp))
            Spacer(Modifier.width(8.dp))
            SkeletonBox(Modifier.size(95.dp, 30.dp), RoundedCornerShape(500.dp))
        }
    }
}

private val AvatarSize = 44.dp
private val OuterPadding = 20.dp
private val OuterShape = RoundedCornerShape(32.dp)

// ------------------------------ Previews ------------------------------
@PreviewLightDarkWithBackground
@Composable
private fun DiscoverFeedPreview() {
    TurninAppTheme {
        DiscoverFeed(
            modifier = Modifier.fillMaxWidth(),
            discoverContext = UiDiscoverContext.sample,
            selected = false,
            onFeedClick = {},
            onUserClick = {},
            onKeywordClick = {},
        )
    }
}

@PreviewLightDarkWithBackground
@Composable
private fun DiscoverFeedKeywordEmptyPreview() {
    TurninAppTheme {
        DiscoverFeed(
            modifier = Modifier.fillMaxWidth(),
            discoverContext = UiDiscoverContext.sample.copy(keywords = emptyList()),
            selected = false,
            onFeedClick = {},
            onUserClick = {},
            onKeywordClick = {},
        )
    }
}

@PreviewLightDarkWithBackground
@Composable
private fun DiscoverFeedSkeletonPreview() {
    TurninAppTheme {
        DiscoverFeedSkeleton()
    }
}
