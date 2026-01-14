package com.peekr.presentation.profile.view

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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.peekr.core.designsystem.component.button.PeekrIconButton
import com.peekr.core.designsystem.component.fab.PeekrFab
import com.peekr.core.designsystem.component.icon.PeekrIconSize
import com.peekr.core.designsystem.component.loading.PeekrLoadingScreen
import com.peekr.core.designsystem.component.skeleton.SkeletonBox
import com.peekr.core.designsystem.component.topbar.PeekrTopBar
import com.peekr.core.designsystem.theme.PeekrAppTheme
import com.peekr.core.designsystem.theme.PeekrTheme
import com.peekr.core.designsystem.util.icon.PeekrIcons
import com.peekr.core.designsystem.util.icon.Plus
import com.peekr.core.designsystem.util.icon.Settings
import com.peekr.core.designsystem.util.token.ScreenTokens
import com.peekr.core.presentation.ui.model.UiUserKeyword
import com.peekr.core.presentation.ui.util.PreviewLightDarkWithBackground
import com.peekr.core.presentation.ui.util.UiText
import com.peekr.presentation.R
import com.peekr.presentation.profile.model.UiKeywordDetail
import com.peekr.presentation.profile.model.UiMyProfile
import com.peekr.presentation.profile.state.MyProfileContract
import com.peekr.presentation.profile.view.common.KeywordsTitleSkeleton
import com.peekr.presentation.profile.view.common.KeywordsTitleView
import com.peekr.presentation.profile.view.common.ProfileFrame
import com.peekr.presentation.profile.view.common.ProfileScreenFrame
import com.peekr.presentation.profile.view.common.ProfileScreenTokens
import com.peekr.presentation.profile.view.common.keywordItemsSkeleton
import com.peekr.presentation.profile.view.common.keywordItemsView

/**
 * 나의 프로필 화면
 *
 * @param modifier [Modifier]
 * @param myProfile 프로필 - [UiMyProfile]
 * @param myKeywords 키워드 - [UiUserKeyword]
 * @param loading 부분 로딩 여부
 * @param fullScreenLoading 전체 화면 로딩 여부
 * @param error 에러
 * @param onUiEvent UI 이벤트
 * @param onSettingClick 설정 클릭 시
 * @param onFriendsCountClick 친구 수 클릭 시
 * @param onNavigateToKeywordAddScreen 키워드 추가 화면 이동 콜백
 */
@Composable
fun MyProfileScreen(
    modifier: Modifier = Modifier,
    myProfile: UiMyProfile?,
    myKeywords: List<UiKeywordDetail>,
    loading: Boolean,
    fullScreenLoading: Boolean,
    error: UiText?,
    onUiEvent: (MyProfileContract.UiEvent) -> Unit,
    onSettingClick: () -> Unit,
    onFriendsCountClick: (Long) -> Unit,
    onNavigateToKeywordAddScreen: () -> Unit,
) {
    Box(modifier) {
        ProfileScreenFrame(
            modifier = Modifier
                .fillMaxSize()
                .background(PeekrTheme.colorScheme.backgroundNormal),
            topBar = {
                myProfile?.let {
                    TopBar(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(
                                start = ScreenTokens.HorizontalPadding,
                                end = ScreenTokens.HorizontalPaddingWithTouchTarget,
                            ),
                        title = myProfile.displayId,
                        onSettingClick = onSettingClick,
                    )
                } ?: TopBarSkeleton()
            },
            profile = {
                myProfile?.let {
                    Profile(
                        modifier = Modifier.fillMaxWidth(),
                        profileImageUrl = myProfile.profileImageUrl,
                        name = myProfile.name,
                        friendsCount = myProfile.friendsCount,
                        introduce = myProfile.introduce,
                        onProfileImageClick = {},
                        onFriendsCountClick = { onFriendsCountClick(myProfile.userId) },
                    )
                } ?: ProfileSkeleton()
            },
            keywordsTitle = {
                if (loading) {
                    KeywordsTitleSkeleton()
                } else {
                    KeywordsTitleView(
                        modifier = Modifier.align(Alignment.CenterStart),
                        count = myKeywords.count(),
                    )
                }
            },
            keywords = {
                if (loading) {
                    keywordItemsSkeleton()
                } else {
                    keywordItemsView(
                        keywords = myKeywords,
                        onClick = { uiUserKeyword ->
                        },
                    )
                }
            },
        )

        myProfile?.let {
            PeekrFab(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(FabPaddingDp)
                    .size(FabSize),
                icon = PeekrIcons.Default.Bold.Plus,
                contentDescription = stringResource(R.string.my_profile_screen_fab_content_desc),
                onClick = onNavigateToKeywordAddScreen,
            )
        }

        if (fullScreenLoading) {
            PeekrLoadingScreen()
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

private val FabSize = 50.dp
private val FabPaddingDp = 20.dp
private val TopBarOptionIconSize = PeekrIconSize.Small

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
            myKeywords = UiKeywordDetail.samples,
            loading = false,
            fullScreenLoading = false,
            error = null,
            onUiEvent = {},
            onNavigateToKeywordAddScreen = {},
            onSettingClick = {},
            onFriendsCountClick = {},
        )
    }
}

@PreviewLightDarkWithBackground
@Composable
private fun SkeletonPreview() {
    PeekrAppTheme {
        MyProfileScreen(
            modifier = Modifier.fillMaxSize(),
            myProfile = null,
            myKeywords = UiKeywordDetail.samples,
            loading = false,
            fullScreenLoading = false,
            error = null,
            onUiEvent = {},
            onNavigateToKeywordAddScreen = {},
            onSettingClick = {},
            onFriendsCountClick = {},
        )
    }
}
