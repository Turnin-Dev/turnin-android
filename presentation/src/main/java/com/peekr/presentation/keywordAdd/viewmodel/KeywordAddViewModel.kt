package com.peekr.presentation.keywordAdd.viewmodel

import androidx.lifecycle.SavedStateHandle
import com.peekr.core.presentation.common.viewmodel.MVIBaseViewModel
import com.peekr.presentation.keywordAdd.state.KeywordAddContract
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class KeywordAddViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
) : MVIBaseViewModel<KeywordAddContract.UiState, KeywordAddContract.UiEvent, KeywordAddContract.UiEffect>() {
    override fun createInitialState(): KeywordAddContract.UiState =
        KeywordAddContract.UiState()

    override suspend fun handleEvent(event: KeywordAddContract.UiEvent) {
        when (event) {
            is KeywordAddContract.UiEvent.OnDescriptionChanged -> {
                updateState {
                    this.copy(keyword = this.keyword.copy(event.value))
                }
            }

            is KeywordAddContract.UiEvent.OnKeywordChanged -> {
                updateState {
                    this.copy(description = event.value)
                }
            }
        }
    }
}
