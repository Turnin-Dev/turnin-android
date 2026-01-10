package com.peekr.presentation.profile.viewmodel

import com.peekr.core.domain.common.Result
import com.peekr.core.domain.common.validation.ValidationResult
import com.peekr.core.domain.model.DisplayId
import com.peekr.core.domain.model.Introduce
import com.peekr.core.domain.model.KeywordDescription
import com.peekr.core.domain.model.KeywordId
import com.peekr.core.domain.model.KeywordName
import com.peekr.core.domain.model.Name
import com.peekr.core.domain.model.UserId
import com.peekr.core.domain.model.UserKeywordId
import com.peekr.core.domain.userKeyword.model.UserKeyword
import com.peekr.core.presentation.MVIBaseViewModelTest
import com.peekr.core.presentation.common.error.asUiText
import com.peekr.core.presentation.ui.component.snackbar.SnackbarController
import com.peekr.core.presentation.ui.component.snackbar.SnackbarEvent
import com.peekr.domain.profile.error.ProfileErrorType
import com.peekr.domain.profile.model.MyProfile
import com.peekr.domain.profile.usecase.MyProfileUseCases
import com.peekr.presentation.profile.error.asUiText
import com.peekr.presentation.profile.model.toUiModel
import com.peekr.presentation.profile.state.MyProfileContract
import com.peekr.presentation.profile.state.SelectedKeywordState
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class MyProfileViewModelTest : MVIBaseViewModelTest<
    MyProfileContract.UiState,
    MyProfileContract.UiEvent,
    MyProfileContract.UiEffect,
    MyProfileViewModel,
>() {
    private val usecases: MyProfileUseCases = mockk()
    private lateinit var viewModel: MyProfileViewModel

    @Before
    fun setUp() {
        // Mock
        every {
            usecases.getMyProfile()
        } returns flowOf(Result.Success(TestMyProfile))
        every {
            usecases.deleteUserKeyword(any())
        } returns flowOf(Result.Success(Unit))
        every {
            usecases.validateKeywordDescription(any())
        } returns ValidationResult.Valid("")

        viewModel = MyProfileViewModel(usecases)
    }

    @Test
    fun `초기 데이터 로드 - 성공 시 나의 프로필을 정상적으로 가져온다`() {
        testState(
            viewModel = viewModel,
            intents = listOf(),
            assertions = listOf(
                MyProfileContract.UiState(
                    myProfile = TestMyProfile.toUiModel(),
                ),
            ),
        )
    }

    @Test
    fun `초기 데이터 로드 - 실패 시 에러 스낵바를 표시한다`() = runTest {
        // given
        val expectedError = ProfileErrorType.Unexpected(null)
        every {
            usecases.getMyProfile()
        } returns flowOf(Result.Error(expectedError))
        viewModel = MyProfileViewModel(usecases)

        val snackEvents = mutableListOf<SnackbarEvent>()
        val snackbarJob = launch {
            SnackbarController.events.toList(snackEvents)
        }

        // when, then
        testState(
            viewModel = viewModel,
            intents = listOf(),
            assertions = listOf(
                MyProfileContract.UiState(
                    myProfile = null,
                    loading = false,
                    error = expectedError.asUiText(),
                ),
            ),
        )

        // then
        assertTrue(snackEvents.isNotEmpty())

        // clean up
        snackbarJob.cancel()
    }

    @Test
    fun `키워드 삭제 - 이벤트 발생 후 성공 시 일부 값을 리셋하고 새로고침을 수행한다`() {
        testState(
            viewModel = viewModel,
            intents = listOf(
                MyProfileContract.UiEvent.DeleteKeyword(UserKeywordId(1L)),
            ),
            assertions = listOf(
                MyProfileContract.UiState(
                    myProfile = TestMyProfile.toUiModel(),
                    fullScreenLoading = false,
                    error = null,
                    selectedKeyword = SelectedKeywordState(),
                ),
            ),
        )
    }

    @Test
    fun `키워드 삭제 - 이벤트 발생 후 성공 시 "모든 모달을 닫는" 일회성 이벤트를 발행하고 스낵바를 표시한다`() = runTest {
        // given
        val snackbarEvents = mutableListOf<SnackbarEvent>()
        val snackbarJob = launch {
            SnackbarController.events.toList(snackbarEvents)
        }

        // when, then
        testEffect(
            viewModel = viewModel,
            intents = listOf(
                MyProfileContract.UiEvent.DeleteKeyword(UserKeywordId(1L)),
            ),
            assertions = listOf(
                MyProfileContract.UiEffect.CloseAllModals,
            ),
        )

        // clean up
        snackbarJob.cancel()
    }

    @Test
    fun `키워드 삭제 - 이벤트 발생 후 실패 시 에러 상태를 업데이트하고 스낵바를 표시한다`() = runTest {
        // given
        val expectedError = ProfileErrorType.Unexpected(null)
        every {
            usecases.deleteUserKeyword(UserKeywordId(1L))
        } returns flowOf(Result.Error(expectedError))
        viewModel = MyProfileViewModel(usecases)

        val snackbarEvents = mutableListOf<SnackbarEvent>()
        val snackbarJob = launch {
            SnackbarController.events.toList(snackbarEvents)
        }

        // when, then
        testState(
            viewModel = viewModel,
            intents = listOf(
                MyProfileContract.UiEvent.DeleteKeyword(UserKeywordId(1L)),
            ),
            assertions = listOf(
                MyProfileContract.UiState(
                    myProfile = TestMyProfile.toUiModel(),
                    fullScreenLoading = false,
                    error = expectedError.asUiText(),
                ),
            ),
        )

        // then
        assertTrue(snackbarEvents.isNotEmpty())

        // clean up
        snackbarJob.cancel()
    }

    @Test
    fun `모든 모달을 닫고 텍스트 필드를 초기화하는 이벤트 발생 시 "모든 모달을 닫는" 이벤트를 발행한다`() {
        testEffect(
            viewModel = viewModel,
            intents = listOf(
                MyProfileContract.UiEvent.CloseAllModalsAndResetTextField,
            ),
            assertions = listOf(
                MyProfileContract.UiEffect.CloseAllModals,
            ),
        )
    }

    @Test
    fun `선택된 키워드 변경 - 이벤트 발생 시 상태를 업데이트한다`() {
        val expectedUserKeywordId = UserKeywordId(1L)
        val expectedKeyword = "hello"

        testState(
            viewModel = viewModel,
            intents = listOf(
                MyProfileContract.UiEvent.OnSelectedKeywordChanged(
                    userKeywordId = expectedUserKeywordId,
                    keyword = expectedKeyword,
                ),
            ),
            assertions = listOf(
                MyProfileContract.UiState(
                    myProfile = TestMyProfile.toUiModel(),
                    selectedKeyword = SelectedKeywordState(
                        userKeywordId = expectedUserKeywordId,
                        keyword = expectedKeyword,
                    ),
                ),
            ),
        )
    }

    companion object {
        private val TestMyUserId = UserId(1L)
        private val TestUserKeyword = UserKeyword(
            id = UserKeywordId(1L),
            keywordId = KeywordId(1L),
            keyword = KeywordName("key"),
            userId = TestMyUserId,
            description = KeywordDescription("hello"),
            createdAt = 1000,
            updatedAt = 1000,
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
            keywords = listOf(TestUserKeyword),
        )
    }
}
