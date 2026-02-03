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
                changeCurrentTargetUser(event.target)
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

    private fun changeCurrentTargetUser(target: UiDiscoverContext) {
        updateState {
            this.copy(
                currentDiscoverTarget = target,
            )
        }
    }

    private suspend fun showSnackbar(message: UiText) {
        snackbarController.sendEvent(SnackbarEvent(message))
    }
}
