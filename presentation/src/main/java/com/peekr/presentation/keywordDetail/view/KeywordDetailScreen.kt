package com.peekr.presentation.keywordDetail.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.peekr.core.designsystem.component.avatar.PeekrAvatar
import com.peekr.core.designsystem.component.button.PeekrIconButton
import com.peekr.core.designsystem.component.icon.PeekrIconSize
import com.peekr.core.designsystem.component.skeleton.SkeletonBox
import com.peekr.core.designsystem.component.topbar.PeekrTopBar
import com.peekr.core.designsystem.theme.PeekrAppTheme
import com.peekr.core.designsystem.theme.PeekrTheme
import com.peekr.core.designsystem.util.icon.More
import com.peekr.core.designsystem.util.icon.PeekrIcons
import com.peekr.core.designsystem.util.icon.Report
import com.peekr.core.designsystem.util.token.ScreenTokens
import com.peekr.core.presentation.ui.util.PreviewLightDarkWithBackground
import com.peekr.presentation.R
import com.peekr.presentation.keywordDetail.model.UiKeywordDetail
import com.peekr.presentation.keywordDetail.state.KeywordDetailContract

/**
 * 키워드 상세 화면 프레임
 *
 * @param modifier [Modifier]
 * @param topBar 탑바 영역
 * @param contents 컨텐츠 영역(사용자 정보, 키워드, 내용 등)
 * @param comments 댓글 영역
 */
@Composable
private fun KeywordDetailScreenFrame(
    modifier: Modifier = Modifier,
    topBar: @Composable ColumnScope.() -> Unit,
    contents: @Composable ColumnScope.() -> Unit,
    comments: @Composable ColumnScope.() -> Unit,
) {
    Column(modifier = modifier) {
        // 탑바
        topBar()

        // 컨텐츠, 댓글
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = ScreenTokens.HorizontalPadding)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(30.dp),
        ) {
            // 컨텐츠
            contents()

            // 구분선
            HorizontalDivider(
                modifier = Modifier.fillMaxWidth(),
                thickness = 0.35.dp,
                color = PeekrTheme.colorScheme.lineDivider,
            )

            // 댓글
            comments()
        }
    }
}

/**
 * 키워드 상세화면
 *
 * @param modifier [Modifier]
 * @param uiState UI 상태
 */
@Composable
fun KeywordDetailScreen(
    modifier: Modifier = Modifier,
    uiState: KeywordDetailContract.UiState,
) {
    KeywordDetailScreenFrame(
        modifier = modifier.fillMaxSize(),
        topBar = {
            if (uiState.loading) {
                TopBarSkeleton()
            } else {
                TopBar(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = ScreenTokens.HorizontalPaddingWithTouchTarget),
                    myKeyword = uiState.myKeyword,
                    onMoreClick = {},
                    onReportClick = {},
                    onBackPressed = {},
                )
            }
        },
        contents = {
            if (uiState.loading) {
                ContentsSkeleton()
            } else {
                uiState.keywordDetail?.let {
                    Contents(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 10.dp),
                        userName = uiState.keywordDetail.userName,
                        profileImageUrl = uiState.keywordDetail.profileImageUrl,
                        createdAt = uiState.keywordDetail.createdAt,
                        keyword = uiState.keywordDetail.keyword,
                        description = uiState.keywordDetail.description,
                    )
                }
            }
        },
        comments = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(500.dp)
                    .background(Color.LightGray),
                contentAlignment = Alignment.TopCenter,
            ) {
                Text("댓글 영역")
            }
        },
    )
}

/**
 * 탑바
 *
 * @param modifier [Modifier]
 * @param myKeyword 내 키워드 여부
 * @param onMoreClick 더보기 클릭 시 콜백
 * @param onReportClick 신고하기 클릭 시 콜백
 * @param onBackPressed 뒤로가기 클릭 시 콜백
 */
@Composable
private fun TopBar(
    modifier: Modifier = Modifier,
    myKeyword: Boolean,
    onMoreClick: () -> Unit,
    onReportClick: () -> Unit,
    onBackPressed: () -> Unit,
) {
    PeekrTopBar(
        modifier = modifier,
        onBackPressed = onBackPressed,
        optionSlot = {
            if (myKeyword) {
                PeekrIconButton(
                    icon = PeekrIcons.Default.Normal.More,
                    iconSize = PeekrIconSize.Small,
                    contentDescription = stringResource(R.string.keyword_detail_screen_top_bar_option),
                    onClick = onMoreClick,
                )
            } else {
                PeekrIconButton(
                    icon = PeekrIcons.Filled.Normal.Report,
                    iconSize = PeekrIconSize.Small,
                    contentDescription = stringResource(R.string.keyword_detail_screen_top_bar_option_2),
                    onClick = onReportClick,
                )
            }
        },
    )
}

/**
 * 컨텐츠 (사용자 정보, 키워드, 내용 등 포함)
 *
 * @param modifier [Modifier]
 * @param userName 사용자 명
 * @param profileImageUrl 사용자 프로필 사진 URL
 * @param createdAt 키워드 생성 일자
 * @param keyword 키워드
 * @param description 키워드 내용
 */
@Composable
private fun Contents(
    modifier: Modifier = Modifier,
    userName: String,
    profileImageUrl: String?,
    createdAt: String,
    keyword: String,
    description: String,
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(20.dp),
    ) {
        // 사용자 정보
        UserInfo(
            userName = userName,
            profileImageUrl = profileImageUrl,
            createdAt = createdAt,
        )

        // 키워드, 내용
        KeywordContents(
            modifier = Modifier.fillMaxWidth(),
            keyword = keyword,
            description = description,
        )
    }
}

/**
 * 사용자 정보
 *
 * @param modifier [Modifier]
 * @param userName 사용자 명
 * @param profileImageUrl 사용자 프로필 사진 URL
 * @param createdAt 키워드 작성 일자
 */
@Composable
private fun UserInfo(
    modifier: Modifier = Modifier,
    userName: String,
    profileImageUrl: String?,
    createdAt: String,
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        // 사용자 프로필 사진
        PeekrAvatar(
            modifier = Modifier.size(UserAvatarSize),
            model = profileImageUrl,
            contentDescription = userName,
        )
        Column(
            verticalArrangement = Arrangement.spacedBy(1.dp, Alignment.CenterVertically),
            horizontalAlignment = Alignment.Start,
        ) {
            // 사용자 명
            Text(
                text = userName,
                style = PeekrTheme.typography.body3Many,
                fontWeight = FontWeight.Normal,
                color = PeekrTheme.colorScheme.textNormal,
            )
            // 키워드 작성 일자
            Text(
                text = createdAt,
                style = PeekrTheme.typography.body5,
                fontWeight = FontWeight.Normal,
                color = PeekrTheme.colorScheme.textAssist,
            )
        }
    }
}

/**
 * 키워드, 내용 영역
 *
 * @param modifier [Modifier]
 * @param keyword 키워드
 * @param description 내용
 */
@Composable
private fun KeywordContents(
    modifier: Modifier = Modifier,
    keyword: String,
    description: String,
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(20.dp),
        horizontalAlignment = Alignment.Start,
    ) {
        Text(
            text = keyword,
            style = PeekrTheme.typography.headline1,
            fontWeight = FontWeight.SemiBold,
            color = PeekrTheme.colorScheme.textNormal,
        )
        Text(
            modifier = Modifier.fillMaxWidth(),
            text = description,
            style = PeekrTheme.typography.body3Content,
            fontWeight = FontWeight.Normal,
            color = PeekrTheme.colorScheme.textNormal,
            textAlign = TextAlign.Start,
        )
    }
}

// ------------------------------ Skeleton ------------------------------

@Composable
private fun TopBarSkeleton() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 60.dp)
            .padding(horizontal = ScreenTokens.HorizontalPadding),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        SkeletonBox(Modifier.size(22.dp), CircleShape)
        SkeletonBox(Modifier.size(22.dp), CircleShape)
    }
}

@Composable
private fun ContentsSkeleton() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 10.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp),
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            SkeletonBox(Modifier.size(UserAvatarSize), CircleShape)
            Column(
                verticalArrangement = Arrangement.spacedBy(2.dp),
                horizontalAlignment = Alignment.Start,
            ) {
                SkeletonBox(Modifier.size(87.dp, 16.dp))
                SkeletonBox(Modifier.size(53.dp, 14.dp))
            }
        }

        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(20.dp),
            horizontalAlignment = Alignment.Start,
        ) {
            SkeletonBox(Modifier.size(170.dp, 24.dp))
            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                SkeletonBox(Modifier.size(208.dp, 18.dp))
                SkeletonBox(Modifier.size(230.dp, 18.dp))
                SkeletonBox(Modifier.size(296.dp, 18.dp))
                SkeletonBox(Modifier.size(246.dp, 18.dp))
            }
        }
    }
}

private val UserAvatarSize = 40.dp

// ------------------------------ Previews ------------------------------
@PreviewLightDarkWithBackground
@Composable
private fun UserInfoPreview() {
    PeekrAppTheme {
        UserInfo(
            userName = "Username",
            createdAt = "2026.01.10",
            profileImageUrl = null,
        )
    }
}

@PreviewLightDarkWithBackground
@Composable
private fun KeywordContentsPreview() {
    PeekrAppTheme {
        KeywordContents(
            keyword = "키워드 텍스트",
            description = "대통령은 국무총리·국무위원·행정각부의 장 기타 법률이 정하는 공사의 직을 겸할 수 없다." +
                "감사원의 조직·직무범위·감사위원의 자격·감사대상공무원의 범위 기타 필요한 사항은 법률로 정한다.\n" +
                "국가는 지역간의 균형있는 발전을 위하여 지역경제를 육성할 의무를 진다. 국가는 건전한 소비행위를 계도하고" +
                "생산품의 품질향상을 촉구하기 위한 소비자보호운동을 법률이 정하는 바에 의하여 보장한다.",
        )
    }
}

@PreviewLightDarkWithBackground
@Composable
private fun ContentsPreview() {
    PeekrAppTheme {
        Contents(
            userName = "Username",
            createdAt = "2026.01.10",
            profileImageUrl = null,
            keyword = "키워드 텍스트",
            description = "대통령은 국무총리·국무위원·행정각부의 장 기타 법률이 정하는 공사의 직을 겸할 수 없다." +
                "감사원의 조직·직무범위·감사위원의 자격·감사대상공무원의 범위 기타 필요한 사항은 법률로 정한다.\n" +
                "국가는 지역간의 균형있는 발전을 위하여 지역경제를 육성할 의무를 진다. 국가는 건전한 소비행위를 계도하고" +
                "생산품의 품질향상을 촉구하기 위한 소비자보호운동을 법률이 정하는 바에 의하여 보장한다.",
        )
    }
}

@PreviewLightDarkWithBackground
@Composable
private fun KeywordDetailScreenPreview() {
    PeekrAppTheme {
        KeywordDetailScreen(
            modifier = Modifier.fillMaxSize(),
            uiState = KeywordDetailContract.UiState(
                keywordDetail = UiKeywordDetail(
                    userKeywordId = 1L,
                    keywordId = 1L,
                    keyword = "키워드 텍스트",
                    description = "대통령은 국무총리·국무위원·행정각부의 장 기타 법률이 정하는 공사의 직을 겸할 수 없다." +
                        "감사원의 조직·직무범위·감사위원의 자격·감사대상공무원의 범위 기타 필요한 사항은 법률로 정한다.\n" +
                        "국가는 지역간의 균형있는 발전을 위하여 지역경제를 육성할 의무를 진다. 국가는 건전한 소비행위를 계도하고" +
                        "생산품의 품질향상을 촉구하기 위한 소비자보호운동을 법률이 정하는 바에 의하여 보장한다.",
                    userId = 1L,
                    userName = "Username",
                    profileImageUrl = null,
                    createdAt = "2026.01.01",
                    updatedAt = "2026.01.01",
                ),
            ),
        )
    }
}

@PreviewLightDarkWithBackground
@Composable
private fun SkeletonPreview() {
    PeekrAppTheme {
        KeywordDetailScreen(
            modifier = Modifier.fillMaxSize(),
            uiState = KeywordDetailContract.UiState(loading = true),
        )
    }
}
