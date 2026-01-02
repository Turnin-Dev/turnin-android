package com.peekr.presentation.profile.viewmodel

import androidx.lifecycle.viewModelScope
import com.peekr.core.domain.common.Result
import com.peekr.core.domain.model.UserKeywordId
import com.peekr.core.presentation.common.error.asUiText
import com.peekr.core.presentation.common.viewmodel.MVIBaseViewModel
import com.peekr.core.presentation.common.viewmodel.setTextFieldValidation
import com.peekr.core.presentation.ui.component.snackbar.SnackbarController
import com.peekr.core.presentation.ui.component.snackbar.SnackbarEvent
import com.peekr.core.presentation.ui.util.UiText
import com.peekr.core.presentation.ui.util.UiText.StringResource
import com.peekr.domain.profile.error.ProfileErrorType
import com.peekr.domain.profile.usecase.MyProfileUseCases
import com.peekr.presentation.R
import com.peekr.presentation.profile.error.asUiText
import com.peekr.presentation.profile.model.toUiModel
import com.peekr.presentation.profile.state.KeywordTextFieldState
import com.peekr.presentation.profile.state.MyProfileContract
import com.peekr.presentation.profile.state.SelectedKeywordState
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

@HiltViewModel
class MyProfileViewModel @Inject constructor(
    private val usecases: MyProfileUseCases,
) : MVIBaseViewModel<MyProfileContract.UiState, MyProfileContract.UiEvent, MyProfileContract.UiEffect>() {
    override fun createInitialState(): MyProfileContract.UiState =
        MyProfileContract.UiState()

    init {
        setKeywordValidation()
        setKeywordDescriptionValidation()
    }

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
            is MyProfileContract.UiEvent.OnKeywordTextChanged -> {
                onKeywordTextChanged(event.value)
            }

            is MyProfileContract.UiEvent.OnKeywordDescTextChanged -> {
                onKeywordDescTextChanged(event.value)
            }

            is MyProfileContract.UiEvent.AddKeyword -> {
                addKeyword(
                    keyword = event.keyword,
                    description = event.description,
                )
            }

            is MyProfileContract.UiEvent.DeleteKeyword -> {
                deleteKeyword(event.userKeywordId)
            }

            is MyProfileContract.UiEvent.CheckSafeCancel -> {
                safeCancel(event.keyword, event.description)
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

    private fun onKeywordTextChanged(keyword: String) {
        updateState {
            this.copy(
                keywordTextField = this.keywordTextField.copy(value = keyword),
            )
        }
    }

    private fun onKeywordDescTextChanged(description: String) {
        updateState {
            this.copy(
                keywordDescTextField = this.keywordDescTextField.copy(value = description),
            )
        }
    }

    private fun closeAllModalsAndResetTextFields() {
        sendEffect { MyProfileContract.UiEffect.CloseAllModals }
        updateState {
            this.copy(
                keywordTextField = KeywordTextFieldState(),
                keywordDescTextField = KeywordTextFieldState(),
            )
        }
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

    private fun safeCancel(
        keyword: String?,
        description: String?,
    ) {
        if ((keyword != null && keyword.isNotEmpty()) ||
            (description != null && description.isNotEmpty())
        ) {
            sendEffect { MyProfileContract.UiEffect.OpenSafeCancelModal }
        } else {
            sendEffect { MyProfileContract.UiEffect.CloseAllModals }
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

    private fun addKeyword(
        keyword: String,
        description: String,
    ) {
        viewModelScope.launch {
            usecases
                .addUserKeyword(keyword, description)
                .onEach { result ->
                    when (result) {
                        Result.Loading -> {
                            updateState {
                                this.copy(fullScreenLoading = true, error = null)
                            }
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
                                    keywordTextField = KeywordTextFieldState(),
                                    keywordDescTextField = KeywordTextFieldState(),
                                    selectedKeyword = SelectedKeywordState(),
                                )
                            }
                            closeAllModalsAndResetTextFields()
                            showSnackBar(StringResource(R.string.profile_success_add_user_keyword))
                            // 성공 시, 초기 데이터 다시 로드 (새로 고침)
                            loadInitialData()
                        }
                    }
                }.launchIn(this)
        }
    }

    private suspend fun showSnackBar(message: UiText) {
        SnackbarController.sendEvent(SnackbarEvent(message = message))
    }

    // ------------------------------ Validation ------------------------------
    private fun setKeywordValidation() {
        uiState.setTextFieldValidation(
            scope = viewModelScope,
            value = { it.keywordTextField.value },
            validator = { usecases.validateKeyword(it) },
            onValid = { _ ->
                updateState {
                    val updatedKeywordTextField = currentUiState
                        .keywordTextField
                        .copy(error = null)
                    this.copy(keywordTextField = updatedKeywordTextField)
                }
            },
            onInvalid = { error ->
                updateState {
                    val updatedKeywordTextField = currentUiState
                        .keywordTextField
                        .copy(error = error.asUiText())
                    this.copy(keywordTextField = updatedKeywordTextField)
                }
            },
        )
    }

    private fun setKeywordDescriptionValidation() {
        uiState.setTextFieldValidation(
            scope = viewModelScope,
            value = { it.keywordDescTextField.value },
            validator = { usecases.validateKeywordDescription(it) },
            onValid = { _ ->
                updateState {
                    val updatedKeywordDescTextField = currentUiState
                        .keywordDescTextField
                        .copy(error = null)
                    this.copy(keywordDescTextField = updatedKeywordDescTextField)
                }
            },
            onInvalid = { error ->
                updateState {
                    val updatedKeywordDescTextField = currentUiState
                        .keywordDescTextField
                        .copy(error = error.asUiText())
                    this.copy(keywordDescTextField = updatedKeywordDescTextField)
                }
            },
        )
    }
}
