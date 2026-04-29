package com.turnin.presentation.setting.route

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.turnin.core.designsystem.component.loading.TurninLoadingScreen
import com.turnin.core.designsystem.component.modal.TurninSimpleModal
import com.turnin.core.designsystem.theme.TurninTheme
import com.turnin.core.presentation.common.util.ObserveAsEvents
import com.turnin.presentation.R
import com.turnin.presentation.setting.model.UiAccountInfo
import com.turnin.presentation.setting.state.SettingContract
import com.turnin.presentation.setting.view.SettingScreen
import com.turnin.presentation.setting.view.detail.DeleteAccountModal
import com.turnin.presentation.setting.viewmodel.SettingViewModel

@Composable
fun SettingRoute(
    onNavigateToAccountInfo: (UiAccountInfo?) -> Unit,
    onNavigateToBlockList: () -> Unit,
    onNavigateToLogin: () -> Unit,
    onNavigateToVersionInfo: () -> Unit,
    onNavigateToQna: (qnaUrl: String) -> Unit,
    onNavigateToNotification: () -> Unit,
    onBackPressed: () -> Unit,
) {
    val viewModel: SettingViewModel = hiltViewModel()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var isLogoutModalOpen by remember { mutableStateOf(false) }
    var isDeleteAccountModalOpen by remember { mutableStateOf(false) }

    // 계정 삭제 모달이 사라질 때마다 텍스트 초기화
    LaunchedEffect(isDeleteAccountModalOpen) {
        if (!isDeleteAccountModalOpen) {
            viewModel.processEvent(SettingContract.UiEvent.OnDeletionStateCleared)
        }
    }

    // 일회성 이벤트 처리
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
                onNavigateToVersionInfo()
            }

            SettingContract.UiEffect.OpenDeleteAccountModal -> {
                isDeleteAccountModalOpen = true
            }

            SettingContract.UiEffect.CloseDeleteAccountModal -> {
                isDeleteAccountModalOpen = false
            }

            is SettingContract.UiEffect.NavigateToQna -> {
                onNavigateToQna(effect.qnaUrl)
            }

            SettingContract.UiEffect.NavigateToNotification -> {
                onNavigateToNotification()
            }
        }
    }

    // 로딩 화면
    if (uiState.fullScreenLoading) {
        TurninLoadingScreen()
    }

    // 로그아웃 모달
    TurninSimpleModal(
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

    // 계정 삭제 모달
    DeleteAccountModal(
        modifier = Modifier.fillMaxSize(),
        isOpen = isDeleteAccountModalOpen,
        isDeletionEnabled = uiState.isDeletionEnabled,
        confirmText = uiState.deletionConfirmText,
        onConfirmTextChanged = {
            viewModel.processEvent(SettingContract.UiEvent.OnDeletionConfirmTextChanged(it))
        },
        onAcceptClick = {
            viewModel.processEvent(SettingContract.UiEvent.DeleteAccount)
        },
        onCancelClick = {
            isDeleteAccountModalOpen = false
        },
    )

    // 설정 화면
    SettingScreen(
        modifier = Modifier
            .fillMaxSize()
            .background(TurninTheme.colorScheme.backgroundNormal),
        accountInfoLoading = uiState.accountInfoLoading,
        onUiEvent = viewModel::processEvent,
        onBackPressed = onBackPressed,
    )
}
