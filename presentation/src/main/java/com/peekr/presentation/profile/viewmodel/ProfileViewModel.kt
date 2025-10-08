package com.peekr.presentation.profile.viewmodel

import com.peekr.core.domain.util.ErrorType
import com.peekr.core.domain.util.Result
import com.peekr.core.presentation.error.asUiText
import com.peekr.core.presentation.util.MVIBaseViewModel
import com.peekr.domain.profile.usecase.GetProfileUseCase
import com.peekr.presentation.profile.model.toUiModel
import com.peekr.presentation.profile.state.ProfileContract
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val getProfileUseCase: GetProfileUseCase,
) : MVIBaseViewModel<ProfileContract.UiState, ProfileContract.UiEvent, ProfileContract.UiEffect>() {
    override fun createInitialState(): ProfileContract.UiState =
        ProfileContract.UiState()

    override suspend fun loadInitialData() {
        getProfileUseCase().collect { result ->
            when (result) {
                Result.Loading -> updateState {
                    this.copy(loading = true)
                }

                is Result.Error<ErrorType> -> updateState {
                    this.copy(loading = false, error = result.error.asUiText())
                }

                is Result.Success -> updateState {
                    this.copy(
                        loading = false,
                        error = null,
                        profile = result.data.toUiModel(),
                    )
                }
            }
        }
    }

    override suspend fun handleEvent(event: ProfileContract.UiEvent) {
        // handle event
    }
}
