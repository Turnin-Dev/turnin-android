package com.peekr.presentation.profile.viewmodel

import androidx.lifecycle.viewModelScope
import com.peekr.core.domain.common.Result
import com.peekr.core.domain.model.UserKeywordId
import com.peekr.core.presentation.common.viewmodel.MVIBaseViewModel
import com.peekr.core.presentation.ui.component.snackbar.SnackbarController
import com.peekr.core.presentation.ui.component.snackbar.SnackbarEvent
import com.peekr.core.presentation.ui.util.UiText
import com.peekr.core.presentation.ui.util.UiText.StringResource
import com.peekr.domain.profile.error.ProfileErrorType
import com.peekr.domain.profile.usecase.MyProfileUseCases
import com.peekr.presentation.R
import com.peekr.presentation.profile.error.asUiText
import com.peekr.presentation.profile.model.toUiModel
import com.peekr.presentation.profile.state.MyProfileContract
import com.peekr.presentation.profile.state.SelectedKeywordState
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

@HiltViewModel
class MyProfileViewModel @Inject constructor(
    private val usecases: MyProfileUseCases,
) : MVIBaseViewModel<MyProfileContract.UiState, MyProfileContract.UiEvent, MyProfileContract.UiEffect>() {
    override fun createInitialState(): MyProfileContract.UiState =
        MyProfileContract.UiState()

    override suspend fun loadInitialData() {
        // 새로고침으로 해당 함수를 호출해도 상관은 없으나, 아래 로직에 캐싱 로직이 있다면
        // 삭제, 수정 후 해당 함수를 호출 시 변경 전 캐시 데이터를 조회할 가능성이 있을 수 있다.
        usecases.getMyProfile().collect { result ->
            when (result) {
                Result.Loading -> {
                    updateState {
                        this.copy(loading = true, error = null)
                    }
                }

                is Result.Error<ProfileErrorType> -> {
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
                            myProfile = result.data.toUiModel(),
                        )
                    }
                }
            }
        }
    }

    override suspend fun handleEvent(event: MyProfileContract.UiEvent) {
        when (event) {
            is MyProfileContract.UiEvent.DeleteKeyword -> {
                deleteKeyword(event.userKeywordId)
            }

            MyProfileContract.UiEvent.CloseAllModalsAndResetTextField -> {
                closeAllModalsAndResetTextFields()
            }

            is MyProfileContract.UiEvent.OnSelectedKeywordChanged -> {
                onSelectedKeywordChanged(
                    userKeywordId = event.userKeywordId,
                    keyword = event.keyword,
                )
            }

            is MyProfileContract.UiEvent.UpdateIntroduce -> {
                // TODO: 소개글 수정 시
            }
        }
    }

    private fun closeAllModalsAndResetTextFields() {
        sendEffect { MyProfileContract.UiEffect.CloseAllModals }
    }

    private fun onSelectedKeywordChanged(
        userKeywordId: UserKeywordId,
        keyword: String,
    ) {
        updateState {
            this.copy(
                selectedKeyword = this.selectedKeyword.copy(
                    userKeywordId = userKeywordId,
                    keyword = keyword,
                ),
            )
        }
    }

    private suspend fun deleteKeyword(userKeywordId: UserKeywordId?) {
        if (userKeywordId == null) {
            showSnackBar(StringResource(R.string.profile_error_not_selected_user_keyword_id))
            return
        } else {
            usecases.deleteUserKeyword(userKeywordId).onEach { result ->
                when (result) {
                    Result.Loading -> {
                        updateState { this.copy(fullScreenLoading = true) }
                    }

                    is Result.Error -> {
                        updateState {
                            this.copy(fullScreenLoading = false, error = result.error.asUiText())
                        }
                        showSnackBar(result.error.asUiText())
                    }

                    is Result.Success -> {
                        updateState {
                            this.copy(
                                fullScreenLoading = false,
                                error = null,
                                selectedKeyword = SelectedKeywordState(),
                            )
                        }
                        sendEffect { MyProfileContract.UiEffect.CloseAllModals }
                        showSnackBar(StringResource(R.string.profile_success_delete_user_keyword))
                        // 성공 시, 초기 데이터 다시 로드 (새로 고침)
                        loadInitialData()
                    }
                }
            }.launchIn(viewModelScope)
        }
    }

    private suspend fun showSnackBar(message: UiText) {
        SnackbarController.sendEvent(SnackbarEvent(message = message))
    }
}
