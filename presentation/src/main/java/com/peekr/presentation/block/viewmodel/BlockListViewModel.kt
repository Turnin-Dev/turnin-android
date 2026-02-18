package com.peekr.presentation.block.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.filter
import androidx.paging.map
import com.peekr.core.common.logger.AppLogger
import com.peekr.core.domain.block.model.BlockedUser
import com.peekr.core.domain.common.Result
import com.peekr.core.presentation.common.snackbar.SnackbarController
import com.peekr.core.presentation.common.snackbar.SnackbarEvent
import com.peekr.core.presentation.ui.util.UiText
import com.peekr.domain.block.usecase.DeleteBlockUseCase
import com.peekr.domain.block.usecase.GetBlockedUsersUseCase
import com.peekr.presentation.block.error.asUiText
import com.peekr.presentation.block.model.toUiModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update

@HiltViewModel
class BlockListViewModel @Inject constructor(
    private val getBlockedUsersUseCase: GetBlockedUsersUseCase,
    private val deleteBlockUseCase: DeleteBlockUseCase,
    private val snackbarController: SnackbarController,
) : ViewModel() {
    private val tag = this::class.java.simpleName

    private val loadingIds = MutableStateFlow<Set<Long>>(emptySet())
    private val removedIds = MutableStateFlow<Set<Long>>(emptySet())

    private val basePagingData = getBlockedUsersUseCase()
        .catch { e ->
            AppLogger.d(tag, e, "Unexpected block list pagination error")
            emit(PagingData.empty())
        }
        .map { pagingData: PagingData<BlockedUser> ->
            pagingData.map { blockedUser ->
                blockedUser.toUiModel()
            }
        }
        .cachedIn(viewModelScope)

    val blockedUsersPagingData =
        combine(basePagingData, loadingIds, removedIds) { pagingData, loadingIds, removedIds ->
            pagingData.filter { it.id !in removedIds }
                .map { it.copy(loading = it.id in loadingIds) }
        }

    /**
     * 차단 해제
     *
     * @param blockId 차단 ID
     */
    fun unblock(blockId: Long) {
        deleteBlockUseCase(blockId).onEach { result ->
            when (result) {
                Result.Loading -> {
                    loadingIds.update { it + blockId }
                }

                is Result.Error -> {
                    loadingIds.update { it - blockId }
                    showSnackbar(result.error.asUiText())
                }

                is Result.Success -> {
                    loadingIds.update { it - blockId }
                    removedIds.update { it + blockId }
                }
            }
        }.launchIn(viewModelScope)
    }

    private suspend fun showSnackbar(message: UiText) {
        snackbarController.sendEvent(SnackbarEvent(message = message))
    }
}
