package com.turnin.presentation.keywordDetail.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.turnin.core.domain.common.Result
import com.turnin.core.domain.user.usecase.GetMyUserIdUseCase
import com.turnin.core.presentation.common.snackbar.SnackbarController
import com.turnin.core.presentation.common.snackbar.SnackbarEvent
import com.turnin.core.presentation.common.viewmodel.MVIBaseViewModel
import com.turnin.core.presentation.ui.util.UiText
import com.turnin.domain.keywordDetail.error.KeywordDetailErrorType
import com.turnin.domain.keywordDetail.usecase.KeywordDetailUseCases
import com.turnin.presentation.R
import com.turnin.presentation.keywordDetail.error.asUiText
import com.turnin.presentation.keywordDetail.model.toUiModel
import com.turnin.presentation.keywordDetail.state.KeywordDetailContract
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

@HiltViewModel
class KeywordDetailViewModel @Inject constructor(
    private val usecase: KeywordDetailUseCases,
    private val getMyUserIdUseCase: GetMyUserIdUseCase,
    private val snackbarController: SnackbarController,
    savedStateHandle: SavedStateHandle,
) : MVIBaseViewModel<KeywordDetailContract.UiState, KeywordDetailContract.UiEvent, KeywordDetailContract.UiEffect>() {
    private val currentUserKeywordId: Long by lazy {
        requireNotNull(savedStateHandle.get<Long>("userKeywordId"))
    }
    private val currentUserId: Long by lazy {
        requireNotNull(savedStateHandle.get<Long>("userId"))
    }

    override fun createInitialState(): KeywordDetailContract.UiState =
        KeywordDetailContract.UiState()

    override suspend fun loadInitialData() {
        val initResult = initNavArgumentData()
        // initNavArgumentData 가 실패할 경우(false를 반환할 경우)
        // 에러 처리를 하고 프로필 로드 기능을 중단한다(다른 기능이 실행될 수 없다).
        if (!initResult) return
        checkMyKeyword()
        loadKeywordDetail()
    }

    private fun initNavArgumentData(): Boolean = runCatching {
        currentUserKeywordId
        currentUserId
    }
        .onFailure {
            updateState {
                this.copy(
                    error = UiText.StringResource(R.string.keyword_detail_screen_error_load_failed),
                )
            }
        }
        .isSuccess

    override suspend fun handleEvent(event: KeywordDetailContract.UiEvent) {
        when (event) {
            KeywordDetailContract.UiEvent.OnRefresh -> {
                refreshKeywordDetail()
            }

            KeywordDetailContract.UiEvent.OnReport -> {
                sendEffect {
                    KeywordDetailContract.UiEffect.NavigateToReport(
                        userId = currentUserId,
                        userKeywordId = currentUserKeywordId,
                    )
                }
            }

            KeywordDetailContract.UiEvent.OnDelete -> {
                deleteKeyword()
            }
        }
    }

    /**
     * 나의 키워드 여부를 확인한다
     *
     * @return 나의 키워드라면 true, 아니라면 false, 아예 ID를 찾을 수 없다면 `null`을 반환한다
     */
    private suspend fun checkMyKeyword() {
        val userId = getMyUserIdUseCase()
        if (userId == null) {
            showSnackBar(KeywordDetailErrorType.UserIdNotFound.asUiText())
        } else {
            updateState {
                this.copy(myKeyword = userId.value == currentUserId)
            }
        }
    }

    private fun loadKeywordDetail() {
        usecase.getKeywordDetail(currentUserId, currentUserKeywordId).onEach { result ->
            when (result) {
                Result.Loading -> {
                    updateState {
                        this.copy(loading = true)
                    }
                }

                is Result.Error -> {
                    updateState {
                        this.copy(loading = false)
                    }
                    showSnackBar(result.error.asUiText())
                }

                is Result.Success -> {
                    updateState {
                        this.copy(
                            loading = false,
                            error = null,
                            keywordDetail = result.data.toUiModel(),
                        )
                    }
                }
            }
        }.launchIn(viewModelScope)
    }

    private fun refreshKeywordDetail() {
        usecase.refreshKeywordDetail(currentUserId, currentUserKeywordId).onEach { result ->
            when (result) {
                Result.Loading -> {
                    updateState {
                        this.copy(loading = true)
                    }
                }

                is Result.Error -> {
                    updateState {
                        this.copy(loading = false)
                    }
                    showSnackBar(result.error.asUiText())
                }

                is Result.Success -> {
                    updateState {
                        this.copy(
                            loading = false,
                            error = null,
                            keywordDetail = result.data.toUiModel(),
                        )
                    }
                }
            }
        }.launchIn(viewModelScope)
    }

    private fun deleteKeyword() {
        usecase.deleteKeyword(currentUserKeywordId).onEach { result ->
            when (result) {
                Result.Loading -> {
                    updateState {
                        this.copy(fullScreenLoading = true)
                    }
                }

                is Result.Error -> {
                    updateState {
                        this.copy(fullScreenLoading = false)
                    }
                    showSnackBar(result.error.asUiText())
                }

                is Result.Success -> {
                    updateState {
                        this.copy(fullScreenLoading = false)
                    }
                    showSnackBar(UiText.StringResource(R.string.keyword_detail_success_delete_keyword))
                    sendEffect {
                        KeywordDetailContract.UiEffect.CloseScreen
                    }
                }
            }
        }.launchIn(viewModelScope)
    }

    private suspend fun showSnackBar(message: UiText) {
        snackbarController.sendEvent(SnackbarEvent(message = message))
    }
}
