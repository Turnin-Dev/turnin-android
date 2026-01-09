package com.peekr.presentation.keywordEdit.viewmodel

import androidx.lifecycle.SavedStateHandle
import com.peekr.core.presentation.common.viewmodel.MVIBaseViewModel
import com.peekr.presentation.keywordEdit.state.KeywordEditContract
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class KeywordEditViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
) : MVIBaseViewModel<KeywordEditContract.UiState, KeywordEditContract.UiEvent, KeywordEditContract.UiEffect>() {
    override fun createInitialState(): KeywordEditContract.UiState =
        KeywordEditContract.UiState()

    override suspend fun handleEvent(event: KeywordEditContract.UiEvent) {
        when (event) {
            is KeywordEditContract.UiEvent.OnDescriptionChanged -> {
                updateState {
                    this.copy(keyword = this.keyword.copy(event.value))
                }
            }

            is KeywordEditContract.UiEvent.OnKeywordChanged -> {
                updateState {
                    this.copy(description = event.value)
                }
            }
        }
    }
}
