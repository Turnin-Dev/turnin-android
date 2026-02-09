package com.peekr.presentation.friend.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.paging.PagingData
import com.peekr.core.domain.friend.model.FriendId
import com.peekr.core.domain.friend.model.FriendInfo
import com.peekr.core.domain.model.DisplayId
import com.peekr.core.domain.model.Name
import com.peekr.core.domain.model.UserId
import com.peekr.core.domain.user.usecase.GetMyUserIdUseCase
import com.peekr.core.presentation.FakeSnackbarController
import com.peekr.core.presentation.MainDispatcherRule
import com.peekr.core.presentation.common.snackbar.SnackbarEvent
import com.peekr.domain.friend.error.FriendErrorType
import com.peekr.domain.friend.usecase.FriendUseCases
import com.peekr.presentation.friend.error.asUiText
import com.peekr.presentation.friend.model.toUiModel
import com.peekr.presentation.util.MockLog
import com.peekr.presentation.util.collectDataForTest
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class FriendListViewModelTest {
    @get:Rule
    val dispatcherRule = MainDispatcherRule()

    private val usecases: FriendUseCases = mockk()
    private val getMyUserIdUseCase: GetMyUserIdUseCase = mockk()
    private val snackbarController = FakeSnackbarController()
    private lateinit var savedStateHandle: SavedStateHandle
    private lateinit var viewModel: FriendListViewModel

    @Before
    fun setUp() {
        every {
            usecases.getFriends(TestUserId.value)
        } returns TestPagingDataFlow
        savedStateHandle = TestSavedStateHandle
        viewModel = FriendListViewModel(
            usecases = usecases,
            getMyUserIdUseCase = getMyUserIdUseCase,
            snackbarController = snackbarController,
            savedStateHandle = savedStateHandle,
        )
        // Paging 라이브러리 내부에서 발생하는 Log 호출 방지
        MockLog.mock()
    }

    @After
    fun teardown() {
        clearAllMocks()
        MockLog.cleanUp()
    }

    @Test
    fun `savedStateHandle 값 로드 실패 테스트`() = runTest {
        // given: 빈 SavedStateHandle로 테스트 구성
        savedStateHandle = SavedStateHandle()
        val snackbarList = mutableListOf<SnackbarEvent>()
        val snackbarJob = launch {
            snackbarController.events.toList(snackbarList)
        }

        // when: 뷰모델 생성
        viewModel = FriendListViewModel(
            usecases = usecases,
            getMyUserIdUseCase = getMyUserIdUseCase,
            snackbarController = snackbarController,
            savedStateHandle = savedStateHandle,
        )

        advanceUntilIdle()

        // then
        assertEquals(
            FriendErrorType.UserIdNotFound.asUiText(),
            snackbarList.last().message,
        )

        // clean up
        snackbarJob.cancel()
    }

    @Test
    fun `초기 페이지네이션 데이터 로드 성공 테스트`() = runTest(dispatcherRule.testDispatcher) {
        val expectedPagingData = TestPagingDataFlow.first()
        val expectedList = expectedPagingData.collectDataForTest(
            dispatcherRule.testDispatcher,
            dispatcherRule.testDispatcher,
        )
            .map { it.toUiModel() }
        val actualPagingData = viewModel.friendsPagingData.first()
        val actualList = actualPagingData.collectDataForTest(
            dispatcherRule.testDispatcher,
            dispatcherRule.testDispatcher,
        )

        assertEquals(expectedList.size, actualList.size)
        assertEquals(expectedList, actualList)
    }

    @Test
    fun `페이지네이션 과정에서 예외 발생 시 빈 페이징 데이터를 반환한다`() = runTest(dispatcherRule.testDispatcher) {
        // given
        every {
            usecases.getFriends(TestUserId.value)
        } returns flow {
            throw Exception("Test exception")
        }
        viewModel = FriendListViewModel(
            usecases = usecases,
            getMyUserIdUseCase = getMyUserIdUseCase,
            snackbarController = snackbarController,
            savedStateHandle = savedStateHandle,
        )

        // when
        val pagingData = viewModel.friendsPagingData.first()
        val actualList = pagingData.collectDataForTest(
            dispatcherRule.testDispatcher,
            dispatcherRule.testDispatcher,
        )

        // then
        assertTrue(actualList.isEmpty())
    }

    companion object {
        private val TestUserId = UserId(100L)
        private val TestSavedStateHandle = SavedStateHandle(
            mapOf("userId" to TestUserId.value),
        )
        private const val TEST_LIST_SIZE = 10
        private val TestPagingDataFlow = flowOf(
            PagingData.from(
                List(TEST_LIST_SIZE) {
                    FriendInfo(
                        id = FriendId(it.toLong()),
                        userId = UserId(it.toLong()),
                        displayId = DisplayId("$it"),
                        name = Name("$it"),
                        profileImageUrl = null,
                        respondedAt = 1000,
                        createdAt = 1000,
                        updatedAt = 1000,
                    )
                },
            ),
        )
    }
}
