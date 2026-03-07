package com.peekr.presentation.setting.viewmodel

import androidx.lifecycle.SavedStateHandle
import com.peekr.core.domain.common.Result
import com.peekr.core.domain.common.validation.ValidationResult
import com.peekr.core.presentation.FakeSnackbarController
import com.peekr.core.presentation.MVIBaseViewModelTest
import com.peekr.core.presentation.common.snackbar.SnackbarEvent
import com.peekr.core.presentation.ui.util.UiText
import com.peekr.domain.setting.error.SettingErrorType
import com.peekr.domain.setting.model.SettingProfileImagePatch
import com.peekr.domain.setting.usecase.AccountInfoUseCases
import com.peekr.presentation.R
import com.peekr.presentation.setting.error.asUiText
import com.peekr.presentation.setting.model.UiEditableAccountInfo
import com.peekr.presentation.setting.state.AccountInfoContract
import io.mockk.clearMocks
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertArrayEquals
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class AccountInfoViewModelTest :
    MVIBaseViewModelTest<
        AccountInfoContract.UiState,
        AccountInfoContract.UiEvent,
        AccountInfoContract.UiEffect,
        AccountInfoViewModel,
    >() {
    private val usecases: AccountInfoUseCases = mockk()
    private val snackbarController = FakeSnackbarController()
    private lateinit var viewModel: AccountInfoViewModel

    private fun createSavedStateHandle(
        displayId: String? = TEST_DISPLAY_ID,
        name: String? = TEST_NAME,
        introduce: String? = TEST_INTRODUCE,
        profileImageUrl: String? = TEST_PROFILE_IMAGE_URL,
    ) = SavedStateHandle(
        mapOf(
            "displayId" to displayId,
            "name" to name,
            "introduce" to introduce,
            "profileImageUrl" to profileImageUrl,
        ),
    )

    @Before
    fun setUp() {
        every {
            usecases.validateDisplayId(any())
        } returns flowOf(ValidationResult.Valid(TEST_DISPLAY_ID))

        every {
            usecases.validateName(any())
        } returns ValidationResult.Valid(TEST_NAME)

        every {
            usecases.validateIntroduce(any())
        } returns ValidationResult.Valid(TEST_INTRODUCE)

        every {
            usecases.updateAccountInfo(
                displayId = any(),
                name = any(),
                introduce = any(),
                oldProfileImageUrl = any(),
                profileImagePatch = any(),
            )
        } returns flowOf(Result.Success(Unit))

        viewModel = AccountInfoViewModel(
            usecases = usecases,
            snackbarController = snackbarController,
            savedStateHandle = createSavedStateHandle(),
        )
    }

    // =====================================================================
    // 초기화 테스트
    // =====================================================================

    @Test
    fun `SavedStateHandle에 초기값이 있을 경우 UiState의 accountInfo가 정상적으로 설정된다`() {
        testState(
            viewModel = viewModel,
            intents = listOf(),
            assertions = listOf(
                AccountInfoContract.UiState(
                    accountInfo = UiEditableAccountInfo(
                        displayId = TEST_DISPLAY_ID,
                        name = TEST_NAME,
                        introduce = TEST_INTRODUCE,
                        profileImageUrl = TEST_PROFILE_IMAGE_URL,
                    ),
                ),
            ),
        )
    }

    // 필수 인자가 없을 때 스낵바 에러 → displayId만 대표로 검증
    @Test
    fun `필수 인자가 null인 경우 스낵바 에러가 표시된다`() = runTest {
        // given
        val snackbarList = mutableListOf<SnackbarEvent>()
        val snackbarJob = launch { snackbarController.events.toList(snackbarList) }

        viewModel = AccountInfoViewModel(
            usecases = usecases,
            snackbarController = snackbarController,
            savedStateHandle = createSavedStateHandle(displayId = null),
        )
        advanceUntilIdle()

        // then
        assertTrue(snackbarList.isNotEmpty())
        assertEquals(
            UiText.StringResource(R.string.setting_error_my_profile_not_found),
            snackbarList.last().message,
        )

        snackbarJob.cancel()
    }

    // =====================================================================
    // 텍스트 필드 변경 테스트
    // =====================================================================

    @Test
    fun `텍스트 필드 변경 시 UiState의 accountInfo와 isAccountInfoEdited가 업데이트된다`() = runTest {
        testState(
            viewModel = viewModel,
            intents = listOf(
                AccountInfoContract.UiEvent.OnDisplayIdChanged(TEST_CHANGED_DISPLAY_ID),
                AccountInfoContract.UiEvent.OnNameChanged(TEST_CHANGED_NAME),
                AccountInfoContract.UiEvent.OnIntroduceChanged(TEST_CHANGED_INTRODUCE),
            ),
            assertions = listOf(
                AccountInfoContract.UiState(
                    accountInfo = UiEditableAccountInfo(
                        displayId = TEST_CHANGED_DISPLAY_ID,
                        name = TEST_CHANGED_NAME,
                        introduce = TEST_CHANGED_INTRODUCE,
                        profileImageUrl = TEST_PROFILE_IMAGE_URL,
                    ),
                    isAccountInfoEdited = true,
                ),
            ),
        )
    }

    @Test
    fun `텍스트 필드가 초기값으로 돌아오면 isAccountInfoEdited가 false이다`() = runTest {
        testState(
            viewModel = viewModel,
            intents = listOf(
                AccountInfoContract.UiEvent.OnDisplayIdChanged(TEST_CHANGED_DISPLAY_ID),
                AccountInfoContract.UiEvent.OnDisplayIdChanged(TEST_DISPLAY_ID),
            ),
            assertions = listOf(
                AccountInfoContract.UiState(
                    accountInfo = UiEditableAccountInfo(
                        displayId = TEST_DISPLAY_ID,
                        name = TEST_NAME,
                        introduce = TEST_INTRODUCE,
                        profileImageUrl = TEST_PROFILE_IMAGE_URL,
                    ),
                    isAccountInfoEdited = false,
                ),
            ),
        )
    }

    // =====================================================================
    // displayId 유효성 검사 테스트
    // =====================================================================

    @Test
    fun `displayId 유효성 검사 성공 시 isDisplayIdValid가 true로 업데이트된다`() = runTest {
        // given
        every {
            usecases.validateDisplayId(TEST_CHANGED_DISPLAY_ID)
        } returns flowOf(ValidationResult.Valid(TEST_CHANGED_DISPLAY_ID))

        // when
        viewModel.processEvent(AccountInfoContract.UiEvent.OnDisplayIdChanged(TEST_CHANGED_DISPLAY_ID))
        advanceTimeBy(301)
        runCurrent()

        // then
        assertTrue(viewModel.displayIdFieldState.value.isDisplayIdValid)
        assertNull(viewModel.displayIdFieldState.value.displayIdError)
        assertFalse(viewModel.displayIdFieldState.value.loading)
    }

    @Test
    fun `displayId 유효성 검사 실패 시 isDisplayIdValid가 false이고 에러가 설정된다`() = runTest {
        // given
        val expectedError = SettingErrorType.DisplayIdNotAvailable
        every {
            usecases.validateDisplayId(TEST_CHANGED_DISPLAY_ID)
        } returns flowOf(ValidationResult.Invalid(expectedError))

        // when
        viewModel.processEvent(AccountInfoContract.UiEvent.OnDisplayIdChanged(TEST_CHANGED_DISPLAY_ID))
        advanceTimeBy(301)
        runCurrent()

        // then
        assertFalse(viewModel.displayIdFieldState.value.isDisplayIdValid)
        assertEquals(expectedError.asUiText(), viewModel.displayIdFieldState.value.displayIdError)
        assertFalse(viewModel.displayIdFieldState.value.loading)
    }

    // 엣지 케이스: 빠른 연속 입력 시 마지막 값에 대해서만 검증
    @Test
    fun `displayId를 빠르게 연속 입력 시 마지막 값에 대해서만 validateDisplayId가 호출된다`() = runTest {
        // given
        advanceTimeBy(301)
        runCurrent()
        clearMocks(usecases, answers = false)
        every {
            usecases.validateDisplayId(any())
        } returns flowOf(ValidationResult.Valid(TEST_CHANGED_DISPLAY_ID))

        // when
        viewModel.processEvent(AccountInfoContract.UiEvent.OnDisplayIdChanged("a"))
        viewModel.processEvent(AccountInfoContract.UiEvent.OnDisplayIdChanged("ab"))
        viewModel.processEvent(AccountInfoContract.UiEvent.OnDisplayIdChanged(TEST_CHANGED_DISPLAY_ID))
        advanceTimeBy(301)
        runCurrent()

        // then
        verify(exactly = 1) { usecases.validateDisplayId(any()) }
        verify(exactly = 1) { usecases.validateDisplayId(TEST_CHANGED_DISPLAY_ID) }
    }

    @Test
    fun `displayId를 초기값과 동일하게 입력 시 validateDisplayId가 호출되지 않는다`() = runTest {
        // given
        advanceTimeBy(301)
        runCurrent()
        clearMocks(usecases, answers = false)

        // when: 초기값과 동일한 값 입력
        viewModel.processEvent(AccountInfoContract.UiEvent.OnDisplayIdChanged(TEST_DISPLAY_ID))
        viewModel.processEvent(AccountInfoContract.UiEvent.OnDisplayIdChanged(TEST_DISPLAY_ID))
        advanceTimeBy(301)
        runCurrent()

        // then
        verify(exactly = 0) { usecases.validateDisplayId(any()) }
    }

    // =====================================================================
    // 계정 정보 저장 테스트
    // =====================================================================

    @Test
    fun `계정 정보 수정 후 저장 성공 시 isAccountInfoEdited가 false로 업데이트되고 스낵바가 표시된다`() = runTest {
        // given
        every {
            usecases.validateDisplayId(TEST_CHANGED_DISPLAY_ID)
        } returns flowOf(ValidationResult.Valid(TEST_CHANGED_DISPLAY_ID))

        val snackbarList = mutableListOf<SnackbarEvent>()
        val snackbarJob = launch { snackbarController.events.toList(snackbarList) }
        val states = mutableListOf<AccountInfoContract.UiState>()
        val job = launch { viewModel.uiState.toList(states) }
        advanceUntilIdle()

        viewModel.processEvent(AccountInfoContract.UiEvent.OnDisplayIdChanged(TEST_CHANGED_DISPLAY_ID))
        advanceTimeBy(301)
        runCurrent()

        // when
        viewModel.processEvent(AccountInfoContract.UiEvent.OnSaveAccountInfo)
        advanceUntilIdle()

        // then
        val lastState = states.last()
        assertFalse(lastState.isAccountInfoEdited)
        assertEquals(SettingProfileImagePatch.Unchanged, lastState.profileImagePatch)
        assertNull(lastState.error)

        // 스낵바 검증
        assertEquals(
            UiText.StringResource(R.string.setting_success_update_account_info),
            snackbarList.last().message,
        )

        job.cancel()
        snackbarJob.cancel()
    }

    @Test
    fun `계정 정보 저장 성공 시 스낵바 성공 메시지가 표시된다`() = runTest {
        // given
        every {
            usecases.validateDisplayId(TEST_CHANGED_DISPLAY_ID)
        } returns flowOf(ValidationResult.Valid(TEST_CHANGED_DISPLAY_ID))
        viewModel.processEvent(AccountInfoContract.UiEvent.OnDisplayIdChanged(TEST_CHANGED_DISPLAY_ID))
        advanceTimeBy(301)
        runCurrent()

        val snackbarList = mutableListOf<SnackbarEvent>()
        val snackbarJob = launch { snackbarController.events.toList(snackbarList) }

        // when
        viewModel.processEvent(AccountInfoContract.UiEvent.OnSaveAccountInfo)
        advanceUntilIdle()

        // then
        assertTrue(snackbarList.isNotEmpty())
        assertEquals(
            UiText.StringResource(R.string.setting_success_update_account_info),
            snackbarList.last().message,
        )

        snackbarJob.cancel()
    }

    @Test
    fun `계정 정보 저장 실패 시 스낵바 에러가 표시된다`() = runTest {
        // given
        val expectedError = SettingErrorType.Unexpected(null)
        every {
            usecases.updateAccountInfo(
                displayId = any(),
                name = any(),
                introduce = any(),
                oldProfileImageUrl = any(),
                profileImagePatch = any(),
            )
        } returns flowOf(Result.Error(expectedError))

        every {
            usecases.validateDisplayId(TEST_CHANGED_DISPLAY_ID)
        } returns flowOf(ValidationResult.Valid(TEST_CHANGED_DISPLAY_ID))
        viewModel.processEvent(AccountInfoContract.UiEvent.OnDisplayIdChanged(TEST_CHANGED_DISPLAY_ID))
        advanceTimeBy(301)
        runCurrent()

        val snackbarList = mutableListOf<SnackbarEvent>()
        val snackbarJob = launch { snackbarController.events.toList(snackbarList) }

        // when
        viewModel.processEvent(AccountInfoContract.UiEvent.OnSaveAccountInfo)
        advanceUntilIdle()

        // then
        assertTrue(snackbarList.isNotEmpty())
        assertEquals(expectedError.asUiText(), snackbarList.last().message)

        snackbarJob.cancel()
    }

    // 엣지 케이스: 수정 안 했을 때 저장 불가
    @Test
    fun `isAccountInfoEdited가 false인 경우 저장 이벤트를 보내도 updateAccountInfo가 호출되지 않는다`() = runTest {
        testState(
            viewModel = viewModel,
            intents = listOf(AccountInfoContract.UiEvent.OnSaveAccountInfo),
            assertions = listOf(
                AccountInfoContract.UiState(
                    accountInfo = UiEditableAccountInfo(
                        displayId = TEST_DISPLAY_ID,
                        name = TEST_NAME,
                        introduce = TEST_INTRODUCE,
                        profileImageUrl = TEST_PROFILE_IMAGE_URL,
                    ),
                ),
            ),
        )

        verify(exactly = 0) {
            usecases.updateAccountInfo(any(), any(), any(), any(), any())
        }
    }

    // 엣지 케이스: 유효성 검사 실패 시 저장 불가 → displayId만 대표로 검증
    @Test
    fun `displayId 유효성 검사가 Invalid인 경우 저장 이벤트를 보내도 updateAccountInfo가 호출되지 않는다`() = runTest {
        // given
        every {
            usecases.validateDisplayId(TEST_CHANGED_DISPLAY_ID)
        } returns flowOf(ValidationResult.Invalid(SettingErrorType.DisplayIdNotAvailable))

        viewModel.processEvent(AccountInfoContract.UiEvent.OnDisplayIdChanged(TEST_CHANGED_DISPLAY_ID))
        advanceTimeBy(301)
        runCurrent()

        testState(
            viewModel = viewModel,
            intents = listOf(AccountInfoContract.UiEvent.OnSaveAccountInfo),
            assertions = listOf(
                AccountInfoContract.UiState(
                    accountInfo = UiEditableAccountInfo(
                        displayId = TEST_CHANGED_DISPLAY_ID,
                        name = TEST_NAME,
                        introduce = TEST_INTRODUCE,
                        profileImageUrl = TEST_PROFILE_IMAGE_URL,
                    ),
                    isAccountInfoEdited = true,
                ),
            ),
        )

        verify(exactly = 0) {
            usecases.updateAccountInfo(any(), any(), any(), any(), any())
        }
    }

    // =====================================================================
    // 프로필 이미지 테스트
    // =====================================================================

    @Test
    fun `프로필 이미지 업데이트 시 localProfileImage와 profileImagePatch가 업데이트된다`() = runTest {
        // given
        val states = mutableListOf<AccountInfoContract.UiState>()
        val job = launch {
            viewModel.uiState.toList(states)
        }
        advanceUntilIdle()

        // when
        viewModel.processEvent(AccountInfoContract.UiEvent.OnProfileImageUpdated(TestImageBytes))
        advanceUntilIdle()

        // then
        val lastState = states.last()
        assertArrayEquals(TestImageBytes, viewModel.localProfileImage.value)
        assertTrue(lastState.isAccountInfoEdited)
        assertTrue(lastState.profileImagePatch is SettingProfileImagePatch.Update)
        assertArrayEquals(
            TestImageBytes,
            (lastState.profileImagePatch as SettingProfileImagePatch.Update).imageBytes,
        )

        job.cancel()
    }

    @Test
    fun `프로필 이미지가 null로 들어오는 경우 스낵바 에러가 표시된다`() = runTest {
        // given
        val snackbarList = mutableListOf<SnackbarEvent>()
        val snackbarJob = launch { snackbarController.events.toList(snackbarList) }

        // when
        viewModel.processEvent(AccountInfoContract.UiEvent.OnProfileImageUpdated(null))
        advanceUntilIdle()

        // then
        assertTrue(snackbarList.isNotEmpty())
        assertEquals(
            UiText.StringResource(R.string.setting_error_updated_image_not_found),
            snackbarList.last().message,
        )

        snackbarJob.cancel()
    }

    @Test
    fun `프로필 이미지 삭제 시 localProfileImage가 null이 되고 profileImagePatch가 Remove로 업데이트된다`() = runTest {
        // given
        val states = mutableListOf<AccountInfoContract.UiState>()
        val job = launch {
            viewModel.uiState.toList(states)
        }

        // when
        viewModel.processEvent(AccountInfoContract.UiEvent.OnProfileImageDeleted)
        advanceUntilIdle()

        // then
        val lastState = states.last()
        assertNull(viewModel.localProfileImage.value)
        assertEquals(SettingProfileImagePatch.Remove, lastState.profileImagePatch)

        job.cancel()
    }

    // 엣지 케이스: 초기 이미지 유무에 따른 isAccountInfoEdited 차이
    @Test
    fun `초기 profileImageUrl이 있는 경우 이미지 삭제 시 isAccountInfoEdited가 true이다`() = runTest {
        testState(
            viewModel = viewModel,
            intents = listOf(AccountInfoContract.UiEvent.OnProfileImageDeleted),
            assertions = listOf(
                AccountInfoContract.UiState(
                    accountInfo = UiEditableAccountInfo(
                        displayId = TEST_DISPLAY_ID,
                        name = TEST_NAME,
                        introduce = TEST_INTRODUCE,
                        profileImageUrl = null,
                    ),
                    isAccountInfoEdited = true,
                    profileImagePatch = SettingProfileImagePatch.Remove,
                ),
            ),
        )
    }

    @Test
    fun `초기 profileImageUrl이 null인 경우 이미지 삭제 시 isAccountInfoEdited가 false이다`() = runTest {
        // given
        viewModel = AccountInfoViewModel(
            usecases = usecases,
            snackbarController = snackbarController,
            savedStateHandle = createSavedStateHandle(profileImageUrl = null),
        )

        testState(
            viewModel = viewModel,
            intents = listOf(AccountInfoContract.UiEvent.OnProfileImageDeleted),
            assertions = listOf(
                AccountInfoContract.UiState(
                    accountInfo = UiEditableAccountInfo(
                        displayId = TEST_DISPLAY_ID,
                        name = TEST_NAME,
                        introduce = TEST_INTRODUCE,
                        profileImageUrl = null,
                    ),
                    isAccountInfoEdited = false,
                    profileImagePatch = SettingProfileImagePatch.Remove,
                ),
            ),
        )
    }

    // =====================================================================
    // 뒤로가기 안전 처리 테스트
    // =====================================================================

    @Test
    fun `수정된 내용이 없을 때 뒤로가기 시 CloseScreen 이펙트가 발생한다`() {
        testEffect(
            viewModel = viewModel,
            intents = listOf(AccountInfoContract.UiEvent.SafeBackPressed),
            assertions = listOf(AccountInfoContract.UiEffect.CloseScreen),
        )
    }

    @Test
    fun `수정된 내용이 있을 때 뒤로가기 시 OpenSafeCancelModal 이펙트가 발생한다`() = runTest {
        // given
        viewModel.processEvent(AccountInfoContract.UiEvent.OnDisplayIdChanged(TEST_CHANGED_DISPLAY_ID))
        advanceUntilIdle()

        // when, then
        testEffect(
            viewModel = viewModel,
            intents = listOf(AccountInfoContract.UiEvent.SafeBackPressed),
            assertions = listOf(AccountInfoContract.UiEffect.OpenSafeCancelModal),
        )
    }

    @Test
    fun `프로필 이미지 수정 후 뒤로가기 시 OpenSafeCancelModal 이펙트가 발생한다`() = runTest {
        testEffect(
            viewModel = viewModel,
            intents = listOf(
                AccountInfoContract.UiEvent.OnProfileImageUpdated(TestImageBytes),
                AccountInfoContract.UiEvent.SafeBackPressed,
            ),
            assertions = listOf(AccountInfoContract.UiEffect.OpenSafeCancelModal),
        )
    }

    companion object {
        private const val TEST_DISPLAY_ID = "testDisplayId"
        private const val TEST_NAME = "testName"
        private const val TEST_INTRODUCE = "testIntroduce"
        private const val TEST_PROFILE_IMAGE_URL = "https://test.com/image.jpg"
        private const val TEST_CHANGED_DISPLAY_ID = "changedDisplayId"
        private const val TEST_CHANGED_NAME = "changedName"
        private const val TEST_CHANGED_INTRODUCE = "changedIntroduce"
        private val TestImageBytes = byteArrayOf(0x01, 0x02, 0x03)
    }
}
