package com.peekr.presentation.keywordDetail.viewmodel

import androidx.lifecycle.SavedStateHandle
import com.peekr.core.domain.common.Result
import com.peekr.core.domain.model.KeywordDescription
import com.peekr.core.domain.model.KeywordId
import com.peekr.core.domain.model.KeywordName
import com.peekr.core.domain.model.Name
import com.peekr.core.domain.model.UserId
import com.peekr.core.domain.model.UserKeywordId
import com.peekr.core.domain.user.usecase.GetUserIdUseCase
import com.peekr.core.presentation.FakeSnackbarController
import com.peekr.core.presentation.MVIBaseViewModelTest
import com.peekr.core.presentation.common.snackbar.SnackbarEvent
import com.peekr.core.presentation.ui.util.UiText
import com.peekr.domain.keywordDetail.error.KeywordDetailErrorType
import com.peekr.domain.keywordDetail.model.KeywordDetail
import com.peekr.domain.keywordDetail.usecase.KeywordDetailUseCases
import com.peekr.presentation.R
import com.peekr.presentation.keywordDetail.error.asUiText
import com.peekr.presentation.keywordDetail.model.toUiModel
import com.peekr.presentation.keywordDetail.state.KeywordDetailContract
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class KeywordDetailViewModelTest : MVIBaseViewModelTest<
    KeywordDetailContract.UiState,
    KeywordDetailContract.UiEvent,
    KeywordDetailContract.UiEffect,
    KeywordDetailViewModel,
>() {
    private lateinit var savedStateHandle: SavedStateHandle
    private lateinit var viewModel: KeywordDetailViewModel
    private val getUserIdUseCase: GetUserIdUseCase = mockk()
    private val usecase: KeywordDetailUseCases = mockk()
    private val snackbarController = FakeSnackbarController()

    @Before
    fun setUp() {
        // 나의 키워드인 경우를 테스트하기 위해 나의 UserId로 설정한다.
        coEvery { getUserIdUseCase() } returns TestMyUserId
    }

    // ------------------------------ 나의 키워드인 경우 ------------------------------

    @Test
    fun `나의 키워드인 경우 - 초기 데이터인 키워드 상세 정보를 정상적으로 조회한다`() {
        // given
        every {
            usecase.getKeywordDetail(TestMyUserId.value, TestMyUserKeywordId.value)
        } returns flowOf(Result.Success(TestMyKeywordDetail))
        savedStateHandle = TestMySavedStateHandle
        viewModel = KeywordDetailViewModel(
            usecase = usecase,
            getUserIdUseCase = getUserIdUseCase,
            snackbarController = snackbarController,
            savedStateHandle = savedStateHandle,
        )

        // when, then
        testState(
            viewModel = viewModel,
            intents = emptyList(),
            assertions = listOf(
                KeywordDetailContract.UiState(
                    myKeyword = true,
                    keywordDetail = TestMyKeywordDetail.toUiModel(),
                ),
            ),
        )
    }

    @Test
    fun `나의 키워드인 경우 - 초기 데이터 조회 시 에러가 발생하면 상태를 초기화하고 스낵바를 표시한다`() = runTest {
        // given
        val expectedError = KeywordDetailErrorType.UserIdNotFound
        every {
            usecase.getKeywordDetail(TestMyUserId.value, TestMyUserKeywordId.value)
        } returns flowOf(Result.Error(expectedError))
        savedStateHandle = TestMySavedStateHandle
        viewModel = KeywordDetailViewModel(
            usecase = usecase,
            getUserIdUseCase = getUserIdUseCase,
            snackbarController = snackbarController,
            savedStateHandle = savedStateHandle,
        )

        val snackbarList = mutableListOf<SnackbarEvent>()
        val snackbarJob = launch {
            snackbarController.events.toList(snackbarList)
        }

        // when, then
        testState(
            viewModel = viewModel,
            intents = emptyList(),
            assertions = listOf(
                KeywordDetailContract.UiState(
                    myKeyword = true,
                    loading = false,
                ),
            ),
        )

        // then: 스낵바 이벤트 검증
        assertTrue(snackbarList.isNotEmpty())
        assertEquals(expectedError.asUiText(), snackbarList.last().message)

        // clean up
        snackbarJob.cancel()
    }

    @Test
    fun `나의 키워드인 경우 - 삭제 성공 시 상태를 초기화하고 스낵바 표시 후 화면을 닫는 일회성 이벤트를 발행한다`() = runTest {
        // given
        every {
            usecase.getKeywordDetail(TestMyUserId.value, TestMyUserKeywordId.value)
        } returns flowOf(Result.Success(TestMyKeywordDetail))
        every {
            usecase.deleteKeyword(TestMyUserKeywordId.value)
        } returns flowOf(Result.Success(Unit))
        savedStateHandle = TestMySavedStateHandle
        viewModel = KeywordDetailViewModel(
            usecase = usecase,
            getUserIdUseCase = getUserIdUseCase,
            snackbarController = snackbarController,
            savedStateHandle = savedStateHandle,
        )

        val snackbarList = mutableListOf<SnackbarEvent>()
        val snackbarJob = launch {
            snackbarController.events.toList(snackbarList)
        }

        // when: 키워드 삭제를 진행한다.
        // then: 로딩 상태 초기화 및 화면을 닫는 일회성 이벤트가 발행된다.
        testAll(
            viewModel = viewModel,
            intents = listOf(
                KeywordDetailContract.UiEvent.OnDelete,
            ),
            assertionStates = listOf(
                KeywordDetailContract.UiState(
                    myKeyword = true,
                    fullScreenLoading = false,
                    keywordDetail = TestMyKeywordDetail.toUiModel(),
                ),
            ),
            assertionEffects = listOf(
                KeywordDetailContract.UiEffect.CloseScreen,
            ),
        )

        // then: 스낵바 이벤트 검증
        assertEquals(
            UiText.StringResource(R.string.keyword_detail_success_delete_keyword),
            snackbarList.last().message,
        )

        // clean up
        snackbarJob.cancel()
    }

    @Test
    fun `나의 키워드인 경우 - 삭제 실패 시 상태를 초기화하고 스낵바를 표시한다`() = runTest {
        // given
        val expectedError = KeywordDetailErrorType.Unexpected(null)
        every {
            usecase.getKeywordDetail(TestMyUserId.value, TestMyUserKeywordId.value)
        } returns flowOf(Result.Success(TestMyKeywordDetail))
        every {
            usecase.deleteKeyword(TestMyUserKeywordId.value)
        } returns flowOf(Result.Error(expectedError))
        savedStateHandle = TestMySavedStateHandle
        viewModel = KeywordDetailViewModel(
            usecase = usecase,
            getUserIdUseCase = getUserIdUseCase,
            snackbarController = snackbarController,
            savedStateHandle = savedStateHandle,
        )

        val snackbarList = mutableListOf<SnackbarEvent>()
        val snackbarJob = launch {
            snackbarController.events.toList(snackbarList)
        }

        // when: 키워드 삭제를 진행한다.
        // then: 로딩 상태 초기화를 초기화한다.
        testState(
            viewModel = viewModel,
            intents = listOf(
                KeywordDetailContract.UiEvent.OnDelete,
            ),
            assertions = listOf(
                KeywordDetailContract.UiState(
                    myKeyword = true,
                    fullScreenLoading = false,
                    keywordDetail = TestMyKeywordDetail.toUiModel(),
                ),
            ),
        )

        // then: 스낵바 이벤트 검증
        assertEquals(expectedError.asUiText(), snackbarList.last().message)

        // clean up
        snackbarJob.cancel()
    }

    // ------------------------------ 사용자 키워드인 경우 ------------------------------

    @Test
    fun `사용자 키워드인 경우 - 초기 데이터인 키워드 상세 정보를 정상적으로 조회한다`() {
        // given
        every {
            usecase.getKeywordDetail(TestUserId.value, TestUserKeywordId.value)
        } returns flowOf(Result.Success(TestKeywordDetail))
        savedStateHandle = TestSavedStateHandle
        viewModel = KeywordDetailViewModel(
            usecase = usecase,
            getUserIdUseCase = getUserIdUseCase,
            snackbarController = snackbarController,
            savedStateHandle = savedStateHandle,
        )

        // when, then
        testState(
            viewModel = viewModel,
            intents = emptyList(),
            assertions = listOf(
                KeywordDetailContract.UiState(
                    myKeyword = false,
                    keywordDetail = TestKeywordDetail.toUiModel(),
                ),
            ),
        )
    }

    @Test
    fun `사용자 키워드인 경우 - 초기 데이터 조회 시 에러가 발생하면 상태를 초기화하고 스낵바를 표시한다`() = runTest {
        // given
        val expectedError = KeywordDetailErrorType.UserIdNotFound
        every {
            usecase.getKeywordDetail(TestUserId.value, TestUserKeywordId.value)
        } returns flowOf(Result.Error(expectedError))
        savedStateHandle = TestSavedStateHandle
        viewModel = KeywordDetailViewModel(
            usecase = usecase,
            getUserIdUseCase = getUserIdUseCase,
            snackbarController = snackbarController,
            savedStateHandle = savedStateHandle,
        )

        val snackbarList = mutableListOf<SnackbarEvent>()
        val snackbarJob = launch {
            snackbarController.events.toList(snackbarList)
        }

        // when, then
        testState(
            viewModel = viewModel,
            intents = emptyList(),
            assertions = listOf(
                KeywordDetailContract.UiState(
                    myKeyword = false,
                    loading = false,
                ),
            ),
        )

        // then: 스낵바 이벤트 검증
        assertTrue(snackbarList.isNotEmpty())
        assertEquals(expectedError.asUiText(), snackbarList.last().message)

        // clean up
        snackbarJob.cancel()
    }

    companion object {
        // ------------------------------ 나의 키워드 테스트 데이터 ------------------------------
        private val TestMyUserId = UserId(1L)
        private val TestMyUserKeywordId = UserKeywordId(1L)
        private val TestMySavedStateHandle = SavedStateHandle(
            mapOf(
                "userId" to TestMyUserId.value,
                "userKeywordId" to TestMyUserKeywordId.value,
            ),
        )
        private val TestMyKeywordDetail = KeywordDetail(
            userKeywordId = TestMyUserKeywordId,
            keywordId = KeywordId(1L),
            keyword = KeywordName("k_name"),
            description = KeywordDescription("desc"),
            userId = TestMyUserId,
            userName = Name("name"),
            profileImageUrl = null,
            createdAt = 0L,
            updatedAt = 0L,
        )

        // ------------------------------ 사용자 키워드 테스트 데이터 ------------------------------
        private val TestUserId = UserId(100L)
        private val TestUserKeywordId = UserKeywordId(100L)
        private val TestSavedStateHandle = SavedStateHandle(
            mapOf(
                "userId" to TestUserId.value,
                "userKeywordId" to TestUserKeywordId.value,
            ),
        )
        private val TestKeywordDetail = KeywordDetail(
            userKeywordId = TestUserKeywordId,
            keywordId = KeywordId(1L),
            keyword = KeywordName("k_name"),
            description = KeywordDescription("desc"),
            userId = TestUserId,
            userName = Name("name"),
            profileImageUrl = null,
            createdAt = 0L,
            updatedAt = 0L,
        )
    }
}
