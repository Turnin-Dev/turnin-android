package com.turnin.presentation.notification

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
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

    ObserveAsEvents(viewModel.navigateToNotificationDetail) { deepLink ->
        onNavigateToNotificationDetail(deepLink)
    }

    NotificationScreen(
        modifier = Modifier
            .fillMaxSize()
            .background(TurninTheme.colorScheme.backgroundNormal),
        notifications = notificationsPagingData,
        onNotificationClick = { notification ->
            viewModel.onNotificationClick(notification.id, notification.deepLink)
        },
        onBackPress = onBackPressed,
    )
}
