package com.turnin.presentation.login

import androidx.compose.ui.Modifier
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.turnin.core.domain.util.analytics.AnalyticsEvent
import com.turnin.core.presentation.common.analytics.LocalAnalyticsTracker
import com.turnin.core.presentation.common.analytics.ScreenTrackers
import com.turnin.core.presentation.common.navigation.SubGraph
import com.turnin.core.presentation.common.navigation.navigateToRegister

fun NavGraphBuilder.loginNavigation(
    appNavController: NavHostController,
) {
    navigation<SubGraph.Login.Root>(startDestination = SubGraph.Login.Main) {
        composable<SubGraph.Login.Main> {
            ScreenTrackers(SubGraph.Login.Main.analyticsName)
            val analyticsTracker = LocalAnalyticsTracker.current

            LoginRoute(
                modifier = Modifier,
                onNavigateMain = { provider ->
                    analyticsTracker.logEvent(AnalyticsEvent.Login(provider.name))
                    appNavController.navigate(SubGraph.BottomNav.Root) {
                        popUpTo(0) { inclusive = true }
                    }
                },
                onNavigateRegister = { provider, providerId ->
                    appNavController.navigateToRegister(provider, providerId)
                },
            )
        }
    }
}
