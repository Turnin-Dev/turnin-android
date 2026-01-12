package com.peekr.presentation.profile.viewmodel

import androidx.lifecycle.viewModelScope
import com.peekr.core.domain.common.Result
import com.peekr.core.presentation.common.snackbar.SnackbarController
import com.peekr.core.presentation.common.snackbar.SnackbarEvent
import com.peekr.core.presentation.common.viewmodel.MVIBaseViewModel
import com.peekr.core.presentation.ui.model.toUiModel
import com.peekr.core.presentation.ui.util.UiText
import com.peekr.domain.profile.error.ProfileErrorType
import com.peekr.domain.profile.usecase.MyProfileUseCases
import com.peekr.presentation.profile.error.asUiText
import com.peekr.presentation.profile.model.toUiModel
import com.peekr.presentation.profile.state.MyProfileContract
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

@HiltViewModel
class MyProfileViewModel @Inject constructor(
    private val snackbarController: SnackbarController,
    private val usecases: MyProfileUseCases,
) : MVIBaseViewModel<MyProfileContract.UiState, MyProfileContract.UiEvent, MyProfileContract.UiEffect>() {
    override fun createInitialState(): MyProfileContract.UiState =
        MyProfileContract.UiState()

    override suspend fun loadInitialData() {
        observeMyProfile()
        refreshMyProfile()
        getMyKeywords()
    }

    override suspend fun handleEvent(event: MyProfileContract.UiEvent) {
        when (event) {
            else -> {}
        }
    }

    private fun observeMyProfile() {
        usecases.getMyProfile()
            .onEach { myProfile ->
                if (myProfile == null) {
                    showSnackBar(ProfileErrorType.ProfileLoadFailed.asUiText())
                }
                updateState {
                    this.copy(myProfile = myProfile?.toUiModel())
                }
            }
            .launchIn(viewModelScope)
    }

    private fun refreshMyProfile() {
        usecases.refreshMyProfile()
            .onEach { result ->
                when (result) {
                    Result.Loading -> {
                        updateState {
                            this.copy(loading = true)
                        }
                    }

                    is Result.Error -> {
                        updateState {
                            this.copy(
                                loading = false,
                                error = result.error.asUiText(),
                            )
                        }
                        showSnackBar(ProfileErrorType.ProfileLoadFailed.asUiText())
                    }

                    is Result.Success -> {
                        updateState {
                            this.copy(
                                loading = false,
                                error = null,
                            )
                        }
                    }
                }
            }.launchIn(viewModelScope)
    }

    /**
     * 나의 키워드를 조회한다.
     */
    private fun getMyKeywords() {
        usecases.getMyKeywords().onEach { result ->
            when (result) {
                Result.Loading -> {
                    updateState {
                        this.copy(
                            loading = true,
                            error = null,
                        )
                    }
                }

                is Result.Error -> {
                    updateState {
                        this.copy(
                            loading = false,
                            error = result.error.asUiText(),
                        )
                    }
                    showSnackBar(ProfileErrorType.ProfileLoadFailed.asUiText())
                }

                is Result.Success -> {
                    updateState {
                        this.copy(
                            loading = false,
                            myKeywords = result.data.keywords.toUiModel(),
                        )
                    }
                }
            }
        }.launchIn(viewModelScope)
    }

    private suspend fun showSnackBar(message: UiText) {
        snackbarController.sendEvent(SnackbarEvent(message = message))
    }
}
