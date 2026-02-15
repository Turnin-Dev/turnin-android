package com.peekr.presentation.block.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.peekr.core.domain.common.Result
import com.peekr.core.presentation.common.viewmodel.MVIBaseViewModel
import com.peekr.domain.block.usecase.CreateBlockUseCase
import com.peekr.domain.block.usecase.GetBlockReasonsUseCase
import com.peekr.presentation.block.error.asUiText
import com.peekr.presentation.block.model.UiBlockReason
import com.peekr.presentation.block.model.toUiModel
import com.peekr.presentation.block.state.BlockModalContract
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

@HiltViewModel
class BlockModalViewModel @Inject constructor(
    private val getBlockReasonsUseCase: GetBlockReasonsUseCase,
    private val createBlockUseCase: CreateBlockUseCase,
    savedStateHandle: SavedStateHandle,
) : MVIBaseViewModel<BlockModalContract.UiState, BlockModalContract.UiEvent, BlockModalContract.UiEffect>() {
    /** 차단할 사용자 ID */
    private val blockedId: Long? by lazy {
        savedStateHandle.get<Long>("userId")
    }

    /** 선택된 차단 사유 */
    private var selectedBlockReason: UiBlockReason? = null

    override fun createInitialState(): BlockModalContract.UiState =
        BlockModalContract.UiState()

    override suspend fun handleEvent(event: BlockModalContract.UiEvent) {
        when (event) {
            BlockModalContract.UiEvent.GetBlockReasons -> {
                getBlockReasons()
            }

            is BlockModalContract.UiEvent.SelectBlockReason -> {
                selectedBlockReason = event.blockReason
            }

            is BlockModalContract.UiEvent.OnBlock -> {
                block(event.reason)
            }
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

    // 차단 사유 목록 조회
    private fun getBlockReasons() {
        getBlockReasonsUseCase().onEach { result ->
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
                            blockReasons = result.data.map { it.toUiModel() },
                        )
                    }
                }
            }
        }.launchIn(viewModelScope)
    }

    // 사용자 차단
    private fun block(reason: String) {
        // 차단 사유 미 선택 시 에러 발생
        if (selectedBlockReason == null) {
            sendEffect {
                BlockModalContract.UiEffect.CloseBlockModal
            }
            return
        }

        // 차단 수행
        createBlockUseCase(
            blockedId = blockedId,
            reasonId = selectedBlockReason?.id,
            customReason = reason,
        ).onEach { result ->
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
                        )
                    }

                    sendEffect {
                        BlockModalContract.UiEffect.NavigateToBlockResult
                    }
                }
            }
        }.launchIn(viewModelScope)
    }
}
