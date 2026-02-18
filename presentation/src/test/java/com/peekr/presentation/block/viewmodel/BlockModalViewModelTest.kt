package com.peekr.presentation.block.viewmodel

import androidx.lifecycle.SavedStateHandle
import com.peekr.core.domain.block.model.BlockReason
import com.peekr.core.domain.block.model.BlockReasonId
import com.peekr.core.domain.common.Result
import com.peekr.core.domain.model.UserId
import com.peekr.core.presentation.MVIBaseViewModelTest
import com.peekr.domain.block.error.BlockErrorType
import com.peekr.domain.block.usecase.CreateBlockUseCase
import com.peekr.domain.block.usecase.GetBlockReasonsUseCase
import com.peekr.presentation.block.error.asUiText
import com.peekr.presentation.block.model.toUiModel
import com.peekr.presentation.block.state.BlockModalContract
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import org.junit.Before
import org.junit.Test

class BlockModalViewModelTest : MVIBaseViewModelTest<
    BlockModalContract.UiState,
    BlockModalContract.UiEvent,
    BlockModalContract.UiEffect,
    BlockModalViewModel,
>() {
    private val getBlockReasonsUseCase: GetBlockReasonsUseCase = mockk()
    private val createBlockUseCase: CreateBlockUseCase = mockk()
    private lateinit var savedStateHandle: SavedStateHandle
    private lateinit var viewModel: BlockModalViewModel

    @Before
    fun setUp() {
        savedStateHandle = SavedStateHandle(
            mapOf("userId" to TestUserId.value),
        )
        viewModel =
            BlockModalViewModel(getBlockReasonsUseCase, createBlockUseCase, savedStateHandle)
    }

    @Test
    fun `초기 데이터 로드 실패 시 에러가 일회성 이벤트로 발생한다`() {
        // given
        savedStateHandle = SavedStateHandle()
        viewModel =
            BlockModalViewModel(getBlockReasonsUseCase, createBlockUseCase, savedStateHandle)

        // when, then
        testEffect(
            viewModel = viewModel,
            intents = listOf(),
            assertions = listOf(
                BlockModalContract.UiEffect.CloseBlockModal,
            ),
        )
    }

    @Test
    fun `차단 사유 목록 조회 성공 시 목록 상태를 업데이트한다`() {
        // given
        every {
            getBlockReasonsUseCase()
        } returns flowOf(Result.Success(TestBlockReasons))

        // when: 차단 사유 목록 조회
        // this: 차단 사유 목록 상태 업데이트
        testState(
            viewModel = viewModel,
            intents = listOf(
                BlockModalContract.UiEvent.GetBlockReasons,
            ),
            assertions = listOf(
                BlockModalContract.UiState(
                    loading = false,
                    error = null,
                    blockReasons = TestBlockReasons.map { it.toUiModel() },
                ),
            ),
        )
    }

    @Test
    fun `차단 사유 목록 조회 실패 시 에러 상태를 업데이트한다`() {
        // given
        val expectedError = BlockErrorType.Unexpected(null)
        every {
            getBlockReasonsUseCase()
        } returns flowOf(Result.Error(expectedError))

        // when: 차단 사유 목록 조회
        // this: 차단 사유 목록 상태 업데이트
        testState(
            viewModel = viewModel,
            intents = listOf(
                BlockModalContract.UiEvent.GetBlockReasons,
            ),
            assertions = listOf(
                BlockModalContract.UiState(
                    loading = false,
                    error = expectedError.asUiText(),
                    blockReasons = emptyList(),
                ),
            ),
        )
    }

    @Test
    fun `차단 수행 성공 시 일부 상태를 초기화하고 차단 결과 화면으로 이동하는 일회성 이벤트를 발행한다`() {
        // given
        every {
            createBlockUseCase(any(), any(), any())
        } returns flowOf(Result.Success(Unit))

        // when: 차단 수행
        // then: 일부 상태 초기화, 차단 결과 화면으로 이동하는 일회성 이벤트 발행
        testAll(
            viewModel = viewModel,
            intents = listOf(
                BlockModalContract.UiEvent.SelectBlockReason(TestBlockReasons.first().toUiModel()),
                BlockModalContract.UiEvent.OnBlock("custom-reason"),
            ),
            assertionStates = listOf(
                BlockModalContract.UiState(
                    loading = false,
                    error = null,
                ),
            ),
            assertionEffects = listOf(
                BlockModalContract.UiEffect.NavigateToBlockResult,
            ),
        )
    }

    @Test
    fun `차단 수행 실패 시 에러 상태를 업데이트한다`() {
        // given
        val expectedError = BlockErrorType.Unexpected(null)
        every {
            createBlockUseCase(any(), any(), any())
        } returns flowOf(Result.Error(expectedError))

        // when: 차단 수행
        // then: 에러 상태 업데이트
        testState(
            viewModel = viewModel,
            intents = listOf(
                BlockModalContract.UiEvent.SelectBlockReason(TestBlockReasons.first().toUiModel()),
                BlockModalContract.UiEvent.OnBlock("custom-reason"),
            ),
            assertions = listOf(
                BlockModalContract.UiState(
                    loading = false,
                    error = expectedError.asUiText(),
                ),
            ),
        )
    }

    companion object {
        private val TestUserId = UserId(1L)
        private val TestBlockReasons = listOf(
            BlockReason(BlockReasonId(1L), "code", "desc"),
        )
    }
}
