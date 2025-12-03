package com.peekr.presentation.profile.route

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.peekr.core.designsystem.theme.PeekrTheme
import com.peekr.core.presentation.common.util.ObserveAsEvents
import com.peekr.core.presentation.ui.util.LockScreenOrientation
import com.peekr.presentation.profile.view.UserProfileScreen
import com.peekr.presentation.profile.viewmodel.UserProfileViewModel

@Composable
internal fun UserProfileRoute(
    onBackPressed: () -> Unit,
) {
    // Lock Orientation
    LockScreenOrientation()

    val viewModel: UserProfileViewModel = hiltViewModel()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    // ------------------------------ UiEffect ------------------------------
    ObserveAsEvents(viewModel.effect) {
        when (it) {
            else -> {}
        }
    }

    // ------------------------------ Screen ------------------------------
    UserProfileScreen(
        modifier = Modifier
            .fillMaxSize()
            .background(PeekrTheme.colorScheme.backgroundNormal),
        userProfile = uiState.userProfile,
        onUiEvent = viewModel::processEvent,
        onBackPressed = onBackPressed,
    )
}
