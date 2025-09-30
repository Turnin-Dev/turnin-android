package com.peekr.presentation.profile.viewmodel

import androidx.lifecycle.ViewModel
import com.peekr.presentation.profile.state.ProfileState
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class ProfileViewModel @Inject constructor() : ViewModel() {
    private val _profileState = MutableStateFlow(ProfileState())
    val profileState = _profileState.asStateFlow()
}
