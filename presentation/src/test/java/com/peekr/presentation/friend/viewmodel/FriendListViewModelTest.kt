package com.peekr.presentation.friend.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import com.peekr.core.domain.common.Result
import com.peekr.core.domain.friend.model.FriendId
import com.peekr.core.domain.friend.model.FriendInfo
import com.peekr.core.domain.friend.model.FriendStatus
import com.peekr.core.domain.friend.model.IncomingRequest
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
import io.mockk.coEvery
import io.mockk.coVerify
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
        setMocking(myUserId = 1L, currentUserId = 1L)
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
    fun `나의 사용자 ID 로드 실패 테스트`() = runTest {
        // given
        setMocking(myUserId = 1L, currentUserId = 1L)
        coEvery { getMyUserIdUseCase() } returns null
        val snackbarList = mutableListOf<SnackbarEvent>()
        val snackbarJob = launch {
            snackbarController.events.toList(snackbarList)
        }

        // when
        viewModel = FriendListViewModel(
            usecases = usecases,
            getMyUserIdUseCase = getMyUserIdUseCase,
            snackbarController = snackbarController,
            savedStateHandle = savedStateHandle,
        )

        advanceUntilIdle()

        // then
        assertEquals(
            FriendErrorType.MyUserIdNotFound.asUiText(),
            snackbarList.last().message,
        )

        // clean up
        snackbarJob.cancel()
    }

    @Test
    fun `모든 초기 데이터 로드 성공 시 어떤 스낵바 이벤트도 발생하지 않아야 한다`() = runTest {
        // given
        setMocking(myUserId = 1L, currentUserId = 1L)
        val snackbarList = mutableListOf<SnackbarEvent>()
        val snackbarJob = launch {
            snackbarController.events.toList(snackbarList)
        }

        // when
        viewModel = FriendListViewModel(
            usecases = usecases,
            getMyUserIdUseCase = getMyUserIdUseCase,
            snackbarController = snackbarController,
            savedStateHandle = savedStateHandle,
        )

        advanceUntilIdle()

        // then
        assertTrue(snackbarList.isEmpty())

        // clean up
        snackbarJob.cancel()
    }

    @Test
    fun `친구 목록 초기 페이징 데이터 로드 성공 테스트`() = runTest(dispatcherRule.testDispatcher) {
        setMocking(myUserId = 1L, currentUserId = 1L)
        val expectedPagingData = TestFriendsPagingDataFlow.first()
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
    fun `친구 요청 목록 페이징 데이터는 초기 로드 트리거 시 정상적으로 로드된다`() = runTest(dispatcherRule.testDispatcher) {
        setMocking(myUserId = 1L, currentUserId = 1L)
        viewModel.initRequestersPagingData()

        val expectedPagingData = TestRequestersPagingDataFlow.first()
        val expectedList = expectedPagingData.collectDataForTest(
            dispatcherRule.testDispatcher,
            dispatcherRule.testDispatcher,
        )
            .map { it.toUiModel() }
        val actualPagingData = viewModel.requestersPagingData.first()
        val actualList = actualPagingData.collectDataForTest(
            dispatcherRule.testDispatcher,
            dispatcherRule.testDispatcher,
        )

        assertEquals(expectedList.size, actualList.size)
        assertEquals(expectedList, actualList)
    }

    @Test
    fun `친구 목록 페이지네이션 과정에서 예외 발생 시 빈 페이징 데이터를 반환한다`() = runTest(dispatcherRule.testDispatcher) {
        // given
        setMocking(myUserId = 1L, currentUserId = 1L)
        every {
            usecases.getFriends(1L)
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

    @Test
    fun `친구 요청 목록 페이지네이션 과정에서 예외 발생 시 빈 페이징 데이터를 반환한다`() = runTest(dispatcherRule.testDispatcher) {
        // given
        setMocking(myUserId = 1L, currentUserId = 1L)
        every {
            usecases.getIncomingRequests()
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
        viewModel.initRequestersPagingData()
        val pagingData = viewModel.requestersPagingData.first()
        val actualList = pagingData.collectDataForTest(
            dispatcherRule.testDispatcher,
            dispatcherRule.testDispatcher,
        )

        // then
        assertTrue(actualList.isEmpty())
    }

    @Test
    fun `친구 요청 수락 수행 조건 위반 테스트 - 나의 사용자 ID가 null인 경우 아무 작업도 수행하지 않는다`() = runTest {
        // given
        val targetId = 100L
        setMocking(myUserId = 1L, currentUserId = 1L)
        coEvery { getMyUserIdUseCase() } returns null
        coEvery {
            usecases.acceptFriendRequest(1L, targetId)
        } returns flowOf(Result.Success(Unit))
        viewModel = FriendListViewModel(
            usecases = usecases,
            getMyUserIdUseCase = getMyUserIdUseCase,
            snackbarController = snackbarController,
            savedStateHandle = savedStateHandle,
        )

        // when
        viewModel.acceptFriendRequest(targetId, FriendStatus.RECEIVED)

        // then
        coVerify(exactly = 0) {
            usecases.acceptFriendRequest(myUserId = 1L, targetUserId = targetId)
        }
    }

    @Test
    fun `친구 요청 수락 수행 조건 위반 테스트 - 현재 친구 상태가 '친구'인 경우 아무 작업도 수행하지 않는다`() = runTest {
        // given
        val targetId = 100L
        setMocking(myUserId = 1L, currentUserId = 1L)
        coEvery {
            usecases.acceptFriendRequest(1L, targetId)
        } returns flowOf(Result.Success(Unit))
        viewModel = FriendListViewModel(
            usecases = usecases,
            getMyUserIdUseCase = getMyUserIdUseCase,
            snackbarController = snackbarController,
            savedStateHandle = savedStateHandle,
        )

        // when
        viewModel.acceptFriendRequest(targetId, FriendStatus.FRIENDS)

        // then
        coVerify(exactly = 0) {
            usecases.acceptFriendRequest(myUserId = 1L, targetUserId = targetId)
        }
    }

    @Test
    fun `친구 요청 수락 수행 조건 위반 테스트 - 나의 사용자 ID와 현재 사용자 ID가 다른 경우 아무 작업도 수행하지 않는다`() = runTest {
        // given
        val targetId = 100L
        setMocking(myUserId = 2L, currentUserId = 1L)
        coEvery {
            usecases.acceptFriendRequest(any(), any())
        } returns flowOf(Result.Success(Unit))
        viewModel = FriendListViewModel(
            usecases = usecases,
            getMyUserIdUseCase = getMyUserIdUseCase,
            snackbarController = snackbarController,
            savedStateHandle = savedStateHandle,
        )

        // when
        viewModel.acceptFriendRequest(targetId, FriendStatus.RECEIVED)

        // then
        coVerify(exactly = 0) {
            usecases.acceptFriendRequest(any(), any())
        }
    }

    @Test
    fun `친구 요청 수락 수행 조건을 모두 만족하는 경우 정상적으로 수락 처리를 하고 requestersStatus를 업데이트한다`() = runTest {
        // given
        val targetId = 100L
        setMocking(myUserId = 1L, currentUserId = 1L)
        coEvery {
            usecases.acceptFriendRequest(myUserId = 1L, targetUserId = targetId)
        } returns flowOf(Result.Success(Unit))

        val requestersStatusList = mutableListOf<Map<Long, FriendStatus>>()
        val job = launch {
            viewModel.requesterStatus.toList(requestersStatusList)
        }

        // when: 친구 요청 수락 수행
        viewModel.acceptFriendRequest(targetUserId = targetId, FriendStatus.RECEIVED)

        advanceUntilIdle()

        // then: 친구 요청 수락이 성공했으므로 requestersStatus에 '친구' 상태로 값이 존재해야 한다.
        assertEquals(FriendStatus.FRIENDS, requestersStatusList.first()[targetId])

        // clean up
        job.cancel()
    }

    @Test
    fun `친구 요청 수락 수행 중 에러가 발생하면 스낵바를 표시하고 requestersStatus 상태를 롤백한다`() = runTest {
        // given
        setMocking(myUserId = 1L, currentUserId = 1L)

        val targetId = 100L
        val expectedError = FriendErrorType.Unexpected(null)

        coEvery {
            usecases.acceptFriendRequest(myUserId = 1L, targetUserId = targetId)
        } returns flowOf(Result.Error(expectedError))

        val requestersStatusList = mutableListOf<Map<Long, FriendStatus>>()
        val snackbarList = mutableListOf<SnackbarEvent>()
        val job = viewModel.viewModelScope.launch {
            launch { viewModel.requesterStatus.toList(requestersStatusList) }
            launch { snackbarController.events.toList(snackbarList) }
        }

        // when: 친구 요청 수락 수행
        viewModel.acceptFriendRequest(targetUserId = targetId, FriendStatus.RECEIVED)

        advanceUntilIdle()

        // then: 친구 요청 수락이 실패했으므로 친구 상태는 그대로여야 하고 스낵바가 표시된다.
        assertEquals(FriendStatus.RECEIVED, requestersStatusList.last()[targetId])
        assertEquals(expectedError.asUiText(), snackbarList.last().message)

        // clean up
        job.cancel()
    }

    private fun setMocking(
        myUserId: Long,
        currentUserId: Long,
    ) {
        val myUserIdVO = UserId(myUserId)
        val currentUserIdVO = UserId(currentUserId)
        every { usecases.getFriends(currentUserIdVO.value) } returns TestFriendsPagingDataFlow
        every { usecases.getIncomingRequests() } returns TestRequestersPagingDataFlow
        coEvery { getMyUserIdUseCase() } returns myUserIdVO
        savedStateHandle = SavedStateHandle(
            mapOf("userId" to currentUserIdVO.value),
        )

        viewModel = FriendListViewModel(
            usecases = usecases,
            getMyUserIdUseCase = getMyUserIdUseCase,
            snackbarController = snackbarController,
            savedStateHandle = savedStateHandle,
        )
    }

    companion object {
        private const val TEST_LIST_SIZE = 10
        private val TestFriendsPagingDataFlow = flowOf(
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
        private val TestRequestersPagingDataFlow = flowOf(
            PagingData.from(
                List(TEST_LIST_SIZE) {
                    IncomingRequest(
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
