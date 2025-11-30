package com.peekr.peekrapp

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.peekr.core.data.source.local.datastore.DataStoreKey
import com.peekr.core.data.source.local.datastore.DataStoreManager
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
) : ViewModel() {
    private val _isLoading = MutableStateFlow(true)
    val isLoading = _isLoading.asStateFlow()

    private val _loggedIn: MutableStateFlow<Boolean?> = MutableStateFlow(null)
    val loggedIn = _loggedIn.asStateFlow()

    init {
        viewModelScope.launch {
            val result = checkLoggedIn()
            _loggedIn.update { result }
            _isLoading.update { false }
        }
    }

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
}
