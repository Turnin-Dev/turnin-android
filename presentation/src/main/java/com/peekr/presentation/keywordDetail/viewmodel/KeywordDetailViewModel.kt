package com.peekr.presentation.keywordDetail.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.peekr.core.domain.util.Result
import com.peekr.core.presentation.util.SnackbarController
import com.peekr.core.presentation.util.SnackbarEvent
import com.peekr.core.presentation.util.UiText
import com.peekr.core.presentation.viewmodel.MVIBaseViewModel
import com.peekr.domain.keywordDetail.usecase.GetUserIdUseCase
import com.peekr.presentation.R
import com.peekr.presentation.keywordDetail.error.asUiText
import com.peekr.presentation.keywordDetail.state.KeywordDetailContract
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.launch

@HiltViewModel
class KeywordDetailViewModel @Inject constructor(
    private val getUserIdUseCase: GetUserIdUseCase,
    savedStateHandle: SavedStateHandle,
) : MVIBaseViewModel<KeywordDetailContract.UiState, KeywordDetailContract.UiEvent, KeywordDetailContract.UiEffect>() {
    private val currentUserKeywordId: Long by lazy {
        requireNotNull(savedStateHandle.get<Long>("userKeywordId"))
    }
    private val currentUserId: Long by lazy {
        requireNotNull(savedStateHandle.get<Long>("userId"))
    }
    private val currentKeyword: String by lazy {
        requireNotNull(savedStateHandle.get<String>("keyword"))
    }

    override fun createInitialState(): KeywordDetailContract.UiState =
        KeywordDetailContract.UiState()

    override suspend fun loadInitialData() {
        viewModelScope.launch {
            launch { initNavArgumentData() }
            launch { checkMyKeyword() }
            launch { setKeyword() }
        }
    }

    override suspend fun handleEvent(event: KeywordDetailContract.UiEvent) {
        when (event) {
            is KeywordDetailContract.UiEvent.OnDescriptionChanged -> TODO()
            KeywordDetailContract.UiEvent.SafeCancel -> {
                // 임시
                sendEffect {
                    KeywordDetailContract.UiEffect.BackStack
                }
            }

            is KeywordDetailContract.UiEvent.UpdateDescription -> TODO()
        }
    }

    private fun initNavArgumentData() {
        runCatching {
            currentUserKeywordId
            currentUserId
            currentKeyword
        }
            .onFailure {
                sendEffect {
                    KeywordDetailContract.UiEffect.FullScreenError(
                        errorMessage = UiText.StringResource(R.string.keyword_detail_modal_error_nav_arg_null),
                    )
                }
            }
    }

    private fun setKeyword() {
        updateState {
            this.copy(keyword = currentKeyword)
        }
    }

    private suspend fun checkMyKeyword() {
        getUserIdUseCase().collect { result ->
            when (result) {
                Result.Loading -> updateState {
                    this.copy(loading = true)
                }

                is Result.Error -> updateState {
                    this.copy(
                        loading = false,
                        error = result.error.asUiText(),
                    )
                }

                is Result.Success -> updateState {
                    this.copy(
                        loading = false,
                        myKeyword = result.data.value == currentUserId,
                    )
                }
            }
        }
    }

    private suspend fun showSnackBar(message: UiText) {
        SnackbarController.sendEvent(SnackbarEvent(message = message))
    }
}
