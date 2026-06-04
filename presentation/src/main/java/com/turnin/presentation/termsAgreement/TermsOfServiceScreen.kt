package com.turnin.presentation.termsAgreement

import androidx.compose.animation.AnimatedContentTransitionScope.SlideDirection
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.turnin.core.designsystem.component.topbar.TurninTopBar
import com.turnin.core.designsystem.util.token.ScreenTokens
import com.turnin.core.presentation.common.navigation.Screens
import com.turnin.core.presentation.common.webview.DefaultWebView
import com.turnin.presentation.BuildConfig

/**
 * 서비스 이용약관 화면
 *
 * @param modifier [Modifier]
 * @param onBackPressed 뒤로가기 클릭 시 콜백
 */
@Composable
fun TermsOfServiceScreen(
    modifier: Modifier = Modifier,
    onBackPressed: () -> Unit,
) {
    Column(modifier.wrapContentHeight()) {
        // 탑바
        TurninTopBar(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = ScreenTokens.HorizontalPaddingWithTouchTarget),
            onBackPressed = onBackPressed,
        )

        // 웹뷰
        DefaultWebView(
            modifier = Modifier.weight(1f),
            url = BuildConfig.TERMS_OF_SERVICE_URL,
        )
    }
}

fun NavGraphBuilder.termsOfServiceScreen(
    navController: NavController,
) {
    composable<Screens.TermsOfService>(
        enterTransition = { slideIntoContainer(SlideDirection.Up, tween(300)) },
        exitTransition = { slideOutOfContainer(SlideDirection.Down, tween(300)) },
        popEnterTransition = { slideIntoContainer(SlideDirection.Up, tween(300)) },
        popExitTransition = { slideOutOfContainer(SlideDirection.Down, tween(300)) },
    ) {
        TermsOfServiceScreen(
            onBackPressed = {
                navController.popBackStack()
            },
        )
    }
}
