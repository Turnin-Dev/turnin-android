package com.peekr.core.presentation.feature.keyword.view.graph

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.dropShadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.peekr.core.designsystem.component.avatar.PeekrAvatar
import com.peekr.core.designsystem.theme.PeekrAppTheme
import com.peekr.core.designsystem.theme.PeekrTheme
import com.peekr.core.designsystem.util.click.clickableSingle
import com.peekr.core.designsystem.util.token.ScreenTokens
import com.peekr.core.presentation.feature.keyword.model.UiUserNode
import com.peekr.core.presentation.ui.util.PreviewLightDarkWithBackground

/**
 * 사용자 카드 리스트
 *
 * @param modifier [Modifier]
 * @param state [LazyListState]
 * @param users 사용자 노드(정보) 리스트
 * @param selectedUserId 선택된 사용자 ID
 * @param onUserClick 사용자 클릭 콜백
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserCardList(
    modifier: Modifier = Modifier,
    state: LazyListState,
    users: List<UiUserNode>,
    selectedUserId: Long?,
    onUserClick: (UiUserNode) -> Unit,
) {
    LazyRow(
        modifier = modifier,
        state = state,
        contentPadding = PaddingValues(horizontal = ScreenTokens.HorizontalPadding),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        flingBehavior = rememberSnapFlingBehavior(state),
    ) {
        items(
            items = users,
            key = { it.userId },
        ) { user ->
            UserCard(
                user = user,
                isSelected = user.userId == selectedUserId,
                onClick = { onUserClick(user) },
            )
        }
    }
}

/**
 * 사용자 카드
 *
 * @param modifier [Modifier]
 * @param user 사용자 정보
 * @param isSelected 사용자 선택 여부
 * @param onClick 사용자 클릭 콜백
 */
@Composable
private fun UserCard(
    modifier: Modifier = Modifier,
    user: UiUserNode,
    isSelected: Boolean,
    onClick: () -> Unit,
) {
    val isDarkMode = isSystemInDarkTheme()

    Box(
        modifier = modifier
            .widthIn(min = 100.dp)
            .heightIn(min = 120.dp)
            .graphicsLayer {
                translationY = if (isSelected) -20f else 0f
            }
            .dropShadow(
                shape = CardShape,
                block = {
                    radius = 5f
                    color = Color.Black.copy(
                        alpha = if (isDarkMode) 0.5f else 0.25f,
                    )
                },
            )
            .clip(CardShape)
            .background(PeekrTheme.colorScheme.backgroundNormal, CardShape)
            .border(
                width = if (isSelected) 1.dp else 0.dp,
                color = if (isSelected) PeekrTheme.colorScheme.primary else Color.Transparent,
                shape = CardShape,
            )
            .clickableSingle {
                onClick()
            },
        contentAlignment = Alignment.Center,
    ) {
        Column(
            modifier = Modifier.padding(CardInnerPadding),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            PeekrAvatar(
                modifier = Modifier.size(AvatarSize),
                model = user.profileImageUrl,
                contentDescription = user.userName,
            )

            Spacer(Modifier.height(CardInnerPadding))

            Text(
                text = user.userName,
                style = PeekrTheme.typography.caption2,
                fontWeight = FontWeight.Normal,
                color = PeekrTheme.colorScheme.textNormal,
            )
        }
    }
}

private val CardShape = RoundedCornerShape(16.dp)
private val AvatarSize = 60.dp
private val CardInnerPadding = 8.dp

// ------------------------------ Previews ------------------------------
@PreviewLightDarkWithBackground
@Composable
private fun UserCardPreview() {
    PeekrAppTheme {
        Row(Modifier.padding(20.dp), Arrangement.spacedBy(20.dp)) {
            UserCard(
                user = UiUserNode(0L, "username", null),
                isSelected = false,
                onClick = {},
            )

            UserCard(
                user = UiUserNode(0L, "username", null),
                isSelected = true,
                onClick = {},
            )
        }
    }
}

@Preview(backgroundColor = 0x00FFFFFF)
@Composable
private fun UserCardListPreview() {
    PeekrAppTheme {
        val state = rememberLazyListState()
        var selectedUserId by remember { mutableStateOf<Long?>(null) }

        Box(
            Modifier
                .fillMaxSize()
                .padding(bottom = 24.dp),
            Alignment.BottomCenter,
        ) {
            UserCardList(
                modifier = Modifier.fillMaxWidth(),
                state = state,
                users = List(50) {
                    UiUserNode(it.toLong(), "username$it", null)
                },
                selectedUserId = selectedUserId,
                onUserClick = { selectedUserId = it.userId },
            )
        }
    }
}
