package com.turnin.presentation.notification

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.compose.collectAsLazyPagingItems
import com.turnin.core.designsystem.theme.TurninTheme
import com.turnin.core.presentation.common.util.ObserveAsEvents
import com.turnin.presentation.notification.view.NotificationScreen
import com.turnin.presentation.notification.viewmodel.NotificationViewModel

@Composable
fun NotificationRoute(
    onNavigateToNotificationDetail: (String) -> Unit,
    onBackPressed: () -> Unit,
) {
    val viewModel: NotificationViewModel = hiltViewModel()
    val notificationsPagingData = viewModel.notificationsPagingData.collectAsLazyPagingItems()
    val announcementState by viewModel.announcementUiState.collectAsStateWithLifecycle()

    ObserveAsEvents(viewModel.navigateToNotificationDetail) { deepLink ->
        onNavigateToNotificationDetail(deepLink)
    }

    NotificationScreen(
        modifier = Modifier
            .fillMaxSize()
            .background(TurninTheme.colorScheme.backgroundNormal),
        notifications = notificationsPagingData,
        announcementState = announcementState,
        onAnnouncementRefresh = viewModel::getAnnouncements,
        onNotificationClick = { notification ->
            viewModel.onNotificationClick(
                notificationId = notification.id,
                deepLink = notification.deepLink,
                currentIsRead = notification.isRead,
            )
        },
        onAnnouncementClick = viewModel::markAnnouncementAsRead,
        onBackPress = onBackPressed,
    )
}
