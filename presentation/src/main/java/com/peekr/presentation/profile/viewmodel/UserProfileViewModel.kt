package com.peekr.presentation.profile.viewmodel

import androidx.lifecycle.SavedStateHandle
import com.peekr.core.domain.common.Result
import com.peekr.core.domain.friend.model.FriendshipStatus
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
    private val currentUserId: Long by lazy {
        requireNotNull(savedStateHandle.get<Long>("userId"))
    }

    override fun createInitialState(): UserProfileContract.UiState =
        UserProfileContract.UiState()

    override suspend fun handleEvent(event: UserProfileContract.UiEvent) {
        when (event) {
            is UserProfileContract.UiEvent.OnReport -> {
                report()
            }

            is UserProfileContract.UiEvent.OnFriendshipButtonClick -> {
                updateFriendshipStatus(event.friendshipStatus)
            }
        }
    }

    override suspend fun loadInitialData() {
        val initResult = initNavArgumentData()
        // initNavArgumentData 가 실패할 경우(false를 반환할 경우)
        // 에러 처리를 하고 프로필 로드 기능을 중단한다(다른 기능이 실행될 수 없다).
        if (!initResult) return
        getUserProfile()
    }

    private suspend fun initNavArgumentData(): Boolean = runCatching {
        currentUserId
    }
        .onFailure {
            showSnackBar(ProfileErrorType.ProfileLoadFailed.asUiText())
        }
        .isSuccess

    private suspend fun getUserProfile() {
        usecases.getUserProfile(currentUserId).collect { result ->
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

    // 신고
    private fun report() {
        sendEffect {
            UserProfileContract.UiEffect.NavigateToReport(currentUserId)
        }
    }

    // 친구 상태에 따라 기능 수행
    private fun updateFriendshipStatus(
        friendshipStatus: FriendshipStatus,
    ) {
    }

    private suspend fun showSnackBar(message: UiText) {
        SnackbarController.sendEvent(SnackbarEvent(message = message))
    }
}
