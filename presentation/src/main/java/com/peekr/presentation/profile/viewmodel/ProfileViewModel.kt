package com.peekr.presentation.profile.viewmodel

import androidx.lifecycle.viewModelScope
import com.peekr.core.domain.model.UserKeywordId
import com.peekr.core.domain.util.Result
import com.peekr.core.presentation.error.asUiText
import com.peekr.core.presentation.util.SnackbarController
import com.peekr.core.presentation.util.SnackbarEvent
import com.peekr.core.presentation.util.UiText
import com.peekr.core.presentation.util.UiText.StringResource
import com.peekr.core.presentation.viewmodel.MVIBaseViewModel
import com.peekr.core.presentation.viewmodel.setTextFieldValidation
import com.peekr.domain.profile.error.ProfileErrorType
import com.peekr.domain.profile.usecase.ProfileUseCases
import com.peekr.presentation.R
import com.peekr.presentation.profile.error.asUiText
import com.peekr.presentation.profile.model.toUiModel
import com.peekr.presentation.profile.state.ChangedKeywordNodeOffset
import com.peekr.presentation.profile.state.ProfileContract
import dagger.hilt.android.lifecycle.HiltViewModel
import java.util.concurrent.atomic.AtomicInteger
import javax.inject.Inject
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

@OptIn(FlowPreview::class)
@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val usecases: ProfileUseCases,
) : MVIBaseViewModel<ProfileContract.UiState, ProfileContract.UiEvent, ProfileContract.UiEffect>() {
    override fun createInitialState(): ProfileContract.UiState =
        ProfileContract.UiState()

    init {
        setKeywordValidation()
        setKeywordDescriptionValidation()
    }

    override suspend fun loadInitialData() {
        // 새로고침으로 해당 함수를 호출해도 상관은 없으나, 아래 로직에 캐싱 로직이 있다면
        // 삭제, 수정 후 해당 함수를 호출 시 변경 전 캐시 데이터를 조회할 가능성이 있을 수 있다.
        usecases.getProfile().collect { result ->
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

            is ProfileContract.UiEvent.OnKeywordNodeOffsetChanged -> {
                changeKeywordNodeOffset(
                    userKeywordId = event.userKeywordId,
                    offsetX = event.offsetX,
                    offsetY = event.offsetY,
                )
            }

            ProfileContract.UiEvent.UpdateKeywordNodeOffset -> {
                updateKeywordNodeOffset(currentUiState.updatedKeywordNodesOffset)
            }

            ProfileContract.UiEvent.ResetKeywordNodeOffset -> {
                updateState {
                    this.copy(updatedKeywordNodesOffset = emptyMap())
                }
            }

            is ProfileContract.UiEvent.DeleteKeyword -> {
                event.userKeywordId?.let {
                    deleteKeyword(it)
                }
                    ?: showSnackBar(StringResource(R.string.profile_error_not_selected_user_keyword_id))
            }

            is ProfileContract.UiEvent.UpdateKeywordDescription -> {
                event.userKeywordId?.let {
                    updateKeywordNode(it, event.description)
                }
                    ?: showSnackBar(StringResource(R.string.profile_error_not_selected_user_keyword_id))
            }
        }
    }

    private fun updateKeywordNode(
        userKeywordId: UserKeywordId,
        description: String,
    ) {
        usecases.updateUserKeywordDescription(userKeywordId, description).onEach { result ->
            when (result) {
                Result.Loading -> updateState { this.copy(loading = true) }
                is Result.Error -> updateState {
                    this.copy(loading = false, error = result.error.asUiText())
                }

                is Result.Success -> {
                    updateState {
                        this.copy(loading = false, error = null)
                    }

                    sendEffect { ProfileContract.UiEffect.SuccessUpdateKeywordDesc }

                    showSnackBar(StringResource(R.string.profile_success_update_user_keyword_desc))

                    // 성공 시, 초기 데이터 다시 로드 (새로 고침)
                    loadInitialData()
                }
            }
        }.launchIn(viewModelScope)
    }

    private fun deleteKeyword(userKeywordId: UserKeywordId) {
        usecases.deleteUserKeyword(userKeywordId).onEach { result ->
            when (result) {
                Result.Loading -> updateState { this.copy(loading = true) }
                is Result.Error -> updateState {
                    this.copy(loading = false, error = result.error.asUiText())
                }

                is Result.Success -> {
                    updateState {
                        this.copy(loading = false, error = null)
                    }

                    sendEffect { ProfileContract.UiEffect.SuccessDeleteKeyword }

                    showSnackBar(StringResource(R.string.profile_success_delete_user_keyword))

                    // 성공 시, 초기 데이터 다시 로드 (새로 고침)
                    loadInitialData()
                }
            }
        }.launchIn(viewModelScope)
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

                            sendEffect { ProfileContract.UiEffect.SuccessAddKeyword }

                            showSnackBar(
                                UiText.StringResource(
                                    R.string.profile_success_add_user_keyword,
                                ),
                            )

                            // 성공 시, 초기 데이터 다시 로드 (새로 고침)
                            loadInitialData()
                        }
                    }
                }.launchIn(this)
        }
    }

    private fun changeKeywordNodeOffset(
        userKeywordId: UserKeywordId,
        offsetX: Float,
        offsetY: Float,
    ) {
        val changedKeywordNodeOffset = ChangedKeywordNodeOffset(offsetX, offsetY)
        updateState {
            this.copy(
                updatedKeywordNodesOffset =
                    this.updatedKeywordNodesOffset + (userKeywordId to changedKeywordNodeOffset),
            )
        }
    }

    fun updateKeywordNodeOffset(keywordNodes: Map<UserKeywordId, ChangedKeywordNodeOffset>) {
        viewModelScope.launch {
            var successCount = AtomicInteger(0)
            keywordNodes
                .map { (userKeywordId, offset) ->
                    async {
                        usecases
                            .updateUserKeywordOffset(userKeywordId, offset.offsetX, offset.offsetY)
                            .collect { result ->
                                when (result) {
                                    Result.Loading -> updateState { this.copy(loading = true) }
                                    is Result.Error -> updateState {
                                        this.copy(loading = false, error = result.error.asUiText())
                                    }

                                    is Result.Success -> {
                                        updateState {
                                            this.copy(loading = false, error = null)
                                        }
                                        successCount.incrementAndGet()
                                    }
                                }
                            }
                    }
                }.awaitAll()

            if (successCount.get() == keywordNodes.size) {
                showSnackBar(
                    UiText.StringResource(
                        R.string.profile_success_update_user_keyword_offset,
                    ),
                )
            }
        }
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

    private suspend fun showSnackBar(message: UiText) {
        SnackbarController.sendEvent(SnackbarEvent(message = message))
    }
}
