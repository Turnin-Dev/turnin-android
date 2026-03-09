package com.peekr.presentation.setting.route

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.peekr.core.designsystem.component.loading.PeekrLoadingScreen
import com.peekr.core.designsystem.theme.PeekrTheme
import com.peekr.core.presentation.common.util.ObserveAsEvents
import com.peekr.core.presentation.ui.component.modal.PeekrSimpleModal
import com.peekr.presentation.R
import com.peekr.presentation.setting.model.UiAccountInfo
import com.peekr.presentation.setting.state.SettingContract
import com.peekr.presentation.setting.view.SettingScreen
import com.peekr.presentation.setting.viewmodel.SettingViewModel

@Composable
fun SettingRoute(
    onNavigateToAccountInfo: (UiAccountInfo?) -> Unit,
    onNavigateToBlockList: () -> Unit,
    onNavigateToLogin: () -> Unit,
    onNaivgateToVersionInfo: () -> Unit,
    onBackPressed: () -> Unit,
) {
    val viewModel: SettingViewModel = hiltViewModel()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var isLogoutModalOpen by remember { mutableStateOf(false) }

    ObserveAsEvents(viewModel.effect) { effect ->
        when (effect) {
            is SettingContract.UiEffect.NavigateToAccountInfo -> {
                onNavigateToAccountInfo(effect.accountInfo)
            }

            SettingContract.UiEffect.NavigateToBlockList -> {
                onNavigateToBlockList()
            }

            SettingContract.UiEffect.NavigateToLogin -> {
                onNavigateToLogin()
            }

            SettingContract.UiEffect.OpenLogoutModal -> {
                isLogoutModalOpen = true
            }

            SettingContract.UiEffect.NavigateToVersionInfo -> {
                onNaivgateToVersionInfo()
            }
        }
    }

    if (uiState.fullScreenLoading) {
        PeekrLoadingScreen()
    }

    PeekrSimpleModal(
        modifier = Modifier.fillMaxSize(),
        isOpen = isLogoutModalOpen,
        title = R.string.setting_screen_modal_logout_title,
        onAcceptClick = {
            viewModel.processEvent(SettingContract.UiEvent.Logout)
        },
        onCancelClick = {
            isLogoutModalOpen = false
        },
    )

    SettingScreen(
        modifier = Modifier
            .fillMaxSize()
            .background(PeekrTheme.colorScheme.backgroundNormal),
        accountInfoLoading = uiState.accountInfoLoading,
        onUiEvent = viewModel::processEvent,
        onBackPressed = onBackPressed,
    )
}
