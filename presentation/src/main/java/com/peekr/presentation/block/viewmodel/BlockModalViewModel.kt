package com.peekr.presentation.block.viewmodel

import androidx.lifecycle.SavedStateHandle
import com.peekr.core.presentation.common.viewmodel.MVIBaseViewModel
import com.peekr.presentation.block.state.BlockModalContract
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class BlockModalViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
) : MVIBaseViewModel<BlockModalContract.UiState, BlockModalContract.UiEvent, BlockModalContract.UiEffect>() {
    private val blockedId: Long? by lazy {
        savedStateHandle.get<Long>("userId")
    }

    override fun createInitialState(): BlockModalContract.UiState =
        BlockModalContract.UiState()

    override suspend fun handleEvent(event: BlockModalContract.UiEvent) {
        when (event) {
            else -> TODO()
        }
    }

    override suspend fun loadInitialData() {
        val initResult = initNavArgumentData()
        if (!initResult) return
    }

    // 초기 데이터 로드: 이전 백스택에서 넘어온 인자 값 로드
    private fun initNavArgumentData(): Boolean =
        if (blockedId == null) {
            sendEffect {
                BlockModalContract.UiEffect.CloseBlockModal
            }
            false
        } else {
            true
        }
}
