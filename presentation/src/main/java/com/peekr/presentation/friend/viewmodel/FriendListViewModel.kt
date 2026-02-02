package com.peekr.presentation.friend.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.map
import com.peekr.core.common.logger.AppLogger
import com.peekr.core.domain.friend.model.FriendInfo
import com.peekr.core.domain.user.usecase.GetMyUserIdUseCase
import com.peekr.core.presentation.common.snackbar.SnackbarController
import com.peekr.core.presentation.common.snackbar.SnackbarEvent
import com.peekr.core.presentation.ui.util.UiText
import com.peekr.domain.friend.error.FriendErrorType
import com.peekr.domain.friend.usecase.GetFriendsPaginationUseCase
import com.peekr.presentation.friend.error.asUiText
import com.peekr.presentation.friend.model.toUiModel
import com.peekr.presentation.friend.state.FriendEffect
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

@HiltViewModel
class FriendListViewModel @Inject constructor(
    private val getFriendsPaginationUseCase: GetFriendsPaginationUseCase,
    private val getMyUserIdUseCase: GetMyUserIdUseCase,
    private val snackbarController: SnackbarController,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {
    private val tag = this::class.java.simpleName

    private val _effect = Channel<FriendEffect>()
    val effect = _effect.receiveAsFlow()

    private val currentUserId: Long by lazy {
        requireNotNull(savedStateHandle.get<Long>("userId"))
    }

    init {
        viewModelScope.launch {
            checkNavArgument()
        }
    }

    // TODO: 이렇게 검사할거면 UserId VO 객체의 유효성 검사가 의미가 있는지 생각해보기
    val friendsPagingData = getFriendsPaginationUseCase(currentUserId)
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

    // TODO: 친구 목록에서 '나'를 클릭 시 처리 필요

    /**
     * [otherUserId]값과 나의 사용자 ID를 비교한 후
     * 사용자 프로필 혹은 나의 프로필로 이동하는 일회성 이벤트를 보낸다.
     *
     * @param otherUserId 비교할 사용자 ID
     */
    fun navigateToUserProfileOrMyProfile(otherUserId: Long) {
        viewModelScope.launch {
            val myUserId = getMyUserIdUseCase()
            if (myUserId == null || myUserId.value != otherUserId) {
                _effect.send(FriendEffect.NavigateToUserProfile(otherUserId))
            } else {
                _effect.send(FriendEffect.NavigateToMyProfile)
            }
        }
    }

    private suspend fun checkNavArgument() = runCatching {
        currentUserId
    }
        .onFailure {
            showSnackbar(FriendErrorType.UserIdNotFound.asUiText())
        }

    private suspend fun showSnackbar(message: UiText) {
        snackbarController.sendEvent(SnackbarEvent(message = message))
    }

    override fun onCleared() {
        super.onCleared()
        _effect.close()
    }
}
