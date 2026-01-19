package com.peekr.presentation.profile.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.peekr.core.domain.common.Result
import com.peekr.core.domain.friend.model.FriendStatus
import com.peekr.core.presentation.common.snackbar.SnackbarController
import com.peekr.core.presentation.common.snackbar.SnackbarEvent
import com.peekr.core.presentation.common.viewmodel.MVIBaseViewModel
import com.peekr.core.presentation.ui.model.toUiModel
import com.peekr.core.presentation.ui.util.UiText
import com.peekr.domain.profile.error.ProfileErrorType
import com.peekr.domain.profile.usecase.UserProfileUseCases
import com.peekr.presentation.profile.error.asUiText
import com.peekr.presentation.profile.model.toUiModel
import com.peekr.presentation.profile.state.UserProfileContract
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

@HiltViewModel
class UserProfileViewModel @Inject constructor(
    private val snackbarController: SnackbarController,
    private val usecases: UserProfileUseCases,
    savedStateHandle: SavedStateHandle,
) : MVIBaseViewModel<UserProfileContract.UiState, UserProfileContract.UiEvent, UserProfileContract.UiEffect>() {
    /** 친구 사용자 ID */
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

            is UserProfileContract.UiEvent.OnFriendButtonClick -> {
                if (event.friendStatus == FriendStatus.FRIENDS) {
                    sendEffect {
                        UserProfileContract.UiEffect.OpenDeleteFriendModal
                    }
                } else {
                    updateFriendStatus(event.friendStatus)
                }
            }

            UserProfileContract.UiEvent.DeleteFriend -> {
                updateFriendStatus(FriendStatus.FRIENDS)
            }
        }
    }

    override suspend fun loadInitialData() {
        val initResult = initNavArgumentData()
        // initNavArgumentData 가 실패할 경우(false를 반환할 경우)
        // 에러 처리를 하고 프로필 로드 기능을 중단한다(다른 기능이 실행될 수 없다).
        if (!initResult) return
        getUserProfile()
        getUserKeywords()
    }

    private suspend fun initNavArgumentData(): Boolean = runCatching {
        currentUserId
    }
        .onFailure {
            showSnackBar(ProfileErrorType.ProfileLoadFailed.asUiText())
        }
        .isSuccess

    private fun getUserProfile() {
        usecases.getUserProfile(currentUserId).onEach { result ->
            when (result) {
                Result.Loading -> {
                    updateState {
                        this.copy(profileLoading = true)
                    }
                }

                is Result.Error -> {
                    updateState {
                        this.copy(profileLoading = false)
                    }
                    showSnackBar(ProfileErrorType.ProfileLoadFailed.asUiText())
                }

                is Result.Success -> {
                    updateState {
                        this.copy(
                            profileLoading = false,
                            profile = result.data.toUiModel(),
                        )
                    }
                }
            }
        }.launchIn(viewModelScope)
    }

    private fun getUserKeywords() {
        usecases.getUserKeywords(currentUserId).onEach { result ->
            when (result) {
                Result.Loading -> {
                    updateState {
                        this.copy(keywordsLoading = true)
                    }
                }

                is Result.Error -> {
                    updateState {
                        this.copy(keywordsLoading = false)
                    }
                    showSnackBar(ProfileErrorType.KeywordsLoadFailed.asUiText())
                }

                is Result.Success -> {
                    updateState {
                        this.copy(
                            keywordsLoading = false,
                            keywords = result.data.map { it.toUiModel() },
                        )
                    }
                }
            }
        }.launchIn(viewModelScope)
    }

    // 신고
    private fun report() {
        sendEffect {
            UserProfileContract.UiEffect.NavigateToReport(currentUserId)
        }
    }

    // 친구 상태에 따라 기능 수행
    private fun updateFriendStatus(
        friendStatus: FriendStatus,
    ) {
        // 1) 친구 상태 즉시 업데이트 (UI 우선 업데이트)
        updateState {
            this.copy(
                profile = this.profile?.copy(friendStatus = friendStatus.toggle()),
            )
        }

        // 2) 친구 상태 업데이트 요청
        usecases.updateFriendStatus(
            receiverId = currentUserId,
            currentFriendStatus = friendStatus,
        ).onEach { result ->
            when (result) {
                Result.Loading -> {}

                is Result.Error -> {
                    // 3) 실패 시 친구 상태 롤백
                    updateState {
                        this.copy(
                            profile = this.profile?.copy(friendStatus = friendStatus),
                        )
                    }
                    showSnackBar(result.error.asUiText())
                }

                is Result.Success -> {
                    if (result.data != friendStatus.toggle()) {
                        // 4) 친구 상태 결과 값과 달라도 롤백
                        updateState {
                            this.copy(
                                profile = this.profile?.copy(friendStatus = friendStatus),
                            )
                        }
                    }
                }
            }
        }.launchIn(viewModelScope)
    }

    private suspend fun showSnackBar(message: UiText) {
        snackbarController.sendEvent(SnackbarEvent(message = message))
    }
}
