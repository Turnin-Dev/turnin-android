package com.peekr.presentation.profile.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
import com.peekr.core.designsystem.component.icon.PeekrIcon
import com.peekr.core.designsystem.component.icon.PeekrIconSize
import com.peekr.core.designsystem.component.loading.PeekrLoadingScreen
import com.peekr.core.designsystem.component.skeleton.SkeletonBox
import com.peekr.core.designsystem.component.topbar.PeekrTopBar
import com.peekr.core.designsystem.theme.PeekrAppTheme
import com.peekr.core.designsystem.theme.PeekrTheme
import com.peekr.core.designsystem.util.PeekrShadowType
import com.peekr.core.designsystem.util.click.clickableSingle
import com.peekr.core.designsystem.util.click.clickableSingleWithoutRipple
import com.peekr.core.designsystem.util.icon.Cancel
import com.peekr.core.designsystem.util.icon.Check
import com.peekr.core.designsystem.util.icon.PeekrIconType
import com.peekr.core.designsystem.util.icon.PeekrIcons
import com.peekr.core.designsystem.util.icon.Settings
import com.peekr.core.designsystem.util.peekrShadow
import com.peekr.core.designsystem.util.token.ScreenTokens
import com.peekr.core.domain.model.FriendshipStatus
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
import com.peekr.presentation.profile.model.UiProfile
import com.peekr.presentation.profile.state.ProfileContract

/**
 * 프로필 화면
 *
 * @param modifier [Modifier]
 * @param profile 사용자 프로필
 */
@Composable
internal fun ProfileScreen(
    modifier: Modifier = Modifier,
    profile: UiProfile?,
    loading: Boolean,
    fullScreenLoading: Boolean,
    error: UiText?,
    onUiEvent: (ProfileContract.UiEvent) -> Unit,
    onOpenAddKeywordModal: () -> Unit,
    onOpenNodeOptionModal: (UserKeywordIdType, KeywordNameType) -> Unit,
    onOpenKeywordDetailModal: (UserKeywordIdType, UserIdType, KeywordNameType) -> Unit,
) {
    Box(modifier) {
        ProfileScreenFrame(
            modifier = Modifier.fillMaxSize(),
            topBar = {
                profile?.let {
                    TopBar(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(PeekrTheme.colorScheme.backgroundNormal)
                            .padding(horizontal = 10.dp),
                        title = profile.displayId,
                    )
                } ?: TopBarSkeleton()
            },
            profile = {
                profile?.let {
                    Profile(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 20.dp),
                        profileImageUrl = profile.profileImageUrl,
                        name = profile.name,
                        friendsTotal = profile.friendsCount,
                        introduce = profile.introduce,
                        onProfileImageClick = { TODO() },
                        onFriendsTotalClick = { TODO() },
                    )
                } ?: ProfileSkeleton()
            },
            keywordGraph = {
                profile?.let {
                    KeywordGraph(
                        modifier = Modifier.fillMaxWidth(),
                        profileImageUrl = profile.profileImageUrl,
                        keywords = profile.keywords,
                        onUiEvent = onUiEvent,
                        onNodeClick = { userKeywordId, userId, keyword ->
                            onOpenKeywordDetailModal(userKeywordId, userId, keyword)
                        },
                        onNodeLongClick = { userKeywordId, keyword ->
                            onOpenNodeOptionModal(userKeywordId, keyword)
                        },
                        onNodeChanged = { userKeywordId, offsetX, offsetY ->
                            onUiEvent(
                                ProfileContract.UiEvent.OnKeywordNodeOffsetChanged(
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

        profile?.let {
            PeekrFab(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(FabPaddingDp)
                    .size(FabSize),
                contentDescription = stringResource(R.string.profile_screen_fab_content_desc),
                onClick = onOpenAddKeywordModal,
            )
        }

        if (fullScreenLoading && error != null) {
            PeekrLoadingScreen()
        }
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
    Column(modifier.verticalScroll(rememberScrollState())) {
        Column(
            Modifier
                .heightIn(min = TopSectionMinHeightDp)
                .zIndex(2f),
        ) {
            // TopBar
            topBar()

            // Profile
            Box(
                Modifier
                    .background(PeekrTheme.colorScheme.backgroundNormal)
                    .padding(horizontal = ScreenTokens.HorizontalPadding)
                    .zIndex(1f),
            ) {
                profile()
            }
            ShadowSection(Modifier.fillMaxWidth())
        }

        // Keyword Graph
        Box(
            modifier = Modifier.weight(1f),
            contentAlignment = Alignment.Center,
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
        modifier = modifier.padding(
            start = ScreenTokens.HorizontalPadding,
            end = ScreenTokens.HorizontalPaddingWithTouchTarget,
        ),
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

@Preview
@Composable
private fun TopBarSkeleton() {
    PeekrTopBar(
        modifier = Modifier.fillMaxWidth(),
        title = "",
        optionSlot = { Spacer(Modifier.size(1.dp)) },
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
        modifier = modifier,
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
private fun ProfileSkeleton(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 20.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(30.dp, alignment = Alignment.Start),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            SkeletonBox(Modifier.size(AvatarSize, AvatarSize), CircleShape)
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
        }
    }
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
    onUiEvent: (ProfileContract.UiEvent) -> Unit,
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
                    onUiEvent(ProfileContract.UiEvent.UpdateKeywordNodeOffset)
                    nodeChanged = false
                },
                onCancel = {
                    onUiEvent(ProfileContract.UiEvent.ResetKeywordNodeOffset)
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
fun NodeChangedButtons(
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
            contentDescription = stringResource(R.string.profile_screen_node_changed_btn_desc_ok),
            onClick = onChange,
        )
        NodeChangedButton(
            modifier = Modifier,
            icon = PeekrIcons.Default.Bold.Cancel,
            contentDescription = stringResource(R.string.profile_screen_node_changed_btn_desc_cancel),
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
fun NodeChangedButton(
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

@Composable
private fun KeywordGraphSkeleton() {
    Box(Modifier.fillMaxSize(), Alignment.Center) {
        SkeletonBox(Modifier.size(49.dp, 49.dp), CircleShape)
    }
}

@Composable
private fun ShadowSection(modifier: Modifier = Modifier) {
    HorizontalDivider(
        modifier = modifier
            .fillMaxWidth()
            .peekrShadow(
                type = PeekrShadowType.Custom(
                    blur = 6.dp,
                    lightColor = Color.Black,
                    darkColor = Color.White,
                    alpha = 0.25f,
                ),
            ),
        color = Color.Transparent,
    )
}

private val FabSize = 50.dp
private val AvatarSize = 70.dp
private val TopSectionMinHeightDp = 166.dp
private val FabPaddingDp = 20.dp

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

@PreviewLightDarkWithBackground
@Composable
private fun NodeChangedButtonsPreview() {
    PeekrAppTheme {
        NodeChangedButtons(
            onChange = {},
            onCancel = {},
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
                .background(Color.LightGray),
            profile = UiProfile(
                displayId = "Honggd123",
                name = "홍길동",
                profileImageUrl = null,
                introduce = "이 부분은 나를 간단히 소개할 수 있는 곳입니다.\n" +
                    "1 ~ 2줄 정도로 간단히 본인을 소개하세요.",
                friendsCount = 86,
                lastLoginAt = 1000L,
                active = true,
                friendshipStatus = FriendshipStatus.NOTHING,
                keywords = UiUserKeyword.samples,
            ),
            loading = false,
            fullScreenLoading = false,
            error = null,
            onUiEvent = {},
            onOpenAddKeywordModal = {},
            onOpenNodeOptionModal = { _, _ -> },
            onOpenKeywordDetailModal = { _, _, _ -> },
        )
    }
}

@PreviewLightDark
@Composable
private fun ProfileScreenShimmerPreview() {
    PeekrAppTheme {
        ProfileScreen(
            modifier = Modifier.fillMaxSize(),
            profile = null,
            loading = false,
            fullScreenLoading = false,
            error = null,
            onUiEvent = {},
            onOpenAddKeywordModal = {},
            onOpenNodeOptionModal = { _, _ -> },
            onOpenKeywordDetailModal = { _, _, _ -> },
        )
    }
}
