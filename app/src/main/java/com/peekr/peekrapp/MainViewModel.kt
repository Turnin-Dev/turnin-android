package com.peekr.peekrapp

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.peekr.core.common.logger.AppLogger
import com.peekr.core.domain.auth.repository.AuthRepository
import com.peekr.core.domain.auth.usecase.LogoutUseCase
import com.peekr.core.domain.common.Result
import com.peekr.core.domain.eventBus.AuthEventBus
import com.peekr.core.domain.user.repository.UserRepository
import com.peekr.peekrapp.util.notification.NotificationSyncManager
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
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
) : ViewModel() {
    private val tag = this::class.java.simpleName

    private val _isLoading = MutableStateFlow(true)
    val isLoading = _isLoading.asStateFlow()

    private val _loggedIn: MutableStateFlow<Boolean?> = MutableStateFlow(null)
    val loggedIn = _loggedIn.asStateFlow()

    private val _navigateToLogin = Channel<Unit>(Channel.BUFFERED)
    val navigateToLogin = _navigateToLogin.receiveAsFlow()

    init {
        // 로그인 체크
        viewModelScope.launch {
            if (BuildConfig.DEBUG) {
                // TODO: In debug mode
                val result = checkLoggedIn()
                _loggedIn.update { result }
                if (result) {
                    preloadUserData()
                    notificationSyncManager.sync()
                }
                _isLoading.update { false }
            } else {
                // TODO: In production mode
                val result = checkLoggedIn()
                _loggedIn.update { result }
                if (result) {
                    preloadUserData()
                    notificationSyncManager.sync()
                }
                _isLoading.update { false }
            }
        }

        // 로그아웃 감지
        authEventBus.logoutEvent
            .onEach { logout() }
            .launchIn(viewModelScope)

        // 로그인 감지
        authEventBus.loginEvent
            .onEach {
                preloadUserData()
                notificationSyncManager.sync()
            }
            .launchIn(viewModelScope)
    }

    // onResume에서 호출
    fun syncNotificationState() {
        viewModelScope.launch {
            if (loggedIn.value != true) return@launch
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
            is Result.Success -> _navigateToLogin.send(Unit)
            else -> Unit
        }
    }

    /**
     * 로그인 성공 조건:
     * - 3개의 데이터가 모두 존재하며 암호화된 데이터도 정상적으로 복호화에 성공한 경우
     *
     * 로그인 실패 조건:
     * - 3개의 데이터 중 하나라도 없는 경우
     * - 암호화된 데이터를 복호화하는 과정에서 오류가 발생한 경우
     */
    private suspend fun checkLoggedIn(): Boolean =
        authRepository.isLoggedIn()

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
}
