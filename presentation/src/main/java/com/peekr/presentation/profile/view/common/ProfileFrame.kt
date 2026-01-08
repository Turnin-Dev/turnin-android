package com.peekr.presentation.profile.view.common

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.peekr.core.designsystem.component.avatar.PeekrAvatar
import com.peekr.core.designsystem.theme.PeekrAppTheme
import com.peekr.core.designsystem.theme.PeekrTheme
import com.peekr.core.designsystem.util.click.clickableSingle
import com.peekr.core.designsystem.util.click.clickableSingleWithoutRipple
import com.peekr.core.presentation.ui.util.PreviewLightDarkWithBackground
import com.peekr.presentation.R

/**
 * 프로필 프레임
 *
 * @param modifier [Modifier]
 * @param profileImageUrl 프로필 사진 url
 * @param name 이름
 * @param friendsCount 친구 수
 * @param introduce 소개 글
 * @param onProfileImageClick 프로필 사진 클릭 시
 * @param onFriendsCountClick 친구 수 클릭 시
 * @param friendshipStatusButton 친구 관계 상태 버튼 (나의 프로필 에선 활성화하지 않는다.)
 */
@Composable
fun ProfileFrame(
    modifier: Modifier = Modifier,
    profileImageUrl: String?,
    name: String,
    friendsCount: Long,
    introduce: String,
    onProfileImageClick: () -> Unit,
    onFriendsCountClick: () -> Unit,
    friendshipStatusButton: (@Composable () -> Unit)? = null,
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(20.dp),
    ) {
        // 프로필 사진, 이름, 친구 수, 친구 상태 버튼
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(30.dp, alignment = Alignment.Start),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            // 프로필 사진
            PeekrAvatar(
                modifier = Modifier.size(ProfileScreenTokens.AvatarSize),
                model = profileImageUrl,
                contentDescription = stringResource(R.string.my_profile_screen_avatar_content_desc),
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
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                // 친구 수
                Row(
                    modifier = Modifier.clickableSingleWithoutRipple(onClick = onFriendsCountClick),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                ) {
                    Text(
                        text = stringResource(R.string.my_profile_screen_friends_total),
                        style = PeekrTheme.typography.body3Normal,
                        fontWeight = FontWeight.Bold,
                        color = PeekrTheme.colorScheme.textNormal,
                    )
                    Text(
                        text = "$friendsCount",
                        style = PeekrTheme.typography.body3Normal,
                        fontWeight = FontWeight.Normal,
                        color = PeekrTheme.colorScheme.textNormal,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
            }

            // 친구 상태 버튼
            Box(
                modifier = Modifier
                    .weight(1f)
                    .wrapContentSize(Alignment.CenterEnd),
            ) {
                friendshipStatusButton?.invoke()
            }
        }

        // 소개 글
        Introduce(
            modifier = Modifier.fillMaxWidth(),
            introduce = introduce,
        )
    }
}

@Composable
private fun Introduce(
    modifier: Modifier = Modifier,
    introduce: String,
) {
    var expandedIntroduce by rememberSaveable {
        mutableStateOf(false)
    }
    var isIntroduceTextOverFlowing by rememberSaveable {
        mutableStateOf(false)
    }

    Column(modifier = modifier) {
        Text(
            text = introduce,
            style = PeekrTheme.typography.body4,
            fontWeight = FontWeight.Normal,
            color = PeekrTheme.colorScheme.textNormal,
            textAlign = TextAlign.Start,
            maxLines = if (expandedIntroduce) 10 else INTRODUCE_MAX_LINE_COUNT,
            overflow = if (expandedIntroduce) TextOverflow.Visible else TextOverflow.Ellipsis,
            onTextLayout = { textLayoutResult ->
                isIntroduceTextOverFlowing = textLayoutResult.hasVisualOverflow ||
                    textLayoutResult.lineCount > INTRODUCE_MAX_LINE_COUNT
            },
        )
        if (isIntroduceTextOverFlowing) {
            Text(
                modifier = Modifier
                    .align(Alignment.Start)
                    .clickableSingle {
                        expandedIntroduce = !expandedIntroduce
                    },
                text = if (expandedIntroduce) "접기" else "...더보기",
                style = PeekrTheme.typography.caption1,
                fontWeight = FontWeight.Normal,
                color = PeekrTheme.colorScheme.textAssist2,
                textAlign = TextAlign.End,
            )
        }
    }
}

private const val INTRODUCE_MAX_LINE_COUNT = 2

@PreviewLightDarkWithBackground
@Composable
private fun ProfileFramePreview() {
    PeekrAppTheme {
        ProfileFrame(
            modifier = Modifier.fillMaxWidth(),
            profileImageUrl = null,
            name = "홍길동",
            friendsCount = 33,
            introduce = "이 부분은 나를 간단히 소개할 수 있는 곳입니다.\n" +
                "1 ~ 2줄 정도로 간단히 본인을 소개하세요.\n" +
                "더보기 테스트용 셋째 줄",
            onProfileImageClick = {},
            onFriendsCountClick = {},
        )
    }
}
