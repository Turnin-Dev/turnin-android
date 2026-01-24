package com.peekr.peekrapp

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.peekr.core.data.source.local.datastore.DataStoreKey
import com.peekr.core.data.source.local.datastore.DataStoreManager
import com.peekr.core.domain.auth.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class MainViewModel @Inject constructor(
    private val dataStoreManager: DataStoreManager,
    private val authRepository: AuthRepository,
) : ViewModel() {
    private val _isLoading = MutableStateFlow(true)
    val isLoading = _isLoading.asStateFlow()

    private val _loggedIn: MutableStateFlow<Boolean?> = MutableStateFlow(null)
    val loggedIn = _loggedIn.asStateFlow()

    init {
        viewModelScope.launch {
            if (BuildConfig.DEBUG) {
                // TODO: In debug mode
                _loggedIn.update { true }
                _isLoading.update { false }
            } else {
                // TODO: In production mode
                val result = checkLoggedIn()
                _loggedIn.update { result }
                _isLoading.update { false }
            }
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
    private suspend fun checkLoggedIn(): Boolean {
        // 로그인 여부 판단 로직, 추후 캡슐화 예정
        return combine(
            dataStoreManager.getLongData(DataStoreKey.User.UserId),
            dataStoreManager.getEncryptedStringData(DataStoreKey.Auth.AccessToken),
            dataStoreManager.getEncryptedStringData(DataStoreKey.Auth.RefreshToken),
        ) { userId, accessToken, refreshToken ->
            userId != null && accessToken != null && refreshToken != null
        }.first()
    }

    fun cleanUp() {
        viewModelScope.launch {
            authRepository.cleanUp()
        }
    }
}
