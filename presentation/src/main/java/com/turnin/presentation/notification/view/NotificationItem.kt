package com.turnin.presentation.notification.view

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.turnin.core.designsystem.component.avatar.TurninAvatar
import com.turnin.core.designsystem.component.skeleton.SkeletonBox
import com.turnin.core.designsystem.theme.TurninAppTheme
import com.turnin.core.designsystem.theme.TurninTheme
import com.turnin.core.designsystem.util.click.clickableSingle
import com.turnin.core.domain.model.NotificationType
import com.turnin.core.presentation.ui.util.PreviewLightDarkWithBackground

/**
 * 알림 항목
 *
 * @param modifier [Modifier]
 * @param notiType 알림 유형
 * @param isRead 읽음 여부
 * @param date 날짜
 * @param title 제목
 * @param message 내용
 * @param imageUrl 이미지 URL
 * @param isExpanded 컨텐츠 확장 여부
 * @param onClick 알림 항목 클릭 시 콜백
 */
@Composable
fun NotificationItem(
    modifier: Modifier = Modifier,
    notiType: NotificationType,
    isRead: Boolean,
    date: String,
    title: String?,
    message: String,
    imageUrl: String?,
    isExpanded: Boolean? = null,
    onClick: () -> Unit,
) {
    Column(
        modifier = modifier
            .background(
                if (isRead) {
                    Color.Transparent
                } else {
                    TurninTheme.colorScheme.backgroundUnread
                },
            )
            .clickableSingle(onClick = onClick)
            .padding(ContainerPaddingValues)
            .animateContentSize(tween(150)),
        verticalArrangement = Arrangement.spacedBy(ItemColumnGapDp),
        horizontalAlignment = Alignment.Start,
    ) {
        // 날짜
        Date(date = date)
        // 알림 컨텐츠
        Contents(
            modifier = Modifier.fillMaxWidth(),
            notiType = notiType,
            title = title,
            message = message,
            imageUrl = imageUrl,
            isExpanded = isExpanded,
        )
    }
}

/**
 * 날짜
 *
 * @param modifier [Modifier]
 * @param date 날짜
 */
@Composable
private fun Date(
    modifier: Modifier = Modifier,
    date: String,
) {
    Text(
        modifier = modifier,
        text = date,
        style = TurninTheme.typography.caption2,
        fontWeight = FontWeight.Medium,
        color = TurninTheme.colorScheme.textAssist2,
    )
}

/**
 * 알림 컨텐츠
 *
 * @param modifier [Modifier]
 * @param notiType 알림 유형
 * @param title 제목
 * @param message 내용
 * @param imageUrl 이미지 URL
 * @param isExpanded 컨텐츠 확장 여부
 */
@Composable
private fun Contents(
    modifier: Modifier = Modifier,
    notiType: NotificationType,
    title: String?,
    message: String,
    imageUrl: String?,
    isExpanded: Boolean? = null,
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(ContentsRowGapDp),
    ) {
        // 이미지
        if (imageUrl != null || notiType.isFriendRelated) {
            TurninAvatar(
                modifier = Modifier.size(AvatarSize),
                model = imageUrl,
                contentDescription = null,
            )
        }

        Column(
            modifier = modifier
                .weight(1f)
                .wrapContentWidth(Alignment.Start),
            verticalArrangement = Arrangement.spacedBy(ContentsTextGapDp),
        ) {
            // 제목
            title?.let {
                Text(
                    text = it,
                    style = TurninTheme.typography.body3,
                    fontWeight = FontWeight.Medium,
                    color = TurninTheme.colorScheme.textNormal,
                )
            }
            // 내용
            if (isExpanded == null) {
                Text(
                    text = message,
                    style = TurninTheme.typography.body4,
                    fontWeight = FontWeight.Normal,
                    color = TurninTheme.colorScheme.textNormal,
                )
            } else {
                Text(
                    text = message,
                    style = TurninTheme.typography.body4,
                    fontWeight = FontWeight.Normal,
                    color = TurninTheme.colorScheme.textNormal,
                    maxLines = if (isExpanded) Int.MAX_VALUE else 2,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }
    }
}

/**
 * 알림 항목 스켈레톤
 */
@Composable
fun NotificationItemSkeleton() {
    Column(
        modifier = Modifier
            .padding(ContainerPaddingValues)
            .fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(ItemColumnGapDp),
        horizontalAlignment = Alignment.Start,
    ) {
        // 날짜
        SkeletonBox(Modifier.size(70.dp, 14.dp))
        // 알림 컨텐츠
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(ContentsRowGapDp),
        ) {
            // 이미지
            SkeletonBox(Modifier.size(AvatarSize), CircleShape)

            Column(
                modifier = Modifier
                    .weight(1f)
                    .wrapContentWidth(Alignment.Start),
                verticalArrangement = Arrangement.spacedBy(ContentsTextGapDp),
            ) {
                // 제목
                SkeletonBox(Modifier.size(60.dp, 16.dp))
                // 내용
                SkeletonBox(Modifier.size(162.dp, 16.dp))
            }
        }
    }
}

private val ContainerPaddingValues = PaddingValues(20.dp, 10.dp)
private val ItemColumnGapDp = 10.dp
private val ContentsRowGapDp = 10.dp
private val ContentsTextGapDp = 5.dp
private val AvatarSize = 43.dp

// ------------------------------ Preview ------------------------------

@PreviewLightDarkWithBackground
@Composable
private fun NotificationItemUnreadPreview() {
    TurninAppTheme {
        NotificationItem(
            notiType = NotificationType.FRIEND_ACCEPT,
            isRead = false,
            date = "2024.03.29",
            title = "새로운 알림",
            message = "새로운 알림이 도착했습니다. 확인해보세요.",
            imageUrl = null,
            onClick = {},
        )
    }
}

@PreviewLightDarkWithBackground
@Composable
private fun NotificationItemReadPreview() {
    TurninAppTheme {
        NotificationItem(
            notiType = NotificationType.FRIEND_ACCEPT,
            isRead = true,
            date = "2024.03.28",
            title = "읽은 알림",
            message = "이미 읽은 알림입니다. 배경색이 다릅니다.",
            imageUrl = null,
            onClick = {},
        )
    }
}

@PreviewLightDarkWithBackground
@Composable
private fun ExpandedNotificationItemPreview() {
    var isExpanded by remember { mutableStateOf(false) }

    TurninAppTheme {
        NotificationItem(
            modifier = Modifier.background(Color.LightGray),
            notiType = NotificationType.FRIEND_ACCEPT,
            isRead = true,
            date = "2024.03.28",
            title = "읽은 알림",
            message = "이미 읽은 알림입니다. 배경색이 다릅니다.".repeat(10),
            imageUrl = null,
            isExpanded = isExpanded,
            onClick = { isExpanded = !isExpanded },
        )
    }
}
