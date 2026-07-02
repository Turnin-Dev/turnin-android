package com.turnin.presentation.setting.viewmodel

import androidx.lifecycle.viewModelScope
import com.turnin.core.domain.common.Result
import com.turnin.core.presentation.common.error.asUiText
import com.turnin.core.presentation.common.snackbar.SnackbarController
import com.turnin.core.presentation.common.snackbar.SnackbarEvent
import com.turnin.core.presentation.common.viewmodel.MVIBaseViewModel
import com.turnin.core.presentation.ui.util.UiText
import com.turnin.domain.setting.usecase.SettingUseCases
import com.turnin.presentation.BuildConfig
import com.turnin.presentation.setting.error.asUiText
import com.turnin.presentation.setting.model.UiAccountInfo
import com.turnin.presentation.setting.model.toUiModel
import com.turnin.presentation.setting.state.SettingContract
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

@HiltViewModel
class SettingViewModel @Inject constructor(
    private val usecases: SettingUseCases,
    private val snackbarController: SnackbarController,
) : MVIBaseViewModel<SettingContract.UiState, SettingContract.UiEvent, SettingContract.UiEffect>() {
    override fun createInitialState(): SettingContract.UiState =
        SettingContract.UiState()

    private var initialAccountInfo: UiAccountInfo? = null

    init {
        loadAccountInfo()
    }

    override suspend fun handleEvent(event: SettingContract.UiEvent) {
        when (event) {
            SettingContract.UiEvent.OnNavigateToAccountInfo -> {
                if (initialAccountInfo != null) {
                    sendEffect {
                        SettingContract.UiEffect.NavigateToAccountInfo(initialAccountInfo)
                    }
                }
            }

            SettingContract.UiEvent.OnNavigateToBlockList -> {
                sendEffect {
                    SettingContract.UiEffect.NavigateToBlockList
                }
            }

            SettingContract.UiEvent.Logout -> logout()

            SettingContract.UiEvent.OnLogoutClick -> {
                sendEffect {
                    SettingContract.UiEffect.OpenLogoutModal
                }
            }

            SettingContract.UiEvent.OnNavigateToVersionInfo -> {
                sendEffect {
                    SettingContract.UiEffect.NavigateToVersionInfo
                }
            }

            SettingContract.UiEvent.OnNavigateToQna -> {
                sendEffect {
                    SettingContract.UiEffect.NavigateToQna(BuildConfig.QNA_URL)
                }
            }

            SettingContract.UiEvent.OnNavigateToNotification -> {
                sendEffect {
                    SettingContract.UiEffect.NavigateToNotification
                }
            }

            is SettingContract.UiEvent.OnDeletionConfirmTextChanged -> {
                updateState {
                    copy(
                        deletionConfirmText = event.text,
                        isDeletionEnabled = event.text == DELETION_CONFIRM_TEXT,
                    )
                }
            }

            SettingContract.UiEvent.OnDeletionStateCleared -> {
                updateState { copy(deletionConfirmText = "", isDeletionEnabled = false) }
            }

            SettingContract.UiEvent.OnDeleteAccountClick -> {
                sendEffect {
                    SettingContract.UiEffect.OpenDeleteAccountModal
                }
            }

            SettingContract.UiEvent.DeleteAccount -> deleteAccount()
        }
    }

    // 계정 정보 초기 로드
    private fun loadAccountInfo() {
        usecases.getAccountInfo().onEach { result ->
            when (result) {
                is Result.Error -> {
                    updateState { copy(accountInfoLoading = false) }
                    showSnackbar(result.error.asUiText())
                }

                Result.Loading -> updateState { copy(accountInfoLoading = true) }
                is Result.Success -> {
                    // 초기 계정 정보 저장
                    val accountInfo = result.data.toUiModel()
                    initialAccountInfo = accountInfo
                    updateState { copy(accountInfoLoading = false) }
                }
            }
        }
            .launchIn(viewModelScope)
    }

    // 로그아웃
    private fun logout() {
        usecases.logout().onEach { result ->
            when (result) {
                Result.Loading -> updateState { copy(fullScreenLoading = true) }
                is Result.Error -> {
                    showSnackbar(result.error.asUiText())
                    updateState { copy(fullScreenLoading = false) }
                }

                is Result.Success -> {
                    sendEffect { SettingContract.UiEffect.NavigateToLogin }
                    updateState { copy(fullScreenLoading = false) }
                }
            }
        }
            .launchIn(viewModelScope)
    }

    // 계정 삭제
    private fun deleteAccount() {
        usecases.deleteAccount().onEach { result ->
            when (result) {
                Result.Loading -> updateState { copy(fullScreenLoading = true) }
                is Result.Error -> {
                    sendEffect { SettingContract.UiEffect.CloseDeleteAccountModal }
                    updateState { copy(fullScreenLoading = false) }
                    showSnackbar(result.error.asUiText())
                }

                is Result.Success -> {
                    sendEffect { SettingContract.UiEffect.NavigateToLogin }
                    updateState { copy(fullScreenLoading = false) }
                }
            }
        }
            .launchIn(viewModelScope)
    }

    private suspend fun showSnackbar(message: UiText) {
        snackbarController.sendEvent(SnackbarEvent(message = message))
    }
}

private const val DELETION_CONFIRM_TEXT = "삭제"
