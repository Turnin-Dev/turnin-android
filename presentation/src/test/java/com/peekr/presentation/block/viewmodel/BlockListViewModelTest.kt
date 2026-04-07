package com.peekr.presentation.block.viewmodel

import androidx.paging.PagingData
import com.peekr.core.domain.block.model.BlockedUser
import com.peekr.core.domain.common.Result
import com.peekr.core.domain.model.BlockId
import com.peekr.core.domain.model.DisplayId
import com.peekr.core.domain.model.Name
import com.peekr.core.domain.model.UserId
import com.peekr.core.presentation.FakeSnackbarController
import com.peekr.core.presentation.MainDispatcherRule
import com.peekr.core.presentation.common.snackbar.SnackbarEvent
import com.peekr.domain.block.error.BlockErrorType
import com.peekr.domain.block.usecase.DeleteBlockUseCase
import com.peekr.domain.block.usecase.GetBlockedUsersUseCase
import com.peekr.presentation.block.error.asUiText
import com.peekr.presentation.block.model.toUiModel
import com.peekr.presentation.util.MockLog
import com.peekr.presentation.util.collectDataForTest
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
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class BlockListViewModelTest {
    @get:Rule
    val dispatcherRule = MainDispatcherRule()

    private val getBlockedUsersUseCase: GetBlockedUsersUseCase = mockk()
    private val deleteBlockUseCase: DeleteBlockUseCase = mockk()
    private val snackbarController = FakeSnackbarController()
    private lateinit var viewModel: BlockListViewModel

    @Before
    fun setUp() {
        MockLog.mock()
    }

    @After
    fun teardown() {
        MockLog.cleanUp()
    }

    @Test
    fun `차단 사용자 목록 초기 페이징 데이터 로드 성공 테스트`() = runTest {
        every { getBlockedUsersUseCase() } returns TestPagingDataFlow
        viewModel =
            BlockListViewModel(getBlockedUsersUseCase, deleteBlockUseCase, snackbarController)

        val expectedPagingData = TestPagingDataFlow.first()
        val expectedList = expectedPagingData.collectDataForTest(
            dispatcherRule.testDispatcher,
            dispatcherRule.testDispatcher,
        )
            .map { it.toUiModel() }

        val actualPagingData = viewModel.blockedUsersPagingData.first()
        val actualList = actualPagingData.collectDataForTest(
            dispatcherRule.testDispatcher,
            dispatcherRule.testDispatcher,
        )

        assertEquals(expectedList.size, actualList.size)
        assertEquals(expectedList, actualList)
    }

    @Test
    fun `차단 사용자 목록 페이지네이션 과정에서 예외 발생 시 빈 페이징 데이터를 반환한다`() = runTest {
        // given
        every { getBlockedUsersUseCase() } returns flow {
            throw Exception("Test exception")
        }
        viewModel =
            BlockListViewModel(getBlockedUsersUseCase, deleteBlockUseCase, snackbarController)

        // when
        val pagingData = viewModel.blockedUsersPagingData.first()
        val actualList = pagingData.collectDataForTest(
            dispatcherRule.testDispatcher,
            dispatcherRule.testDispatcher,
        )

        // then
        assertEquals(0, actualList.size)
    }

    @Test
    fun `차단 해제 성공 시 페이징 데이터에서 해당 데이터를 제외한다`() = runTest {
        // given
        every { getBlockedUsersUseCase() } returns TestPagingDataFlow
        every { deleteBlockUseCase(any(), any()) } returns flowOf(Result.Success(Unit))
        viewModel =
            BlockListViewModel(getBlockedUsersUseCase, deleteBlockUseCase, snackbarController)

        val expectedPagingData = TestPagingDataFlow.first()
        val expectedList = expectedPagingData.collectDataForTest(
            dispatcherRule.testDispatcher,
            dispatcherRule.testDispatcher,
        )
            .map { it.toUiModel() }

        // when: 차단 ID가 1인 데이터로 차단 해제 수행
        viewModel.unblock(1L, 1L)
        advanceUntilIdle()
        val actualPagingData = viewModel.blockedUsersPagingData.first()
        val actualList = actualPagingData.collectDataForTest(
            dispatcherRule.testDispatcher,
            dispatcherRule.testDispatcher,
        )

        // then: 페이징 데이터에서 차단 ID가 1인 데이터를 제외한 나머지 데이터가 반환된다.
        assertEquals(expectedList.size - 1, actualList.size)
        assertEquals(expectedList.filter { it.id != 1L }, actualList)
    }

    @Test
    fun `차단 해제 실패 시 스낵바를 표시한다`() = runTest {
        // given
        val expectedError = BlockErrorType.Unexpected(null)
        every { getBlockedUsersUseCase() } returns TestPagingDataFlow
        every { deleteBlockUseCase(any(), any()) } returns flowOf(Result.Error(expectedError))
        viewModel =
            BlockListViewModel(getBlockedUsersUseCase, deleteBlockUseCase, snackbarController)

        val snackbarList = mutableListOf<SnackbarEvent>()
        val snackbarJob = launch {
            snackbarController.events.toList(snackbarList)
        }

        // when: 차단 해제 수행
        viewModel.unblock(1L, 1L)
        advanceUntilIdle()

        // then: 스낵바 검증
        assertEquals(expectedError.asUiText(), snackbarList.last().message)

        // clean up
        snackbarJob.cancel()
    }

    companion object {
        private const val TEST_LIST_SIZE = 10
        private val TestPagingDataFlow = flowOf(
            PagingData.from(
                List(TEST_LIST_SIZE) {
                    val id = it + 1L
                    BlockedUser(
                        id = BlockId(id),
                        userId = UserId(id),
                        displayId = DisplayId("did$id"),
                        name = Name("name$id"),
                        profileImageUrl = null,
                    )
                },
            ),
        )
    }
}
