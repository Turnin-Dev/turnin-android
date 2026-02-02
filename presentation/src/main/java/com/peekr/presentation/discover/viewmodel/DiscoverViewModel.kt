package com.peekr.presentation.discover.viewmodel

import androidx.lifecycle.viewModelScope
import com.peekr.core.presentation.common.snackbar.SnackbarController
import com.peekr.core.presentation.common.snackbar.SnackbarEvent
import com.peekr.core.presentation.common.viewmodel.MVIBaseViewModel
import com.peekr.core.presentation.ui.util.UiText
import com.peekr.domain.discover.error.DiscoverErrorType
import com.peekr.domain.discover.usecase.GetMyHistoryUserUseCase
import com.peekr.presentation.discover.error.asUiText
import com.peekr.presentation.discover.model.UiHistoryUser
import com.peekr.presentation.discover.model.toUiModel
import com.peekr.presentation.discover.state.DiscoverContract
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.launch

@HiltViewModel
class DiscoverViewModel @Inject constructor(
    private val getMyHistoryUserUseCase: GetMyHistoryUserUseCase,
    private val snackbarController: SnackbarController,
) : MVIBaseViewModel<DiscoverContract.UiState, DiscoverContract.UiEvent, DiscoverContract.UiEffect>() {
    init {
        initialize()
    }

    override fun createInitialState(): DiscoverContract.UiState =
        DiscoverContract.UiState()

    override suspend fun handleEvent(event: DiscoverContract.UiEvent) {
        when (event) {
            is DiscoverContract.UiEvent.RefreshDiscoverContexts -> {
                refreshDiscoverContexts(event.userId)
            }
        }
    }

    /**
     * 초기화 작업
     *
     * 1. 히스토리 바에 나를 추가
     * 2. 현재 탐색 대상을 나로 설정
     */
    private fun initialize() {
        viewModelScope.launch {
            val myHistoryUser = getMyHistoryUserUseCase()
            if (myHistoryUser == null) {
                showSnackbar(DiscoverErrorType.MyProfileNotFound.asUiText())
                return@launch
            }
            // 히스토리에 나를 추가하고 현재 탐색 대상을 나로 설정
            val myHistoryUserUiModel = myHistoryUser.toUiModel()
            updateState {
                this.copy(
                    historyUsers = emptyList<UiHistoryUser>() + myHistoryUserUiModel,
                    currentTargetUserId = myHistoryUserUiModel.userId,
                )
            }
        }
    }

    // 탐색 컨텍스트 새로고침 (새롭게 로드)
    private fun refreshDiscoverContexts(userId: Long) {
        // - 현재 탐색 대상과 같으면 수행하지 않음
    }

    private suspend fun showSnackbar(message: UiText) {
        snackbarController.sendEvent(SnackbarEvent(message))
    }
}
