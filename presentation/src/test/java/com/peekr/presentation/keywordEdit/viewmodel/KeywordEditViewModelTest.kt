package com.peekr.presentation.keywordEdit.viewmodel

import com.peekr.core.domain.common.Result
import com.peekr.core.domain.common.validation.ValidationResult
import com.peekr.core.domain.model.KeywordDescription
import com.peekr.core.domain.model.KeywordId
import com.peekr.core.domain.model.KeywordName
import com.peekr.core.domain.model.UserId
import com.peekr.core.domain.model.UserKeywordId
import com.peekr.core.domain.userKeyword.model.UserKeyword
import com.peekr.core.presentation.MVIBaseViewModelTest
import com.peekr.core.presentation.ui.component.snackbar.SnackbarController
import com.peekr.core.presentation.ui.component.snackbar.SnackbarEvent
import com.peekr.domain.keywordEdit.error.KeywordEditErrorType
import com.peekr.domain.keywordEdit.usecase.AddUserKeywordUseCase
import com.peekr.domain.keywordEdit.usecase.ValidateKeywordUseCase
import com.peekr.presentation.keywordEdit.error.asUiText
import com.peekr.presentation.keywordEdit.state.KeywordEditContract
import com.peekr.presentation.profile.state.KeywordTextFieldState
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class KeywordEditViewModelTest : MVIBaseViewModelTest<
    KeywordEditContract.UiState,
    KeywordEditContract.UiEvent,
    KeywordEditContract.UiEffect,
    KeywordEditViewModel,
>() {
    private val addUserKeywordUseCase: AddUserKeywordUseCase = mockk()
    private val validateKeywordUseCase: ValidateKeywordUseCase = mockk()
    private lateinit var viewModel: KeywordEditViewModel

    @Before
    fun setUp() {
        SnackbarController.reset()

        every {
            addUserKeywordUseCase(any(), any())
        } returns flowOf(Result.Success(TestUserKeyword))
        every {
            validateKeywordUseCase(any())
        } returns ValidationResult.Valid("")

        viewModel = KeywordEditViewModel(
            addUserKeywordUseCase,
            validateKeywordUseCase,
        )
    }

    @Test
    fun `키워드 입력 유효성 검사 - 성공 시 정상적으로 상태를 업데이트한다`() {
        val expectedKeyword = "hello"
        every {
            validateKeywordUseCase(any())
        } returns ValidationResult.Valid(expectedKeyword)
        viewModel = KeywordEditViewModel(
            addUserKeywordUseCase,
            validateKeywordUseCase,
        )

        testState(
            viewModel = viewModel,
            intents = listOf(
                KeywordEditContract.UiEvent.OnKeywordChanged(
                    value = expectedKeyword,
                ),
            ),
            assertions = listOf(
                KeywordEditContract.UiState(
                    keyword = KeywordTextFieldState(
                        value = expectedKeyword,
                        error = null,
                    ),
                ),
            ),
        )
    }

    @Test
    fun `키워드 추가 - 성공 시 화면을 닫는 일회성 이벤트를 발행한다`() = runTest {
        // given
        val snackbarJob = launch {
            SnackbarController.events.collect {}
        }

        // when, then
        testEffect(
            viewModel = viewModel,
            intents = listOf(
                KeywordEditContract.UiEvent.AddKeyword,
            ),
            assertions = listOf(
                KeywordEditContract.UiEffect.CloseScreen,
            ),
        )

        // clean up
        snackbarJob.cancel()
    }

    @Test
    fun `키워드 추가 - 성공 시 로딩, 에러 상태를 초기화한다`() = runTest {
        // given
        val snackbarJob = launch {
            SnackbarController.events.collect {}
        }

        // when, then
        testState(
            viewModel = viewModel,
            intents = listOf(
                KeywordEditContract.UiEvent.AddKeyword,
            ),
            assertions = listOf(
                KeywordEditContract.UiState(
                    loading = false,
                    error = null,
                ),
            ),
        )

        // clean up
        snackbarJob.cancel()
    }

    @Test
    fun `키워드 추가 - 이벤트 발생 후 실패 시 에러 상태를 업데이트하고 스낵바를 표시한다`() = runTest {
        // given
        val expectedError = KeywordEditErrorType.Unexpected(null)
        every {
            addUserKeywordUseCase(any(), any())
        } returns flowOf(Result.Error(expectedError))
        viewModel = KeywordEditViewModel(
            addUserKeywordUseCase,
            validateKeywordUseCase,
        )

        val snackbarEvents = mutableListOf<SnackbarEvent>()
        val snackbarJob = launch {
            SnackbarController.events.toList(snackbarEvents)
        }

        // when, then
        testState(
            viewModel = viewModel,
            intents = listOf(
                KeywordEditContract.UiEvent.AddKeyword,
            ),
            assertions = listOf(
                KeywordEditContract.UiState(
                    error = expectedError.asUiText(),
                ),
            ),
        )

        // then
        assertTrue(snackbarEvents.isNotEmpty())

        // clean up
        snackbarJob.cancel()
    }

    companion object {
        private val TestMyUserId = UserId(1L)
        private val TestUserKeyword = UserKeyword(
            id = UserKeywordId(1L),
            keywordId = KeywordId(1L),
            keyword = KeywordName("key"),
            userId = TestMyUserId,
            description = KeywordDescription("hello"),
            createdAt = 1000,
            updatedAt = 1000,
        )
    }
}
