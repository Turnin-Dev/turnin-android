package com.peekr.peekrapp

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.peekr.core.common.logger.AppLogger
import com.peekr.core.data.source.local.datastore.DataStoreKey
import com.peekr.core.data.source.local.datastore.DataStoreManager
import com.peekr.core.domain.auth.usecase.LogoutUseCase
import com.peekr.core.domain.common.Result
import com.peekr.core.domain.eventBus.AuthEventBus
import com.peekr.core.domain.user.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class MainViewModel @Inject constructor(
    private val dataStoreManager: DataStoreManager,
    private val authEventBus: AuthEventBus,
    private val logoutUseCase: LogoutUseCase,
    private val userRepository: UserRepository,
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
                if (result) preloadUserData() // 사용자 데이터 미리 로드
                _isLoading.update { false }
            } else {
                // TODO: In production mode
                val result = checkLoggedIn()
                _loggedIn.update { result }
                if (result) preloadUserData() // 사용자 데이터 미리 로드
                _isLoading.update { false }
            }
        }

        // 로그아웃 감지
        authEventBus.logoutEvent
            .onEach { logout() }
            .launchIn(viewModelScope)

        // 로그인 감지
        authEventBus.loginEvent
            .onEach { preloadUserData() }
            .launchIn(viewModelScope)
    }

    // 로그아웃
    fun logout() {
        logoutUseCase().onEach { result ->
            when (result) {
                Result.Loading -> {}
                is Result.Error -> {
                    AppLogger.e(tag, "Failed to logout in MainViewModel.")
                }

                is Result.Success -> {
                    _navigateToLogin.send(Unit)
                }
            }
        }
            .launchIn(viewModelScope)
    }

    /**
     * 로그인 성공 조건:
     * - 3개의 데이터가 모두 존재하며 암호화된 데이터도 정상적으로 복호화에 성공한 경우
     *
     * 로그인 실패 조건:
     * - 3개의 데이터 중 하나라도 없는 경우
     * - 암호화된 데이터를 복호화하는 과정에서 오류가 발생한 경우
     */
    private suspend fun checkLoggedIn(): Boolean {
        // 로그인 여부 판단 로직, 추후 캡슐화 예정
        dataStoreManager.getLongData(DataStoreKey.User.UserId).firstOrNull()
            ?: return false

        val accessToken =
            dataStoreManager.getEncryptedStringData(DataStoreKey.Auth.AccessToken).firstOrNull()
        val refreshToken =
            dataStoreManager.getEncryptedStringData(DataStoreKey.Auth.RefreshToken).firstOrNull()

        return accessToken != null && refreshToken != null
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
}
