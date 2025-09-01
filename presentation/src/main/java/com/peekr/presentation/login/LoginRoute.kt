package com.peekr.presentation.login

import android.widget.Toast
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.peekr.presentation.common.util.event.LaunchedUiEffectHandler
import com.peekr.presentation.login.model.UiSocialLoginProvider
import com.peekr.presentation.login.state.LoginUiEvent
import com.peekr.presentation.login.view.LoginScreen
import com.peekr.presentation.login.viewmodel.LoginViewModel
import timber.log.Timber

@Composable
fun LoginRoute(
    modifier: Modifier = Modifier,
    onNavigateMain: () -> Unit,
    onNavigateRegister: (UiSocialLoginProvider, String) -> Unit,
) {
    val loginViewModel: LoginViewModel = hiltViewModel()
    val loginState by loginViewModel.loginState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    LaunchedEffect(loginState.error) {
        loginState.error?.let { error ->
            Timber.d(error.asString(context))
            Toast.makeText(context, error.asString(context), Toast.LENGTH_SHORT).show()
        }
        loginViewModel.onErrorMessageShown()
    }

    LaunchedUiEffectHandler(
        effectFlow = loginViewModel.loginEvents,
        onConsumeEffect = { loginViewModel.onEventConsumed() },
        onEffect = { event ->
            when (event) {
                is LoginUiEvent.NavigateToRegister -> {
                    onNavigateRegister(event.provider, event.providerId)
                }

                LoginUiEvent.NavigateToMain -> {
                    onNavigateMain()
                }
            }
        },
    )

    LoginScreen(
        modifier = modifier.fillMaxSize(),
        loginState = loginState,
        login = loginViewModel::login,
    )
}
