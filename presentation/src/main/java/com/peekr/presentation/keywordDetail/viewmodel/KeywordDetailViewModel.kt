package com.peekr.presentation.keywordDetail.viewmodel

import androidx.lifecycle.ViewModel
import com.peekr.presentation.keywordDetail.state.KeywordDetailState
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

@HiltViewModel
class KeywordDetailViewModel @Inject constructor() : ViewModel() {
    private val _uiState = MutableStateFlow(KeywordDetailState())
    val uiState = _uiState.asStateFlow()
}
