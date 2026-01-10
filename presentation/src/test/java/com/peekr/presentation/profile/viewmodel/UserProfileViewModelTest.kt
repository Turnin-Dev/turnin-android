package com.peekr.presentation.profile.viewmodel

import androidx.lifecycle.SavedStateHandle
import com.peekr.core.domain.common.Result
import com.peekr.core.domain.friend.model.FriendStatus
import com.peekr.core.domain.model.DisplayId
import com.peekr.core.domain.model.Introduce
import com.peekr.core.domain.model.Name
import com.peekr.core.domain.model.UserId
import com.peekr.core.presentation.MVIBaseViewModelTest
import com.peekr.core.presentation.ui.component.snackbar.SnackbarController
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
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class UserProfileViewModelTest : MVIBaseViewModelTest<
    UserProfileContract.UiState,
    UserProfileContract.UiEvent,
    UserProfileContract.UiEffect,
    UserProfileViewModel,
>() {
    private val usecases: UserProfileUseCases = mockk()
    private lateinit var savedStateHandle: SavedStateHandle
    private lateinit var viewModel: UserProfileViewModel

    @Before
    fun setUp() {
        SnackbarController.reset()

        // Mock
        every {
            usecases.getUserProfile(TestUserId.value)
        } returns flowOf(Result.Success(TestUserProfile))

        FriendStatus.entries.forEach {
            every {
                usecases.updateFriendStatus(
                    receiverId = TestUserId.value,
                    currentFriendStatus = it,
                )
            } returns flowOf(Result.Success(it.toggle()))
        }

        savedStateHandle = TestSavedStateHandle
        viewModel = UserProfileViewModel(usecases, savedStateHandle)
    }

    @Test
    fun `초기 데이터 로드 성공 시 사용자 프로필을 정상적으로 가져온다`() {
        testState(
            viewModel = viewModel,
            assertAllState = true,
            intents = listOf(),
            assertions = listOf(
                UserProfileContract.UiState(),
                UserProfileContract.UiState(
                    userProfile = TestUserProfile.toUiModel(),
                ),
            ),
        )
    }

    @Test
    fun `초기 데이터 로드 실패 시 에러가 발생한다`() = runTest {
        // given
        val expectedError = ProfileErrorType.Unexpected(null)
        every {
            usecases.getUserProfile(any())
        } returns flowOf(Result.Error(expectedError))
        viewModel = UserProfileViewModel(usecases, savedStateHandle)

        val snackbarJob = launch {
            SnackbarController.events.collect {}
        }

        // when, then
        testState(
            viewModel = viewModel,
            intents = listOf(),
            assertions = listOf(
                UserProfileContract.UiState(
                    error = expectedError.asUiText(),
                ),
            ),
        )

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
                            userProfile = TestUserProfile.toUiModel().copy(
                                friendStatus = status.toggle(),
                            ),
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
                    userProfile = TestUserProfile.toUiModel().copy(
                        friendStatus = FriendStatus.NOTHING,
                    ),
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
            keywords = emptyList(),
        )
    }
}
