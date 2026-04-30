package com.turnin.presentation.register.viewmodel

import androidx.compose.ui.graphics.ImageBitmap
import com.turnin.core.domain.common.Result
import com.turnin.core.domain.common.validation.ValidationErrorType
import com.turnin.core.domain.common.validation.ValidationResult
import com.turnin.core.domain.model.DisplayId
import com.turnin.core.domain.model.Introduce
import com.turnin.core.domain.model.Name
import com.turnin.core.presentation.MainDispatcherRule
import com.turnin.core.presentation.ui.model.UiSocialLoginProvider
import com.turnin.domain.register.error.RegisterErrorType
import com.turnin.domain.register.model.ExistsResult
import com.turnin.domain.register.usecase.CheckDisplayIdExistsUseCase
import com.turnin.domain.register.usecase.RegisterIntegrationUseCase
import com.turnin.domain.register.usecase.ValidateDisplayIdUseCase
import com.turnin.domain.register.usecase.ValidateIntroduceUseCase
import com.turnin.domain.register.usecase.ValidateNameUseCase
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.awaitCancellation
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class RegisterViewModelTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val validateDisplayIdUseCase: ValidateDisplayIdUseCase = mockk()
    private val validateNameUseCase: ValidateNameUseCase = mockk()
    private val validateIntroduceUseCase: ValidateIntroduceUseCase = mockk()
    private val checkDisplayIdExistsUseCase: CheckDisplayIdExistsUseCase = mockk()
    private val registerIntegrationUseCase: RegisterIntegrationUseCase = mockk()
    private val ioDispatcher: CoroutineDispatcher = StandardTestDispatcher()

    private lateinit var viewModel: RegisterViewModel

    @Before
    fun setUp() {
        every { validateDisplayIdUseCase(any()) } returns flowOf(ValidationResult.Valid(DisplayId("a")))
        every { validateNameUseCase(any()) } returns flowOf(ValidationResult.Valid(Name("a")))
        every { validateIntroduceUseCase(any()) } returns flowOf(ValidationResult.Valid(Introduce("a")))

        viewModel = RegisterViewModel(
            validateDisplayIdUseCase = validateDisplayIdUseCase,
            validateNameUseCase = validateNameUseCase,
            validateIntroduceUseCase = validateIntroduceUseCase,
            checkDisplayIdExistsUseCase = checkDisplayIdExistsUseCase,
            registerIntegrationUseCase = registerIntegrationUseCase,
            ioDispatcher = ioDispatcher,
        )
    }

    // ==================================================
    // 단순 상태 업데이트
    // ==================================================

    @Test
    fun `onDisplayIdChanged 호출 시 displayId 상태가 업데이트된다`() = runTest {
        // given
        val displayId = "test_display_id"

        // when
        viewModel.onDisplayIdChanged(displayId)

        // then
        assertEquals(displayId, viewModel.displayIdState.value.displayId)
    }

    @Test
    fun `onNameChanged 호출 시 name 상태가 업데이트된다`() = runTest {
        // given
        val name = "테스트"

        // when
        viewModel.onNameChanged(name)

        // then
        assertEquals(name, viewModel.nameState.value.name)
    }

    @Test
    fun `onIntroduceChanged 호출 시 introduce 상태가 업데이트된다`() = runTest {
        // given
        val introduce = "안녕하세요"

        // when
        viewModel.onIntroduceChanged(introduce)

        // then
        assertEquals(introduce, viewModel.profileState.value.introduce)
    }

    @Test
    fun `selectProfileImage 호출 시 image 상태가 업데이트된다`() = runTest {
        // given
        val image = mockk<ImageBitmap>()

        // when
        viewModel.selectProfileImage(image)

        // then
        assertEquals(image, viewModel.profileState.value.image)
    }

    @Test
    fun `selectProfileImage에 null을 전달하면 image 상태가 null이 된다`() = runTest {
        // given & when
        viewModel.selectProfileImage(null)

        // then
        assertNull(viewModel.profileState.value.image)
    }

    @Test
    fun `selectOriginalImage 호출 시 originalImage 상태가 업데이트되고 이미지 크롭 이벤트가 발행된다`() = runTest {
        // given
        val image = mockk<ImageBitmap>()

        // when
        viewModel.selectOriginalImage(image)

        // then
        assertEquals(image, viewModel.profileState.value.originalImage)
        assertTrue(viewModel.registerEventState.value.navigateToCropImageScreen)
    }

    @Test
    fun `selectOriginalImage에 null을 전달하면 이미지 크롭 이벤트가 발행되지 않는다`() = runTest {
        // given & when
        viewModel.selectOriginalImage(null)

        // then
        assertFalse(viewModel.registerEventState.value.navigateToCropImageScreen)
    }

    // ==================================================
    // validateDisplayIdState
    // ==================================================

    @Test
    fun `displayId가 변경되면 유효성 검사를 수행한다`() = runTest {
        // given
        val displayId = "valid_id"
        every { validateDisplayIdUseCase(displayId) } returns flowOf(ValidationResult.Valid(DisplayId(displayId)))

        // when
        viewModel.onDisplayIdChanged(displayId)
        advanceUntilIdle()

        // then
        assertNull(viewModel.displayIdState.value.displayIdError)
        assertTrue(viewModel.displayIdState.value.canNext)
    }

    @Test
    fun `displayId 유효성 검사 실패 시 에러 상태가 업데이트되고 canNext가 false가 된다`() = runTest {
        // given
        val displayId = "invalid id"
        val error = ValidationErrorType.Unexpected
        every { validateDisplayIdUseCase(displayId) } returns flowOf(ValidationResult.Invalid(error))

        // when
        viewModel.onDisplayIdChanged(displayId)
        advanceUntilIdle()

        // then
        assertNotNull(viewModel.displayIdState.value.displayIdError)
        assertFalse(viewModel.displayIdState.value.canNext)
    }

    @Test
    fun `displayId가 빈 값이면 canNext가 false가 된다`() = runTest {
        // given & when
        viewModel.onDisplayIdChanged("")
        advanceUntilIdle()

        // then
        assertFalse(viewModel.displayIdState.value.canNext)
    }

    // ==================================================
    // validateNameState
    // ==================================================

    @Test
    fun `name이 변경되면 유효성 검사를 수행한다`() = runTest {
        // given
        val name = "홍길동"
        every { validateNameUseCase(name) } returns flowOf(ValidationResult.Valid(Name(name)))

        // when
        viewModel.onNameChanged(name)
        advanceUntilIdle()

        // then
        assertNull(viewModel.nameState.value.nameError)
        assertTrue(viewModel.nameState.value.canNext)
    }

    @Test
    fun `name 유효성 검사 실패 시 에러 상태가 업데이트되고 canNext가 false가 된다`() = runTest {
        // given
        val name = "a"
        val error = ValidationErrorType.Unexpected
        every { validateNameUseCase(name) } returns flowOf(ValidationResult.Invalid(error))

        // when
        viewModel.onNameChanged(name)
        advanceUntilIdle()

        // then
        assertNotNull(viewModel.nameState.value.nameError)
        assertFalse(viewModel.nameState.value.canNext)
    }

    @Test
    fun `name이 빈 값이면 canNext가 false가 된다`() = runTest {
        // given & when
        viewModel.onNameChanged("")
        advanceUntilIdle()

        // then
        assertFalse(viewModel.nameState.value.canNext)
    }

    // ==================================================
    // validateIntroduceState
    // ==================================================

    @Test
    fun `introduce가 변경되면 유효성 검사를 수행한다`() = runTest {
        // given
        val introduce = "안녕하세요"
        every { validateIntroduceUseCase(introduce) } returns flowOf(ValidationResult.Valid(Introduce(introduce)))

        // when
        viewModel.onIntroduceChanged(introduce)
        advanceUntilIdle()

        // then
        assertNull(viewModel.profileState.value.introduceError)
        assertTrue(viewModel.profileState.value.canNext)
    }

    @Test
    fun `introduce 유효성 검사 실패 시 에러 상태가 업데이트되고 canNext가 false가 된다`() = runTest {
        // given
        val introduce = "a"
        val error = ValidationErrorType.Unexpected
        every { validateIntroduceUseCase(introduce) } returns flowOf(ValidationResult.Invalid(error))

        // when
        viewModel.onIntroduceChanged(introduce)
        advanceUntilIdle()

        // then
        assertNotNull(viewModel.profileState.value.introduceError)
        assertFalse(viewModel.profileState.value.canNext)
    }

    @Test
    fun `introduce가 빈 값이면 canNext가 false가 된다`() = runTest {
        // given & when
        viewModel.onIntroduceChanged("")
        advanceUntilIdle()

        // then
        assertFalse(viewModel.profileState.value.canNext)
    }

    // ==================================================
    // checkDisplayIdExists
    // ==================================================

    @Test
    fun `displayId 중복 검사 시 중복되지 않으면 다음 화면으로 이동하는 이벤트를 발행한다`() = runTest {
        // given
        val displayId = "available_id"
        every { checkDisplayIdExistsUseCase(displayId) } returns flowOf(
            Result.Loading,
            Result.Success(ExistsResult(exists = false)),
        )

        // when
        viewModel.checkDisplayIdExists(displayId)
        advanceUntilIdle()

        // then
        assertTrue(viewModel.registerEventState.value.navigateToNextScreen)
        assertNull(viewModel.displayIdState.value.displayIdError)
    }

    @Test
    fun `displayId 중복 검사 시 중복되면 에러 상태가 업데이트된다`() = runTest {
        // given
        val displayId = "duplicated_id"
        every { checkDisplayIdExistsUseCase(displayId) } returns flowOf(
            Result.Loading,
            Result.Success(ExistsResult(exists = true)),
        )

        // when
        viewModel.checkDisplayIdExists(displayId)
        advanceUntilIdle()

        // then
        assertFalse(viewModel.registerEventState.value.navigateToNextScreen)
        assertNotNull(viewModel.displayIdState.value.displayIdError)
    }

    @Test
    fun `displayId 중복 검사 시 에러가 발생하면 에러 상태가 업데이트된다`() = runTest {
        // given
        val displayId = "test_id"
        val error = RegisterErrorType.Unexpected(null)
        every { checkDisplayIdExistsUseCase(displayId) } returns flowOf(
            Result.Loading,
            Result.Error(error),
        )

        // when
        viewModel.checkDisplayIdExists(displayId)
        advanceUntilIdle()

        // then
        assertNotNull(viewModel.displayIdState.value.displayIdError)
        assertFalse(viewModel.displayIdState.value.loading)
    }

    @Test
    fun `displayId가 빈 값이면 중복 검사를 수행하지 않고 에러 상태가 업데이트된다`() = runTest {
        // given & when
        viewModel.checkDisplayIdExists("   ")
        advanceUntilIdle()

        // then
        assertNotNull(viewModel.displayIdState.value.displayIdError)
        verify(exactly = 0) { checkDisplayIdExistsUseCase(any()) }
    }

    // ==================================================
    // register
    // ==================================================

    @Test
    fun `회원가입 성공 시 다음 화면으로 이동하는 이벤트를 발행한다`() = runTest {
        // given
        every { registerIntegrationUseCase(any(), any(), any(), any(), any(), any()) } returns flowOf(
            Result.Loading,
            Result.Success(Unit),
        )

        // when
        viewModel.register(
            provider = UiSocialLoginProvider.KAKAO,
            providerId = "test_provider_id",
            image = null,
        )
        advanceUntilIdle()

        // then
        assertTrue(viewModel.registerEventState.value.navigateToNextScreen)
        assertFalse(viewModel.profileState.value.loading)
    }

    @Test
    fun `회원가입 실패 시 에러 이벤트를 발행한다`() = runTest {
        // given
        val error = RegisterErrorType.Unexpected(null)
        every { registerIntegrationUseCase(any(), any(), any(), any(), any(), any()) } returns flowOf(
            Result.Loading,
            Result.Error(error),
        )

        // when
        viewModel.register(
            provider = UiSocialLoginProvider.KAKAO,
            providerId = "test_provider_id",
            image = null,
        )
        advanceUntilIdle()

        // then
        assertNotNull(viewModel.registerEventState.value.error)
        assertFalse(viewModel.profileState.value.loading)
    }

    @Test
    fun `회원가입 진행 중 loading 상태가 true가 된다`() = runTest {
        // given
        every { registerIntegrationUseCase(any(), any(), any(), any(), any(), any()) } returns flow {
            emit(Result.Loading)
            awaitCancellation()
        }

        // when
        viewModel.register(
            provider = UiSocialLoginProvider.KAKAO,
            providerId = "test_provider_id",
            image = null,
        )
        advanceUntilIdle()

        // then
        assertTrue(viewModel.profileState.value.loading)
    }

    // ==================================================
    // onConsumeEventState
    // ==================================================

    @Test
    fun `onConsumeEventState 호출 시 모든 이벤트 상태가 초기화된다`() = runTest {
        // given
        every { checkDisplayIdExistsUseCase(any()) } returns flowOf(
            Result.Success(ExistsResult(exists = false)),
        )
        viewModel.checkDisplayIdExists("test_id")
        advanceUntilIdle()

        // when
        viewModel.onConsumeEventState()

        // then
        assertFalse(viewModel.registerEventState.value.navigateToNextScreen)
        assertFalse(viewModel.registerEventState.value.navigateToCropImageScreen)
        assertNull(viewModel.registerEventState.value.error)
    }
}
