package com.peekr.peekrapp

import android.net.ConnectivityManager
import android.net.Network
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.peekr.core.common.logger.AppLogger
import com.peekr.core.domain.auth.repository.AuthRepository
import com.peekr.core.domain.auth.usecase.LogoutUseCase
import com.peekr.core.domain.common.Result
import com.peekr.core.domain.eventBus.AuthEventBus
import com.peekr.core.domain.notification.NotificationSyncManager
import com.peekr.core.domain.user.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class MainViewModel @Inject constructor(
    private val authEventBus: AuthEventBus,
    private val logoutUseCase: LogoutUseCase,
    private val userRepository: UserRepository,
    private val authRepository: AuthRepository,
    private val notificationSyncManager: NotificationSyncManager,
    private val connectivityManager: ConnectivityManager,
) : ViewModel() {
    private val tag = this::class.java.simpleName

    private val _isLoading = MutableStateFlow(true)
    val isLoading = _isLoading.asStateFlow()

    private val _loggedIn: MutableStateFlow<Boolean?> = MutableStateFlow(null)
    val loggedIn = _loggedIn.asStateFlow()

    private val _navigateToLogin = Channel<Unit>(Channel.CONFLATED)
    val navigateToLogin = _navigateToLogin.receiveAsFlow()

    init {
        // 로그인 체크
        viewModelScope.launch {
            val result = authRepository.isLoggedIn()
            _loggedIn.update { result }
            if (result) {
                preloadUserData()
                notificationSyncManager.sync()
            }
            _isLoading.update { false }
        }

        // 로그아웃 감지
        authEventBus.logoutEvent
            .onEach { logout() }
            .launchIn(viewModelScope)

        // 로그인 감지
        authEventBus.loginEvent
            .onEach { onLogin() }
            .launchIn(viewModelScope)

        // 인터넷 연결 감지
        observeNetworkConnectivity()
    }

    // onResume에서 호출
    fun syncNotificationState() {
        viewModelScope.launch {
            val isLoggedIn = loggedIn.first { it != null }
            if (isLoggedIn != true) return@launch
            notificationSyncManager.sync()
        }
    }

    // 로그아웃
    private suspend fun logout() {
        val result = logoutUseCase()
            .filter { it !is Result.Loading }
            .first()

        when (result) {
            is Result.Error -> AppLogger.e(tag, "Failed to logout in MainViewModel.")
            is Result.Success -> {
                _loggedIn.update { false }
                _navigateToLogin.send(Unit)
            }

            else -> Unit
        }
    }

    // 로그인 시 수행
    private fun onLogin() {
        _loggedIn.update { true }
        preloadUserData()
        notificationSyncManager.sync()
    }

    /**
     * 사용자 데이터를 미리 로드한다.
     */
    private fun preloadUserData() {
        if (userRepository.myProfile.value == null) {
            userRepository.getMyProfileRefresh()
                .catch { e -> AppLogger.e(tag, e, "Failed to preload user data.") }
                .launchIn(viewModelScope)
        }
    }

    // 인터넷 연결 감지 시 수행할 작업
    private fun observeNetworkConnectivity() {
        callbackFlow {
            val callback = object : ConnectivityManager.NetworkCallback() {
                override fun onAvailable(network: Network) {
                    trySend(Unit)
                }
            }
            connectivityManager.registerDefaultNetworkCallback(callback)
            awaitClose { connectivityManager.unregisterNetworkCallback(callback) }
        }
            .drop(1)
            .onEach {
                // 네트워크 재연결 시 프로필이 없는 경우에만 재시도
                preloadUserData()
            }
            .launchIn(viewModelScope)
    }
}
