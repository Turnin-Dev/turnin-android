package com.peekr.presentation.profile.viewmodel

import com.peekr.core.presentation.common.viewmodel.MVIBaseViewModel
import com.peekr.domain.profile.usecase.GetUserProfileUseCase
import com.peekr.presentation.profile.state.UserProfileContract
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class UserProfileViewModel @Inject constructor(
    private val getUserProfileUseCase: GetUserProfileUseCase,
) : MVIBaseViewModel<UserProfileContract.UiState, UserProfileContract.UiEvent, UserProfileContract.UiEffect>() {
    override fun createInitialState(): UserProfileContract.UiState =
        UserProfileContract.UiState()

    override suspend fun handleEvent(event: UserProfileContract.UiEvent) {
        when (event) {
            else -> {}
        }
    }
}
