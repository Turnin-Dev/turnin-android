package com.peekr.presentation.profile.viewmodel

import com.peekr.core.presentation.util.MVIBaseViewModel
import com.peekr.domain.profile.usecase.GetProfileUseCase
import com.peekr.presentation.profile.state.ProfileContract
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val getProfileUseCase: GetProfileUseCase,
) : MVIBaseViewModel<ProfileContract.UiState, ProfileContract.UiEvent, ProfileContract.UiEffect>() {
    override val initialState: ProfileContract.UiState = ProfileContract.UiState()

    override suspend fun loadInitialData() {
        // 초기 데이터 로드 (사용자 프로필 조회)
    }

    override suspend fun handleEvent(event: ProfileContract.UiEvent) {
        // handle event
    }
}
