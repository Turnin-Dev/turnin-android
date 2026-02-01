package com.peekr.presentation.friend.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.map
import com.peekr.core.common.logger.AppLogger
import com.peekr.core.domain.friend.model.FriendInfo
import com.peekr.domain.friend.usecase.GetFriendsPaginationUseCase
import com.peekr.presentation.friend.model.toUiModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map

@HiltViewModel
class FriendListViewModel @Inject constructor(
    private val getFriendsPaginationUseCase: GetFriendsPaginationUseCase,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {
    private val tag = this::class.java.simpleName

    val userId: Long = savedStateHandle.get<Long>("userId") ?: -1

    // TODO: 이렇게 검사할거면 UserId VO 객체의 유효성 검사가 의미가 있는지 생각해보기
    val friendsPagingData = if (userId > 0) {
        getFriendsPaginationUseCase(userId)
            .catch { e ->
                AppLogger.d(tag, e, "Unexpected friend pagination error")
                emit(PagingData.empty())
            }
            .map { pagingData: PagingData<FriendInfo> ->
                pagingData.map { friendInfo ->
                    friendInfo.toUiModel()
                }
            }
            .cachedIn(viewModelScope)
    } else {
        flowOf(PagingData.empty())
    }
}
