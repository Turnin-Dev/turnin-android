package com.peekr.presentation.profile.viewmodel

import androidx.lifecycle.viewModelScope
import com.peekr.core.common.logger.AppLogger
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
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

@HiltViewModel
class MyProfileViewModel @Inject constructor(
    private val snackbarController: SnackbarController,
    private val usecases: MyProfileUseCases,
) : MVIBaseViewModel<MyProfileContract.UiState, MyProfileContract.UiEvent, MyProfileContract.UiEffect>() {
    private val tag = this::class.java.simpleName

    override fun createInitialState(): MyProfileContract.UiState =
        MyProfileContract.UiState()

    init {
        // 각각 viewModelScope 내에서 병렬 수행
        observeMyProfile()
        observeMyKeywords()
        refreshMyProfile(false)
        refreshMyKeywords(false)
    }

    override suspend fun handleEvent(event: MyProfileContract.UiEvent) {
        when (event) {
            MyProfileContract.UiEvent.Refresh -> {
                refreshMyProfile(true)
                refreshMyKeywords(true)
            }
        }
    }

    /**
     * 나의 프로필 조회 (로컬 데이터 구독)
     */
    private fun observeMyProfile() {
        usecases.getMyProfile()
            .distinctUntilChanged()
            .onEach { myProfile ->
                updateState {
                    this.copy(myProfile = myProfile?.toUiModel())
                }
            }
            .catch { e ->
                AppLogger.e(tag, e, "getMyProfile() failed. (cause: ${e.message})")
                showSnackBar(ProfileErrorType.ProfileLoadFailed.asUiText())
            }
            .launchIn(viewModelScope)
    }

    /**
     * 나의 키워드 리스트 조회 (로컬 데이터 구독)
     */
    private fun observeMyKeywords() {
        usecases.getMyKeywords()
            .distinctUntilChanged()
            .onEach { myKeywords ->
                updateState {
                    this.copy(myKeywords = myKeywords.toUiModel())
                }
            }
            .catch { e ->
                AppLogger.e(tag, e, "getMyKeywords() failed. (cause: ${e.message})")
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
                                this.copy(myProfileLoading = true)
                            }
                        }
                    }

                    is Result.Error -> {
                        updateState {
                            this.copy(myProfileLoading = false)
                        }
                        showSnackBar(ProfileErrorType.ProfileLoadFailed.asUiText())
                    }

                    is Result.Success -> {
                        updateState {
                            this.copy(myProfileLoading = false)
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
                            this.copy(myKeywordsLoading = true)
                        }
                    }
                }

                is Result.Error -> {
                    updateState {
                        this.copy(myKeywordsLoading = false)
                    }
                    showSnackBar(ProfileErrorType.KeywordsLoadFailed.asUiText())
                }

                is Result.Success -> {
                    updateState {
                        this.copy(myKeywordsLoading = false)
                    }
                }
            }
        }.launchIn(viewModelScope)
    }

    private suspend fun showSnackBar(message: UiText) {
        snackbarController.sendEvent(SnackbarEvent(message = message))
    }
}
