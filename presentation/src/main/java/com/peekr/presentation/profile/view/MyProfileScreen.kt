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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.peekr.core.designsystem.component.button.PeekrIconButton
import com.peekr.core.designsystem.component.fab.PeekrFab
import com.peekr.core.designsystem.component.icon.PeekrIcon
import com.peekr.core.designsystem.component.icon.PeekrIconSize
import com.peekr.core.designsystem.component.loading.PeekrLoadingScreen
import com.peekr.core.designsystem.component.skeleton.SkeletonBox
import com.peekr.core.designsystem.component.topbar.PeekrTopBar
import com.peekr.core.designsystem.theme.PeekrAppTheme
import com.peekr.core.designsystem.theme.PeekrTheme
import com.peekr.core.designsystem.util.PeekrShadowType
import com.peekr.core.designsystem.util.click.clickableSingle
import com.peekr.core.designsystem.util.icon.Cancel
import com.peekr.core.designsystem.util.icon.Check
import com.peekr.core.designsystem.util.icon.PeekrIconType
import com.peekr.core.designsystem.util.icon.PeekrIcons
import com.peekr.core.designsystem.util.icon.Settings
import com.peekr.core.designsystem.util.peekrShadow
import com.peekr.core.designsystem.util.token.ScreenTokens
import com.peekr.core.presentation.feature.keyword.KeywordNameType
import com.peekr.core.presentation.feature.keyword.NodeOffsetXType
import com.peekr.core.presentation.feature.keyword.NodeOffsetYType
import com.peekr.core.presentation.feature.keyword.UserIdType
import com.peekr.core.presentation.feature.keyword.UserKeywordIdType
import com.peekr.core.presentation.feature.keyword.graph.KeywordGraphView
import com.peekr.core.presentation.ui.model.UiUserKeyword
import com.peekr.core.presentation.ui.util.PreviewLightDarkWithBackground
import com.peekr.core.presentation.ui.util.UiText
import com.peekr.presentation.R
import com.peekr.presentation.profile.model.UiMyProfile
import com.peekr.presentation.profile.state.MyProfileContract
import com.peekr.presentation.profile.view.frame.ProfileFrame
import com.peekr.presentation.profile.view.frame.ProfileScreenFrame
import com.peekr.presentation.profile.view.frame.ProfileScreenTokens

/**
 * 나의 프로필 화면
 *
 * @param modifier [Modifier]
 * @param myProfile 프로필 - [UiMyProfile]
 * @param fullScreenLoading 전체 화면 로딩 여부
 * @param error 에러
 * @param onUiEvent UI 이벤트
 * @param onSettingClick 설정 클릭 시
 * @param onOpenAddKeywordModal 키워드 추가 모달 열기 콜백
 * @param onOpenNodeOptionModal 키워드 노드 옵션 모달 열기 콜백
 * @param onOpenKeywordDetailModal 키워드 설명 모달 열기 콜백
 */
@Composable
fun MyProfileScreen(
    modifier: Modifier = Modifier,
    myProfile: UiMyProfile?,
    fullScreenLoading: Boolean,
    error: UiText?,
    onUiEvent: (MyProfileContract.UiEvent) -> Unit,
    onSettingClick: () -> Unit,
    onOpenAddKeywordModal: () -> Unit,
    onOpenNodeOptionModal: (UserKeywordIdType, KeywordNameType) -> Unit,
    onOpenKeywordDetailModal: (UserKeywordIdType, UserIdType, KeywordNameType) -> Unit,
) {
    Box(modifier) {
        ProfileScreenFrame(
            modifier = Modifier.fillMaxSize(),
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
                        onFriendsCountClick = {},
                    )
                } ?: ProfileSkeleton()
            },
            keywordGraph = {
                myProfile?.let {
                    KeywordGraph(
                        modifier = Modifier.fillMaxWidth(),
                        profileImageUrl = myProfile.profileImageUrl,
                        keywords = myProfile.keywords,
                        onUiEvent = onUiEvent,
                        onNodeClick = { userKeywordId, userId, keyword ->
                            onOpenKeywordDetailModal(userKeywordId, userId, keyword)
                        },
                        onNodeLongClick = { userKeywordId, keyword ->
                            onOpenNodeOptionModal(userKeywordId, keyword)
                        },
                        onNodeChanged = { userKeywordId, offsetX, offsetY ->
                            onUiEvent(
                                MyProfileContract.UiEvent.OnKeywordNodeOffsetChanged(
                                    userKeywordId = userKeywordId,
                                    offsetX = offsetX,
                                    offsetY = offsetY,
                                ),
                            )
                        },
                    )
                } ?: KeywordGraphSkeleton()
            },
        )

        myProfile?.let {
            PeekrFab(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(FabPaddingDp)
                    .size(FabSize),
                contentDescription = stringResource(R.string.my_profile_screen_fab_content_desc),
                onClick = onOpenAddKeywordModal,
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

/**
 * 키워드 그래프 뷰 영역
 *
 * @param modifier [Modifier]
 * @param profileImageUrl 사용자 프로필 사진 url
 * @param keywords 사용자 키워드 리스트
 * @param onUiEvent UI 이벤트
 * @param onNodeClick 사용자 키워드 노드 클릭 시
 * @param onNodeLongClick 사용자 키워드 노드 길게 클릭 시
 * @param onNodeChanged 사용자 키워드 노드 오프셋 변경 시
 */
@Composable
private fun KeywordGraph(
    modifier: Modifier = Modifier,
    profileImageUrl: String?,
    keywords: List<UiUserKeyword>,
    onUiEvent: (MyProfileContract.UiEvent) -> Unit,
    onNodeClick: (UserKeywordIdType, UserIdType, KeywordNameType) -> Unit,
    onNodeLongClick: (UserKeywordIdType, KeywordNameType) -> Unit,
    onNodeChanged: (UserKeywordIdType, NodeOffsetXType, NodeOffsetYType) -> Unit,
) {
    var nodeChanged by rememberSaveable { mutableStateOf(false) }
    var nodeReset by rememberSaveable { mutableStateOf(false) }
    LaunchedEffect(nodeReset) { if (nodeReset) nodeReset = false }

    Box(modifier = modifier) {
        KeywordGraphView(
            modifier = Modifier,
            profileImageUrl = profileImageUrl,
            keywords = keywords,
            nodeReset = nodeReset,
            onNodeClick = { userKeywordId, userId, keyword ->
                onNodeClick(userKeywordId, userId, keyword)
            },
            onNodeLongClick = { userKeywordId, keyword ->
                onNodeLongClick(userKeywordId, keyword)
            },
            onNodeChanged = { userKeywordId, offsetX, offsetY ->
                nodeChanged = keywords.any { it.id == userKeywordId }
                onNodeChanged(userKeywordId, offsetX, offsetY)
            },
        )
        if (nodeChanged) {
            NodeChangedButtons(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(FabPaddingDp),
                onChange = {
                    onUiEvent(MyProfileContract.UiEvent.UpdateKeywordNodeOffset)
                    nodeChanged = false
                },
                onCancel = {
                    onUiEvent(MyProfileContract.UiEvent.ResetKeywordNodeOffset)
                    nodeReset = true
                    nodeChanged = false
                },
            )
        }
    }
}

/**
 * 노드 위치 변경 시 표시할 버튼 모음
 *
 * @param modifier [Modifier]
 * @param onChange 노드 위치 변경 수락 시
 * @param onCancel 노드 위치 변경 취소 시
 */
@Composable
private fun NodeChangedButtons(
    modifier: Modifier = Modifier,
    onChange: () -> Unit,
    onCancel: () -> Unit,
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        NodeChangedButton(
            modifier = Modifier,
            icon = PeekrIcons.Default.Bold.Check,
            contentDescription = stringResource(R.string.my_profile_screen_node_changed_btn_desc_ok),
            onClick = onChange,
        )
        NodeChangedButton(
            modifier = Modifier,
            icon = PeekrIcons.Default.Bold.Cancel,
            contentDescription = stringResource(R.string.my_profile_screen_node_changed_btn_desc_cancel),
            onClick = onCancel,
        )
    }
}

/**
 * 노드 위치 변경 시 표시할 버튼
 *
 * @param modifier [Modifier]
 * @param icon [PeekrIconType]
 * @param contentDescription 아이콘 설명
 * @param onClick 버튼 클릭 시 콜백
 */
@Composable
private fun NodeChangedButton(
    modifier: Modifier = Modifier,
    icon: PeekrIconType,
    contentDescription: String,
    onClick: () -> Unit,
) {
    Box(
        modifier = modifier
            .peekrShadow(
                type = PeekrShadowType.Normal,
                shape = RoundedCornerShape(12.dp),
            )
            .clip(RoundedCornerShape(12.dp))
            .size(40.dp)
            .background(PeekrTheme.colorScheme.backgroundNormal)
            .clickableSingle(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        PeekrIcon(
            icon = icon,
            iconSize = PeekrIconSize.Small,
            contentDescription = contentDescription,
            tint = PeekrTheme.colorScheme.textNormal,
        )
    }
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

@Composable
private fun KeywordGraphSkeleton() {
    Box(Modifier.fillMaxSize(), Alignment.Center) {
        SkeletonBox(Modifier.size(49.dp, 49.dp), CircleShape)
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
                keywords = UiUserKeyword.samples,
            ),
            fullScreenLoading = false,
            error = null,
            onUiEvent = {},
            onOpenAddKeywordModal = {},
            onOpenNodeOptionModal = { _, _ -> },
            onOpenKeywordDetailModal = { _, _, _ -> },
            onSettingClick = {},
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
            fullScreenLoading = false,
            error = null,
            onUiEvent = {},
            onOpenAddKeywordModal = {},
            onOpenNodeOptionModal = { _, _ -> },
            onOpenKeywordDetailModal = { _, _, _ -> },
            onSettingClick = {},
        )
    }
}
