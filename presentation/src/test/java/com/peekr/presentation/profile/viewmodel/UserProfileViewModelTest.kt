package com.peekr.presentation.profile.viewmodel

import androidx.lifecycle.SavedStateHandle
import com.peekr.core.domain.common.Result
import com.peekr.core.domain.friend.model.FriendStatus
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
import com.peekr.domain.profile.model.UserProfile
import com.peekr.domain.profile.usecase.UserProfileUseCases
import com.peekr.presentation.profile.error.asUiText
import com.peekr.presentation.profile.model.toUiModel
import com.peekr.presentation.profile.state.UserProfileContract
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class UserProfileViewModelTest : MVIBaseViewModelTest<
    UserProfileContract.UiState,
    UserProfileContract.UiEvent,
    UserProfileContract.UiEffect,
    UserProfileViewModel,
>() {
    private val snackbarController = FakeSnackbarController()
    private val usecases: UserProfileUseCases = mockk()
    private lateinit var savedStateHandle: SavedStateHandle
    private lateinit var viewModel: UserProfileViewModel

    @Before
    fun setUp() {
        // Mock
        every {
            usecases.getUserProfile(TestUserId.value)
        } returns flowOf(Result.Success(TestUserProfile))
        every {
            usecases.getUserKeywords(TestUserId.value)
        } returns flowOf(Result.Success(TestUserKeywords))

        FriendStatus.entries.forEach {
            every {
                usecases.updateFriendStatus(
                    receiverId = TestUserId.value,
                    currentFriendStatus = it,
                )
            } returns flowOf(Result.Success(it.toggle()))
        }

        savedStateHandle = TestSavedStateHandle
        viewModel = UserProfileViewModel(snackbarController, usecases, savedStateHandle)
    }

    @Test
    fun `초기 데이터 로드 성공 시 사용자 프로필과 키워드 리스트를 정상적으로 가져온다`() {
        testState(
            viewModel = viewModel,
            assertAllState = true,
            intents = listOf(),
            assertions = listOf(
                UserProfileContract.UiState(),
                UserProfileContract.UiState(
                    profile = TestUserProfile.toUiModel(),
                    keywords = TestUserKeywords.map { it.toUiModel() },
                ),
            ),
        )
    }

    @Test
    fun `사용자 프로필 조회 시 에러가 발생하는 경우 에러 발생 후 키워드 리스트는 정상적으로 업데이트 된다`() = runTest {
        // given
        val expectedError = ProfileErrorType.Unexpected(null)
        every {
            usecases.getUserProfile(any())
        } returns flowOf(Result.Error(expectedError))
        viewModel = UserProfileViewModel(snackbarController, usecases, savedStateHandle)

        val snackbarList = mutableListOf<SnackbarEvent>()
        val snackbarJob = launch {
            snackbarController.events.toList(snackbarList)
        }

        // when, then
        testState(
            viewModel = viewModel,
            intents = listOf(),
            assertions = listOf(
                UserProfileContract.UiState(
                    profileError = expectedError.asUiText(),
                    keywords = TestUserKeywords.map { it.toUiModel() },
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
    fun `사용자 키워드 리스트 조회 시 에러가 발생하는 경우 에러 발생 후 프로필은 정상적으로 업데이트 된다`() = runTest {
        // given
        val expectedError = ProfileErrorType.Unexpected(null)
        every {
            usecases.getUserKeywords(any())
        } returns flowOf(Result.Error(expectedError))
        viewModel = UserProfileViewModel(snackbarController, usecases, savedStateHandle)

        val snackbarList = mutableListOf<SnackbarEvent>()
        val snackbarJob = launch {
            snackbarController.events.toList(snackbarList)
        }

        // when, then
        testState(
            viewModel = viewModel,
            intents = listOf(),
            assertions = listOf(
                UserProfileContract.UiState(
                    keywordsError = expectedError.asUiText(),
                    profile = TestUserProfile.toUiModel(),
                ),
            ),
        )

        // then: 스낵바 이벤트 검증
        assertTrue(snackbarList.isNotEmpty())
        assertEquals(ProfileErrorType.KeywordsLoadFailed.asUiText(), snackbarList.last().message)

        // clean up
        snackbarJob.cancel()
    }

    @Test
    fun `친구 상태 업데이트 이벤트 발생 시 정상적으로 친구 상태가 업데이트된다`() {
        FriendStatus.entries.forEach { status ->
            if (status != FriendStatus.FRIENDS) {
                testState(
                    viewModel = viewModel,
                    intents = listOf(
                        UserProfileContract.UiEvent.OnFriendButtonClick(
                            friendStatus = status,
                        ),
                    ),
                    assertions = listOf(
                        UserProfileContract.UiState(
                            profile = TestUserProfile.toUiModel().copy(
                                friendStatus = status.toggle(),
                            ),
                            keywords = TestUserKeywords.map { it.toUiModel() },
                        ),
                    ),
                )
            }
        }
    }

    @Test
    fun `"FRIENDS" 상태에서 친구 상태 업데이트 이벤트 발생 시 모달 이벤트 발행 후 모달에서 삭제를 진행해야 정상적으로 업데이트된다`() {
        // 모달 일회성 이벤트 발행 테스트
        testEffect(
            viewModel = viewModel,
            intents = listOf(
                UserProfileContract.UiEvent.OnFriendButtonClick(
                    friendStatus = FriendStatus.FRIENDS,
                ),
            ),
            assertions = listOf(
                UserProfileContract.UiEffect.OpenDeleteFriendModal,
            ),
        )

        // 상태 업데이트 테스트: FRIENDS -> NOTHING
        testState(
            viewModel = viewModel,
            intents = listOf(
                UserProfileContract.UiEvent.OnFriendButtonClick(
                    friendStatus = FriendStatus.FRIENDS,
                ),
                UserProfileContract.UiEvent.DeleteFriend,
            ),
            assertions = listOf(
                UserProfileContract.UiState(
                    profile = TestUserProfile.toUiModel().copy(
                        friendStatus = FriendStatus.NOTHING,
                    ),
                    keywords = TestUserKeywords.map { it.toUiModel() },
                ),
            ),
        )
    }

    companion object {
        private val TestUserId = UserId(1L)
        private val TestSavedStateHandle = SavedStateHandle(
            mapOf("userId" to TestUserId.value),
        )
        private val TestUserProfile = UserProfile(
            userId = TestUserId,
            displayId = DisplayId("did"),
            name = Name("name"),
            profileImageUrl = null,
            introduce = Introduce("hello"),
            lastLoginAt = 1000L,
            friendsCount = 50L,
            active = true,
            friendStatus = FriendStatus.NOTHING,
        )
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
    }
}
