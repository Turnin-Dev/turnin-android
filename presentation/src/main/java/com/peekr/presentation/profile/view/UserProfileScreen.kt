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
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.peekr.core.designsystem.component.button.PeekrButtonStyle
import com.peekr.core.designsystem.component.button.PeekrIconButton
import com.peekr.core.designsystem.component.button.PeekrOutlinedButton
import com.peekr.core.designsystem.component.button.PeekrSolidButton
import com.peekr.core.designsystem.component.icon.PeekrIconSize
import com.peekr.core.designsystem.component.skeleton.SkeletonBox
import com.peekr.core.designsystem.component.topbar.PeekrTopBar
import com.peekr.core.designsystem.theme.PeekrAppTheme
import com.peekr.core.designsystem.theme.PeekrTheme
import com.peekr.core.designsystem.util.icon.Arrow2Right
import com.peekr.core.designsystem.util.icon.Cancel
import com.peekr.core.designsystem.util.icon.Check
import com.peekr.core.designsystem.util.icon.PeekrIcons
import com.peekr.core.designsystem.util.icon.Plus
import com.peekr.core.designsystem.util.icon.Report
import com.peekr.core.designsystem.util.token.ScreenTokens
import com.peekr.core.domain.friend.model.FriendStatus
import com.peekr.core.presentation.ui.model.UiUserKeyword
import com.peekr.core.presentation.ui.util.PreviewLightDarkWithBackground
import com.peekr.presentation.R
import com.peekr.presentation.profile.model.UiUserProfile
import com.peekr.presentation.profile.state.UserProfileContract
import com.peekr.presentation.profile.view.common.KeywordsTitleSkeleton
import com.peekr.presentation.profile.view.common.KeywordsTitleView
import com.peekr.presentation.profile.view.common.ProfileFrame
import com.peekr.presentation.profile.view.common.ProfileScreenFrame
import com.peekr.presentation.profile.view.common.ProfileScreenTokens
import com.peekr.presentation.profile.view.common.keywordItemsSkeleton
import com.peekr.presentation.profile.view.common.keywordItemsView

/**
 * 사용자 프로필 화면
 *
 * @param modifier [Modifier]
 * @param uiState UI 상태
 * @param onUiEvent UI 이벤트
 * @param onNavigateToKeywordDetail 키워드 상세 화면 이동 콜백
 * @param onBackPressed 뒤로가기 클릭 시 콜백
 */
@Composable
fun UserProfileScreen(
    modifier: Modifier = Modifier,
    uiState: UserProfileContract.UiState,
    onUiEvent: (UserProfileContract.UiEvent) -> Unit,
    onNavigateToKeywordDetail: (userId: Long, userKeywordId: Long) -> Unit,
    onBackPressed: () -> Unit, // TODO: 람다로 직접 받을지, 이벤트로 받을지 고민
) {
    Box(modifier) {
        ProfileScreenFrame(
            modifier = Modifier.fillMaxSize(),
            isRefreshing = uiState.isRefreshing,
            onRefresh = { onUiEvent(UserProfileContract.UiEvent.Refresh) },
            topBar = {
                if (uiState.profileLoading) {
                    TopBarSkeleton()
                } else {
                    uiState.profile?.let {
                        TopBar(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = ScreenTokens.HorizontalPaddingWithTouchTarget),
                            title = uiState.profile.displayId,
                            onReportClick = {
                                onUiEvent(
                                    UserProfileContract.UiEvent.OnReport,
                                )
                            },
                            onBackPressed = onBackPressed,
                        )
                    }
                }
            },
            profile = {
                if (uiState.profileLoading) {
                    ProfileSkeleton()
                } else {
                    uiState.profile?.let {
                        Profile(
                            modifier = Modifier.fillMaxWidth(),
                            profileImageUrl = it.profileImageUrl,
                            name = it.name,
                            friendsCount = it.friendsCount,
                            introduce = it.introduce,
                            friendStatus = it.friendStatus,
                            onProfileImageClick = {},
                            onFriendsCountClick = {},
                            onFriendsButtonClick = { currentFriendshipStatus ->
                                onUiEvent(
                                    UserProfileContract.UiEvent.OnFriendButtonClick(
                                        friendStatus = currentFriendshipStatus,
                                    ),
                                )
                            },
                        )
                    }
                }
            },
            keywordsTitle = {
                if (uiState.keywordsLoading) {
                    KeywordsTitleSkeleton()
                } else {
                    KeywordsTitleView(
                        modifier = Modifier.align(Alignment.CenterStart),
                        count = uiState.keywords.count(),
                    )
                }
            },
            keywords = {
                if (uiState.keywordsLoading) {
                    keywordItemsSkeleton()
                } else {
                    keywordItemsView(
                        keywords = uiState.keywords,
                        onClick = { uiUserKeyword ->
                            uiState.profile?.let {
                                onNavigateToKeywordDetail(it.userId, uiUserKeyword.id)
                            }
                        },
                    )
                }
            },
        )
    }
}

/**
 * 탑바
 *
 * @param modifier [Modifier]
 * @param title 탑바 타이틀
 * @param onReportClick 신고 클릭 시
 * @param onBackPressed 뒤로 가기 클릭 시
 */
@Composable
private fun TopBar(
    modifier: Modifier = Modifier,
    title: String,
    onReportClick: () -> Unit,
    onBackPressed: () -> Unit,
) {
    PeekrTopBar(
        modifier = modifier,
        title = title,
        optionSlot = {
            PeekrIconButton(
                icon = PeekrIcons.Filled.Bold.Report,
                iconSize = TopBarOptionIconSize,
                contentDescription = stringResource(R.string.user_profile_screen_top_bar_report),
                onClick = onReportClick,
            )
        },
        onBackPressed = onBackPressed,
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
 * @param friendStatus 친구 관계 상태
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
    friendStatus: FriendStatus,
    onProfileImageClick: () -> Unit,
    onFriendsCountClick: () -> Unit,
    onFriendsButtonClick: (currentFriendStatus: FriendStatus) -> Unit,
) {
    ProfileFrame(
        modifier = modifier,
        profileImageUrl = profileImageUrl,
        name = name,
        friendsCount = friendsCount,
        introduce = introduce,
        onProfileImageClick = onProfileImageClick,
        onFriendsCountClick = onFriendsCountClick,
        friendshipStatusButton = {
            when (friendStatus) {
                FriendStatus.NOTHING -> {
                    PeekrSolidButton(
                        text = stringResource(R.string.user_profile_screen_friendship_status_btn_nothing),
                        style = PeekrButtonStyle.Tiny,
                        icon = PeekrIcons.Default.Bold.Plus,
                        onClick = { onFriendsButtonClick(friendStatus) },
                    )
                }

                FriendStatus.FRIENDS -> {
                    PeekrOutlinedButton(
                        text = stringResource(R.string.user_profile_screen_friendship_status_btn_friends),
                        style = PeekrButtonStyle.Tiny,
                        icon = PeekrIcons.Default.Bold.Check,
                        onClick = { onFriendsButtonClick(friendStatus) },
                    )
                }

                FriendStatus.REQUESTED -> {
                    PeekrOutlinedButton(
                        text = stringResource(R.string.user_profile_screen_friendship_status_btn_requested),
                        style = PeekrButtonStyle.Tiny,
                        icon = PeekrIcons.Default.Bold.Cancel,
                        onClick = { onFriendsButtonClick(friendStatus) },
                    )
                }

                FriendStatus.RECEIVED -> {
                    PeekrSolidButton(
                        text = stringResource(R.string.user_profile_screen_friendship_status_btn_received),
                        style = PeekrButtonStyle.Tiny,
                        icon = PeekrIcons.Default.Bold.Arrow2Right,
                        onClick = { onFriendsButtonClick(friendStatus) },
                    )
                }
            }
        },
    )
}

private val TopBarOptionIconSize = PeekrIconSize.Small

// ------------------------------ Skeleton ------------------------------
@Composable
private fun TopBarSkeleton() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(60.dp)
            .padding(
                start = ScreenTokens.HorizontalPadding,
                end = ScreenTokens.HorizontalPaddingWithTouchTarget,
            ),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        SkeletonBox(
            Modifier
                .width(130.dp)
                .height(26.dp),
        )
        SkeletonBox(
            modifier = Modifier.size(40.dp),
            shape = CircleShape,
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

        Box(
            modifier = Modifier
                .weight(1f)
                .wrapContentSize(Alignment.CenterEnd),
        ) {
            SkeletonBox(Modifier.size(81.dp, 25.dp))
        }
    }
}

// ------------------------------ Preview ------------------------------
@PreviewLightDarkWithBackground
@Composable
private fun TopBarPreview() {
    PeekrAppTheme {
        TopBar(
            modifier = Modifier.fillMaxWidth(),
            title = "TopBar",
            onReportClick = {},
            onBackPressed = {},
        )
    }
}

@PreviewLightDarkWithBackground
@Composable
private fun ProfilePreview() {
    PeekrAppTheme {
        Column {
            FriendStatus.entries.forEach {
                Profile(
                    name = "Hong",
                    profileImageUrl = null,
                    introduce = "hello world!",
                    friendsCount = 28,
                    friendStatus = it,
                    onProfileImageClick = {},
                    onFriendsCountClick = {},
                    onFriendsButtonClick = {},
                )
            }
        }
    }
}

@PreviewLightDarkWithBackground
@Composable
private fun UserProfileScreenPreview() {
    PeekrAppTheme {
        UserProfileScreen(
            modifier = Modifier
                .fillMaxSize()
                .background(PeekrTheme.colorScheme.backgroundNormal),
            uiState = UserProfileContract.UiState(
                profile = UiUserProfile(
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
                    friendStatus = FriendStatus.NOTHING,
                ),
                keywords = UiUserKeyword.samples,
            ),
            onUiEvent = {},
            onNavigateToKeywordDetail = { _, _ -> },
            onBackPressed = {},
        )
    }
}
