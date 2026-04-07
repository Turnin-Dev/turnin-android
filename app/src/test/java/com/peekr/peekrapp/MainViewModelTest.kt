package com.peekr.peekrapp

import android.net.ConnectivityManager
import androidx.lifecycle.viewModelScope
import com.peekr.core.domain.auth.repository.AuthRepository
import com.peekr.core.domain.auth.usecase.LogoutUseCase
import com.peekr.core.domain.common.Result
import com.peekr.core.domain.common.error.CommonErrorType
import com.peekr.core.domain.eventBus.AuthEventBus
import com.peekr.core.domain.notification.NotificationSyncManager
import com.peekr.core.domain.user.repository.UserRepository
import io.mockk.Runs
import io.mockk.clearMocks
import io.mockk.coEvery
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class MainViewModelTest {
    // ===== Test Dispatcher =====
    private val testDispatcher = UnconfinedTestDispatcher()

    // ===== Mocks =====
    private val authEventBus: AuthEventBus = mockk()
    private val logoutUseCase: LogoutUseCase = mockk()
    private val userRepository: UserRepository = mockk()
    private val authRepository: AuthRepository = mockk()
    private val notificationSyncManager: NotificationSyncManager = mockk(relaxed = true)
    private val connectivityManager: ConnectivityManager = mockk()

    // ===== Channel (AuthEventBus) =====
    private val logoutEventChannel = Channel<Unit>(Channel.CONFLATED)
    private val loginEventChannel = Channel<Unit>(Channel.CONFLATED)

    // ===== ViewModel =====
    private lateinit var viewModel: MainViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)

        every { authEventBus.logoutEvent } returns logoutEventChannel.receiveAsFlow()
        every { authEventBus.loginEvent } returns loginEventChannel.receiveAsFlow()

        // ConnectivityManager — 기본적으로 callback만 등록/해제되도록 설정
        every {
            connectivityManager.registerDefaultNetworkCallback(any())
        } just Runs
        every {
            connectivityManager.unregisterNetworkCallback(any<ConnectivityManager.NetworkCallback>())
        } just Runs
    }

    @After
    fun tearDown() {
        if (::viewModel.isInitialized) {
            viewModel.viewModelScope.cancel()
            testDispatcher.scheduler.advanceUntilIdle()
        }
        logoutEventChannel.close()
        loginEventChannel.close()
        Dispatchers.resetMain()
    }

    // ===== Helper: ViewModel 생성 =====
    private fun createViewModel() = MainViewModel(
        authEventBus = authEventBus,
        logoutUseCase = logoutUseCase,
        userRepository = userRepository,
        authRepository = authRepository,
        notificationSyncManager = notificationSyncManager,
        connectivityManager = connectivityManager,
    )

    // ===== Helper: myProfile stub =====
    private fun stubMyProfileNull() {
        every { userRepository.myProfile } returns MutableStateFlow(null)
    }

    private fun stubMyProfileExists() {
        every { userRepository.myProfile } returns MutableStateFlow(mockk())
    }

    // ===== Helper: preloadUserData stub =====
    private fun stubGetMyProfileRefresh(flow: Flow<Result<Unit, CommonErrorType>> = emptyFlow()) {
        every { userRepository.getMyProfileRefresh() } returns flow
    }

    // =========================================================
    // 1. 초기화 — 로그인 상태 확인
    // =========================================================

    @Test
    fun `init - 로그인 상태이면 loggedIn=true, isLoading=false, 프리로드 및 알림 동기화 실행`() = runTest {
        // given
        coEvery { authRepository.isLoggedIn() } returns true
        stubMyProfileNull()
        stubGetMyProfileRefresh()

        // when
        viewModel = createViewModel()

        // then
        assertEquals(true, viewModel.loggedIn.value)
        assertEquals(false, viewModel.isLoading.value)
        verify { notificationSyncManager.sync() }
        verify { userRepository.getMyProfileRefresh() }
    }

    @Test
    fun `init - 비로그인 상태이면 loggedIn=false, isLoading=false, 프리로드 미실행`() = runTest {
        // given
        coEvery { authRepository.isLoggedIn() } returns false

        // when
        viewModel = createViewModel()

        // then
        assertEquals(false, viewModel.loggedIn.value)
        assertEquals(false, viewModel.isLoading.value)
        verify(exactly = 0) { notificationSyncManager.sync() }
        verify(exactly = 0) { userRepository.getMyProfileRefresh() }
    }

    @Test
    fun `init - isLoading은 authRepository 응답 전까지 true`() = runTest {
        // given
        // authRepository를 영원히 지연시키려면 async하게 처리
        val deferred = CompletableDeferred<Boolean>()
        coEvery { authRepository.isLoggedIn() } coAnswers { deferred.await() }

        // when
        // ViewModel 생성 직후(아직 isLoggedIn이 리턴 안 됨) → isLoading=true
        viewModel = createViewModel()
        assertEquals(true, viewModel.isLoading.value)

        // 응답 완료
        stubMyProfileNull()
        stubGetMyProfileRefresh()
        deferred.complete(true)
        testDispatcher.scheduler.advanceUntilIdle()

        // then
        assertEquals(false, viewModel.isLoading.value)
    }

    // =========================================================
    // 2. 로그아웃 감지
    // =========================================================

    @Test
    fun `logoutEvent 수신 - 로그아웃 성공 시 loggedIn=false, navigateToLogin 이벤트 발행`() = runTest {
        // given
        coEvery { authRepository.isLoggedIn() } returns true
        stubMyProfileNull()
        stubGetMyProfileRefresh()

        val logoutResult: Flow<Result<Unit, CommonErrorType>> = flowOf(Result.Success(Unit))
        every { logoutUseCase() } returns logoutResult

        viewModel = createViewModel()

        val navigateEvents = mutableListOf<Unit>()
        val job = launch { viewModel.navigateToLogin.toList(navigateEvents) }

        // when
        logoutEventChannel.send(Unit)
        testDispatcher.scheduler.advanceUntilIdle()

        // then
        assertEquals(false, viewModel.loggedIn.value)
        assertEquals(1, navigateEvents.size)

        job.cancel()
    }

    @Test
    fun `logoutEvent 수신 - 로그아웃 실패 시 loggedIn 변경 없음, navigateToLogin 미발행`() = runTest {
        // given
        coEvery { authRepository.isLoggedIn() } returns true
        stubMyProfileNull()
        stubGetMyProfileRefresh()

        val logoutResult: Flow<Result<Unit, CommonErrorType>> =
            flowOf(Result.Error(CommonErrorType.Unexpected(null)))
        every { logoutUseCase() } returns logoutResult

        viewModel = createViewModel()

        val navigateEvents = mutableListOf<Unit>()
        val job = launch { viewModel.navigateToLogin.toList(navigateEvents) }

        // when
        logoutEventChannel.send(Unit)
        testDispatcher.scheduler.advanceUntilIdle()

        // then
        assertEquals(true, viewModel.loggedIn.value) // 변경 없음
        assertEquals(0, navigateEvents.size)

        job.cancel()
    }

    @Test
    fun `logoutEvent 수신 - Loading 결과는 무시하고 최종 결과만 처리`() = runTest {
        // given
        coEvery { authRepository.isLoggedIn() } returns false

        val logoutResult: Flow<Result<Unit, CommonErrorType>> = flowOf(
            Result.Loading,
            Result.Success(Unit),
        )
        every { logoutUseCase() } returns logoutResult

        viewModel = createViewModel()

        val navigateEvents = mutableListOf<Unit>()
        val job = launch { viewModel.navigateToLogin.toList(navigateEvents) }

        // when
        logoutEventChannel.send(Unit)
        testDispatcher.scheduler.advanceUntilIdle()

        // then
        assertEquals(false, viewModel.loggedIn.value)
        assertEquals(1, navigateEvents.size)

        job.cancel()
    }

    // =========================================================
    // 3. 로그인 감지
    // =========================================================

    @Test
    fun `loginEvent 수신 - loggedIn=true, 프리로드 및 알림 동기화 실행`() = runTest {
        // given: 초기 상태를 비로그인으로 고정해 loginEvent로 인한 상태 변화만 검증
        coEvery { authRepository.isLoggedIn() } returns false
        stubMyProfileNull()
        stubGetMyProfileRefresh()

        viewModel = createViewModel()

        // when
        loginEventChannel.send(Unit)
        testDispatcher.scheduler.advanceUntilIdle()

        // then
        assertEquals(true, viewModel.loggedIn.value)
        verify { notificationSyncManager.sync() }
        verify { userRepository.getMyProfileRefresh() }
    }

    @Test
    fun `loginEvent 수신 - 이미 프로필이 있으면 getMyProfileRefresh 미호출`() = runTest {
        // given
        coEvery { authRepository.isLoggedIn() } returns false
        stubMyProfileExists()

        viewModel = createViewModel()

        // when
        loginEventChannel.send(Unit)
        testDispatcher.scheduler.advanceUntilIdle()

        // then
        assertEquals(true, viewModel.loggedIn.value)
        verify(exactly = 0) { userRepository.getMyProfileRefresh() }
    }

    // =========================================================
    // 4. syncNotificationState
    // =========================================================

    @Test
    fun `syncNotificationState - 로그인 상태이면 sync 호출`() = runTest {
        // given
        coEvery { authRepository.isLoggedIn() } returns true
        stubMyProfileNull()
        stubGetMyProfileRefresh()

        viewModel = createViewModel()
        clearMocks(notificationSyncManager, answers = false) // init에서 호출된 것 초기화

        // when
        viewModel.syncNotificationState()
        testDispatcher.scheduler.advanceUntilIdle()

        // then
        verify(exactly = 1) { notificationSyncManager.sync() }
    }

    @Test
    fun `syncNotificationState - 비로그인 상태이면 sync 미호출`() = runTest {
        // given
        coEvery { authRepository.isLoggedIn() } returns false

        viewModel = createViewModel()

        // when
        viewModel.syncNotificationState()
        testDispatcher.scheduler.advanceUntilIdle()

        // then
        verify(exactly = 0) { notificationSyncManager.sync() }
    }

    @Test
    fun `syncNotificationState - loggedIn이 null이면 값이 확정될 때까지 대기 후 처리`() = runTest {
        // given
        // isLoggedIn을 지연시켜 loggedIn=null 상태에서 syncNotificationState 호출
        val deferred = CompletableDeferred<Boolean>()
        coEvery { authRepository.isLoggedIn() } coAnswers { deferred.await() }

        viewModel = createViewModel()
        assertEquals(null, viewModel.loggedIn.value)

        val syncJob = launch { viewModel.syncNotificationState() }

        // 아직 deferred 완료 전 → sync 미호출
        verify(exactly = 0) { notificationSyncManager.sync() }

        // when
        // isLoggedIn = false로 완료
        deferred.complete(false)
        testDispatcher.scheduler.advanceUntilIdle()
        syncJob.join()

        // then
        // 비로그인이므로 sync 미호출
        verify(exactly = 0) { notificationSyncManager.sync() }
    }

    // =========================================================
    // 5. preloadUserData
    // =========================================================

    @Test
    fun `preloadUserData - 프로필이 null이면 getMyProfileRefresh 호출`() = runTest {
        // given
        coEvery { authRepository.isLoggedIn() } returns true
        stubMyProfileNull()
        stubGetMyProfileRefresh()

        // when
        viewModel = createViewModel()

        // then
        verify(exactly = 1) { userRepository.getMyProfileRefresh() }
    }

    @Test
    fun `preloadUserData - 프로필이 있으면 getMyProfileRefresh 미호출`() = runTest {
        // given
        coEvery { authRepository.isLoggedIn() } returns true
        stubMyProfileExists()

        // when
        viewModel = createViewModel()

        // then
        verify(exactly = 0) { userRepository.getMyProfileRefresh() }
    }

    @Test
    fun `preloadUserData - getMyProfileRefresh 에러 발생해도 크래시 없음`() = runTest {
        // given
        coEvery { authRepository.isLoggedIn() } returns true
        stubMyProfileNull()
        every { userRepository.getMyProfileRefresh() } returns flow {
            throw RuntimeException("network error")
        }

        // when
        // ViewModel 생성 및 코루틴 완료까지 대기
        // 예외가 외부로 전파되지 않으면 테스트 통과
        viewModel = createViewModel()
        testDispatcher.scheduler.advanceUntilIdle()

        // then
        // 크래시 없이 여기까지 도달하면 성공
        assertEquals(false, viewModel.isLoading.value)
    }

    // =========================================================
    // 6. 네트워크 재연결 감지
    // =========================================================

    @Test
    fun `네트워크 재연결 - 최초 연결(drop(1))은 무시된다`() = runTest {
        // given
        coEvery { authRepository.isLoggedIn() } returns true
        stubMyProfileExists() // preloadUserData 호출 여부만 검증하기 위해 프로필 있음으로 고정

        val callbackSlot = slot<ConnectivityManager.NetworkCallback>()
        every {
            connectivityManager.registerDefaultNetworkCallback(capture(callbackSlot))
        } just Runs

        viewModel = createViewModel()
        testDispatcher.scheduler.advanceUntilIdle()
        clearMocks(userRepository, answers = false)

        // when — 첫 번째 onAvailable은 drop(1)로 무시됨
        callbackSlot.captured.onAvailable(mockk())
        testDispatcher.scheduler.advanceUntilIdle()

        // then
        verify(exactly = 0) { userRepository.getMyProfileRefresh() }
    }

    @Test
    fun `네트워크 재연결 - 로그인 상태이고 프로필 없으면 getMyProfileRefresh 호출`() = runTest {
        // given
        coEvery { authRepository.isLoggedIn() } returns true
        stubMyProfileNull()
        stubGetMyProfileRefresh()

        val callbackSlot = slot<ConnectivityManager.NetworkCallback>()
        every {
            connectivityManager.registerDefaultNetworkCallback(capture(callbackSlot))
        } just Runs

        viewModel = createViewModel()
        testDispatcher.scheduler.advanceUntilIdle()
        clearMocks(userRepository, answers = false)
        stubMyProfileNull()
        stubGetMyProfileRefresh()

        // drop(1) 소비
        callbackSlot.captured.onAvailable(mockk())
        testDispatcher.scheduler.advanceUntilIdle()

        // when — 두 번째 onAvailable이 실제 재연결
        callbackSlot.captured.onAvailable(mockk())
        testDispatcher.scheduler.advanceUntilIdle()

        // then
        verify(exactly = 1) { userRepository.getMyProfileRefresh() }
    }

    @Test
    fun `네트워크 재연결 - 로그인 상태이고 프로필 있으면 getMyProfileRefresh 미호출`() = runTest {
        // given
        coEvery { authRepository.isLoggedIn() } returns true
        stubMyProfileExists()

        val callbackSlot = slot<ConnectivityManager.NetworkCallback>()
        every {
            connectivityManager.registerDefaultNetworkCallback(capture(callbackSlot))
        } just Runs

        viewModel = createViewModel()
        testDispatcher.scheduler.advanceUntilIdle()
        clearMocks(userRepository, answers = false)
        stubMyProfileExists()

        // drop(1) 소비
        callbackSlot.captured.onAvailable(mockk())
        testDispatcher.scheduler.advanceUntilIdle()

        // when
        callbackSlot.captured.onAvailable(mockk())
        testDispatcher.scheduler.advanceUntilIdle()

        // then
        verify(exactly = 0) { userRepository.getMyProfileRefresh() }
    }

    @Test
    fun `네트워크 재연결 - 비로그인 상태이면 getMyProfileRefresh 미호출`() = runTest {
        // given
        coEvery { authRepository.isLoggedIn() } returns false

        val callbackSlot = slot<ConnectivityManager.NetworkCallback>()
        every {
            connectivityManager.registerDefaultNetworkCallback(capture(callbackSlot))
        } just Runs

        viewModel = createViewModel()
        testDispatcher.scheduler.advanceUntilIdle()

        // drop(1) 소비
        callbackSlot.captured.onAvailable(mockk())
        testDispatcher.scheduler.advanceUntilIdle()

        // when
        callbackSlot.captured.onAvailable(mockk())
        testDispatcher.scheduler.advanceUntilIdle()

        // then
        verify(exactly = 0) { userRepository.getMyProfileRefresh() }
    }

    @Test
    fun `네트워크 재연결 - loggedIn이 null이면 getMyProfileRefresh 미호출`() = runTest {
        // given — isLoggedIn을 지연시켜 _loggedIn=null 유지
        val deferred = CompletableDeferred<Boolean>()
        coEvery { authRepository.isLoggedIn() } coAnswers { deferred.await() }

        val callbackSlot = slot<ConnectivityManager.NetworkCallback>()
        every {
            connectivityManager.registerDefaultNetworkCallback(capture(callbackSlot))
        } just Runs

        viewModel = createViewModel()
        assertEquals(null, viewModel.loggedIn.value)

        // drop(1) 소비
        callbackSlot.captured.onAvailable(mockk())
        testDispatcher.scheduler.advanceUntilIdle()

        // when — _loggedIn이 여전히 null인 상태에서 재연결
        callbackSlot.captured.onAvailable(mockk())
        testDispatcher.scheduler.advanceUntilIdle()

        // then
        verify(exactly = 0) { userRepository.getMyProfileRefresh() }

        // cleanup
        deferred.complete(false)
    }

    @Test
    fun `네트워크 재연결 - ViewModel scope 취소 후 콜백 해제(unregister) 확인`() = runTest {
        // given
        coEvery { authRepository.isLoggedIn() } returns false

        val callbackSlot = slot<ConnectivityManager.NetworkCallback>()
        every {
            connectivityManager.registerDefaultNetworkCallback(capture(callbackSlot))
        } just Runs

        viewModel = createViewModel()
        testDispatcher.scheduler.advanceUntilIdle()

        // when — viewModelScope 취소 → awaitClose 블록 실행
        viewModel.viewModelScope.cancel()
        testDispatcher.scheduler.advanceUntilIdle()

        // then
        verify(exactly = 1) {
            connectivityManager.unregisterNetworkCallback(callbackSlot.captured)
        }
    }
}
