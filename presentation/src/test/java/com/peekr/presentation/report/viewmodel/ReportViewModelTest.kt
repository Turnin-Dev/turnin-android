package com.peekr.presentation.report.viewmodel

import androidx.lifecycle.SavedStateHandle
import com.peekr.core.domain.common.Result
import com.peekr.core.domain.report.model.ReportReason
import com.peekr.core.domain.report.model.ReportReasonId
import com.peekr.core.domain.report.model.ReportReasons
import com.peekr.core.presentation.MVIBaseViewModelTest
import com.peekr.domain.report.usecase.GetReportReasonsUseCase
import com.peekr.domain.report.usecase.ReportUseCase
import com.peekr.presentation.report.model.toUiModel
import com.peekr.presentation.report.state.ReportContract
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import org.junit.Before
import org.junit.Test

class ReportViewModelTest : MVIBaseViewModelTest<
    ReportContract.UiState,
    ReportContract.UiEvent,
    ReportContract.UiEffect,
    ReportViewModel,
>() {
    private val reportUseCase: ReportUseCase = mockk()
    private val getReportReasonsUseCase: GetReportReasonsUseCase = mockk()
    private lateinit var savedStateHandle: SavedStateHandle
    private lateinit var viewModel: ReportViewModel

    @Before
    fun setUp() {
        // Mock
        every {
            reportUseCase(TEST_REPORTED_ID, TestReportReasonId, null)
        } returns flowOf(Result.Success(true))
        every {
            getReportReasonsUseCase()
        } returns flowOf(Result.Success(TestReportReasons))

        savedStateHandle = TestSavedStateHandle

        viewModel = ReportViewModel(
            reportUseCase = reportUseCase,
            getReportReasonsUseCase = getReportReasonsUseCase,
            savedStateHandle = savedStateHandle,
        )
    }

    @Test
    fun `신고 사유 목록 조회 성공 테스트`() {
        testState(
            viewModel = viewModel,
            intents = listOf(
                ReportContract.UiEvent.GetReportReasons,
            ),
            assertions = listOf(
                ReportContract.UiState(
                    loading = false,
                    reportReasons = TestReportReasons.reasons.map { it.toUiModel() },
                ),
            ),
        )
    }

    @Test
    fun `신고 성공 시 신고 결과 상태가 true로 업데이트된다`() {
        // given: 신고 사유 선택
        viewModel.processEvent(
            ReportContract.UiEvent.SelectReportReason(
                reportReason = TestReportReasons.reasons.first().toUiModel(),
            ),
        )

        testState(
            viewModel = viewModel,
            intents = listOf(
                ReportContract.UiEvent.OnReport(null),
            ),
            assertions = listOf(
                ReportContract.UiState(
                    loading = false,
                    reportResult = true,
                ),
            ),
        )
    }

    @Test
    fun `신고 성공 시 신고 확인 창으로 이동하라는 일회성 이벤트가 발생한다`() {
        // given: 신고 사유 선택
        viewModel.processEvent(
            ReportContract.UiEvent.SelectReportReason(
                reportReason = TestReportReasons.reasons.first().toUiModel(),
            ),
        )

        testEffect(
            viewModel = viewModel,
            intents = listOf(
                ReportContract.UiEvent.OnReport(null),
            ),
            assertions = listOf(
                ReportContract.UiEffect.NavigateToReportResult,
            ),
        )
    }

    @Test
    fun `신고 사유 미 선택 시 모든 모달을 닫으라는 일회성 이벤트가 발생한다`() {
        testEffect(
            viewModel = viewModel,
            intents = listOf(
                ReportContract.UiEvent.OnReport(null),
            ),
            assertions = listOf(
                ReportContract.UiEffect.CloseReportModal,
            ),
        )
    }

    companion object {
        private const val TEST_REPORTED_ID = 10L
        private val TestReportReasonId = ReportReasonId(1L)
        private val TestSavedStateHandle = SavedStateHandle(
            mapOf("userId" to TEST_REPORTED_ID),
        )
        private val TestReportReasons = ReportReasons(
            reasons = listOf(
                ReportReason(
                    id = ReportReasonId(1L),
                    code = "SAMPLE",
                    description = "샘플",
                ),
            ),
        )
    }
}
