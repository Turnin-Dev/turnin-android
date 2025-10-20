package com.peekr.peekrapp

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.peekr.core.data.datastore.DataStoreKey
import com.peekr.core.data.datastore.DataStoreManager
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.launch

@HiltViewModel
class TestDataViewModel @Inject constructor(
    private val dataStoreManager: DataStoreManager,
) : ViewModel() {
    init {
        viewModelScope.launch {
            dataStoreManager.saveLongData(DataStoreKey.User.UserId, 1L)
        }
    }
}
