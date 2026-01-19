package com.peekr.presentation.keywordDetail.viewmodel

import androidx.lifecycle.SavedStateHandle
import com.peekr.core.domain.common.Result
import com.peekr.core.domain.model.KeywordDescription
import com.peekr.core.domain.model.KeywordDescription.Companion.invoke
import com.peekr.core.domain.model.KeywordId
import com.peekr.core.domain.model.KeywordId.Companion.invoke
import com.peekr.core.domain.model.KeywordName
import com.peekr.core.domain.model.KeywordName.Companion.invoke
import com.peekr.core.domain.model.Name
import com.peekr.core.domain.model.Name.Companion.invoke
import com.peekr.core.domain.model.UserId
import com.peekr.core.domain.model.UserKeywordId
import com.peekr.core.domain.user.usecase.GetUserIdUseCase
import com.peekr.core.presentation.FakeSnackbarController
import com.peekr.core.presentation.MVIBaseViewModelTest
import com.peekr.core.presentation.common.snackbar.SnackbarEvent
import com.peekr.domain.keywordDetail.error.KeywordDetailErrorType
import com.peekr.domain.keywordDetail.model.KeywordDetail
import com.peekr.domain.keywordDetail.usecase.KeywordDetailUseCases
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
        // 현재 사용자 ID는 나의 사용자 ID로 고정 해놓고 테스트 진행
        coEvery { getUserIdUseCase() } returns TestUserId
    }

    // ------------------------------ 나의 키워드인 경우 ------------------------------

    @Test
    fun `나의 키워드인 경우 - 초기 데이터인 키워드 상세 정보를 정상적으로 조회한다`() {
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
                    myKeyword = true,
                    keywordDetail = TestKeywordDetail.toUiModel(),
                ),
            ),
        )
    }

    @Test
    fun `나의 키워드인 경우 - 초기 데이터 조회 시 에러가 발생하면 정상적으로 에러를 표시한다`() = runTest {
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
                    myKeyword = true,
                    error = expectedError.asUiText(),
                ),
            ),
        )

        // then: 스낵바 이벤트 검증
        assertTrue(snackbarList.isNotEmpty())
        assertEquals(expectedError.asUiText(), snackbarList.last().message)

        // clean up
        snackbarJob.cancel()
    }

    // ------------------------------ 사용자 키워드인 경우 ------------------------------

    @Test
    fun `사용자 키워드인 경우 - 초기 데이터인 키워드 상세 정보를 정상적으로 조회한다`() {
        // given
        every {
            usecase.getKeywordDetail(TestOtherUserId.value, TestOtherUserKeywordId.value)
        } returns flowOf(Result.Success(TestOtherKeywordDetail))
        savedStateHandle = TestOtherSavedStateHandle
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
                    keywordDetail = TestOtherKeywordDetail.toUiModel(),
                ),
            ),
        )
    }

    @Test
    fun `사용자 키워드인 경우 - 초기 데이터 조회 시 에러가 발생하면 정상적으로 에러를 표시한다`() = runTest {
        // given
        val expectedError = KeywordDetailErrorType.UserIdNotFound
        every {
            usecase.getKeywordDetail(TestOtherUserId.value, TestOtherUserKeywordId.value)
        } returns flowOf(Result.Error(expectedError))
        savedStateHandle = TestOtherSavedStateHandle
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
                    error = expectedError.asUiText(),
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
        private val TestUserId = UserId(1L)
        private val TestUserKeywordId = UserKeywordId(1L)
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

        // ------------------------------ 사용자 키워드 테스트 데이터 ------------------------------
        private val TestOtherUserId = UserId(100L)
        private val TestOtherUserKeywordId = UserKeywordId(100L)
        private val TestOtherSavedStateHandle = SavedStateHandle(
            mapOf(
                "userId" to TestOtherUserId.value,
                "userKeywordId" to TestOtherUserKeywordId.value,
            ),
        )
        private val TestOtherKeywordDetail = KeywordDetail(
            userKeywordId = TestOtherUserKeywordId,
            keywordId = KeywordId(1L),
            keyword = KeywordName("k_name"),
            description = KeywordDescription("desc"),
            userId = TestOtherUserId,
            userName = Name("name"),
            profileImageUrl = null,
            createdAt = 0L,
            updatedAt = 0L,
        )
    }
}
