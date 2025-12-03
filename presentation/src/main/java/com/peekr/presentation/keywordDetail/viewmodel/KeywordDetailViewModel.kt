package com.peekr.presentation.keywordDetail.viewmodel

import androidx.compose.ui.text.TextRange
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.peekr.core.domain.common.Result
import com.peekr.core.presentation.common.viewmodel.MVIBaseViewModel
import com.peekr.core.presentation.ui.component.snackbar.SnackbarController
import com.peekr.core.presentation.ui.component.snackbar.SnackbarEvent
import com.peekr.core.presentation.ui.util.UiText
import com.peekr.domain.keywordDetail.usecase.CheckMyKeywordUseCase
import com.peekr.domain.keywordDetail.usecase.GetDescriptionUseCase
import com.peekr.domain.keywordDetail.usecase.UpdateDescriptionUseCase
import com.peekr.presentation.R
import com.peekr.presentation.keywordDetail.error.asUiText
import com.peekr.presentation.keywordDetail.state.KeywordDetailContract
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

@HiltViewModel
class KeywordDetailViewModel @Inject constructor(
    private val checkMyKeywordUseCase: CheckMyKeywordUseCase,
    private val getDescriptionUseCase: GetDescriptionUseCase,
    private val updateDescriptionUseCase: UpdateDescriptionUseCase,
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
        val initResult = initNavArgumentData()
        // initNavArgumentData 가 실패할 경우(false를 반환할 경우)
        // 에러 처리를 하고 프로필 로드 기능을 중단한다(다른 기능이 실행될 수 없다).
        if (!initResult) return

        viewModelScope.launch {
            launch { setKeyword() }
            launch { checkMyKeyword() }
            launch { getDescription() }
        }
    }

    private fun initNavArgumentData(): Boolean = runCatching {
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
        .isSuccess

    override suspend fun handleEvent(event: KeywordDetailContract.UiEvent) {
        when (event) {
            is KeywordDetailContract.UiEvent.OnDescriptionChanged -> {
                updateState {
                    this.copy(description = event.value)
                }
            }

            KeywordDetailContract.UiEvent.SafeCancel -> {
                // 임시
                sendEffect {
                    KeywordDetailContract.UiEffect.BackStack
                }
            }

            is KeywordDetailContract.UiEvent.UpdateDescription -> {
                updateDescription(event.description)
            }

            KeywordDetailContract.UiEvent.EnableEditMode -> {
                updateState {
                    this.copy(editMode = true)
                }
            }
        }
    }

    private fun updateDescription(description: String) {
        updateDescriptionUseCase(currentUserKeywordId, description).onEach { result ->
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
                }

                is Result.Success -> {
                    updateState {
                        this.copy(
                            loading = false,
                            error = null,
                            editMode = false,
                        )
                    }
                }
            }
        }.launchIn(viewModelScope)
    }

    private fun setKeyword() {
        updateState {
            this.copy(keyword = currentKeyword)
        }
    }

    private suspend fun checkMyKeyword() {
        checkMyKeywordUseCase().collect { result ->
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
                }

                is Result.Success -> {
                    updateState {
                        this.copy(
                            loading = false,
                            error = null,
                            myKeyword = result.data.value == currentUserId,
                        )
                    }
                }
            }
        }
    }

    private suspend fun getDescription() {
        getDescriptionUseCase(currentUserKeywordId).collect { result ->
            when (result) {
                Result.Loading -> {
                    updateState {
                        this.copy(loadingDescription = true)
                    }
                }

                is Result.Error -> {
                    updateState {
                        this.copy(
                            loadingDescription = false,
                            error = result.error.asUiText(),
                        )
                    }
                }

                is Result.Success -> {
                    updateState {
                        this.copy(
                            loadingDescription = false,
                            error = null,
                            description = this.description.copy(
                                text = result.data.value,
                                selection = TextRange(result.data.value.length),
                            ),
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
