package com.peekr.presentation.discover.viewmodel

import androidx.lifecycle.SavedStateHandle
import com.peekr.core.presentation.common.viewmodel.MVIBaseViewModel
import com.peekr.presentation.discover.state.DiscoverContract
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class DiscoverViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
) : MVIBaseViewModel<DiscoverContract.UiState, DiscoverContract.UiEvent, DiscoverContract.UiEffect>() {
    override fun createInitialState(): DiscoverContract.UiState =
        DiscoverContract.UiState()

    override suspend fun handleEvent(event: DiscoverContract.UiEvent) {
        when (event) {
            is DiscoverContract.UiEvent.OnSelectedHistoryUser -> TODO()
        }
    }
}
