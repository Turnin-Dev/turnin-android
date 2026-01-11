package com.peekr.presentation.keywordEdit.viewmodel

import androidx.lifecycle.viewModelScope
import com.peekr.core.domain.common.Result
import com.peekr.core.presentation.common.error.asUiText
import com.peekr.core.presentation.common.snackbar.SnackbarController
import com.peekr.core.presentation.common.snackbar.SnackbarEvent
import com.peekr.core.presentation.common.viewmodel.MVIBaseViewModel
import com.peekr.core.presentation.common.viewmodel.setTextFieldValidation
import com.peekr.core.presentation.ui.util.UiText
import com.peekr.domain.keywordEdit.usecase.AddUserKeywordUseCase
import com.peekr.domain.keywordEdit.usecase.ValidateKeywordUseCase
import com.peekr.presentation.R
import com.peekr.presentation.keywordEdit.error.asUiText
import com.peekr.presentation.keywordEdit.state.KeywordEditContract
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

@HiltViewModel
class KeywordEditViewModel @Inject constructor(
    private val snackbarController: SnackbarController,
    private val addUserKeywordUseCase: AddUserKeywordUseCase,
    private val validateKeywordUseCase: ValidateKeywordUseCase,
//    savedStateHandle: SavedStateHandle,
) : MVIBaseViewModel<KeywordEditContract.UiState, KeywordEditContract.UiEvent, KeywordEditContract.UiEffect>() {
    override fun createInitialState(): KeywordEditContract.UiState =
        KeywordEditContract.UiState()

    init {
        setKeywordValidation()
    }

    override suspend fun handleEvent(event: KeywordEditContract.UiEvent) {
        when (event) {
            is KeywordEditContract.UiEvent.OnKeywordChanged -> {
                updateState {
                    this.copy(keyword = this.keyword.copy(event.value))
                }
            }

            is KeywordEditContract.UiEvent.OnDescriptionChanged -> {
                updateState {
                    this.copy(description = event.value)
                }
            }

            KeywordEditContract.UiEvent.AddKeyword -> {
                addKeyword()
            }

            KeywordEditContract.UiEvent.SafeBackPressed -> {
                safeBackPressed(
                    keyword = currentUiState.keyword.value,
                    description = currentUiState.description,
                )
            }

            KeywordEditContract.UiEvent.CloseScreen -> {
                sendEffect { KeywordEditContract.UiEffect.CloseScreen }
            }
        }
    }

    /** 키워드 추가 */
    private fun addKeyword() {
        addUserKeywordUseCase(
            keyword = currentUiState.keyword.value,
            description = currentUiState.description,
        ).onEach { result ->
            when (result) {
                Result.Loading -> {
                    updateState {
                        this.copy(loading = true)
                    }
                }

                is Result.Error -> {
                    updateState {
                        this.copy(
                            loading = false,
                            error = result.error.asUiText(),
                        )
                    }
                    showSnackBar(result.error.asUiText())
                }

                is Result.Success -> {
                    updateState {
                        this.copy(
                            loading = false,
                            error = null,
                        )
                    }
                    showSnackBar(UiText.StringResource(R.string.keyword_edit_success_add_keyword))
                    sendEffect { KeywordEditContract.UiEffect.CloseScreen }
                }
            }
        }.launchIn(viewModelScope)
    }

    /**
     * 안전하게 뒤로가기 할 수 있도록 확인과정을 거친다.
     *
     * 작성중인 텍스트가 있다면 경고 모달을 띄우고 아니라면 뒤로가기를 마저 수행한다.
     */
    private fun safeBackPressed(
        keyword: String?,
        description: String?,
    ) {
        if ((keyword != null && keyword.isNotEmpty()) ||
            (description != null && description.isNotEmpty())
        ) {
            sendEffect { KeywordEditContract.UiEffect.OpenSafeCancelModal }
        } else {
            sendEffect { KeywordEditContract.UiEffect.CloseScreen }
        }
    }

    private suspend fun showSnackBar(message: UiText) {
        snackbarController.sendEvent(SnackbarEvent(message = message))
    }

    // ------------------------------ Validation ------------------------------
    private fun setKeywordValidation() {
        uiState.setTextFieldValidation(
            scope = viewModelScope,
            value = { it.keyword.value },
            validator = { validateKeywordUseCase(it) },
            onValid = { _ ->
                updateState {
                    val updatedKeywordTextField = currentUiState
                        .keyword
                        .copy(error = null)
                    this.copy(keyword = updatedKeywordTextField)
                }
            },
            onInvalid = { error ->
                updateState {
                    val updatedKeywordTextField = currentUiState
                        .keyword
                        .copy(error = error.asUiText())
                    this.copy(keyword = updatedKeywordTextField)
                }
            },
        )
    }
}
