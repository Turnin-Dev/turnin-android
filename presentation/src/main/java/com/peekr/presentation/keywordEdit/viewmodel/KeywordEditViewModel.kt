package com.peekr.presentation.keywordEdit.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.peekr.core.domain.common.Result
import com.peekr.core.presentation.common.error.asUiText
import com.peekr.core.presentation.common.snackbar.SnackbarController
import com.peekr.core.presentation.common.snackbar.SnackbarEvent
import com.peekr.core.presentation.common.viewmodel.MVIBaseViewModel
import com.peekr.core.presentation.common.viewmodel.setTextFieldValidation
import com.peekr.core.presentation.ui.util.UiText
import com.peekr.domain.keywordEdit.usecase.KeywordEditUseCases
import com.peekr.presentation.R
import com.peekr.presentation.keywordEdit.error.asUiText
import com.peekr.presentation.keywordEdit.state.KeywordEditContract
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

@HiltViewModel
class KeywordEditViewModel @Inject constructor(
    private val snackbarController: SnackbarController,
    private val usecases: KeywordEditUseCases,
    savedStateHandle: SavedStateHandle,
) : MVIBaseViewModel<KeywordEditContract.UiState, KeywordEditContract.UiEvent, KeywordEditContract.UiEffect>() {
    private val userKeywordId: Long? by lazy {
        savedStateHandle.get<Long>("userKeywordId")
    }
    private var previousKeyword: String? = null
    private var previousDescription: String? = null

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

            KeywordEditContract.UiEvent.AddOrUpdateKeyword -> {
                if (userKeywordId == null) {
                    addKeyword()
                } else {
                    updateKeyword()
                }
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

    override suspend fun loadInitialData() {
        loadUserKeyword(userKeywordId)
    }

    private suspend fun loadUserKeyword(userKeywordId: Long?) {
        // TODO: 빈 값 조회 시 폴백 필요.
        userKeywordId?.let {
            val userKeywordDetail = usecases.getMyKeyword(it).firstOrNull()
            userKeywordDetail?.let {
                previousKeyword = userKeywordDetail.keywordName.value
                previousDescription = userKeywordDetail.description.value
                updateState {
                    this.copy(
                        keyword = this.keyword.copy(value = it.keywordName.value),
                        description = it.description.value,
                    )
                }
            }
        }
    }

    /** 키워드 추가 */
    private fun addKeyword() {
        usecases.add(
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
                        this.copy(loading = false)
                    }
                    showSnackBar(result.error.asUiText())
                }

                is Result.Success -> {
                    updateState {
                        this.copy(loading = false)
                    }
                    showSnackBar(UiText.StringResource(R.string.keyword_edit_success_add_keyword))
                    sendEffect { KeywordEditContract.UiEffect.CloseScreen }
                }
            }
        }.launchIn(viewModelScope)
    }

    /** 키워드 수정 */
    private fun updateKeyword() {
        // 1) 사용자 키워드 ID, 이전 키워드, 이전 내용 중 하나라도 null이면 수정을 진행하지 않는다.
        if (userKeywordId == null ||
            previousKeyword == null ||
            previousDescription == null
        ) {
            return
        }

        // 2) 수정한 키워드/내용이 이전과 같다면 수정을 진행하지 않고 그냥 화면을 떠난다.
        if (previousKeyword == currentUiState.keyword.value &&
            previousDescription == currentUiState.description
        ) {
            sendEffect { KeywordEditContract.UiEffect.CloseScreen }
            return
        }

        // 3) 수정 진행
        userKeywordId?.let {
            usecases.update(
                userKeywordId = it,
                keywordName = currentUiState.keyword.value,
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
                            this.copy(loading = false)
                        }
                        showSnackBar(result.error.asUiText())
                    }

                    is Result.Success -> {
                        updateState {
                            this.copy(loading = false)
                        }
                        showSnackBar(UiText.StringResource(R.string.keyword_edit_success_update_keyword))
                        sendEffect { KeywordEditContract.UiEffect.CloseScreen }
                    }
                }
            }.launchIn(viewModelScope)
        }
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
            validator = { usecases.validateKeyword(it) },
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
