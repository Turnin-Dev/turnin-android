package com.peekr.presentation.report.viewmodel

import androidx.lifecycle.SavedStateHandle
import com.peekr.core.domain.common.Result
import com.peekr.core.domain.model.UserKeywordId
import com.peekr.core.domain.report.model.ReportReason
import com.peekr.core.domain.report.model.ReportReasonId
import com.peekr.core.domain.report.model.ReportReasons
import com.peekr.core.presentation.MVIBaseViewModelTest
import com.peekr.domain.report.error.ReportErrorType
import com.peekr.domain.report.usecase.GetReportReasonsUseCase
import com.peekr.domain.report.usecase.ReportUseCase
import com.peekr.presentation.report.error.asUiText
import com.peekr.presentation.report.model.toUiModel
import com.peekr.presentation.report.state.ReportContract
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
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
            reportUseCase(TEST_REPORTED_ID, null, any(), any())
        } returns flowOf(Result.Success(Unit))
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
    fun `신고 사유 목록 조회 시 에러가 발생하면 에러를 표시한다`() {
        // given
        val expectedError = ReportErrorType.Unexpected(null)
        every {
            getReportReasonsUseCase()
        } returns flowOf(Result.Error(expectedError))

        // when, then
        testState(
            viewModel = viewModel,
            intents = listOf(
                ReportContract.UiEvent.GetReportReasons,
            ),
            assertions = listOf(
                ReportContract.UiState(
                    loading = false,
                    error = expectedError.asUiText(),
                ),
            ),
        )
    }

    @Test
    fun `신고 성공 시 신고 결과 화면으로 이동한다`() {
        testEffect(
            viewModel = viewModel,
            intents = listOf(
                ReportContract.UiEvent.SelectReportReason(
                    reportReason = TestReportReasons.reasons.first().toUiModel(),
                ),
                ReportContract.UiEvent.OnReport(null),
            ),
            assertions = listOf(
                ReportContract.UiEffect.NavigateToReportResult,
            ),
        )
    }

    @Test
    fun `신고 시 에러가 발생하면 에러 메시지를 표시한다`() {
        // given
        val expectedError = ReportErrorType.Unexpected(null)
        every {
            reportUseCase(TEST_REPORTED_ID, null, any(), any())
        } returns flowOf(Result.Error(expectedError))

        // when, then
        testState(
            viewModel = viewModel,
            intents = listOf(
                ReportContract.UiEvent.SelectReportReason(
                    reportReason = TestReportReasons.reasons.first().toUiModel(),
                ),
                ReportContract.UiEvent.OnReport(null),
            ),
            assertions = listOf(
                ReportContract.UiState(
                    loading = false,
                    error = expectedError.asUiText(),
                ),
            ),
        )
    }

    @Test
    fun `신고 시 에러가 발생하면 신고 결과 화면으로 이동한다`() {
        // given
        val expectedError = ReportErrorType.Unexpected(null)
        every {
            reportUseCase(TEST_REPORTED_ID, null, any(), any())
        } returns flowOf(Result.Error(expectedError))

        // when, then
        testEffect(
            viewModel = viewModel,
            intents = listOf(
                ReportContract.UiEvent.SelectReportReason(
                    reportReason = TestReportReasons.reasons.first().toUiModel(),
                ),
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
                // 신고 사유 선택 없이 신고 시
                ReportContract.UiEvent.OnReport(null),
            ),
            assertions = listOf(
                ReportContract.UiEffect.CloseReportModal,
            ),
        )
    }

    @Test
    fun `키워드 내용을 신고하려는 경우 정상적으로 신고를 수행한다`() = runTest {
        // given
        val userKeywordId = UserKeywordId(1L)
        every {
            reportUseCase(TEST_REPORTED_ID, userKeywordId.value, any(), any())
        } returns flowOf(Result.Success(Unit))
        every {
            getReportReasonsUseCase()
        } returns flowOf(Result.Success(TestReportReasons))
        savedStateHandle = SavedStateHandle(
            mapOf(
                "userId" to TEST_REPORTED_ID,
                "userKeywordId" to userKeywordId.value,
            ),
        )
        viewModel = ReportViewModel(
            reportUseCase = reportUseCase,
            getReportReasonsUseCase = getReportReasonsUseCase,
            savedStateHandle = savedStateHandle,
        )

        // when: 신고 사유를 선택하고 사용자 키워드 ID와 함께 신고 수행
        // then: 로딩 상태를 초기화하고 신고 결과 화면으로 이동하는 일회성 이벤트를 발행한다.
        testAll(
            viewModel = viewModel,
            intents = listOf(
                ReportContract.UiEvent.SelectReportReason(
                    reportReason = TestReportReasons.reasons.first().toUiModel(),
                ),
                ReportContract.UiEvent.OnReport(null),
            ),
            assertionStates = listOf(
                ReportContract.UiState(loading = false),
            ),
            assertionEffects = listOf(
                ReportContract.UiEffect.NavigateToReportResult,
            ),
        )
    }

    @Test
    fun `이전 백스택에서 넘어온 인자 값이 모두 null이면 화면을 닫는 일회성 이벤트를 발행한다`() = runTest {
        // given
        savedStateHandle = SavedStateHandle()
        viewModel = ReportViewModel(
            reportUseCase = reportUseCase,
            getReportReasonsUseCase = getReportReasonsUseCase,
            savedStateHandle = savedStateHandle,
        )

        // when, then
        testEffect(
            viewModel = viewModel,
            intents = listOf(),
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
