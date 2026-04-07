package com.peekr.presentation.discover.viewmodel

import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import com.peekr.core.common.logger.AppLogger
import com.peekr.core.domain.common.Result
import com.peekr.core.domain.discover.model.DiscoverContext
import com.peekr.core.domain.discover.model.DiscoverKeyword
import com.peekr.core.domain.discover.model.DiscoverUser
import com.peekr.core.domain.model.DisplayId
import com.peekr.core.domain.model.KeywordId
import com.peekr.core.domain.model.KeywordName
import com.peekr.core.domain.model.Name
import com.peekr.core.domain.model.UserId
import com.peekr.core.domain.model.UserKeywordId
import com.peekr.core.domain.user.usecase.GetMyUserIdUseCase
import com.peekr.core.presentation.FakeSnackbarController
import com.peekr.core.presentation.MVIBaseViewModelTest
import com.peekr.core.presentation.MainDispatcherRule
import com.peekr.core.presentation.common.navigation.args.UserProfileArgs
import com.peekr.core.presentation.common.snackbar.SnackbarEvent
import com.peekr.domain.discover.error.DiscoverErrorType
import com.peekr.domain.discover.usecase.DiscoverUseCases
import com.peekr.presentation.discover.error.asUiText
import com.peekr.presentation.discover.model.UiDiscoverContext
import com.peekr.presentation.discover.model.UiDiscoverKeyword
import com.peekr.presentation.discover.model.UiDiscoverUser
import com.peekr.presentation.discover.model.toUiModel
import com.peekr.presentation.discover.state.DiscoverContract
import com.peekr.presentation.util.collectDataForTest
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkObject
import io.mockk.unmockkObject
import io.mockk.verify
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class DiscoverViewModelTest : MVIBaseViewModelTest<
    DiscoverContract.UiState,
    DiscoverContract.UiEvent,
    DiscoverContract.UiEffect,
    DiscoverViewModel,
>() {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val usecases: DiscoverUseCases = mockk()
    private val getMyUserIdUseCase: GetMyUserIdUseCase = mockk()
    private val snackbarController = FakeSnackbarController()
    private lateinit var viewModel: DiscoverViewModel

    @Before
    fun setUp() {
        every { usecases.getMyDiscoverContext() } returns flowOf(TestMyDiscoverContext)
        every { usecases.refreshMyKeywords() } returns emptyFlow()
    }

    @Test
    fun `뷰모델 초기화 작업 성공 시 상태를 업데이트한다`() {
        viewModel = DiscoverViewModel(usecases, getMyUserIdUseCase, snackbarController)

        testState(
            viewModel = viewModel,
            intents = listOf(),
            assertions = listOf(
                DiscoverContract.UiState(
                    histories = listOf(TestMyDiscoverContext.toUiModel()),
                    currentDiscoverTarget = TestMyDiscoverContext.toUiModel(),
                ),
            ),
        )
    }

    @Test
    fun `getMyDiscoverContext 예외 발생 시 로그를 출력한다`() = runTest {
        // given
        mockkObject(AppLogger)
        every { usecases.getMyDiscoverContext() } returns flow { throw Exception("error!") }

        // when
        viewModel = DiscoverViewModel(usecases, getMyUserIdUseCase, snackbarController)
        advanceUntilIdle()

        // then
        verify { AppLogger.e(any(), any(), any()) }

        // clean up
        unmockkObject(AppLogger)
    }

    @Test
    fun `refreshMyKeywords 실패 시 스낵바를 표시한다`() = runTest {
        // given
        every {
            usecases.refreshMyKeywords()
        } returns flowOf(Result.Error(DiscoverErrorType.Unexpected(null)))

        val snackbarList = mutableListOf<SnackbarEvent>()
        val snackbarJob = launch {
            snackbarController.events.toList(snackbarList)
        }

        // when
        viewModel = DiscoverViewModel(usecases, getMyUserIdUseCase, snackbarController)
        advanceUntilIdle()

        // then
        assertEquals(
            DiscoverErrorType.MyKeywordsRefreshFailed.asUiText(),
            snackbarList.first().message,
        )

        // clean up
        snackbarJob.cancel()
    }

    @Test
    fun `내 프로필 정보가 변경되면 히스토리의 내 정보를 갱신한다`() = runTest {
        // given: 초기값 방출 후 업데이트된 값 방출
        val updatedMyDiscoverContext = TestMyDiscoverContext.copy(
            user = TestMyDiscoverContext.user.copy(userName = Name("updatedMe")),
        )
        every { usecases.getMyDiscoverContext() } returns flow {
            emit(TestMyDiscoverContext)
            emit(updatedMyDiscoverContext)
        }

        // when
        viewModel = DiscoverViewModel(usecases, getMyUserIdUseCase, snackbarController)
        advanceUntilIdle()

        // then: 히스토리의 '나'가 최신 정보로 갱신된다
        val state = viewModel.uiState.value
        assertEquals(updatedMyDiscoverContext.toUiModel(), state.histories.first())
    }

    @Test
    fun `현재 탐색 대상이 나일 때 내 정보 갱신 시 currentDiscoverTarget도 갱신된다`() = runTest {
        // given: 현재 탐색 대상이 나인 상태에서 내 정보 갱신
        val updatedMyDiscoverContext = TestMyDiscoverContext.copy(
            user = TestMyDiscoverContext.user.copy(userName = Name("updatedMe")),
        )
        every { usecases.getMyDiscoverContext() } returns flow {
            emit(TestMyDiscoverContext)
            emit(updatedMyDiscoverContext)
        }

        // when
        viewModel = DiscoverViewModel(usecases, getMyUserIdUseCase, snackbarController)
        advanceUntilIdle()

        // then: currentDiscoverTarget도 최신 정보로 갱신된다
        val state = viewModel.uiState.value
        assertEquals(updatedMyDiscoverContext.toUiModel(), state.currentDiscoverTarget)
    }

    @Test
    fun `초기 페이징 데이터 로드 성공 테스트`() = runTest {
        // given
        every {
            usecases.getDiscoverContexts(TestMyUserId.value)
        } returns TestPagingDataFlow

        viewModel = DiscoverViewModel(usecases, getMyUserIdUseCase, snackbarController)

        val actualPagingData = async {
            viewModel.discoverContexts.first()
        }

        advanceUntilIdle()

        val expectedPagingData = TestPagingDataFlow.first()
        val expectedList = expectedPagingData.collectDataForTest(
            mainDispatcherRule.testDispatcher,
            mainDispatcherRule.testDispatcher,
        )
            .map { it.toUiModel() }

        // when: 실제 페이징 데이터 수집
        val actualList = actualPagingData.await().collectDataForTest(
            mainDispatcherRule.testDispatcher,
            mainDispatcherRule.testDispatcher,
        )

        // then
        assertTrue(actualList.isNotEmpty())
        assertEquals(expectedList.size, actualList.size)
        assertEquals(expectedList, actualList)
    }

    @Test
    fun `내 프로필 정보가 로드되기 전에는 페이징 데이터를 조회하지 않는다`() = runTest {
        // given
        every { usecases.getMyDiscoverContext() } returns emptyFlow()

        viewModel = DiscoverViewModel(usecases, getMyUserIdUseCase, snackbarController)

        // then: 페이징 데이터가 비어있거나 초기값인지 확인
        val currentState = viewModel.uiState.value
        assertNull(currentState.currentDiscoverTarget)
        assertTrue(currentState.histories.isEmpty())
        verify(exactly = 0) { usecases.getDiscoverContexts(any()) }
    }

    @Test
    fun `초기 페이징 데이터 로드 시 예외가 발생하면 로그를 출력하고 빈 페이징 데이터를 반환한다`() = runTest {
        // given
        mockkObject(AppLogger)
        every {
            usecases.getDiscoverContexts(TestMyUserId.value)
        } returns flow { throw Exception("error!") }

        viewModel = DiscoverViewModel(usecases, getMyUserIdUseCase, snackbarController)

        val actualPagingData = async {
            viewModel.discoverContexts.first()
        }

        advanceUntilIdle()

        // when: 실제 페이징 데이터 수집
        val pagingList = actualPagingData.await().collectDataForTest(
            mainDispatcherRule.testDispatcher,
            mainDispatcherRule.testDispatcher,
        )

        // then
        verify { AppLogger.e(any(), any(), any()) }
        assertTrue(pagingList.isEmpty())

        // clean up
        unmockkObject(AppLogger)
    }

    @Test
    fun `현재 탐색 대상 변경 시 현재 탐색 대상을 변경하고 재탐색 대상을 초기화한다`() {
        // given
        val target = TestUiDiscoverContext
        viewModel = DiscoverViewModel(usecases, getMyUserIdUseCase, snackbarController)

        // when: 현재 탐색 대상 변경
        // then: 현재 탐색 대상을 변경하고 재탐색 대상을 초기화한다.
        testState(
            viewModel = viewModel,
            intents = listOf(
                DiscoverContract.UiEvent.ChangeCurrentDiscoverTarget(target = target),
            ),
            assertions = listOf(
                DiscoverContract.UiState(
                    histories = listOf(TestMyDiscoverContext.toUiModel()),
                    currentDiscoverTarget = target,
                    selectedDiscoverTarget = null,
                ),
            ),
        )
    }

    @Test
    fun `피드 선택 시 현재 선택된 피드가 없는 경우 선택된 피드로 상태를 업데이트한다`() {
        // given
        viewModel = DiscoverViewModel(usecases, getMyUserIdUseCase, snackbarController)

        // when: 피드 선택 이벤트 발생
        // then: 현재 선택된 피드를 업데이트한다.
        testState(
            viewModel = viewModel,
            intents = listOf(
                DiscoverContract.UiEvent.SelectFeed(discoverContext = TestUiDiscoverContext),
            ),
            assertions = listOf(
                DiscoverContract.UiState(
                    histories = listOf(TestMyDiscoverContext.toUiModel()),
                    currentDiscoverTarget = TestMyDiscoverContext.toUiModel(),
                    selectedDiscoverTarget = TestUiDiscoverContext,
                ),
            ),
        )
    }

    @Test
    fun `기존 피드와 다른 피드 선택 시 새로운 피드로 상태를 업데이트한다`() {
        // given
        viewModel = DiscoverViewModel(usecases, getMyUserIdUseCase, snackbarController)

        // when: 피드 선택 이벤트 발생
        // then: 현재 선택된 피드를 업데이트한다.
        testState(
            viewModel = viewModel,
            intents = listOf(
                DiscoverContract.UiEvent.SelectFeed(discoverContext = TestUiDiscoverContext),
            ),
            assertions = listOf(
                DiscoverContract.UiState(
                    histories = listOf(TestMyDiscoverContext.toUiModel()),
                    currentDiscoverTarget = TestMyDiscoverContext.toUiModel(),
                    selectedDiscoverTarget = TestUiDiscoverContext,
                ),
            ),
        )
    }

    @Test
    fun `기존 피드와 같은 피드 선택 시 null로 상태를 업데이트한다`() {
        // given
        viewModel = DiscoverViewModel(usecases, getMyUserIdUseCase, snackbarController)

        // when: 피드 선택 후 현재 선택된 피드와 같은 피드를 선택 (이벤트 2번 발생)
        // then: 기존 피드가 현재 선택된 피드와 같으므로 null로 상태를 업데이트한다.
        testState(
            viewModel = viewModel,
            intents = listOf(
                DiscoverContract.UiEvent.SelectFeed(TestUiDiscoverContext),
                DiscoverContract.UiEvent.SelectFeed(TestUiDiscoverContext),
            ),
            assertions = listOf(
                DiscoverContract.UiState(
                    histories = listOf(TestMyDiscoverContext.toUiModel()),
                    currentDiscoverTarget = TestMyDiscoverContext.toUiModel(),
                    selectedDiscoverTarget = null,
                ),
            ),
        )
    }

    @Test
    fun `재탐색 시 여러 상태를 업데이트한다`() {
        // given
        val initUiState = DiscoverContract.UiState(
            histories = listOf(TestMyDiscoverContext.toUiModel()),
            currentDiscoverTarget = TestMyDiscoverContext.toUiModel(),
        )
        val selectedTarget = TestUiDiscoverContext
        viewModel = DiscoverViewModel(usecases, getMyUserIdUseCase, snackbarController)

        // when: 탐색할 대상 선택, 재탐색 이벤트 발생
        // then:
        // 1. 현재 탐색 대상을 재탐색 대상으로 변경
        // 2. 히스토리에 재탐색 대상 추가
        // 3. 재탐색 대상 초기화
        testState(
            viewModel = viewModel,
            intents = listOf(
                DiscoverContract.UiEvent.SelectFeed(selectedTarget),
                DiscoverContract.UiEvent.ReDiscover,
            ),
            assertions = listOf(
                initUiState.copy(
                    currentDiscoverTarget = selectedTarget,
                    histories = initUiState.histories + selectedTarget,
                    selectedDiscoverTarget = null,
                ),
            ),
        )
    }

    @Test
    fun `재탐색 시 선택된 탐색 대상 혹은 현재 탐색 대상이 없는 경우 스낵바를 표시한다`() = runTest {
        // given
        val initUiState = DiscoverContract.UiState(
            histories = listOf(TestMyDiscoverContext.toUiModel()),
            currentDiscoverTarget = TestMyDiscoverContext.toUiModel(),
        )
        val snackbarList = mutableListOf<SnackbarEvent>()
        val snackbarJob = launch {
            snackbarController.events.toList(snackbarList)
        }

        viewModel = DiscoverViewModel(usecases, getMyUserIdUseCase, snackbarController)

        // when: 선택된 대상 및 피드 없이 재탐색 이벤트 발생
        testState(
            viewModel = viewModel,
            intents = listOf(DiscoverContract.UiEvent.ReDiscover),
            assertions = listOf(initUiState),
        )

        // then: 선택된 탐색 대상 혹은 현재 탐색 대상이 없는 경우 스낵바를 표시한다.
        assertEquals(
            DiscoverErrorType.NotSelectedTarget.asUiText(),
            snackbarList.first().message,
        )

        // clean up
        snackbarJob.cancel()
    }

    @Test
    fun `히스토리 바에서 중간에 있는 사용자 기준으로 재탐색 시 해당 사용자 이후 히스토리는 삭제된다`() = runTest {
        // given
        val user100 = createUiDiscoverContext(100)
        val user101 = createUiDiscoverContext(101)
        val user102 = createUiDiscoverContext(102)
        val user200 = createUiDiscoverContext(200)
        viewModel = DiscoverViewModel(usecases, getMyUserIdUseCase, snackbarController)

        val uiStateList = mutableListOf<DiscoverContract.UiState>()
        val uiStateJob = viewModel.viewModelScope.launch {
            viewModel.uiState.toList(uiStateList)
        }

        // when:
        // 1. 본인 제외 3명(id: 100, 101, 102)을 순차적으로 재탐색
        // 2. id가 100인 사용자를 현재 탐색 대상으로 변경
        // 3. id가 200인 사용자로 재탐색
        // then:
        // 1. 재탐색 후 히스토리에 있는 사용자는 3명이다. (id: 본인, 100, 200)
        // 2. 현재 탐색 대상은 id가 200인 사용자이다.
        testState(
            viewModel = viewModel,
            intents = listOf(
                DiscoverContract.UiEvent.SelectFeed(user100),
                DiscoverContract.UiEvent.ReDiscover,
                DiscoverContract.UiEvent.SelectFeed(user101),
                DiscoverContract.UiEvent.ReDiscover,
                DiscoverContract.UiEvent.SelectFeed(user102),
                DiscoverContract.UiEvent.ReDiscover,
                DiscoverContract.UiEvent.ChangeCurrentDiscoverTarget(user100),
                DiscoverContract.UiEvent.SelectFeed(user200),
                DiscoverContract.UiEvent.ReDiscover,
            ),
            assertions = listOf(
                DiscoverContract.UiState(
                    histories = listOf(TestMyDiscoverContext.toUiModel(), user100, user200),
                    currentDiscoverTarget = user200,
                ),
            ),
        )

        // print
        uiStateList.forEachIndexed { idx, state ->
            println("#${idx + 1}")
            println("currentDiscoverTarget: ${state.currentDiscoverTarget?.user?.userId}")
            println("histories: ${state.histories.map { it.user.userId }}")
        }

        // clean up
        uiStateJob.cancel()
    }

    @Test
    fun `이미 히스토리 바에 있는 사용자를 대상으로 재탐색 하는 경우 해당 사용자 이후 히스토리는 삭제된다`() = runTest {
        // given
        val user100 = createUiDiscoverContext(100)
        val user101 = createUiDiscoverContext(101)
        val user102 = createUiDiscoverContext(102)
        viewModel = DiscoverViewModel(usecases, getMyUserIdUseCase, snackbarController)

        val uiStateList = mutableListOf<DiscoverContract.UiState>()
        val uiStateJob = viewModel.viewModelScope.launch {
            viewModel.uiState.toList(uiStateList)
        }

        // when:
        // 1. 본인 제외 3명(id: 100, 101, 102)을 순차적으로 재탐색
        // 2. 이미 재탐색 한 사용자(id가 100인 사용자)를 대상으로 다시 재탐색 한다.
        // then:
        // 1. 재탐색 후 히스토리에 있는 사용자는 2명이다. (id: 본인, 100)
        // 2. 현재 탐색 대상은 id가 100인 사용자이다.
        testState(
            viewModel = viewModel,
            intents = listOf(
                DiscoverContract.UiEvent.SelectFeed(user100),
                DiscoverContract.UiEvent.ReDiscover,
                DiscoverContract.UiEvent.SelectFeed(user101),
                DiscoverContract.UiEvent.ReDiscover,
                DiscoverContract.UiEvent.SelectFeed(user102),
                DiscoverContract.UiEvent.ReDiscover,
                DiscoverContract.UiEvent.SelectFeed(user100),
                DiscoverContract.UiEvent.ReDiscover,
            ),
            assertions = listOf(
                DiscoverContract.UiState(
                    histories = listOf(TestMyDiscoverContext.toUiModel(), user100),
                    currentDiscoverTarget = user100,
                ),
            ),
        )

        // print
        uiStateList.forEachIndexed { idx, state ->
            println("#${idx + 1}")
            println("currentDiscoverTarget: ${state.currentDiscoverTarget?.user?.userId}")
            println("histories: ${state.histories.map { it.user.userId }}")
        }

        // clean up
        uiStateJob.cancel()
    }

    @Test
    fun `사용자 프로필로 이동 시 나의 사용자 ID가 아닌 경우 사용자 프로필로 이동한다`() {
        // given
        val myUserId = UserId(1L)
        coEvery { getMyUserIdUseCase() } returns myUserId
        viewModel = DiscoverViewModel(usecases, getMyUserIdUseCase, snackbarController)

        // when: 사용자 프로필 이동 이벤트 발생
        // then: 사용자 프로필로 이동하라는 일회성 이벤트를 발행한다.
        testEffect(
            viewModel = viewModel,
            intents = listOf(
                DiscoverContract.UiEvent.NavigateToUserProfile(
                    UserProfileArgs(userId = 2L),
                ),
            ),
            assertions = listOf(
                DiscoverContract.UiEffect.NavigateToUserProfile(
                    UserProfileArgs(userId = 2L),
                ),
            ),
        )
    }

    @Test
    fun `사용자 프로필로 이동 시 나의 사용자 ID인 경우 나의 프로필로 이동한다`() {
        // given
        val myUserId = UserId(1L)
        coEvery { getMyUserIdUseCase() } returns myUserId
        viewModel = DiscoverViewModel(usecases, getMyUserIdUseCase, snackbarController)

        // when: 나의 사용자 ID로 사용자 프로필 이동 이벤트 발생
        // then: 나의 프로필로 이동하라는 일회성 이벤트를 발행한다.
        testEffect(
            viewModel = viewModel,
            intents = listOf(
                DiscoverContract.UiEvent.NavigateToUserProfile(
                    UserProfileArgs(userId = myUserId.value),
                ),
            ),
            assertions = listOf(
                DiscoverContract.UiEffect.NavigateToMyProfile,
            ),
        )
    }

    @Test
    fun `사용자 프로필로 이동 시 나의 사용자 ID 조회에 실패한 경우 스낵바를 표시한다`() = runTest {
        // given
        coEvery { getMyUserIdUseCase() } returns null
        viewModel = DiscoverViewModel(usecases, getMyUserIdUseCase, snackbarController)

        val snackbarList = mutableListOf<SnackbarEvent>()
        val snackbarJob = launch {
            snackbarController.events.toList(snackbarList)
        }

        // when
        viewModel.processEvent(
            DiscoverContract.UiEvent.NavigateToUserProfile(
                UserProfileArgs(userId = 1L),
            ),
        )
        advanceUntilIdle()

        // then: 스낵바 검증
        assertEquals(
            DiscoverErrorType.MyProfileNotFound.asUiText(),
            snackbarList.first().message,
        )

        // clean up
        snackbarJob.cancel()
    }

    private fun createUiDiscoverContext(uniqueValue: Long): UiDiscoverContext =
        UiDiscoverContext(
            user = UiDiscoverUser(
                userId = uniqueValue,
                userName = "name$uniqueValue",
                displayId = "did$uniqueValue",
                profileImageUrl = null,
            ),
            keywords = listOf(
                UiDiscoverKeyword(
                    userKeywordId = uniqueValue,
                    keywordId = uniqueValue,
                    keywordName = "keyword$uniqueValue",
                ),
            ),
        )

    companion object {
        private val TestMyUserId = UserId(1L)
        private val TestMyDiscoverContext = DiscoverContext(
            user = DiscoverUser(
                userId = TestMyUserId,
                userName = Name("me"),
                displayId = DisplayId("did"),
                profileImageUrl = null,
            ),
            keywords = listOf(
                DiscoverKeyword(
                    userKeywordId = UserKeywordId(1L),
                    keywordId = KeywordId(1L),
                    keywordName = KeywordName("my keyword"),
                ),
            ),
        )
        private const val TEST_PAGE_SIZE = 10
        private val TestPagingDataFlow = flowOf(
            PagingData.from(
                List(TEST_PAGE_SIZE) {
                    val id = it + 2L
                    DiscoverContext(
                        user = DiscoverUser(
                            userId = UserId(id),
                            userName = Name("me$id"),
                            displayId = DisplayId("did$id"),
                            profileImageUrl = null,
                        ),
                        keywords = listOf(
                            DiscoverKeyword(
                                userKeywordId = UserKeywordId(id),
                                keywordId = KeywordId(id),
                                keywordName = KeywordName("my keyword $id"),
                            ),
                        ),
                    )
                },
            ),
        )
        private val TestUiDiscoverContext = UiDiscoverContext(
            user = UiDiscoverUser(
                userId = 100L,
                userName = "user",
                displayId = "did",
                profileImageUrl = null,
            ),
            keywords = listOf(
                UiDiscoverKeyword(
                    userKeywordId = 100L,
                    keywordId = 100L,
                    keywordName = "discovered keyword",
                ),
            ),
        )
    }
}
