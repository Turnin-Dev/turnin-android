package com.peekr.presentation.profile.view

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.peekr.core.designsystem.component.button.PeekrButtonStyle
import com.peekr.core.designsystem.component.button.PeekrIconButton
import com.peekr.core.designsystem.component.button.PeekrOutlinedButton
import com.peekr.core.designsystem.component.button.PeekrSolidButton
import com.peekr.core.designsystem.component.icon.PeekrIconSize
import com.peekr.core.designsystem.component.topbar.PeekrTopBar
import com.peekr.core.designsystem.theme.PeekrAppTheme
import com.peekr.core.designsystem.util.icon.Arrow2Right
import com.peekr.core.designsystem.util.icon.Cancel
import com.peekr.core.designsystem.util.icon.Check
import com.peekr.core.designsystem.util.icon.PeekrIcons
import com.peekr.core.designsystem.util.icon.Plus
import com.peekr.core.designsystem.util.icon.Report
import com.peekr.core.designsystem.util.token.ScreenTokens
import com.peekr.core.domain.model.FriendshipStatus
import com.peekr.core.presentation.feature.keyword.KeywordNameType
import com.peekr.core.presentation.feature.keyword.UserIdType
import com.peekr.core.presentation.feature.keyword.UserKeywordIdType
import com.peekr.core.presentation.feature.keyword.view.KeywordGraphView
import com.peekr.core.presentation.ui.model.UiUserKeyword
import com.peekr.core.presentation.ui.util.PreviewLightDarkWithBackground
import com.peekr.presentation.R
import com.peekr.presentation.profile.model.UiUserProfile
import com.peekr.presentation.profile.state.UserProfileContract
import com.peekr.presentation.profile.view.frame.ProfileFrame
import com.peekr.presentation.profile.view.frame.ProfileScreenFrame

@Composable
fun UserProfileScreen(
    modifier: Modifier = Modifier,
    userProfile: UiUserProfile? = null,
    onUiEvent: (UserProfileContract.UiEvent) -> Unit,
    onBackPressed: () -> Unit, // TODO: 람다로 직접 받을지, 이벤트로 받을지 고민
) {
    Box(modifier) {
        ProfileScreenFrame(
            modifier = Modifier.fillMaxSize(),
            topBar = {
                userProfile?.let {
                    TopBar(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = ScreenTokens.HorizontalPaddingWithTouchTarget),
                        title = userProfile.displayId,
                        onReportClick = {},
                        onBackPressed = {},
                    )
                } // TODO: 스켈레톤 화면 추가
            },
            profile = {
                userProfile?.let {
                    Profile(
                        modifier = Modifier.fillMaxWidth(),
                        profileImageUrl = userProfile.profileImageUrl,
                        name = userProfile.name,
                        friendsCount = userProfile.friendsCount,
                        introduce = userProfile.introduce,
                        friendshipStatus = userProfile.friendshipStatus,
                        onProfileImageClick = {},
                        onFriendsCountClick = {},
                        onFriendsButtonClick = { currentFriendshipStatus ->
                        },
                    )
                } // TODO: 스켈레톤 화면 추가
            },
            keywordGraph = {
                userProfile?.let {
                    KeywordGraph(
                        modifier = Modifier.fillMaxWidth(),
                        profileImageUrl = userProfile.profileImageUrl,
                        keywords = userProfile.keywords,
                        onNodeClick = { _, _, _ -> },
                    )
                } // TODO: 스켈레톤 화면 추가
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
 * @param friendshipStatus 친구 관계 상태
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
    friendshipStatus: FriendshipStatus,
    onProfileImageClick: () -> Unit,
    onFriendsCountClick: () -> Unit,
    onFriendsButtonClick: (currentFriendshipStatus: FriendshipStatus) -> Unit,
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
            when (friendshipStatus) {
                FriendshipStatus.NOTHING -> {
                    PeekrSolidButton(
                        text = stringResource(R.string.user_profile_screen_friendship_status_btn_nothing),
                        style = PeekrButtonStyle.Tiny,
                        icon = PeekrIcons.Default.Bold.Plus,
                        onClick = { onFriendsButtonClick(FriendshipStatus.NOTHING) },
                    )
                }

                FriendshipStatus.FRIENDS -> {
                    PeekrOutlinedButton(
                        text = stringResource(R.string.user_profile_screen_friendship_status_btn_nothing),
                        style = PeekrButtonStyle.Tiny,
                        icon = PeekrIcons.Default.Bold.Check,
                        onClick = { onFriendsButtonClick(FriendshipStatus.FRIENDS) },
                    )
                }

                FriendshipStatus.REQUESTED -> {
                    PeekrOutlinedButton(
                        text = stringResource(R.string.user_profile_screen_friendship_status_btn_requested),
                        style = PeekrButtonStyle.Tiny,
                        icon = PeekrIcons.Default.Bold.Cancel,
                        onClick = { onFriendsButtonClick(FriendshipStatus.NOTHING) },
                    )
                }

                FriendshipStatus.RECEIVED -> {
                    PeekrSolidButton(
                        text = stringResource(R.string.user_profile_screen_friendship_status_btn_received),
                        style = PeekrButtonStyle.Tiny,
                        icon = PeekrIcons.Default.Bold.Arrow2Right,
                        onClick = { onFriendsButtonClick(FriendshipStatus.NOTHING) },
                    )
                }
            }
        },
    )
}

/**
 * 키워드 그래프 뷰 영역
 *
 * @param modifier [Modifier]
 * @param profileImageUrl 사용자 프로필 사진 url
 * @param keywords 사용자 키워드 리스트
 * @param onNodeClick 사용자 키워드 노드 클릭 시
 */
@Composable
private fun KeywordGraph(
    modifier: Modifier = Modifier,
    profileImageUrl: String?,
    keywords: List<UiUserKeyword>,
    onNodeClick: (UserKeywordIdType, UserIdType, KeywordNameType) -> Unit,
) {
    Box(modifier = modifier) {
        KeywordGraphView(
            modifier = Modifier,
            profileImageUrl = profileImageUrl,
            keywords = keywords,
            freeGesture = false,
            onNodeClick = { userKeywordId, userId, keyword ->
                onNodeClick(userKeywordId, userId, keyword)
            },
        )
    }
}

private val TopBarOptionIconSize = PeekrIconSize.Small

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
            FriendshipStatus.entries.forEach {
                Profile(
                    name = "Hong",
                    profileImageUrl = null,
                    introduce = "hello world!",
                    friendsCount = 28,
                    friendshipStatus = it,
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
            modifier = Modifier.fillMaxSize(),
            userProfile = UiUserProfile(
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
                friendshipStatus = FriendshipStatus.NOTHING,
                keywords = UiUserKeyword.samples,
            ),
            onUiEvent = {},
            onBackPressed = {},
        )
    }
}
