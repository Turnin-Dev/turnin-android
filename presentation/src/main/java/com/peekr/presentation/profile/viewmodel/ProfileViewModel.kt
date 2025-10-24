package com.peekr.presentation.profile.viewmodel

import androidx.lifecycle.viewModelScope
import com.peekr.core.domain.util.Result
import com.peekr.core.presentation.error.asUiText
import com.peekr.core.presentation.viewmodel.MVIBaseViewModel
import com.peekr.core.presentation.viewmodel.setTextFieldValidation
import com.peekr.domain.profile.error.ProfileErrorType
import com.peekr.domain.profile.usecase.AddUserKeywordUseCase
import com.peekr.domain.profile.usecase.GetProfileUseCase
import com.peekr.domain.profile.usecase.ValidateKeywordDescriptionUseCase
import com.peekr.domain.profile.usecase.ValidateKeywordUseCase
import com.peekr.presentation.profile.error.asUiText
import com.peekr.presentation.profile.model.toUiModel
import com.peekr.presentation.profile.state.ProfileContract
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

@OptIn(FlowPreview::class)
@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val getProfileUseCase: GetProfileUseCase,
    private val addUserKeywordUseCase: AddUserKeywordUseCase,
    private val validateKeywordUseCase: ValidateKeywordUseCase,
    private val validateKeywordDescriptionUseCase: ValidateKeywordDescriptionUseCase,
) : MVIBaseViewModel<ProfileContract.UiState, ProfileContract.UiEvent, ProfileContract.UiEffect>() {
    override fun createInitialState(): ProfileContract.UiState =
        ProfileContract.UiState()

    init {
        setKeywordValidation()
        setKeywordDescriptionValidation()
    }

    override suspend fun loadInitialData() {
        getProfileUseCase().collect { result ->
            when (result) {
                Result.Loading -> updateState {
                    this.copy(loading = true, error = null)
                }

                is Result.Error<ProfileErrorType> -> updateState {
                    this.copy(loading = false, error = result.error.asUiText())
                }

                is Result.Success -> updateState {
                    this.copy(
                        loading = false,
                        error = null,
                        profile = result.data.toUiModel(),
                    )
                }
            }
        }
    }

    override suspend fun handleEvent(event: ProfileContract.UiEvent) {
        when (event) {
            is ProfileContract.UiEvent.OnKeywordTextChanged -> {
                updateState {
                    this.copy(
                        keywordTextField = this.keywordTextField.copy(value = event.value),
                    )
                }
            }

            is ProfileContract.UiEvent.OnKeywordDescTextChanged -> {
                updateState {
                    this.copy(
                        keywordDescTextField = this.keywordDescTextField.copy(value = event.value),
                    )
                }
            }

            is ProfileContract.UiEvent.AddKeyword -> {
                addKeyword(
                    keyword = currentUiState.keywordTextField.value,
                    description = currentUiState.keywordDescTextField.value,
                )
            }
        }
    }

    private fun addKeyword(
        keyword: String,
        description: String,
    ) {
        viewModelScope.launch {
            addUserKeywordUseCase(keyword, description)
                .onEach { result ->
                    when (result) {
                        is Result.Error -> updateState {
                            this.copy(loading = false, error = result.error.asUiText())
                        }

                        Result.Loading -> updateState {
                            this.copy(loading = true, error = null)
                        }

                        is Result.Success -> {
                            updateState {
                                this.copy(loading = false, error = null)
                            }
                            // 성공 시, 초기 데이터 다시 로드 (새로 고침)
                            loadInitialData()
                        }
                    }
                }.launchIn(this)
        }
    }

    private fun setKeywordValidation() {
        uiState.setTextFieldValidation(
            scope = viewModelScope,
            value = { it.keywordTextField.value },
            validator = { validateKeywordUseCase(it) },
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
            validator = { validateKeywordDescriptionUseCase(it) },
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
