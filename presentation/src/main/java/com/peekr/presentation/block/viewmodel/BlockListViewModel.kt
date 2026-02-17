package com.peekr.presentation.block.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.map
import com.peekr.core.common.logger.AppLogger
import com.peekr.core.domain.block.model.BlockedUser
import com.peekr.domain.block.usecase.GetBlockedUsersUseCase
import com.peekr.presentation.block.model.toUiModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map

@HiltViewModel
class BlockListViewModel @Inject constructor(
    private val getBlockedUsersUseCase: GetBlockedUsersUseCase,
) : ViewModel() {
    private val tag = this::class.java.simpleName

    val blockedUsersPagingData = getBlockedUsersUseCase()
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
}
