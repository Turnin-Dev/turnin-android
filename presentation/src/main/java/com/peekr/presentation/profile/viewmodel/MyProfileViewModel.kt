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
        // 각각 viewModelScope 내에서 병렬 수행
        observeMyProfile()
        refreshMyProfile(false)
        observeMyKeywords()
        refreshMyKeywords(false)
    }

    override suspend fun handleEvent(event: MyProfileContract.UiEvent) {
        when (event) {
            else -> {}
        }
    }

    /**
     * 나의 프로필 조회 (로컬 데이터 구독)
     */
    private fun observeMyProfile() {
        usecases.getMyProfile()
            .onEach { myProfile ->
                updateState {
                    this.copy(myProfile = myProfile?.toUiModel())
                }
            }
            .launchIn(viewModelScope)
    }

    /**
     * 나의 키워드 리스트 조회 (로컬 데이터 구독)
     */
    private fun observeMyKeywords() {
        usecases.getMyKeywords()
            .onEach { myKeywords ->
                updateState {
                    this.copy(myKeywords = myKeywords.map { it.toUiModel() })
                }
            }
            .launchIn(viewModelScope)
    }

    /**
     * 나의 프로필 새로고침 (서버에서 조회 후 로컬 데이터 업데이트)
     *
     * @param activeLoading 로딩 활성화 여부
     */
    private fun refreshMyProfile(activeLoading: Boolean) {
        usecases.refreshMyProfile()
            .onEach { result ->
                when (result) {
                    Result.Loading -> {
                        if (activeLoading) {
                            updateState {
                                this.copy(loading = true)
                            }
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
     * 나의 키워드 리스트 새로고침 (서버에서 조회 후 로컬 데이터 업데이트)
     *
     * @param activeLoading 로딩 활성화 여부
     */
    private fun refreshMyKeywords(activeLoading: Boolean) {
        usecases.refreshMyKeywords().onEach { result ->
            when (result) {
                Result.Loading -> {
                    if (activeLoading) {
                        updateState {
                            this.copy(loading = true)
                        }
                    }
                }

                is Result.Error -> {
                    updateState {
                        this.copy(
                            loading = false,
                            error = result.error.asUiText(),
                        )
                    }
                    showSnackBar(ProfileErrorType.KeywordsLoadFailed.asUiText())
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

    private suspend fun showSnackBar(message: UiText) {
        snackbarController.sendEvent(SnackbarEvent(message = message))
    }
}
