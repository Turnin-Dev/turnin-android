package com.peekr.presentation.login.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.peekr.domain.account.usecase.LoginIntegrationUseCase
import com.peekr.domain.shared.util.ErrorType
import com.peekr.domain.shared.util.Result
import com.peekr.presentation.login.model.UiSocialLoginProvider
import com.peekr.presentation.login.state.LoginState
import com.peekr.presentation.shared.util.errorTypeFirst
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val loginIntegrationUseCase: LoginIntegrationUseCase,
) : ViewModel() {
    private val _loginState = MutableStateFlow(LoginState())
    val loginState: StateFlow<LoginState> = _loginState.asStateFlow()

    // TODO: 사용자 존재 여부를 확인하고 만약,
    // 존재한다면 계속해서 로그인 진행
    // 존재하지 않는다면 회원가입 진행
    // (회원가입 진행은 회원가입 화면으로 넘기고 회원가입 뷰모델에서 로직 작성)

    fun login(uiSocialLoginProvider: UiSocialLoginProvider) {
        val socialLoginProvider = uiSocialLoginProvider.toDomainModel()
        loginIntegrationUseCase(socialLoginProvider)
            .onEach { result -> handleLoginResult(result) }
            .launchIn(viewModelScope)
    }

    private fun handleLoginResult(result: Result<Boolean, ErrorType>) {
        when (result) {
            Result.Loading -> {
                _loginState.update { it.copy(loading = true) }
            }

            is Result.Error<ErrorType> -> {
                val error = result.errorTypeFirst()
                _loginState.update { it.copy(loading = false, error = error) }
            }

            is Result.Success -> {
                _loginState.update { it.copy(loading = false, loginSuccess = result.data) }
            }
        }
    }
}
