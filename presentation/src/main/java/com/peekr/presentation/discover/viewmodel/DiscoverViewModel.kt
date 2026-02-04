package com.peekr.presentation.discover.viewmodel

import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.map
import com.peekr.core.common.logger.AppLogger
import com.peekr.core.domain.discover.model.DiscoverContext
import com.peekr.core.presentation.common.snackbar.SnackbarController
import com.peekr.core.presentation.common.snackbar.SnackbarEvent
import com.peekr.core.presentation.common.viewmodel.MVIBaseViewModel
import com.peekr.core.presentation.ui.util.UiText
import com.peekr.domain.discover.error.DiscoverErrorType
import com.peekr.domain.discover.usecase.DiscoverUseCases
import com.peekr.presentation.discover.error.asUiText
import com.peekr.presentation.discover.model.UiDiscoverContext
import com.peekr.presentation.discover.model.toUiModel
import com.peekr.presentation.discover.state.DiscoverContract
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

@HiltViewModel
class DiscoverViewModel @Inject constructor(
    private val usecases: DiscoverUseCases,
    private val snackbarController: SnackbarController,
) : MVIBaseViewModel<DiscoverContract.UiState, DiscoverContract.UiEvent, DiscoverContract.UiEffect>() {
    private val tag = this::class.java.simpleName

    init {
        initialize()
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    val discoverContexts = uiState
        .map { it.currentDiscoverTarget?.user?.userId }
        .distinctUntilChanged()
        .flatMapLatest { userId ->
            if (userId != null) {
                usecases.getDiscoverContexts(userId)
                    .catch { e ->
                        AppLogger.e(tag, e, "Unexpected discover contexts pagination error")
                        emit(PagingData.empty())
                    }
                    .map { pagingData: PagingData<DiscoverContext> ->
                        pagingData.map { discoverContext ->
                            discoverContext.toUiModel()
                        }
                    }
            } else {
                flowOf(PagingData.empty())
            }
        }
        .cachedIn(viewModelScope)

    override fun createInitialState(): DiscoverContract.UiState =
        DiscoverContract.UiState()

    override suspend fun handleEvent(event: DiscoverContract.UiEvent) {
        when (event) {
            is DiscoverContract.UiEvent.ChangeCurrentDiscoverTarget -> {
                changeCurrentDiscoverTarget(event.target)
            }

            is DiscoverContract.UiEvent.SelectFeed -> {
                updateState {
                    this.copy(
                        selectedDiscoverTarget =
                            if (currentUiState.selectedDiscoverTarget == event.discoverContext) {
                                null
                            } else {
                                event.discoverContext
                            },
                    )
                }
            }

            DiscoverContract.UiEvent.ReDiscover -> {
                reDiscover()
            }
        }
    }

    /**
     * 초기화 작업
     *
     * 1. 히스토리 바에 나를 추가
     * 2. 현재 탐색 대상을 나로 설정
     */
    private fun initialize() {
        viewModelScope.launch {
            val myDiscoverContext = usecases.getMyDiscoverContext()
            if (myDiscoverContext == null) {
                showSnackbar(DiscoverErrorType.MyProfileNotFound.asUiText())
                return@launch
            }
            // 히스토리에 나를 추가하고 현재 탐색 대상을 나로 설정
            val myDiscoverContextUiModel = myDiscoverContext.toUiModel()
            updateState {
                this.copy(
                    histories = emptyList<UiDiscoverContext>() + myDiscoverContextUiModel,
                    currentDiscoverTarget = myDiscoverContextUiModel,
                )
            }
        }
    }

    /**
     * 현재 탐색 대상 변경
     *
     * 1. 현재 탐색 대상 변경
     * 2. 재탐색 대상 초기화
     */
    private fun changeCurrentDiscoverTarget(target: UiDiscoverContext) {
        updateState {
            this.copy(
                currentDiscoverTarget = target,
                selectedDiscoverTarget = null,
            )
        }
    }

    /**
     * 재탐색
     *
     * 1. 현재 탐색 대상을 재탐색 대상으로 변경 (새로운 페이징 트리거)
     * 2. 히스토리 바에 재탐색 대상 추가
     * 3. 재탐색 대상 초기화
     */
    private suspend fun reDiscover() {
        val selectedTarget = currentUiState.selectedDiscoverTarget
        val currentTarget = currentUiState.currentDiscoverTarget
        val histories = currentUiState.histories

        // 선택된 탐색 대상 혹은 현재 탐색 대상이 없는 경우 에러 표시
        if (selectedTarget == null || currentTarget == null) {
            showSnackbar(DiscoverErrorType.NotSelectedTarget.asUiText())
            return
        }

        // 1) 선택된 탐색 대상, 현재 탐색 대상 인덱스 결정
        val selectedTargetIndex = histories.indexOfFirst {
            it.user.userId == selectedTarget.user.userId
        }
        val currentTargetIndex = histories.indexOfFirst {
            it.user.userId == currentTarget.user.userId
        }

        // 2) 히스토리 정제
        val trimmedHistories = when {
            // 선택된 탐색 대상이 히스토리에 있는 경우 대상을 제외하고 추출
            selectedTargetIndex != -1 -> {
                histories.subList(0, selectedTargetIndex)
            }

            // 선택된 탐색 대상이 히스토리에 없으므로 현재 탐색 대상을 포함하여 추출
            currentTargetIndex != -1 -> {
                histories.subList(0, currentTargetIndex + 1)
            }

            else -> histories
        }

        // 3) 정제된 히스토리에 새로운 타겟 추가 및 상태 업데이트
        updateState {
            this.copy(
                currentDiscoverTarget = selectedTarget,
                histories = trimmedHistories + selectedTarget,
                selectedDiscoverTarget = null,
            )
        }
    }

    private suspend fun showSnackbar(message: UiText) {
        snackbarController.sendEvent(SnackbarEvent(message))
    }
}
