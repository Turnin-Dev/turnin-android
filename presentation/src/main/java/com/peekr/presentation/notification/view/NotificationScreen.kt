package com.peekr.presentation.notification.view

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
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
import com.peekr.core.designsystem.component.topbar.PeekrTopBar
import com.peekr.core.designsystem.theme.PeekrAppTheme
import com.peekr.core.designsystem.util.token.ScreenTokens
import com.peekr.core.domain.model.NotificationType
import com.peekr.core.presentation.ui.component.EmptyGuidance
import com.peekr.core.presentation.ui.component.error.FooterError
import com.peekr.core.presentation.ui.component.lazycolumn.RefreshableLazyColumn
import com.peekr.core.presentation.ui.component.lazycolumn.pagingItem
import com.peekr.core.presentation.ui.util.PreviewLightDarkWithBackground
import com.peekr.presentation.R
import com.peekr.presentation.notification.model.UiNotification
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
) {
    Column(modifier) {
        topBar()
        notifications()
    }
}

/**
 * 알림 목록 화면
 *
 * @param modifier [Modifier]
 * @param notifications 알림 목록
 * @param onNotificationClick 알림 클릭 시 콜백
 * @param onBackPress 뒤로 가기 클릭 시 콜백
 */
@Composable
fun NotificationScreen(
    modifier: Modifier = Modifier,
    notifications: LazyPagingItems<UiNotification>,
    onNotificationClick: (UiNotification) -> Unit,
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
    PeekrTopBar(
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

private val ListContentPadding = PaddingValues(bottom = 80.dp)

// ------------------------------ Preview ------------------------------

@PreviewLightDarkWithBackground
@Composable
private fun NotificationScreenPreview() {
    val notifications = testNotificationsPagingData.collectAsLazyPagingItems()

    PeekrAppTheme {
        NotificationScreen(
            modifier = Modifier.fillMaxSize(),
            notifications = notifications,
            onNotificationClick = {},
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
