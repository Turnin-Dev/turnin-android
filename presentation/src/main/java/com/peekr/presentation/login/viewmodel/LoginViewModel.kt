package com.peekr.presentation.login.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.peekr.core.domain.auth.model.LoginCredentials
import com.peekr.core.domain.common.Result
import com.peekr.core.presentation.ui.model.UiSocialLoginProvider
import com.peekr.domain.login.error.LoginErrorType
import com.peekr.domain.login.usecase.GetExistingLoginCredentialsUseCase
import com.peekr.domain.login.usecase.LoginIntegrationUseCase
import com.peekr.presentation.login.error.asUiText
import com.peekr.presentation.login.mapper.toDomainModel
import com.peekr.presentation.login.mapper.toUiModel
import com.peekr.presentation.login.state.LoginState
import com.peekr.presentation.login.state.LoginUiEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val loginIntegrationUseCase: LoginIntegrationUseCase,
    private val getExistingLoginCredentialsUseCase: GetExistingLoginCredentialsUseCase,
) : ViewModel() {
    private val _loginState = MutableStateFlow(LoginState())
    val loginState: StateFlow<LoginState> = _loginState.asStateFlow()

    val loginEvents = loginState.map { it.event }

    /**
     * 로그인
     *
     * 사용자 존재 여부를 먼저 파악하고,
     * 사용자가 이미 존재하면 로그인을 계속 진행한다. (성공 시 메인 페이지로 이동)
     * 사용자가 존재하지 않는다면 회원가입 페이지로 이동한다.
     *
     * @param uiSocialLoginProvider [UiSocialLoginProvider]
     */
    fun login(uiSocialLoginProvider: UiSocialLoginProvider) {
        val socialLoginProvider = uiSocialLoginProvider.toDomainModel()
        // 사용자 존재 여부 파악
        getExistingLoginCredentialsUseCase(socialLoginProvider)
            .onEach { result ->
                updateLoginState(result) { loginWithExistsUser ->
                    if (loginWithExistsUser.isExistsUser) {
                        // 로그인 계속 진행
                        proceedWithLoginAndNavigateToMain(loginWithExistsUser.loginCredentials)
                    } else {
                        // 회원가입 진행
                        navigateToRegister(loginWithExistsUser.loginCredentials)
                    }
                }
            }.launchIn(viewModelScope)
    }

    // 회원가입 페이지로 이동
    private fun navigateToRegister(loginCredentials: LoginCredentials) {
        _loginState.update {
            it.copy(
                loading = false,
                event = LoginUiEvent.NavigateToRegister(
                    loginCredentials.provider.toUiModel(),
                    loginCredentials.providerId.uid,
                ),
            )
        }
    }

    fun onEventConsumed() {
        _loginState.update { it.copy(event = null) }
    }

    fun onErrorMessageShown() {
        _loginState.update { it.copy(error = null) }
    }

    // 로그인을 계속 진행하고 성공 시 메인 페이지로 이동
    private fun proceedWithLoginAndNavigateToMain(loginCredentials: LoginCredentials) {
        loginIntegrationUseCase(loginCredentials)
            .onEach { result ->
                updateLoginState(result) { success ->
                    _loginState.update { it.copy(loading = false, event = LoginUiEvent.NavigateToMain) }
                }
            }.launchIn(viewModelScope)
    }

    /**
     * [Result]를 기반으로 한 데이터와 함께 [LoginState]상태를 업데이트 한다.
     *
     * @param result 결과 래퍼 클래스로 감싸있는 데이터
     * @param onSuccess [Result.Success] 시 수행할 작업
     */
    private inline fun <T> updateLoginState(
        result: Result<T, LoginErrorType>,
        onSuccess: (T) -> Unit,
    ) {
        when (result) {
            Result.Loading -> {
                _loginState.update { it.copy(loading = true) }
            }

            is Result.Error<LoginErrorType> -> {
                val error = result.error.asUiText()
                _loginState.update { it.copy(loading = false, error = error) }
            }

            is Result.Success -> {
                onSuccess(result.data)
            }
        }
    }
}
