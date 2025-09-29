package com.peekr.presentation.profile.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.peekr.core.designsystem.component.avatar.PeekrAvatar
import com.peekr.core.designsystem.component.button.PeekrIconButton
import com.peekr.core.designsystem.component.fab.PeekrFab
import com.peekr.core.designsystem.component.icon.PeekrIconSize
import com.peekr.core.designsystem.component.topbar.PeekrTopBar
import com.peekr.core.designsystem.theme.PeekrAppTheme
import com.peekr.core.designsystem.theme.PeekrTheme
import com.peekr.core.designsystem.util.PeekrShadowType
import com.peekr.core.designsystem.util.click.clickableSingleWithoutRipple
import com.peekr.core.designsystem.util.icon.PeekrIcons
import com.peekr.core.designsystem.util.icon.Settings
import com.peekr.core.designsystem.util.peekrShadow
import com.peekr.core.presentation.keyword.graph.KeywordGraphView
import com.peekr.core.presentation.keyword.model.UiKeyword
import com.peekr.core.presentation.token.ScreenTokens
import com.peekr.presentation.R
import com.peekr.presentation.profile.state.ProfileState

/**
 * 프로필 화면
 */
@Composable
fun ProfileScreen(
    modifier: Modifier = Modifier,
    profileState: ProfileState,
) {
    Box(modifier) {
        ProfileScreenFrame(
            modifier = Modifier.fillMaxSize(),
            topBar = {
                TopBar(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 10.dp),
                    title = profileState.displayId,
                )
            },
            profile = {
                Profile(
                    modifier = Modifier.fillMaxWidth(),
                    profileImageUrl = profileState.profileImageUrl,
                    name = profileState.name,
                    friendsTotal = profileState.friendsTotal,
                    introduce = profileState.introduce,
                    onProfileImageClick = {},
                    onFriendsTotalClick = {},
                )
            },
            keywordGraph = {
                KeywordGraph(
                    profileImageUrl = profileState.profileImageUrl,
                    keywords = profileState.keywords,
                )
            },
        )

        PeekrFab(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(20.dp)
                .size(FabSize),
            contentDescription = stringResource(R.string.profile_screen_fab_content_desc),
            onClick = {},
        )
    }
}

/**
 * 프로필 화면 프레임
 *
 * @param topBar 탑바 영역
 * @param profile 프로필 영역
 * @param keywordGraph 키워드 그래프 영역
 */
@Composable
private fun ProfileScreenFrame(
    modifier: Modifier = Modifier,
    topBar: @Composable () -> Unit,
    profile: @Composable () -> Unit,
    keywordGraph: @Composable () -> Unit,
) {
    Column(modifier) {
        Column(
            Modifier
                .background(PeekrTheme.colorScheme.backgroundNormal)
                .zIndex(2f),
        ) {
            // TopBar
            topBar()

            // Profile
            Box(Modifier.padding(horizontal = ScreenTokens.HorizontalPadding)) {
                profile()
            }
            ShadowSection(
                Modifier
                    .fillMaxWidth()
                    .zIndex(2f),
            )
        }

        // Keyword Graph
        Box(
            Modifier
                .weight(1f)
                .padding(horizontal = ScreenTokens.HorizontalPadding)
                .zIndex(1f),
        ) {
            keywordGraph()
        }
    }
}

/**
 * 탑바
 *
 * @param modifier [Modifier]
 * @param title 탑바 타이틀
 */
@Composable
private fun TopBar(
    modifier: Modifier = Modifier,
    title: String,
) {
    PeekrTopBar(
        modifier = modifier,
        title = title,
        optionSlot = {
            PeekrIconButton(
                icon = PeekrIcons.Outlined.Bold.Settings,
                iconSize = PeekrIconSize.Small,
                contentDescription = stringResource(R.string.profile_screen_top_bar_settings),
                onClick = {},
            )
        },
    )
}

/**
 * 프로필
 *
 * @param modifier [Modifier]
 * @param profileImageUrl 프로필 사진 url
 * @param name 이름
 * @param friendsTotal 친구 수
 * @param introduce 소개 글
 * @param onProfileImageClick 프로필 사진 클릭 시
 * @param onFriendsTotalClick 친구 수 클릭 시
 */
@Composable
private fun Profile(
    modifier: Modifier = Modifier,
    profileImageUrl: String?,
    name: String,
    friendsTotal: Long,
    introduce: String,
    onProfileImageClick: () -> Unit,
    onFriendsTotalClick: () -> Unit,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 20.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp),
    ) {
        // 프로필 사진 & 이름 & 친구 수
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(30.dp, alignment = Alignment.Start),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            // 프로필 사진
            PeekrAvatar(
                modifier = Modifier.size(AvatarSize),
                model = profileImageUrl,
                contentDescription = stringResource(R.string.profile_screen_avatar_content_desc),
                onClick = onProfileImageClick,
            )
            Column(
                modifier = Modifier.weight(1f),
                horizontalAlignment = Alignment.Start,
                verticalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                // 이름
                Text(
                    text = name,
                    style = PeekrTheme.typography.body1,
                    fontWeight = FontWeight.Bold,
                    color = PeekrTheme.colorScheme.textNormal,
                )
                // 친구 수
                Row(
                    modifier = Modifier.clickableSingleWithoutRipple(onClick = onFriendsTotalClick),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                ) {
                    Text(
                        text = stringResource(R.string.profile_screen_friends_total),
                        style = PeekrTheme.typography.body3Normal,
                        fontWeight = FontWeight.Bold,
                        color = PeekrTheme.colorScheme.textNormal,
                    )
                    Text(
                        text = "$friendsTotal",
                        style = PeekrTheme.typography.body3Normal,
                        fontWeight = FontWeight.Normal,
                        color = PeekrTheme.colorScheme.textNormal,
                    )
                }
            }
        }

        // 소개 글
        Text(
            text = introduce,
            style = PeekrTheme.typography.body4,
            fontWeight = FontWeight.Normal,
            color = PeekrTheme.colorScheme.textNormal,
            textAlign = TextAlign.Start,
        )
    }
}

@Composable
private fun KeywordGraph(
    modifier: Modifier = Modifier,
    profileImageUrl: String?,
    keywords: List<UiKeyword>,
) {
    KeywordGraphView(
        modifier = modifier,
        profileImageUrl = profileImageUrl,
        keywords = keywords,
    )
}

@Composable
private fun ShadowSection(modifier: Modifier = Modifier) {
    HorizontalDivider(
        modifier = modifier.peekrShadow(PeekrShadowType.Custom(blur = 4.dp)),
        color = Color.Transparent,
    )
}

private val FabSize = 50.dp
private val AvatarSize = 70.dp

// ------------------------------ Previews ------------------------------
@PreviewLightDark
@Composable
private fun TopBarPreview() {
    PeekrAppTheme {
        TopBar(
            modifier = Modifier
                .fillMaxWidth()
                .background(PeekrTheme.colorScheme.backgroundNormal),
            title = "Title",
        )
    }
}

@PreviewLightDark
@Composable
private fun ProfilePreview() {
    PeekrAppTheme {
        Profile(
            modifier = Modifier
                .fillMaxWidth()
                .background(PeekrTheme.colorScheme.backgroundNormal),
            profileImageUrl = null,
            name = "홍길동",
            friendsTotal = 86,
            introduce = "이 부분은 나를 간단히 소개할 수 있는 곳입니다.\n" + "1 ~ 2줄 정도로 간단히 본인을 소개하세요.",
            onProfileImageClick = {},
            onFriendsTotalClick = {},
        )
    }
}

@PreviewLightDark
@Preview
@Composable
private fun ProfileScreenPreview() {
    PeekrAppTheme {
        ProfileScreen(
            modifier = Modifier
                .fillMaxSize()
                .background(PeekrTheme.colorScheme.backgroundNormal),
            profileState = ProfileState(
                displayId = "Honggd123",
                name = "홍길동",
                friendsTotal = 86,
                profileImageUrl = null,
                introduce = "이 부분은 나를 간단히 소개할 수 있는 곳입니다.\n" +
                    "1 ~ 2줄 정도로 간단히 본인을 소개하세요.",
                keywords = UiKeyword.samples,
            ),
        )
    }
}
