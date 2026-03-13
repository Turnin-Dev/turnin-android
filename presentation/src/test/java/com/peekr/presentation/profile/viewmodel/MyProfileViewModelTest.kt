package com.peekr.presentation.profile.viewmodel

import com.peekr.core.common.logger.AppLogger
import com.peekr.core.domain.common.Result
import com.peekr.core.domain.model.DisplayId
import com.peekr.core.domain.model.Introduce
import com.peekr.core.domain.model.KeywordDescription
import com.peekr.core.domain.model.KeywordId
import com.peekr.core.domain.model.KeywordName
import com.peekr.core.domain.model.Name
import com.peekr.core.domain.model.UserId
import com.peekr.core.domain.model.UserKeywordId
import com.peekr.core.domain.userKeyword.model.UserKeyword
import com.peekr.core.presentation.FakeSnackbarController
import com.peekr.core.presentation.MVIBaseViewModelTest
import com.peekr.core.presentation.common.snackbar.SnackbarEvent
import com.peekr.core.presentation.ui.model.toUiModel
import com.peekr.domain.profile.error.ProfileErrorType
import com.peekr.domain.profile.model.MyProfile
import com.peekr.domain.profile.usecase.MyProfileUseCases
import com.peekr.presentation.profile.error.asUiText
import com.peekr.presentation.profile.model.toUiModel
import com.peekr.presentation.profile.state.MyProfileContract
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkObject
import io.mockk.unmockkObject
import io.mockk.verify
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class MyProfileViewModelTest : MVIBaseViewModelTest<
    MyProfileContract.UiState,
    MyProfileContract.UiEvent,
    MyProfileContract.UiEffect,
    MyProfileViewModel,
>() {
    private val snackbarController = FakeSnackbarController()
    private val usecases: MyProfileUseCases = mockk()
    private lateinit var viewModel: MyProfileViewModel

    @Before
    fun setUp() {
        // Mock
        every {
            usecases.getMyProfile()
        } returns flowOf(TestMyProfile)
        every {
            usecases.getMyKeywords()
        } returns flowOf(TestUserKeywords)
        every {
            usecases.refreshMyProfile()
        } returns flowOf(Result.Success(Unit))
        every {
            usecases.refreshMyKeywords()
        } returns flowOf(Result.Success(Unit))

        viewModel = MyProfileViewModel(snackbarController, usecases)

        mockkObject(AppLogger)
    }

    @After
    fun teardown() {
        unmockkObject(AppLogger)
    }

    @Test
    fun `초기 데이터 로드 성공 시 나의 프로필과 키워드 리스트를 정상적으로 가져온다`() {
        testState(
            viewModel = viewModel,
            intents = listOf(),
            assertions = listOf(
                MyProfileContract.UiState(
                    myProfile = TestMyProfile.toUiModel(),
                    myKeywords = TestUserKeywords.map { it.toUiModel() },
                ),
            ),
        )
    }

    @Test
    fun `나의 프로필을 로컬에서 조회 시 예외가 발생하는 경우 로깅 후 나의 키워드 리스트는 정상적으로 업데이트 된다`() = runTest {
        // given
        every {
            usecases.getMyProfile()
        } returns flow { throw Exception() }
        viewModel = MyProfileViewModel(snackbarController, usecases)

        val snackbarList = mutableListOf<SnackbarEvent>()
        val snackbarJob = launch {
            snackbarController.events.toList(snackbarList)
        }

        // when, then
        testState(
            viewModel = viewModel,
            intents = listOf(),
            assertions = listOf(
                MyProfileContract.UiState(
                    myKeywords = TestUserKeywords.map { it.toUiModel() },
                ),
            ),
        )

        // then: 스낵바 이벤트 검증
        assertTrue(snackbarList.isEmpty())
        verify(exactly = 1) { AppLogger.e(any(), any(), any()) }

        // clean up
        snackbarJob.cancel()
    }

    @Test
    fun `나의 키워드 리스트를 로컬에서 조회 시 예외가 발생하는 경우 로깅 후 나의 프로필은 정상적으로 업데이트 된다`() = runTest {
        // given
        every {
            usecases.getMyKeywords()
        } returns flow { throw Exception() }
        viewModel = MyProfileViewModel(snackbarController, usecases)

        val snackbarList = mutableListOf<SnackbarEvent>()
        val snackbarJob = launch {
            snackbarController.events.toList(snackbarList)
        }

        // when, then
        testState(
            viewModel = viewModel,
            intents = listOf(),
            assertions = listOf(
                MyProfileContract.UiState(
                    myProfile = TestMyProfile.toUiModel(),
                ),
            ),
        )

        // then
        assertTrue(snackbarList.isEmpty())
        verify(exactly = 1) { AppLogger.e(any(), any(), any()) }

        // clean up
        snackbarJob.cancel()
    }

    @Test
    fun `새로고침 시 프로필, 키워드 리스트가 업데이트된다`() {
        testState(
            viewModel = viewModel,
            intents = listOf(MyProfileContract.UiEvent.Refresh),
            assertions = listOf(
                MyProfileContract.UiState(
                    myProfile = TestMyProfile.toUiModel(),
                    myKeywords = TestUserKeywords.map { it.toUiModel() },
                ),
            ),
        )
    }

    @Test
    fun `나의 프로필 새로고침 시 에러가 발생하는 경우 스낵바 에러가 표시된다`() = runTest {
        // given
        val expectedError = ProfileErrorType.Unexpected(null)
        every {
            usecases.refreshMyProfile()
        } returns flowOf(Result.Error(expectedError))

        val snackbarList = mutableListOf<SnackbarEvent>()
        val snackbarJob = launch {
            snackbarController.events.toList(snackbarList)
        }

        // when, then
        testState(
            viewModel = viewModel,
            intents = listOf(MyProfileContract.UiEvent.Refresh),
            assertions = listOf(
                MyProfileContract.UiState(
                    myProfile = TestMyProfile.toUiModel(),
                    myKeywords = TestUserKeywords.map { it.toUiModel() },
                ),
            ),
        )

        // then: 스낵바 이벤트 검증
        assertTrue(snackbarList.isNotEmpty())
        assertEquals(ProfileErrorType.ProfileLoadFailed.asUiText(), snackbarList.last().message)

        // clean up
        snackbarJob.cancel()
    }

    @Test
    fun `나의 키워드 새로고침 시 에러가 발생하는 경우 스낵바 에러가 표시된다`() = runTest {
        // given
        val expectedError = ProfileErrorType.Unexpected(null)
        every {
            usecases.refreshMyKeywords()
        } returns flowOf(Result.Error(expectedError))

        val snackbarList = mutableListOf<SnackbarEvent>()
        val snackbarJob = launch {
            snackbarController.events.toList(snackbarList)
        }

        // when, then
        testState(
            viewModel = viewModel,
            intents = listOf(MyProfileContract.UiEvent.Refresh),
            assertions = listOf(
                MyProfileContract.UiState(
                    myProfile = TestMyProfile.toUiModel(),
                    myKeywords = TestUserKeywords.map { it.toUiModel() },
                ),
            ),
        )

        // then: 스낵바 이벤트 검증
        assertTrue(snackbarList.isNotEmpty())
        assertEquals(ProfileErrorType.KeywordsLoadFailed.asUiText(), snackbarList.last().message)

        // clean up
        snackbarJob.cancel()
    }

    companion object {
        private val TestMyUserId = UserId(1L)
        private val TestUserKeywords = listOf(
            UserKeyword(
                id = UserKeywordId(1L),
                keywordId = KeywordId(1L),
                keyword = KeywordName("key"),
                description = KeywordDescription("hello"),
                createdAt = 1000,
                updatedAt = 1000,
            ),
        )
        private val TestMyProfile = MyProfile(
            userId = TestMyUserId,
            displayId = DisplayId("did"),
            name = Name("name"),
            profileImageUrl = "",
            introduce = Introduce("hello"),
            lastLoginAt = 1000L,
            active = true,
            friendsCount = 51,
        )
    }
}
