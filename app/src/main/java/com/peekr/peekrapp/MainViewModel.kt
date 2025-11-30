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
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class MainViewModel @Inject constructor(
    private val dataStoreManager: DataStoreManager,
) : ViewModel() {
    private val _isLoading = MutableStateFlow(true)
    val isLoading = _isLoading.asStateFlow()

    private val _loggedIn = MutableStateFlow(false)
    val loggedIn = _loggedIn.asStateFlow()

    init {
        checkLoggedIn()
    }

    private fun checkLoggedIn() {
        viewModelScope.launch {
            combine(
                dataStoreManager.getLongData(DataStoreKey.User.UserId),
                dataStoreManager.getEncryptedStringData(DataStoreKey.Auth.AccessToken),
                dataStoreManager.getEncryptedStringData(DataStoreKey.Auth.RefreshToken),
            ) { userId, accessToken, refreshToken ->
                if (userId != null &&
                    accessToken != null &&
                    refreshToken != null
                ) {
                    _loggedIn.update { true }
                } else {
                    _loggedIn.update { false }
                }
            }
        }

        _isLoading.update { false }
    }
}
