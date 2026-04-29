package com.turnin.presentation.profile.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.turnin.core.designsystem.component.button.PeekrIconButton
import com.turnin.core.designsystem.component.fab.PeekrFab
import com.turnin.core.designsystem.component.icon.PeekrIconSize
import com.turnin.core.designsystem.component.skeleton.SkeletonBox
import com.turnin.core.designsystem.component.topbar.PeekrTopBar
import com.turnin.core.designsystem.theme.PeekrAppTheme
import com.turnin.core.designsystem.theme.PeekrTheme
import com.turnin.core.designsystem.util.icon.Exclamation
import com.turnin.core.designsystem.util.icon.PeekrIcons
import com.turnin.core.designsystem.util.icon.Plus
import com.turnin.core.designsystem.util.icon.Settings
import com.turnin.core.designsystem.util.token.ScreenTokens
import com.turnin.core.presentation.ui.component.EmptyGuidance
import com.turnin.core.presentation.ui.model.UiUserKeyword
import com.turnin.core.presentation.ui.util.PreviewLightDarkWithBackground
import com.turnin.presentation.R
import com.turnin.presentation.profile.model.UiMyProfile
import com.turnin.presentation.profile.state.MyProfileContract
import com.turnin.presentation.profile.view.common.KeywordsTitleSkeleton
import com.turnin.presentation.profile.view.common.KeywordsTitleView
import com.turnin.presentation.profile.view.common.ProfileFrame
import com.turnin.presentation.profile.view.common.ProfileScreenFrame
import com.turnin.presentation.profile.view.common.ProfileScreenTokens
import com.turnin.presentation.profile.view.common.keywordItemsSkeleton
import com.turnin.presentation.profile.view.common.keywordItemsView

/**
 * 나의 프로필 화면
 *
 * @param modifier [Modifier]
 * @param uiState UI 상태
 * @param onUiEvent UI 이벤트
 * @param onSettingClick 설정 클릭 시
 * @param onFriendsCountClick 친구 수 클릭 시
 * @param onNavigateToKeywordAdd 키워드 추가 화면 이동 콜백
 * @param onNavigateToKeywordDetail 키워드 상세 화면 이동 콜백
 */
@Composable
fun MyProfileScreen(
    modifier: Modifier = Modifier,
    uiState: MyProfileContract.UiState,
    onUiEvent: (MyProfileContract.UiEvent) -> Unit,
    onSettingClick: () -> Unit,
    onFriendsCountClick: (Long) -> Unit,
    onNavigateToKeywordAdd: () -> Unit,
    onNavigateToKeywordDetail: (userId: Long, userKeywordId: Long) -> Unit,
) {
    Box(modifier) {
        ProfileScreenFrame(
            modifier = Modifier
                .fillMaxSize()
                .background(PeekrTheme.colorScheme.backgroundNormal),
            isRefreshing = uiState.myProfileLoading || uiState.myKeywordsLoading,
            onRefresh = { onUiEvent(MyProfileContract.UiEvent.Refresh) },
            topBar = {
                TopBar(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(
                            start = ScreenTokens.HorizontalPadding,
                            end = ScreenTokens.HorizontalPaddingWithTouchTarget,
                        ),
                    title = uiState.myProfile?.displayId ?: "",
                    onSettingClick = onSettingClick,
                )
            },
            profile = {
                if (uiState.myProfileLoading || uiState.myProfile == null) {
                    ProfileSkeleton()
                } else {
                    Profile(
                        modifier = Modifier.fillMaxWidth(),
                        profileImageUrl = uiState.myProfile.profileImageUrl,
                        name = uiState.myProfile.name,
                        friendsCount = uiState.myProfile.friendsCount,
                        introduce = uiState.myProfile.introduce,
                        onProfileImageClick = {},
                        onFriendsCountClick = { onFriendsCountClick(uiState.myProfile.userId) },
                    )
                }
            },
            keywordsTitle = {
                if (uiState.myKeywordsLoading || uiState.myKeywords == null) {
                    KeywordsTitleSkeleton()
                } else {
                    KeywordsTitleView(
                        modifier = Modifier.align(Alignment.CenterStart),
                        count = uiState.myKeywords.count(),
                    )
                }
            },
            keywords = {
                when {
                    uiState.myKeywordsLoading || uiState.myKeywords == null -> {
                        keywordItemsSkeleton()
                    }

                    uiState.myKeywords.isEmpty() -> {
                        keywordEmptyGuidance()
                    }

                    else -> {
                        keywordItemsView(
                            keywords = uiState.myKeywords,
                            onClick = { uiUserKeyword ->
                                uiState.myProfile?.let {
                                    onNavigateToKeywordDetail(it.userId, uiUserKeyword.id)
                                }
                            },
                        )
                    }
                }
            },
        )

        uiState.myProfile?.let {
            PeekrFab(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(FabPaddingDp)
                    .size(FabSize),
                icon = PeekrIcons.Default.Bold.Plus,
                contentDescription = stringResource(R.string.my_profile_screen_fab_content_desc),
                onClick = onNavigateToKeywordAdd,
            )
        }
    }
}

/**
 * 탑바
 *
 * @param modifier [Modifier]
 * @param title 탑바 타이틀
 * @param onSettingClick 설정 클릭 시
 */
@Composable
private fun TopBar(
    modifier: Modifier = Modifier,
    title: String,
    onSettingClick: () -> Unit,
) {
    PeekrTopBar(
        modifier = modifier,
        title = title,
        optionSlot = {
            PeekrIconButton(
                icon = PeekrIcons.Outlined.Bold.Settings,
                iconSize = TopBarOptionIconSize,
                contentDescription = stringResource(R.string.my_profile_screen_top_bar_settings),
                onClick = onSettingClick,
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
 * @param friendsCount 친구 수
 * @param introduce 소개 글
 * @param onProfileImageClick 프로필 사진 클릭 시
 * @param onFriendsCountClick 친구 수 클릭 시
 */
@Composable
private fun Profile(
    modifier: Modifier = Modifier,
    profileImageUrl: String?,
    name: String,
    friendsCount: Long,
    introduce: String,
    onProfileImageClick: () -> Unit,
    onFriendsCountClick: () -> Unit,
) {
    ProfileFrame(
        modifier = modifier,
        profileImageUrl = profileImageUrl,
        name = name,
        friendsCount = friendsCount,
        introduce = introduce,
        onProfileImageClick = onProfileImageClick,
        onFriendsCountClick = onFriendsCountClick,
    )
}

/**
 * 키워드 빈 화면 안내 뷰
 */
private fun LazyListScope.keywordEmptyGuidance() {
    item {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = ScreenTokens.HorizontalPadding * 3),
            contentAlignment = Alignment.Center,
        ) {
            EmptyGuidance(
                icon = PeekrIcons.Filled.Normal.Exclamation,
                title = stringResource(R.string.my_profile_screen_keywords_empty_title),
                description = buildString {
                    append(stringResource(R.string.my_profile_screen_keywords_empty_desc_1))
                    append("\n")
                    append(stringResource(R.string.my_profile_screen_keywords_empty_desc_2))
                    append("\n")
                    append(stringResource(R.string.my_profile_screen_keywords_empty_desc_3))
                    append("\n")
                    append(stringResource(R.string.my_profile_screen_keywords_empty_desc_4))
                },
            )
        }
    }
}

private val FabSize = 50.dp
private val FabPaddingDp = 20.dp
private val TopBarOptionIconSize = PeekrIconSize.Normal

// ------------------------------ Skeletons ------------------------------
@Composable
private fun TopBarSkeleton() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(60.dp)
            .padding(horizontal = ScreenTokens.HorizontalPadding),
        contentAlignment = Alignment.CenterStart,
    ) {
        SkeletonBox(
            Modifier
                .width(130.dp)
                .height(26.dp),
        )
    }
}

@Composable
private fun ProfileSkeleton(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(20.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(30.dp, alignment = Alignment.Start),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            SkeletonBox(Modifier.size(ProfileScreenTokens.AvatarSize), CircleShape)
            Column(
                modifier = Modifier.weight(1f),
                horizontalAlignment = Alignment.Start,
                verticalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                SkeletonBox(Modifier.size(59.dp, 24.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    SkeletonBox(Modifier.size(94.dp, 20.dp))
                }
            }
        }
        Column(
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            SkeletonBox(Modifier.size(218.dp, 18.dp))
            SkeletonBox(Modifier.size(163.dp, 18.dp))
        }
    }
}

// ------------------------------ Previews ------------------------------
@PreviewLightDarkWithBackground
@Composable
private fun TopBarPreview() {
    PeekrAppTheme {
        TopBar(
            modifier = Modifier.fillMaxWidth(),
            title = "TopBar",
            onSettingClick = {},
        )
    }
}

@PreviewLightDarkWithBackground
@Composable
private fun ProfilePreview() {
    PeekrAppTheme {
        Profile(
            name = "Hong",
            profileImageUrl = null,
            introduce = "hello world!",
            friendsCount = 28,
            onProfileImageClick = {},
            onFriendsCountClick = {},
        )
    }
}

@PreviewLightDarkWithBackground
@Composable
private fun MyProfileScreenPreview() {
    PeekrAppTheme {
        MyProfileScreen(
            modifier = Modifier.fillMaxSize(),
            uiState = MyProfileContract.UiState(
                myProfile = UiMyProfile(
                    userId = 1L,
                    displayId = "Honggd123",
                    name = "홍길동",
                    profileImageUrl = null,
                    introduce = "이 부분은 나를 간단히 소개할 수 있는 곳입니다.\n" +
                        "1 ~ 2줄 정도로 간단히 본인을 소개하세요. 1 ~ 2줄 정도로 간단히 본인을 소개하세요.\n" +
                        "이 부분은 나를 간단히 소개할 수 있는 곳입니다.\n" +
                        "1 ~ 2줄 정도로 간단히 본인을 소개하세요. 1 ~ 2줄 정도로 간단히 본인을 소개하세요.",
                    friendsCount = 86,
                    lastLoginAt = 1000L,
                    active = true,
                ),
                myKeywords = UiUserKeyword.samples,
            ),
            onUiEvent = {},
            onNavigateToKeywordAdd = {},
            onSettingClick = {},
            onFriendsCountClick = {},
            onNavigateToKeywordDetail = { _, _ -> },
        )
    }
}

@PreviewLightDarkWithBackground
@Composable
private fun MyProfileScreen_Empty_Preview() {
    PeekrAppTheme {
        MyProfileScreen(
            modifier = Modifier.fillMaxSize(),
            uiState = MyProfileContract.UiState(
                myProfile = UiMyProfile(
                    userId = 1L,
                    displayId = "Honggd123",
                    name = "홍길동",
                    profileImageUrl = null,
                    introduce = "이 부분은 나를 간단히 소개할 수 있는 곳입니다.\n" +
                        "1 ~ 2줄 정도로 간단히 본인을 소개하세요. 1 ~ 2줄 정도로 간단히 본인을 소개하세요.\n" +
                        "이 부분은 나를 간단히 소개할 수 있는 곳입니다.\n" +
                        "1 ~ 2줄 정도로 간단히 본인을 소개하세요. 1 ~ 2줄 정도로 간단히 본인을 소개하세요.",
                    friendsCount = 86,
                    lastLoginAt = 1000L,
                    active = true,
                ),
                myKeywords = emptyList(),
            ),
            onUiEvent = {},
            onNavigateToKeywordAdd = {},
            onSettingClick = {},
            onFriendsCountClick = {},
            onNavigateToKeywordDetail = { _, _ -> },
        )
    }
}

@PreviewLightDarkWithBackground
@Composable
private fun SkeletonPreview() {
    PeekrAppTheme {
        MyProfileScreen(
            modifier = Modifier.fillMaxSize(),
            uiState = MyProfileContract.UiState(
                myProfile = UiMyProfile(
                    userId = 1L,
                    displayId = "Honggd123",
                    name = "홍길동",
                    profileImageUrl = null,
                    introduce = "이 부분은 나를 간단히 소개할 수 있는 곳입니다.\n" +
                        "1 ~ 2줄 정도로 간단히 본인을 소개하세요. 1 ~ 2줄 정도로 간단히 본인을 소개하세요.\n" +
                        "이 부분은 나를 간단히 소개할 수 있는 곳입니다.\n" +
                        "1 ~ 2줄 정도로 간단히 본인을 소개하세요. 1 ~ 2줄 정도로 간단히 본인을 소개하세요.",
                    friendsCount = 86,
                    lastLoginAt = 1000L,
                    active = true,
                ),
                myKeywords = UiUserKeyword.samples,
            ),
            onUiEvent = {},
            onNavigateToKeywordAdd = {},
            onSettingClick = {},
            onFriendsCountClick = {},
            onNavigateToKeywordDetail = { _, _ -> },
        )
    }
}
