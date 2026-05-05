package com.turnin.presentation.friend.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import com.turnin.core.domain.common.Result
import com.turnin.core.domain.friend.model.FriendId
import com.turnin.core.domain.friend.model.FriendInfo
import com.turnin.core.domain.friend.model.FriendStatus
import com.turnin.core.domain.friend.model.IncomingRequest
import com.turnin.core.domain.model.DisplayId
import com.turnin.core.domain.model.Name
import com.turnin.core.domain.model.UserId
import com.turnin.core.presentation.FakeSnackbarController
import com.turnin.core.presentation.MainDispatcherRule
import com.turnin.core.presentation.common.navigation.args.UserProfileArgs
import com.turnin.core.presentation.common.snackbar.SnackbarEvent
import com.turnin.domain.friend.error.FriendErrorType
import com.turnin.domain.friend.usecase.FriendUseCases
import com.turnin.presentation.friend.error.asUiText
import com.turnin.presentation.friend.model.UiRequester
import com.turnin.presentation.friend.model.toUiFriendInfo
import com.turnin.presentation.friend.model.toUiModel
import com.turnin.presentation.friend.state.FriendEffect
import com.turnin.presentation.util.MockLog
import com.turnin.presentation.util.collectDataForTest
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
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class FriendListViewModelTest {
    @get:Rule
    val dispatcherRule = MainDispatcherRule()

    private val usecases: FriendUseCases = mockk()
    private val snackbarController = FakeSnackbarController()
    private lateinit var savedStateHandle: SavedStateHandle
    private lateinit var viewModel: FriendListViewModel

    @Before
    fun setUp() {
        MockLog.mock()
    }

    @After
    fun teardown() {
        clearAllMocks()
        MockLog.cleanUp()
    }

    // ------------------------------ 초기 데이터 로드 ------------------------------

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
            snackbarController = snackbarController,
            savedStateHandle = savedStateHandle,
        )
        advanceUntilIdle()

        // then: UserIdNotFound 스낵바가 표시된다
        assertEquals(
            FriendErrorType.UserIdNotFound.asUiText(),
            snackbarList.last().message,
        )

        snackbarJob.cancel()
    }

    @Test
    fun `나의 사용자 ID 로드 실패 테스트`() = runTest {
        // given: getMyUserId()가 null을 반환하도록 설정
        setMocking(myUserId = 1L, currentUserId = 1L)
        every { usecases.getMyUserId() } returns null
        val snackbarList = mutableListOf<SnackbarEvent>()
        val snackbarJob = launch {
            snackbarController.events.toList(snackbarList)
        }

        // when
        viewModel = FriendListViewModel(
            usecases = usecases,
            snackbarController = snackbarController,
            savedStateHandle = savedStateHandle,
        )
        advanceUntilIdle()

        // then: MyUserIdNotFound 스낵바가 표시된다
        assertEquals(
            FriendErrorType.MyUserIdNotFound.asUiText(),
            snackbarList.last().message,
        )

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
            snackbarController = snackbarController,
            savedStateHandle = savedStateHandle,
        )
        advanceUntilIdle()

        // then
        assertTrue(snackbarList.isEmpty())

        snackbarJob.cancel()
    }

    // ------------------------------ isMyFriendList ------------------------------

    @Test
    fun `나의 사용자 ID와 현재 사용자 ID가 같으면 isMyFriendList는 true이다`() = runTest {
        // given: myUserId == currentUserId
        setMocking(myUserId = 1L, currentUserId = 1L)

        // when
        advanceUntilIdle()

        // then
        assertTrue(viewModel.isMyFriendList.value)
    }

    @Test
    fun `나의 사용자 ID와 현재 사용자 ID가 다르면 isMyFriendList는 false이다`() = runTest {
        // given: myUserId != currentUserId
        setMocking(myUserId = 2L, currentUserId = 1L)

        // when
        advanceUntilIdle()

        // then
        assertFalse(viewModel.isMyFriendList.value)
    }

    // ------------------------------ 친구 목록 (friendsBasePagingData / friendsPagingData) ------------------------------

    @Test
    fun `friendsPagingData 초기 로드 성공 테스트`() = runTest(dispatcherRule.testDispatcher) {
        // given
        setMocking(myUserId = 1L, currentUserId = 1L)
        val expectedList = TestFriendsPagingDataFlow.first()
            .collectDataForTest(dispatcherRule.testDispatcher, dispatcherRule.testDispatcher)
            .map { it.toUiModel() }

        // when
        val actualList = viewModel.friendsPagingData.first()
            .collectDataForTest(dispatcherRule.testDispatcher, dispatcherRule.testDispatcher)

        // then
        assertEquals(expectedList.size, actualList.size)
        assertEquals(expectedList, actualList)
    }

    @Test
    fun `친구 목록 페이지네이션 과정에서 예외 발생 시 빈 페이징 데이터를 반환한다`() = runTest(dispatcherRule.testDispatcher) {
        // given: getFriends()가 예외를 던지도록 설정
        setMocking(myUserId = 1L, currentUserId = 1L)
        every { usecases.getFriends(1L) } returns flow { throw Exception("Test exception") }
        viewModel = FriendListViewModel(
            usecases = usecases,
            snackbarController = snackbarController,
            savedStateHandle = savedStateHandle,
        )

        // when
        val actualList = viewModel.friendsPagingData.first()
            .collectDataForTest(dispatcherRule.testDispatcher, dispatcherRule.testDispatcher)

        // then
        assertTrue(actualList.isEmpty())
    }

    @Test
    fun `친구 요청 수락 성공 후 friendsPagingData 상단에 수락된 요청자가 삽입된다`() =
        runTest(dispatcherRule.testDispatcher) {
            // given
            setMocking(myUserId = 1L, currentUserId = 1L)
            val targetUserId = 100L
            val uiRequester = createUiRequester(id = 999L, userId = targetUserId)
            coEvery {
                usecases.acceptFriendRequest(myUserId = 1L, targetUserId = targetUserId)
            } returns flowOf(Result.Success(Unit))

            // when: 친구 요청 수락
            viewModel.acceptFriendRequest(uiRequester, FriendStatus.RECEIVED)
            advanceUntilIdle()

            // then: 수락된 요청자가 목록 최상단에 위치해야 한다
            val actualList = viewModel.friendsPagingData.first()
                .collectDataForTest(dispatcherRule.testDispatcher, dispatcherRule.testDispatcher)
            assertEquals(uiRequester.toUiFriendInfo(), actualList.first())
        }

    @Test
    fun `친구 요청 수락 성공 후 friendsPagingData에서 동일 id의 기존 항목이 중복 표시되지 않는다`() =
        runTest(dispatcherRule.testDispatcher) {
            // given: 베이스 페이징 데이터 내 id=1L 항목(index 1)과 동일한 id를 가진 요청자
            setMocking(myUserId = 1L, currentUserId = 1L)
            val targetUserId = 100L
            val duplicateId = 1L // TestFriendsPagingDataFlow의 두 번째 항목 id
            val uiRequester = createUiRequester(id = duplicateId, userId = targetUserId)
            coEvery {
                usecases.acceptFriendRequest(myUserId = 1L, targetUserId = targetUserId)
            } returns flowOf(Result.Success(Unit))

            // when
            viewModel.acceptFriendRequest(uiRequester, FriendStatus.RECEIVED)
            advanceUntilIdle()

            // then: 기존 항목이 필터링되고 상단 삽입으로 인해 총 개수는 유지된다
            val actualList = viewModel.friendsPagingData.first()
                .collectDataForTest(dispatcherRule.testDispatcher, dispatcherRule.testDispatcher)
            assertEquals(TEST_LIST_SIZE, actualList.size)
            assertEquals(uiRequester.toUiFriendInfo(), actualList.first())
        }

    // ------------------------------ 캐시 초기화 (resetFriendsCache / resetRequestersCache) ------------------------------

    @Test
    fun `resetFriendsCache 호출 시 friendsPagingData가 베이스 데이터 상태로 돌아온다`() =
        runTest(dispatcherRule.testDispatcher) {
            // given: 친구 요청을 수락하여 cachedAcceptedRequesters에 항목 추가
            setMocking(myUserId = 1L, currentUserId = 1L)
            val targetUserId = 100L
            val uiRequester = createUiRequester(id = 999L, userId = targetUserId)
            coEvery {
                usecases.acceptFriendRequest(myUserId = 1L, targetUserId = targetUserId)
            } returns flowOf(Result.Success(Unit))
            viewModel.acceptFriendRequest(uiRequester, FriendStatus.RECEIVED)
            advanceUntilIdle()

            // when: 캐시 초기화
            viewModel.resetFriendsCache()
            advanceUntilIdle()

            // then: 수락된 요청자가 사라지고 원래 베이스 데이터와 동일해야 한다
            val expectedList = TestFriendsPagingDataFlow.first()
                .collectDataForTest(dispatcherRule.testDispatcher, dispatcherRule.testDispatcher)
                .map { it.toUiModel() }
            val actualList = viewModel.friendsPagingData.first()
                .collectDataForTest(dispatcherRule.testDispatcher, dispatcherRule.testDispatcher)
            assertEquals(expectedList, actualList)
        }

    @Test
    fun `resetRequestersCache 호출 시 requesterStatus가 빈 맵으로 초기화된다`() = runTest {
        // given: 친구 요청을 수락하여 requesterStatus에 항목 추가
        setMocking(myUserId = 1L, currentUserId = 1L)
        val targetUserId = 100L
        coEvery {
            usecases.acceptFriendRequest(myUserId = 1L, targetUserId = targetUserId)
        } returns flowOf(Result.Success(Unit))
        viewModel.acceptFriendRequest(createUiRequester(id = 1L, userId = targetUserId), FriendStatus.RECEIVED)
        advanceUntilIdle()

        // when
        viewModel.resetRequestersCache()

        // then
        assertTrue(viewModel.requesterStatus.value.isEmpty())
    }

    // ------------------------------ 친구 요청 목록 (requestersPagingData) ------------------------------

    @Test
    fun `친구 요청 목록 페이징 데이터는 initRequestersPagingData 호출 시 정상적으로 로드된다`() =
        runTest(dispatcherRule.testDispatcher) {
            // given
            setMocking(myUserId = 1L, currentUserId = 1L)
            val expectedList = TestRequestersPagingDataFlow.first()
                .collectDataForTest(dispatcherRule.testDispatcher, dispatcherRule.testDispatcher)
                .map { it.toUiModel() }

            // when
            viewModel.initRequestersPagingData()
            val actualList = viewModel.requestersPagingData.first()
                .collectDataForTest(dispatcherRule.testDispatcher, dispatcherRule.testDispatcher)

            // then
            assertEquals(expectedList.size, actualList.size)
            assertEquals(expectedList, actualList)
        }

    @Test
    fun `친구 요청 목록 페이지네이션 과정에서 예외 발생 시 빈 페이징 데이터를 반환한다`() = runTest(dispatcherRule.testDispatcher) {
        // given: getIncomingRequests()가 예외를 던지도록 설정
        setMocking(myUserId = 1L, currentUserId = 1L)
        every { usecases.getIncomingRequests() } returns flow { throw Exception("Test exception") }
        viewModel = FriendListViewModel(
            usecases = usecases,
            snackbarController = snackbarController,
            savedStateHandle = savedStateHandle,
        )

        // when
        viewModel.initRequestersPagingData()
        val actualList = viewModel.requestersPagingData.first()
            .collectDataForTest(dispatcherRule.testDispatcher, dispatcherRule.testDispatcher)

        // then
        assertTrue(actualList.isEmpty())
    }

    @Test
    fun `initRequestersPagingData를 여러 번 호출해도 실제 로드는 1회만 발생한다`() =
        runTest(dispatcherRule.testDispatcher) {
            // given: getIncomingRequests() 호출 횟수 추적
            setMocking(myUserId = 1L, currentUserId = 1L)
            var loadCount = 0
            every { usecases.getIncomingRequests() } answers {
                loadCount++
                TestRequestersPagingDataFlow
            }

            // when: initRequestersPagingData를 3회 연속 호출하고 실제로 collect
            val job = launch { viewModel.requestersPagingData.collect {} }
            viewModel.initRequestersPagingData()
            viewModel.initRequestersPagingData()
            viewModel.initRequestersPagingData()
            advanceUntilIdle()

            // then: isInitRequestersPagingData는 처음 true가 된 이후 변하지 않으므로 로드는 1회
            assertEquals(1, loadCount)

            job.cancel()
        }

    // ------------------------------ 친구 요청 수락 (acceptFriendRequest) ------------------------------

    @Test
    fun `친구 요청 수락 조건 위반 - 나의 사용자 ID가 null인 경우 아무 작업도 수행하지 않는다`() = runTest {
        // given: getMyUserId()가 null을 반환하도록 설정
        setMocking(myUserId = 1L, currentUserId = 1L)
        every { usecases.getMyUserId() } returns null
        val targetUserId = 100L
        viewModel = FriendListViewModel(
            usecases = usecases,
            snackbarController = snackbarController,
            savedStateHandle = savedStateHandle,
        )

        // when
        viewModel.acceptFriendRequest(createUiRequester(id = 1L, userId = targetUserId), FriendStatus.RECEIVED)
        advanceUntilIdle()

        // then: acceptFriendRequest usecase가 호출되지 않아야 한다
        coVerify(exactly = 0) {
            usecases.acceptFriendRequest(myUserId = any(), targetUserId = any())
        }
    }

    @Test
    fun `친구 요청 수락 조건 위반 - 현재 친구 상태가 FRIENDS인 경우 아무 작업도 수행하지 않는다`() = runTest {
        // given
        setMocking(myUserId = 1L, currentUserId = 1L)
        val targetUserId = 100L

        // when
        viewModel.acceptFriendRequest(createUiRequester(id = 1L, userId = targetUserId), FriendStatus.FRIENDS)
        advanceUntilIdle()

        // then
        coVerify(exactly = 0) {
            usecases.acceptFriendRequest(myUserId = any(), targetUserId = any())
        }
    }

    @Test
    fun `친구 요청 수락 조건 위반 - 나의 사용자 ID와 현재 사용자 ID가 다른 경우 아무 작업도 수행하지 않는다`() = runTest {
        // given: myUserId(2L) != currentUserId(1L)
        setMocking(myUserId = 2L, currentUserId = 1L)
        val targetUserId = 100L

        // when
        viewModel.acceptFriendRequest(createUiRequester(id = 1L, userId = targetUserId), FriendStatus.RECEIVED)
        advanceUntilIdle()

        // then
        coVerify(exactly = 0) {
            usecases.acceptFriendRequest(myUserId = any(), targetUserId = any())
        }
    }

    @Test
    fun `친구 요청 수락 성공 시 requesterStatus가 즉시 FRIENDS로 업데이트된다 (낙관적 UI)`() = runTest {
        // given
        setMocking(myUserId = 1L, currentUserId = 1L)
        val targetUserId = 100L
        coEvery {
            usecases.acceptFriendRequest(myUserId = 1L, targetUserId = targetUserId)
        } returns flowOf(Result.Success(Unit))

        val requestersStatusList = mutableListOf<Map<Long, FriendStatus>>()
        val job = launch { viewModel.requesterStatus.toList(requestersStatusList) }

        // when
        viewModel.acceptFriendRequest(createUiRequester(id = 1L, userId = targetUserId), FriendStatus.RECEIVED)
        advanceUntilIdle()

        // then: API 응답 전 낙관적으로 FRIENDS 상태가 반영된다
        assertEquals(FriendStatus.FRIENDS, requestersStatusList.first()[targetUserId])

        job.cancel()
    }

    @Test
    fun `친구 요청 수락 중 에러 발생 시 스낵바를 표시하고 requesterStatus를 원래 상태로 롤백한다`() = runTest {
        // given
        setMocking(myUserId = 1L, currentUserId = 1L)
        val targetUserId = 100L
        val expectedError = FriendErrorType.Unexpected(null)
        coEvery {
            usecases.acceptFriendRequest(myUserId = 1L, targetUserId = targetUserId)
        } returns flowOf(Result.Error(expectedError))

        val requestersStatusList = mutableListOf<Map<Long, FriendStatus>>()
        val snackbarList = mutableListOf<SnackbarEvent>()
        val job = viewModel.viewModelScope.launch {
            launch { viewModel.requesterStatus.toList(requestersStatusList) }
            launch { snackbarController.events.toList(snackbarList) }
        }

        // when
        viewModel.acceptFriendRequest(createUiRequester(id = 1L, userId = targetUserId), FriendStatus.RECEIVED)
        advanceUntilIdle()

        // then: 롤백으로 원래 상태(RECEIVED)로 돌아오고 스낵바가 표시된다
        assertEquals(FriendStatus.RECEIVED, requestersStatusList.last()[targetUserId])
        assertEquals(expectedError.asUiText(), snackbarList.last().message)

        job.cancel()
    }

    @Test
    fun `AlreadyProceed 에러 발생 시 requesterStatus를 롤백하지 않고 FRIENDS 상태를 유지한다`() = runTest {
        // given: 이미 처리된 요청으로 AlreadyProceed 에러 반환
        setMocking(myUserId = 1L, currentUserId = 1L)
        val targetUserId = 100L
        coEvery {
            usecases.acceptFriendRequest(myUserId = 1L, targetUserId = targetUserId)
        } returns flowOf(Result.Error(FriendErrorType.AlreadyProceed))

        val requestersStatusList = mutableListOf<Map<Long, FriendStatus>>()
        val job = launch { viewModel.requesterStatus.toList(requestersStatusList) }

        // when
        viewModel.acceptFriendRequest(createUiRequester(id = 1L, userId = targetUserId), FriendStatus.RECEIVED)
        advanceUntilIdle()

        // then: 이미 수락된 상태이므로 FRIENDS 상태를 그대로 유지한다
        assertEquals(FriendStatus.FRIENDS, requestersStatusList.last()[targetUserId])

        job.cancel()
    }

    @Test
    fun `AlreadyProceed 에러 발생 시 friendsPagingData 상단에 해당 요청자가 추가된다`() =
        runTest(dispatcherRule.testDispatcher) {
            // given: 이미 처리된 요청으로 AlreadyProceed 에러 반환
            setMocking(myUserId = 1L, currentUserId = 1L)
            val targetUserId = 100L
            val uiRequester = createUiRequester(id = 999L, userId = targetUserId)
            coEvery {
                usecases.acceptFriendRequest(myUserId = 1L, targetUserId = targetUserId)
            } returns flowOf(Result.Error(FriendErrorType.AlreadyProceed))

            // when
            viewModel.acceptFriendRequest(uiRequester, FriendStatus.RECEIVED)
            advanceUntilIdle()

            // then: 이미 수락된 항목이므로 cachedAcceptedRequesters에 추가되어 상단에 표시된다
            val actualList = viewModel.friendsPagingData.first()
                .collectDataForTest(dispatcherRule.testDispatcher, dispatcherRule.testDispatcher)
            assertEquals(uiRequester.toUiFriendInfo(), actualList.first())
        }

    @Test
    fun `AlreadyProceed 에러 발생 시 스낵바를 표시하지 않는다`() = runTest {
        // given
        setMocking(myUserId = 1L, currentUserId = 1L)
        val targetUserId = 100L
        coEvery {
            usecases.acceptFriendRequest(myUserId = 1L, targetUserId = targetUserId)
        } returns flowOf(Result.Error(FriendErrorType.AlreadyProceed))

        val snackbarList = mutableListOf<SnackbarEvent>()
        val snackbarJob = launch { snackbarController.events.toList(snackbarList) }

        // when
        viewModel.acceptFriendRequest(createUiRequester(id = 1L, userId = targetUserId), FriendStatus.RECEIVED)
        advanceUntilIdle()

        // then: AlreadyProceed는 사용자에게 별도 에러를 노출하지 않는다
        assertTrue(snackbarList.isEmpty())

        snackbarJob.cancel()
    }

    // ------------------------------ navigateToUserProfileOrMyProfile ------------------------------

    @Test
    fun `args의 userId가 나의 userId와 다르면 NavigateToUserProfile 이벤트를 발생시킨다`() = runTest {
        // given
        setMocking(myUserId = 1L, currentUserId = 1L)
        val args = UserProfileArgs(userId = 99L)
        val effectList = mutableListOf<FriendEffect>()
        val job = launch { viewModel.effect.toList(effectList) }
        advanceUntilIdle()

        // when
        viewModel.navigateToUserProfileOrMyProfile(args)
        advanceUntilIdle()

        // then
        assertEquals(FriendEffect.NavigateToUserProfile(args), effectList.last())

        job.cancel()
    }

    @Test
    fun `args의 userId가 나의 userId와 같으면 NavigateToMyProfile 이벤트를 발생시킨다`() = runTest {
        // given
        setMocking(myUserId = 1L, currentUserId = 1L)
        val args = UserProfileArgs(userId = 1L)
        val effectList = mutableListOf<FriendEffect>()
        val job = launch { viewModel.effect.toList(effectList) }
        advanceUntilIdle()

        // when
        viewModel.navigateToUserProfileOrMyProfile(args)
        advanceUntilIdle()

        // then
        assertEquals(FriendEffect.NavigateToMyProfile, effectList.last())

        job.cancel()
    }

    @Test
    fun `getMyUserId가 null인 경우 navigateToUserProfileOrMyProfile 호출 시 스낵바를 표시한다`() = runTest {
        // given: init 이후에 myUserId가 null이 되는 상황을 가정
        setMocking(myUserId = 1L, currentUserId = 1L)
        every { usecases.getMyUserId() } returns null
        viewModel = FriendListViewModel(
            usecases = usecases,
            snackbarController = snackbarController,
            savedStateHandle = savedStateHandle,
        )
        val snackbarList = mutableListOf<SnackbarEvent>()
        val snackbarJob = launch { snackbarController.events.toList(snackbarList) }
        advanceUntilIdle()

        // when
        viewModel.navigateToUserProfileOrMyProfile(UserProfileArgs(userId = 99L))
        advanceUntilIdle()

        // then
        assertEquals(FriendErrorType.MyUserIdNotFound.asUiText(), snackbarList.last().message)

        snackbarJob.cancel()
    }

    // ------------------------------ Utils ------------------------------

    private fun setMocking(
        myUserId: Long,
        currentUserId: Long,
    ) {
        val myUserIdVO = UserId(myUserId)
        val currentUserIdVO = UserId(currentUserId)
        every { usecases.getFriends(currentUserIdVO.value) } returns TestFriendsPagingDataFlow
        every { usecases.getIncomingRequests() } returns TestRequestersPagingDataFlow
        every { usecases.getMyUserId() } returns myUserIdVO
        savedStateHandle = SavedStateHandle(
            mapOf("userId" to currentUserIdVO.value),
        )
        viewModel = FriendListViewModel(
            usecases = usecases,
            snackbarController = snackbarController,
            savedStateHandle = savedStateHandle,
        )
    }

    private fun createUiRequester(id: Long, userId: Long): UiRequester =
        UiRequester(
            id = id,
            userId = userId,
            displayId = "1",
            name = "1",
            profileImageUrl = null,
            respondedAt = 1000,
            createdAt = 1000,
            updatedAt = 1000,
        )

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
