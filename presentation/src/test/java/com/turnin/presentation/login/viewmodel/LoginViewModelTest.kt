package com.turnin.presentation.login.viewmodel

import com.turnin.core.domain.auth.model.LoginCredentials
import com.turnin.core.domain.common.Result
import com.turnin.core.domain.model.ProviderId
import com.turnin.core.domain.model.SocialLoginProvider
import com.turnin.core.presentation.MainDispatcherRule
import com.turnin.core.presentation.ui.model.UiSocialLoginProvider
import com.turnin.domain.login.error.LoginErrorType
import com.turnin.domain.login.model.LoginWithExistsUser
import com.turnin.domain.login.usecase.GetSocialLoginResultUseCase
import com.turnin.domain.login.usecase.LoginUseCase
import com.turnin.presentation.login.state.LoginUiEvent
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.awaitCancellation
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
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
class LoginViewModelTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val getSocialLoginResult: GetSocialLoginResultUseCase = mockk()
    private val loginUseCase: LoginUseCase = mockk()

    private lateinit var viewModel: LoginViewModel

    private val mockProvider = UiSocialLoginProvider.KAKAO
    private val mockLoginCredentials = LoginCredentials(
        provider = SocialLoginProvider.KAKAO,
        providerId = ProviderId("test_provider_id"),
    )
    private val mockLoginWithExistsUser = LoginWithExistsUser(
        loginCredentials = mockLoginCredentials,
        isExistsUser = true,
    )
    private val mockLoginWithNewUser = LoginWithExistsUser(
        loginCredentials = mockLoginCredentials,
        isExistsUser = false,
    )

    @Before
    fun setUp() {
        viewModel = LoginViewModel(
            getSocialLoginResult = getSocialLoginResult,
            loginUseCase = loginUseCase,
        )
    }

    @Test
    fun `로그인 시 사용자가 존재하면 메인 화면으로 이동하는 이벤트를 발행한다`() = runTest {
        // given
        every { getSocialLoginResult(any()) } returns flowOf(
            Result.Loading,
            Result.Success(mockLoginWithExistsUser),
        )
        every { loginUseCase(any()) } returns flowOf(
            Result.Loading,
            Result.Success(Unit),
        )

        // when
        viewModel.login(mockProvider)
        advanceUntilIdle()

        // then
        assertEquals(LoginUiEvent.NavigateToMain, viewModel.loginState.value.event)
    }

    @Test
    fun `로그인 시 사용자가 존재하면 isNavigating이 true가 된다`() = runTest {
        // given
        every { getSocialLoginResult(any()) } returns flowOf(
            Result.Loading,
            Result.Success(mockLoginWithExistsUser),
        )
        every { loginUseCase(any()) } returns flowOf(
            Result.Loading,
            Result.Success(Unit),
        )

        // when
        viewModel.login(mockProvider)
        advanceUntilIdle()

        // then
        assertTrue(viewModel.loginState.value.isNavigating)
    }

    @Test
    fun `로그인 시 사용자가 존재하지 않으면 회원가입 화면으로 이동하는 이벤트를 발행한다`() = runTest {
        // given
        every { getSocialLoginResult(any()) } returns flowOf(
            Result.Loading,
            Result.Success(mockLoginWithNewUser),
        )

        // when
        viewModel.login(mockProvider)
        advanceUntilIdle()

        // then
        val event = viewModel.loginState.value.event
        assertTrue(event is LoginUiEvent.NavigateToRegister)
    }

    @Test
    fun `로그인 시 사용자가 존재하지 않으면 isNavigating이 true가 된다`() = runTest {
        // given
        every { getSocialLoginResult(any()) } returns flowOf(
            Result.Loading,
            Result.Success(mockLoginWithNewUser),
        )

        // when
        viewModel.login(mockProvider)
        advanceUntilIdle()

        // then
        assertTrue(viewModel.loginState.value.isNavigating)
    }

    @Test
    fun `getSocialLoginResult가 Loading을 방출하면 loading이 true가 된다`() = runTest {
        // given
        every { getSocialLoginResult(any()) } returns flow {
            emit(Result.Loading)
            awaitCancellation()
        }

        // when
        viewModel.login(mockProvider)
        advanceUntilIdle()

        // then
        assertTrue(viewModel.loginState.value.loading)
    }

    @Test
    fun `loginUseCase가 Loading을 방출하면 loading이 true가 된다`() = runTest {
        // given
        every { getSocialLoginResult(any()) } returns flowOf(
            Result.Loading,
            Result.Success(mockLoginWithExistsUser),
        )
        every { loginUseCase(any()) } returns flow {
            emit(Result.Loading)
            awaitCancellation()
        }

        // when
        viewModel.login(mockProvider)
        advanceUntilIdle()

        // then
        assertTrue(viewModel.loginState.value.loading)
    }

    @Test
    fun `getSocialLoginResult가 에러를 반환하면 error 상태가 업데이트된다`() = runTest {
        // given
        val error = LoginErrorType.LoginFailed
        every { getSocialLoginResult(any()) } returns flowOf(
            Result.Loading,
            Result.Error(error),
        )

        // when
        viewModel.login(mockProvider)
        advanceUntilIdle()

        // then
        assertNotNull(viewModel.loginState.value.error)
        assertFalse(viewModel.loginState.value.loading)
    }

    @Test
    fun `loginUseCase가 에러를 반환하면 error 상태가 업데이트된다`() = runTest {
        // given
        val error = LoginErrorType.LoginFailed
        every { getSocialLoginResult(any()) } returns flowOf(
            Result.Loading,
            Result.Success(mockLoginWithExistsUser),
        )
        every { loginUseCase(any()) } returns flowOf(
            Result.Loading,
            Result.Error(error),
        )

        // when
        viewModel.login(mockProvider)
        advanceUntilIdle()

        // then
        assertNotNull(viewModel.loginState.value.error)
        assertFalse(viewModel.loginState.value.loading)
    }

    @Test
    fun `onEventConsumed 호출 시 event가 null이 된다`() = runTest {
        // given
        every { getSocialLoginResult(any()) } returns flowOf(
            Result.Success(mockLoginWithNewUser),
        )
        viewModel.login(mockProvider)
        advanceUntilIdle()

        // when
        viewModel.onEventConsumed()

        // then
        assertNull(viewModel.loginState.value.event)
    }

    @Test
    fun `onErrorMessageShown 호출 시 error가 null이 된다`() = runTest {
        // given
        val error = LoginErrorType.LoginFailed
        every { getSocialLoginResult(any()) } returns flowOf(
            Result.Error(error),
        )
        viewModel.login(mockProvider)
        advanceUntilIdle()

        // when
        viewModel.onErrorMessageShown()

        // then
        assertNull(viewModel.loginState.value.error)
    }

    @Test
    fun `onResetNavigating 호출 시 isNavigating이 false가 된다`() = runTest {
        // given
        every { getSocialLoginResult(any()) } returns flowOf(
            Result.Success(mockLoginWithNewUser),
        )
        viewModel.login(mockProvider)
        advanceUntilIdle()

        // when
        viewModel.onResetNavigating()

        // then
        assertFalse(viewModel.loginState.value.isNavigating)
    }
}
