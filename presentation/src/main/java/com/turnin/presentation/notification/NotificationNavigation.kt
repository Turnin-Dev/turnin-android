package com.turnin.presentation.notification

import androidx.core.net.toUri
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navDeepLink
import com.turnin.core.presentation.common.navigation.Screens
import com.turnin.core.presentation.common.navigation.deepLink.DeepLink

fun NavGraphBuilder.notificationScreen(
    navController: NavController,
) {
    composable<Screens.Notifications>(
        deepLinks = listOf(
            navDeepLink {
                uriPattern = DeepLink.Pattern.NOTIFICATIONS
            },
        ),
    ) {
        NotificationRoute(
            onNavigateToNotificationDetail = { deepLink ->
                navController.navigate(deepLink.toUri())
            },
            onBackPressed = {
                navController.popBackStack()
            },
        )
    }
}
