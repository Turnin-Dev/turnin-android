package com.turnin.presentation.notification.view

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.PagingData
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import com.turnin.core.designsystem.component.tabBar.TurninTabBar
import com.turnin.core.designsystem.component.topbar.TurninTopBar
import com.turnin.core.designsystem.theme.TurninAppTheme
import com.turnin.core.designsystem.util.token.ScreenTokens
import com.turnin.core.domain.model.NotificationType
import com.turnin.core.presentation.ui.component.EmptyGuidance
import com.turnin.core.presentation.ui.component.error.FooterError
import com.turnin.core.presentation.ui.component.lazycolumn.RefreshableLazyColumn
import com.turnin.core.presentation.ui.component.lazycolumn.pagingItem
import com.turnin.core.presentation.ui.util.PreviewLightDarkWithBackground
import com.turnin.core.presentation.ui.util.UiText
import com.turnin.presentation.R
import com.turnin.presentation.notification.model.UiAnnouncement
import com.turnin.presentation.notification.model.UiNotification
import com.turnin.presentation.notification.state.AnnouncementState
import kotlin.random.Random
import kotlinx.coroutines.flow.MutableStateFlow

/**
 * 알림 목록 화면 프레임
 *
 * @param modifier [Modifier]
 * @param topBar 탑바
 * @param notifications 알림 목록
 */
@Composable
private fun NotificationScreenFrame(
    modifier: Modifier = Modifier,
    topBar: @Composable () -> Unit,
    notifications: @Composable () -> Unit,
    announcements: @Composable () -> Unit,
) {
    val tabs = listOf(
        stringResource(R.string.notification_screen_top_bar_title_1),
        stringResource(R.string.notification_screen_top_bar_title_2),
    )

    Column(modifier) {
        topBar()
        TurninTabBar(
            modifier = Modifier.fillMaxWidth(),
            tabs = tabs,
            pageContent = { page ->
                when (page) {
                    0 -> notifications()
                    1 -> announcements()
                    else -> {}
                }
            },
        )
    }
}

/**
 * 알림 목록 화면
 *
 * @param modifier [Modifier]
 * @param notifications 알림 목록
 * @param announcementState 공지 UI 상태
 * @param onAnnouncementRefresh 공지 목록 새로고침
 * @param onNotificationClick 알림 클릭 시 콜백
 * @param onAnnouncementClick 공지 클릭 시 콜백 (공지 ID, 현재 읽음 여부 상태 전달)
 * @param onBackPress 뒤로 가기 클릭 시 콜백
 */
@Composable
fun NotificationScreen(
    modifier: Modifier = Modifier,
    notifications: LazyPagingItems<UiNotification>,
    announcementState: AnnouncementState,
    onAnnouncementRefresh: () -> Unit,
    onNotificationClick: (UiNotification) -> Unit,
    onAnnouncementClick: (Long, Boolean) -> Unit,
    onBackPress: () -> Unit,
) {
    NotificationScreenFrame(
        modifier = modifier,
        topBar = {
            TopBar(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = ScreenTokens.HorizontalPaddingWithTouchTarget),
                onBackPress = onBackPress,
            )
        },
        notifications = {
            NotificationList(
                modifier = Modifier.fillMaxSize(),
                notifications = notifications,
                onNotificationClick = onNotificationClick,
            )
        },
        announcements = {
            Announcements(
                modifier = Modifier.fillMaxSize(),
                announcements = announcementState.announcements,
                isRefreshing = announcementState.loading,
                error = announcementState.error,
                onRefresh = onAnnouncementRefresh,
                onAnnouncementClick = { announcement ->
                    onAnnouncementClick(announcement.id, announcement.isRead)
                },
            )
        },
    )
}

/**
 * 탑바
 *
 * @param modifier [Modifier]
 * @param onBackPress 뒤로가기 콜백
 */
@Composable
private fun TopBar(
    modifier: Modifier = Modifier,
    onBackPress: () -> Unit,
) {
    TurninTopBar(
        modifier = modifier,
        title = stringResource(R.string.notification_screen_top_bar_title),
        onBackPressed = onBackPress,
    )
}

/**
 * 알림 목록
 *
 * @param modifier [Modifier]
 * @param notifications 알림 목록
 * @param onNotificationClick 알림 클릭 시 콜백
 */
@Composable
private fun NotificationList(
    modifier: Modifier = Modifier,
    notifications: LazyPagingItems<UiNotification>,
    onNotificationClick: (UiNotification) -> Unit,
) {
    var isManualRefresh by rememberSaveable { mutableStateOf(false) }
    val isRefreshing = remember {
        derivedStateOf {
            isManualRefresh && notifications.loadState.refresh is LoadState.Loading
        }
    }.value

    LaunchedEffect(isRefreshing) {
        if (!isRefreshing) isManualRefresh = false
    }

    RefreshableLazyColumn(
        modifier = modifier,
        isRefreshing = isRefreshing,
        onRefresh = {
            isManualRefresh = true
            notifications.refresh()
        },
        contentPadding = ListContentPadding,
    ) {
        pagingItem(
            pagingItems = notifications,
            key = notifications.itemKey { it.id },
            skeletonCount = 10,
            skeleton = { NotificationItemSkeleton() },
            initialError = {
                FooterError(
                    modifier = Modifier.fillMaxWidth(),
                    errorMessage = stringResource(R.string.notification_screen_error_message_default),
                    onRetry = { notifications.retry() },
                )
            },
            footerError = {
                FooterError(
                    modifier = Modifier.fillMaxWidth(),
                    errorMessage = stringResource(R.string.notification_screen_error_message_footer),
                    onRetry = { notifications.retry() },
                )
            },
            emptyGuidance = { EmptyGuidance(title = stringResource(R.string.notification_screen_empty_guidance)) },
        ) { idx ->
            val notification = notifications[idx]
            notification?.let {
                NotificationItem(
                    modifier = Modifier.fillMaxWidth(),
                    notiType = notification.notiType,
                    isRead = it.isRead,
                    date = it.createdAt,
                    title = it.title,
                    message = it.message,
                    imageUrl = it.imageUrl,
                    onClick = { onNotificationClick(it) },
                )
            }
        }
    }
}

/**
 * 공지 목록
 *
 * @param modifier [Modifier]
 * @param announcements 공지 목록
 * @param isRefreshing 새로고침 여부
 * @param error 에러 메시지
 * @param onRefresh 새로고침시 수행할 작업
 * @param onAnnouncementClick 공지 클릭 시 수행할 작업
 */
@Composable
private fun Announcements(
    modifier: Modifier = Modifier,
    announcements: List<UiAnnouncement>,
    isRefreshing: Boolean,
    error: UiText?,
    onRefresh: () -> Unit,
    onAnnouncementClick: (UiAnnouncement) -> Unit,
) {
    RefreshableLazyColumn(
        modifier = modifier,
        isRefreshing = isRefreshing,
        onRefresh = onRefresh,
        contentPadding = ListContentPadding,
    ) {
        item {
            error?.let {
                FooterError(
                    modifier = Modifier.fillMaxWidth(),
                    errorMessage = error.asString().ifEmpty {
                        stringResource(R.string.notification_error_unexpected)
                    },
                    onRetry = onRefresh,
                )
            }
        }

        items(
            items = announcements,
            key = { it.id },
        ) { announcement ->
            var isExpanded by remember { mutableStateOf(false) }

            NotificationItem(
                modifier = Modifier.fillMaxWidth(),
                notiType = NotificationType.NOTICE,
                isRead = announcement.isRead,
                date = announcement.createdAt,
                title = announcement.title,
                message = announcement.content,
                imageUrl = null,
                isExpanded = isExpanded,
                onClick = {
                    isExpanded = !isExpanded
                    onAnnouncementClick(announcement)
                },
            )
        }
    }
}

private val ListContentPadding = PaddingValues(top = 10.dp, bottom = 100.dp)

// ------------------------------ Preview ------------------------------

@PreviewLightDarkWithBackground
@Composable
private fun NotificationScreenPreview() {
    val notifications = testNotificationsPagingData.collectAsLazyPagingItems()
    val announcementState = AnnouncementState(
        announcements = List(40) {
            val id = it + 1L
            UiAnnouncement.sample.copy(id = id, isRead = Random.nextBoolean())
        },
    )

    TurninAppTheme {
        NotificationScreen(
            modifier = Modifier.fillMaxSize(),
            notifications = notifications,
            announcementState = announcementState,
            onAnnouncementRefresh = {},
            onNotificationClick = {},
            onAnnouncementClick = { _, _ -> },
            onBackPress = {},
        )
    }
}

@PreviewLightDarkWithBackground
@Composable
private fun NotificationEmptyScreenPreview() {
    val notifications = testEmptyPagingData.collectAsLazyPagingItems()

    TurninAppTheme {
        NotificationScreen(
            modifier = Modifier.fillMaxSize(),
            notifications = notifications,
            announcementState = AnnouncementState(),
            onAnnouncementRefresh = {},
            onNotificationClick = {},
            onAnnouncementClick = { _, _ -> },
            onBackPress = {},
        )
    }
}

private val testNotificationsPagingData = MutableStateFlow(
    PagingData.from(
        List(40) {
            UiNotification.sample.copy(
                id = it + 1L,
                notiType = NotificationType.entries.random(),
                isRead = Random.nextBoolean(),
            )
        },
    ),
)

private val testEmptyPagingData = MutableStateFlow(PagingData.empty<UiNotification>())
