package com.turnin.presentation.keywordEdit.viewmodel

import androidx.lifecycle.SavedStateHandle
import com.turnin.core.domain.common.Result
import com.turnin.core.domain.common.validation.ValidationResult
import com.turnin.core.domain.model.KeywordDescription
import com.turnin.core.domain.model.KeywordId
import com.turnin.core.domain.model.KeywordName
import com.turnin.core.domain.model.Name
import com.turnin.core.domain.model.UserId
import com.turnin.core.domain.model.UserKeywordId
import com.turnin.core.domain.userKeyword.model.UserInfo
import com.turnin.core.domain.userKeyword.model.UserKeyword
import com.turnin.core.domain.userKeyword.model.UserKeywordDetail
import com.turnin.core.presentation.FakeSnackbarController
import com.turnin.core.presentation.MVIBaseViewModelTest
import com.turnin.core.presentation.common.snackbar.SnackbarEvent
import com.turnin.core.presentation.ui.util.UiText
import com.turnin.domain.keywordEdit.error.KeywordEditErrorType
import com.turnin.domain.keywordEdit.usecase.KeywordEditUseCases
import com.turnin.presentation.R
import com.turnin.presentation.keywordEdit.error.asUiText
import com.turnin.presentation.keywordEdit.state.KeywordEditContract
import com.turnin.presentation.profile.state.KeywordTextFieldState
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class KeywordEditViewModelTest : MVIBaseViewModelTest<
    KeywordEditContract.UiState,
    KeywordEditContract.UiEvent,
    KeywordEditContract.UiEffect,
    KeywordEditViewModel,
>() {
    private val snackbarController = FakeSnackbarController()
    private val usecases: KeywordEditUseCases = mockk()
    private lateinit var viewModel: KeywordEditViewModel

    @Before
    fun setUp() {
        every {
            usecases.validateKeyword(any())
        } returns ValidationResult.Valid("")
    }

    @Test
    fun `키워드 입력 유효성 검사 - 성공 시 정상적으로 상태를 업데이트한다`() {
        val expectedKeyword = "hello"
        every {
            usecases.validateKeyword(any())
        } returns ValidationResult.Valid(expectedKeyword)
        viewModel = KeywordEditViewModel(
            snackbarController,
            usecases,
            TestSavedStateHandle,
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
    fun `키워드 추가 - 성공 시 상태를 초기화하고 화면을 닫는 일회성 이벤트를 발행한다`() = runTest {
        // given
        every {
            usecases.add(any(), any())
        } returns flowOf(Result.Success(TestUserKeyword))
        viewModel = KeywordEditViewModel(
            snackbarController,
            usecases,
            TestSavedStateHandle,
        )
        val snackbarList = mutableListOf<SnackbarEvent>()
        val snackbarJob = launch {
            snackbarController.events.toList(snackbarList)
        }

        // when, then
        testAll(
            viewModel = viewModel,
            intents = listOf(
                KeywordEditContract.UiEvent.AddOrUpdateKeyword,
            ),
            assertionStates = listOf(
                KeywordEditContract.UiState(loading = false),
            ),
            assertionEffects = listOf(
                KeywordEditContract.UiEffect.CloseScreen,
            ),
        )

        // then: 스낵바 이벤트 검증
        assertEquals(
            UiText.StringResource(R.string.keyword_edit_success_add_keyword),
            snackbarList.last().message,
        )

        // clean up
        snackbarJob.cancel()
    }

    @Test
    fun `키워드 추가 - 실패 시 상태를 초기화하고 스낵바를 표시한다`() = runTest {
        // given
        val expectedError = KeywordEditErrorType.Unexpected(null)
        every {
            usecases.add(any(), any())
        } returns flowOf(Result.Error(expectedError))
        viewModel = KeywordEditViewModel(
            snackbarController,
            usecases,
            TestSavedStateHandle,
        )

        val snackbarList = mutableListOf<SnackbarEvent>()
        val snackbarJob = launch {
            snackbarController.events.toList(snackbarList)
        }

        // when, then
        testState(
            viewModel = viewModel,
            intents = listOf(
                KeywordEditContract.UiEvent.AddOrUpdateKeyword,
            ),
            assertions = listOf(
                KeywordEditContract.UiState(loading = false),
            ),
        )

        // then
        assertEquals(expectedError.asUiText(), snackbarList.last().message)

        // clean up
        snackbarJob.cancel()
    }

    @Test
    fun `나의 키워드인 경우 초기 데이터를 로드한다`() = runTest {
        // given
        every {
            usecases.getMyKeyword(TestMyUserId.value)
        } returns flowOf(TestUserKeywordDetail)
        viewModel = KeywordEditViewModel(
            snackbarController,
            usecases,
            TestMySavedStateHandle,
        )

        // when, then
        testState(
            viewModel = viewModel,
            intents = listOf(),
            assertions = listOf(
                KeywordEditContract.UiState(
                    keyword = KeywordTextFieldState(value = TestUserKeywordDetail.keywordName.value),
                    description = TestUserKeywordDetail.description.value,
                ),
            ),
        )
    }

    @Test
    fun `나의 키워드 수정 - 수정 시 필요한 데이터가 null인 경우 아무 작업도 하지 않는다`() = runTest {
        // given
        every {
            usecases.getMyKeyword(TestMyUserId.value)
        } returns flowOf(null)
        viewModel = KeywordEditViewModel(
            snackbarController,
            usecases,
            TestMySavedStateHandle,
        )

        // when, then
        testState(
            viewModel = viewModel,
            intents = listOf(
                KeywordEditContract.UiEvent.AddOrUpdateKeyword,
            ),
            assertions = listOf(
                KeywordEditContract.UiState(),
            ),
        )
    }

    @Test
    fun `나의 키워드 수정 - 수정 시 이전 키워드와 내용이 같다면 그냥 화면을 닫는다`() = runTest {
        // given
        every {
            usecases.getMyKeyword(TestMyUserId.value)
        } returns flowOf(TestUserKeywordDetail)
        viewModel = KeywordEditViewModel(
            snackbarController,
            usecases,
            TestMySavedStateHandle,
        )

        // when: 이전 값과 같은 값이거나 변경하지 않은 값이 있는 상태에서 수정을 요청한다.
        // then: 화면을 닫는 일회성 이벤트를 발행한다.
        testEffect(
            viewModel = viewModel,
            intents = listOf(
                KeywordEditContract.UiEvent.OnKeywordChanged(
                    value = TestUserKeywordDetail.keywordName.value,
                ),
                KeywordEditContract.UiEvent.AddOrUpdateKeyword,
            ),
            assertions = listOf(
                KeywordEditContract.UiEffect.CloseScreen,
            ),
        )
    }

    @Test
    fun `나의 키워드 수정 - 성공 시 상태를 초기화하고 스낵바 표시 후 화면을 닫는 일회성 이벤트를 발행한다`() = runTest {
        // given
        every {
            usecases.getMyKeyword(TestMyUserId.value)
        } returns flowOf(TestUserKeywordDetail)
        every {
            usecases.update(any(), any(), any())
        } returns flowOf(Result.Success(Unit))
        viewModel = KeywordEditViewModel(
            snackbarController,
            usecases,
            TestMySavedStateHandle,
        )
        val snackbarList = mutableListOf<SnackbarEvent>()
        val snackbarJob = launch {
            snackbarController.events.toList(snackbarList)
        }

        // when: 새로운 내용으로 변경 후 수정 요청을 한다.
        // then: 새로운 내용으로 상태 업데이트가 이뤄지고 화면을 닫는 일회성 이벤트를 발행한다
        testAll(
            viewModel = viewModel,
            intents = listOf(
                KeywordEditContract.UiEvent.OnDescriptionChanged("newDescription"),
                KeywordEditContract.UiEvent.AddOrUpdateKeyword,
            ),
            assertionStates = listOf(
                KeywordEditContract.UiState(
                    loading = false,
                    keyword = KeywordTextFieldState(value = TestUserKeywordDetail.keywordName.value),
                    description = "newDescription",
                ),
            ),
            assertionEffects = listOf(
                KeywordEditContract.UiEffect.CloseScreen,
            ),
        )

        // then: 스낵바 이벤트 검증
        assertEquals(
            UiText.StringResource(R.string.keyword_edit_success_update_keyword),
            snackbarList.last().message,
        )

        // clean up
        snackbarJob.cancel()
    }

    @Test
    fun `나의 키워드 수정 - 실패 시 상태를 초기화하고 스낵바를 표시한다`() = runTest {
        // given
        val expectedError = KeywordEditErrorType.Unexpected(null)
        every {
            usecases.getMyKeyword(TestMyUserId.value)
        } returns flowOf(TestUserKeywordDetail)
        every {
            usecases.update(any(), any(), any())
        } returns flowOf(Result.Error(expectedError))
        viewModel = KeywordEditViewModel(
            snackbarController,
            usecases,
            TestMySavedStateHandle,
        )
        val snackbarList = mutableListOf<SnackbarEvent>()
        val snackbarJob = launch {
            snackbarController.events.toList(snackbarList)
        }

        // when: 새로운 내용으로 변경 후 수정 요청을 한다.
        // then: 새로운 내용으로 상태 업데이트만 이뤄진다.
        testState(
            viewModel = viewModel,
            intents = listOf(
                KeywordEditContract.UiEvent.OnDescriptionChanged("newDescription"),
                KeywordEditContract.UiEvent.AddOrUpdateKeyword,
            ),
            assertions = listOf(
                KeywordEditContract.UiState(
                    loading = false,
                    keyword = KeywordTextFieldState(value = TestUserKeywordDetail.keywordName.value),
                    description = "newDescription",
                ),
            ),
        )

        // then: 스낵바 이벤트 검증
        assertEquals(expectedError.asUiText(), snackbarList.last().message)

        // clean up
        snackbarJob.cancel()
    }

    @Test
    fun `키워드나 내용 중 내용이 비어있지 않는 경우 SafeCancel 일회성 이벤트를 발행한다`() = runTest {
        viewModel = KeywordEditViewModel(
            snackbarController,
            usecases,
            TestSavedStateHandle,
        )

        testEffect(
            viewModel = viewModel,
            intents = listOf(
                KeywordEditContract.UiEvent.OnDescriptionChanged("hello"),
                KeywordEditContract.UiEvent.SafeBackPressed,
            ),
            assertions = listOf(
                KeywordEditContract.UiEffect.OpenSafeCancelModal,
            ),
        )
    }

    @Test
    fun `키워드, 내용 전부 내용이 비어있는 경우 화면을 닫는 일회성 이벤트를 발행한다`() = runTest {
        viewModel = KeywordEditViewModel(
            snackbarController,
            usecases,
            TestSavedStateHandle,
        )

        testEffect(
            viewModel = viewModel,
            intents = listOf(
                KeywordEditContract.UiEvent.SafeBackPressed,
            ),
            assertions = listOf(
                KeywordEditContract.UiEffect.CloseScreen,
            ),
        )
    }

    companion object {
        private val TestMyUserId = UserId(1L)
        private val TestUserKeyword = UserKeyword(
            id = UserKeywordId(1L),
            keywordId = KeywordId(1L),
            keyword = KeywordName("key"),
            description = KeywordDescription("hello"),
            createdAt = 1000,
            updatedAt = 1000,
        )
        private val TestMySavedStateHandle = SavedStateHandle(
            mapOf("userKeywordId" to 1L),
        )
        private val TestSavedStateHandle = SavedStateHandle()
        private val TestUserKeywordDetail = UserKeywordDetail(
            userKeywordId = UserKeywordId(1L),
            keywordId = KeywordId(1L),
            keywordName = KeywordName("key"),
            description = KeywordDescription("hello"),
            userInfo = UserInfo(
                userId = TestMyUserId,
                userName = Name("name"),
                profileImageUrl = null,
            ),
            createdAt = 1000,
            updatedAt = 1000,
        )
    }
}
