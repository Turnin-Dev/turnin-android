package com.peekr.presentation.profile.viewmodel

import com.peekr.core.domain.common.Result
import com.peekr.core.domain.common.validation.ValidationErrorType
import com.peekr.core.domain.common.validation.ValidationResult
import com.peekr.core.domain.model.DisplayId
import com.peekr.core.domain.model.Introduce
import com.peekr.core.domain.model.KeywordDescription
import com.peekr.core.domain.model.KeywordId
import com.peekr.core.domain.model.KeywordName
import com.peekr.core.domain.model.Name
import com.peekr.core.domain.model.UserId
import com.peekr.core.domain.model.UserKeywordId
import com.peekr.core.domain.userKeyword.model.PatchOffset
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
import com.peekr.presentation.profile.state.ChangedKeywordNodeOffset
import com.peekr.presentation.profile.state.KeywordTextFieldState
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
            usecases.addUserKeyword(any(), any())
        } returns flowOf(Result.Success(TestUserKeyword))
        every {
            usecases.updateUserKeywordOffset(any(), any(), any())
        } returns flowOf(Result.Success(TestPatchOffset))
        every {
            usecases.validateKeyword(any())
        } returns ValidationResult.Valid("")
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
    fun `키워드 입력 유효성 검사 - 성공 시 정상적으로 상태를 업데이트한다`() {
        val expectedKeyword = "hello"
        every {
            usecases.validateKeyword(any())
        } returns ValidationResult.Valid(expectedKeyword)
        viewModel = MyProfileViewModel(usecases)

        testState(
            viewModel = viewModel,
            intents = listOf(
                MyProfileContract.UiEvent.OnKeywordTextChanged(
                    value = expectedKeyword,
                ),
            ),
            assertions = listOf(
                MyProfileContract.UiState(
                    myProfile = TestMyProfile.toUiModel(),
                    keywordTextField = KeywordTextFieldState(
                        value = expectedKeyword,
                        error = null,
                    ),
                ),
            ),
        )
    }

    @Test
    fun `키워드 입력 유효성 검사 - 실패 시 에러 상태를 업데이트한다`() {
        val expectedError = ValidationErrorType.Unexpected
        every {
            usecases.validateKeyword(any())
        } returns ValidationResult.Invalid(expectedError)
        viewModel = MyProfileViewModel(usecases)

        testState(
            viewModel = viewModel,
            intents = listOf(
                MyProfileContract.UiEvent.OnKeywordTextChanged(
                    value = "invalid",
                ),
            ),
            assertions = listOf(
                MyProfileContract.UiState(
                    myProfile = TestMyProfile.toUiModel(),
                    keywordTextField = KeywordTextFieldState(
                        value = "invalid",
                        error = expectedError.asUiText(),
                    ),
                ),
            ),
        )
    }

    @Test
    fun `키워드 설명 입력 유효성 검사 - 성공 시 정상적으로 상태를 업데이트한다`() {
        val expectedDescription = "hello"
        every {
            usecases.validateKeywordDescription(any())
        } returns ValidationResult.Valid(expectedDescription)
        viewModel = MyProfileViewModel(usecases)

        testState(
            viewModel = viewModel,
            intents = listOf(
                MyProfileContract.UiEvent.OnKeywordDescTextChanged(
                    value = expectedDescription,
                ),
            ),
            assertions = listOf(
                MyProfileContract.UiState(
                    myProfile = TestMyProfile.toUiModel(),
                    keywordDescTextField = KeywordTextFieldState(
                        value = expectedDescription,
                        error = null,
                    ),
                ),
            ),
        )
    }

    @Test
    fun `키워드 설명 입력 유효성 검사 - 실패 시 정상적으로 에러 상태를 업데이트한다`() {
        val expectedError = ValidationErrorType.Unexpected
        every {
            usecases.validateKeywordDescription(any())
        } returns ValidationResult.Invalid(expectedError)
        viewModel = MyProfileViewModel(usecases)

        testState(
            viewModel = viewModel,
            intents = listOf(
                MyProfileContract.UiEvent.OnKeywordDescTextChanged(
                    value = "invalid-description",
                ),
            ),
            assertions = listOf(
                MyProfileContract.UiState(
                    myProfile = TestMyProfile.toUiModel(),
                    keywordDescTextField = KeywordTextFieldState(
                        value = "invalid-description",
                        error = expectedError.asUiText(),
                    ),
                ),
            ),
        )
    }

    @Test
    fun `키워드 추가 - 이벤트 발생 후 성공 시 일부 값을 리셋하고 새로고침을 수행한다`() = runTest {
        // given
        val snackbarJob = launch {
            SnackbarController.events.collect {}
        }

        // when, then
        testState(
            viewModel = viewModel,
            intents = listOf(
                MyProfileContract.UiEvent.AddKeyword("", ""),
            ),
            assertions = listOf(
                MyProfileContract.UiState(
                    // (초기 데이터 로드)
                    myProfile = TestMyProfile.toUiModel(),
                    // 에러 및 텍스트 필드 리셋
                    fullScreenLoading = false,
                    error = null,
                    keywordTextField = KeywordTextFieldState(),
                    keywordDescTextField = KeywordTextFieldState(),
                    // 선택된 키워드 상태 값 리셋
                    selectedKeyword = SelectedKeywordState(),
                ),
            ),
        )

        // clean up
        snackbarJob.cancel()
    }

    @Test
    fun `키워드 추가 - 이벤트 발생 후 성공 시 "모든 모달을 닫는" 일회성 이벤트를 발행한다`() = runTest {
        // given
        val snackbarJob = launch {
            SnackbarController.events.collect {}
        }

        // when, then
        testEffect(
            viewModel = viewModel,
            intents = listOf(
                MyProfileContract.UiEvent.AddKeyword("", ""),
            ),
            assertions = listOf(
                MyProfileContract.UiEffect.CloseAllModals,
            ),
        )

        // clean up
        snackbarJob.cancel()
    }

    @Test
    fun `키워드 추가 - 이벤트 발생 후 실패 시 에러 상태를 업데이트하고 스낵바를 표시한다`() = runTest {
        // given
        val expectedError = ProfileErrorType.Unexpected(null)
        every {
            usecases.addUserKeyword(any(), any())
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
                MyProfileContract.UiEvent.AddKeyword("", ""),
            ),
            assertions = listOf(
                MyProfileContract.UiState(
                    // (초기 데이터 로드)
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
    fun `키워드 노드 오프셋 변경 - 이벤트 발생 시 정상적으로 상태를 업데이트한다`() {
        val expectedUserKeywordId = UserKeywordId(1L)
        val expectedKeywordNodeOffset = ChangedKeywordNodeOffset(1.0f, 2.0f)

        testState(
            viewModel = viewModel,
            intents = listOf(
                MyProfileContract.UiEvent.OnKeywordNodeOffsetChanged(
                    userKeywordId = expectedUserKeywordId,
                    offsetX = expectedKeywordNodeOffset.offsetX,
                    offsetY = expectedKeywordNodeOffset.offsetY,
                ),
            ),
            assertions = listOf(
                MyProfileContract.UiState(
                    myProfile = TestMyProfile.toUiModel(),
                    updatedKeywordNodesOffset =
                        mapOf(
                            expectedUserKeywordId to expectedKeywordNodeOffset,
                        ),
                ),
            ),
        )
    }

    @Test
    fun `키워드 노드 오프셋 업데이트 - 이벤트 발생 후 성공 시 스낵바를 표시한다`() = runTest {
        // given
        val snackbarEvents = mutableListOf<SnackbarEvent>()
        val snackbarJob = launch {
            SnackbarController.events.toList(snackbarEvents)
        }

        // when, then
        testState(
            viewModel = viewModel,
            intents = listOf(
                MyProfileContract.UiEvent.UpdateKeywordNodeOffset,
            ),
            assertions = listOf(
                MyProfileContract.UiState(
                    myProfile = TestMyProfile.toUiModel(),
                    fullScreenLoading = false,
                    error = null,
                ),
            ),
        )

        // then: 스낵바 호출되었는지 검증
        assertTrue(snackbarEvents.isNotEmpty())

        // clean up
        snackbarJob.cancel()
    }

    @Test
    fun `키워드 노드 오프셋 업데이트 - 이벤트 발생 후 실패 시 에러 상태를 업데이트하고 스낵바를 표시한다`() = runTest {
        // given
        val expectedError = ProfileErrorType.Unexpected(null)
        every {
            usecases.updateUserKeywordOffset(
                userKeywordId = any(),
                offsetX = any(),
                offsetY = any(),
            )
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
                MyProfileContract.UiEvent.OnKeywordNodeOffsetChanged(
                    userKeywordId = UserKeywordId(1L),
                    offsetX = 1.0f,
                    offsetY = 2.0f,
                ),
                MyProfileContract.UiEvent.UpdateKeywordNodeOffset,
            ),
            assertions = listOf(
                MyProfileContract.UiState(
                    myProfile = TestMyProfile.toUiModel(),
                    fullScreenLoading = false,
                    updatedKeywordNodesOffset = mapOf(
                        UserKeywordId(1L) to ChangedKeywordNodeOffset(1.0f, 2.0f),
                    ),
                    error = expectedError.asUiText(),
                ),
            ),
        )

        // then: 스낵바 호출되었는지 검증
        assertTrue(snackbarEvents.isNotEmpty())

        // clean up
        snackbarJob.cancel()
    }

    @Test
    fun `키워드 노드 오프셋 리셋 - 이벤트 발생 시 변경된 키워드 노드 오프셋 상태 값을 빈 Map로 업데이트한다`() {
        testState(
            viewModel = viewModel,
            assertAllState = true,
            intents = listOf(
                MyProfileContract.UiEvent.OnKeywordNodeOffsetChanged(
                    userKeywordId = UserKeywordId(1L),
                    offsetX = 1.0f,
                    offsetY = 2.0f,
                ),
                MyProfileContract.UiEvent.ResetKeywordNodeOffset,
            ),
            assertions = listOf(
                MyProfileContract.UiState(
                    myProfile = TestMyProfile.toUiModel(),
                ),
                MyProfileContract.UiState(
                    myProfile = TestMyProfile.toUiModel(),
                    updatedKeywordNodesOffset = mapOf(
                        UserKeywordId(1L) to ChangedKeywordNodeOffset(1.0f, 2.0f),
                    ),
                ),
                MyProfileContract.UiState(
                    myProfile = TestMyProfile.toUiModel(),
                    updatedKeywordNodesOffset = emptyMap(),
                ),
            ),
        )
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
                    keywordTextField = KeywordTextFieldState(),
                    keywordDescTextField = KeywordTextFieldState(),
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
    fun `키워드 추가 모달에서 취소 전에 텍스트 필드에 입력된 값이 있는지 확인하는 이벤트 발생 후 성공 시 조건에 따라 이벤트를 발행한다`() {
        // 키워드, 키워드 설명이 하나라도 비어있지 않는 경우 '안전 취소 모달' 이벤트를 발행한다.
        testEffect(
            viewModel = viewModel,
            intents = listOf(
                MyProfileContract.UiEvent.CheckSafeCancel(
                    keyword = "hello",
                    description = "",
                ),
            ),
            assertions = listOf(
                MyProfileContract.UiEffect.OpenSafeCancelModal,
            ),
        )

        // 키워드, 키워드 설명이 전부 비어있는 경우 모든 모달을 닫는 이벤트를 발행한다.
        testEffect(
            viewModel = viewModel,
            intents = listOf(
                MyProfileContract.UiEvent.CheckSafeCancel(
                    keyword = "",
                    description = "",
                ),
            ),
            assertions = listOf(
                MyProfileContract.UiEffect.CloseAllModals,
            ),
        )
    }

    @Test
    fun `모든 모달을 닫고 텍스트 필드를 초기화하는 이벤트 발생 시 텍스트 필드 상태를 초기화한다`() {
        testState(
            viewModel = viewModel,
            intents = listOf(
                MyProfileContract.UiEvent.CloseAllModalsAndResetTextField,
            ),
            assertions = listOf(
                MyProfileContract.UiState(
                    myProfile = TestMyProfile.toUiModel(),
                    keywordTextField = KeywordTextFieldState(),
                    keywordDescTextField = KeywordTextFieldState(),
                ),
            ),
        )
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
            offsetX = 0.0,
            offsetY = 0.0,
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
        private val TestPatchOffset = PatchOffset(
            offsetX = 100.0,
            offsetY = 200.0,
        )
    }
}
