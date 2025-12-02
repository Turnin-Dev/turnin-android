package com.peekr.presentation.profile.viewmodel

import androidx.lifecycle.SavedStateHandle
import com.peekr.core.domain.common.Result
import com.peekr.core.presentation.common.viewmodel.MVIBaseViewModel
import com.peekr.core.presentation.ui.component.snackbar.SnackbarController
import com.peekr.core.presentation.ui.component.snackbar.SnackbarEvent
import com.peekr.core.presentation.ui.util.UiText
import com.peekr.domain.profile.error.ProfileErrorType
import com.peekr.domain.profile.usecase.UserProfileUseCases
import com.peekr.presentation.profile.error.asUiText
import com.peekr.presentation.profile.model.toUiModel
import com.peekr.presentation.profile.state.UserProfileContract
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class UserProfileViewModel @Inject constructor(
    private val usecases: UserProfileUseCases,
    savedStateHandle: SavedStateHandle,
) : MVIBaseViewModel<UserProfileContract.UiState, UserProfileContract.UiEvent, UserProfileContract.UiEffect>() {
    private val currentDisplayId: String by lazy {
        requireNotNull(savedStateHandle.get<String>("displayId"))
    }

    override fun createInitialState(): UserProfileContract.UiState =
        UserProfileContract.UiState()

    override suspend fun handleEvent(event: UserProfileContract.UiEvent) {
        when (event) {
            is UserProfileContract.UiEvent.OnReport -> {
                // TODO: 신고 로직 작성
            }
        }
    }

    override suspend fun loadInitialData() {
        val initResult = initNavArgumentData()
        // initNavArgumentData 가 결과를 반환하기 전(false를 반환할 경우)
        // 화면 전체를 에러로 표시하는 이벤트를 UI로 보내기 때문에 다른 기능이 실행될 수 없다.
        if (!initResult) return
        getUserProfile()
    }

    private suspend fun initNavArgumentData(): Boolean = runCatching {
        currentDisplayId
    }
        .onFailure {
            showSnackBar(ProfileErrorType.ProfileLoadFailed.asUiText())
        }
        .isSuccess

    private suspend fun getUserProfile() {
        usecases.getUserProfile(currentDisplayId).collect { result ->
            when (result) {
                Result.Loading -> {
                    updateState {
                        this.copy(loading = true, error = null)
                    }
                }

                is Result.Error -> {
                    updateState {
                        this.copy(loading = false, error = result.error.asUiText())
                    }
                    showSnackBar(ProfileErrorType.ProfileLoadFailed.asUiText())
                }

                is Result.Success -> {
                    updateState {
                        this.copy(
                            loading = false,
                            error = null,
                            userProfile = result.data.toUiModel(),
                        )
                    }
                }
            }
        }
    }

    private suspend fun showSnackBar(message: UiText) {
        SnackbarController.sendEvent(SnackbarEvent(message = message))
    }
}
