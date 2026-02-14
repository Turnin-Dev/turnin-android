package com.peekr.presentation.block.viewmodel

import androidx.lifecycle.SavedStateHandle
import com.peekr.core.presentation.common.viewmodel.MVIBaseViewModel
import com.peekr.presentation.block.state.BlockContract
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class BlockViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
) : MVIBaseViewModel<BlockContract.UiState, BlockContract.UiEvent, BlockContract.UiEffect>() {
    override fun createInitialState(): BlockContract.UiState =
        BlockContract.UiState()

    override suspend fun handleEvent(event: BlockContract.UiEvent) {
        when (event) {
            else -> TODO()
        }
    }
}
