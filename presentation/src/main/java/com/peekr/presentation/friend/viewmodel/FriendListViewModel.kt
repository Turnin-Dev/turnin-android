package com.peekr.presentation.friend.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.map
import com.peekr.core.common.logger.AppLogger
import com.peekr.core.domain.common.Result
import com.peekr.core.domain.friend.model.FriendInfo
import com.peekr.core.domain.friend.model.FriendStatus
import com.peekr.core.domain.friend.model.IncomingRequest
import com.peekr.core.domain.user.usecase.GetMyUserIdUseCase
import com.peekr.core.presentation.common.snackbar.SnackbarController
import com.peekr.core.presentation.common.snackbar.SnackbarEvent
import com.peekr.core.presentation.ui.util.UiText
import com.peekr.domain.friend.error.FriendErrorType
import com.peekr.domain.friend.usecase.AcceptFriendRequestUseCase
import com.peekr.domain.friend.usecase.GetFriendsPaginationUseCase
import com.peekr.domain.friend.usecase.GetIncomingRequestsUseCase
import com.peekr.presentation.friend.error.asUiText
import com.peekr.presentation.friend.model.toUiModel
import com.peekr.presentation.friend.state.FriendEffect
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

private typealias FriendID = Long

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class FriendListViewModel @Inject constructor(
    private val getFriendsPaginationUseCase: GetFriendsPaginationUseCase,
    private val getIncomingRequestsUseCase: GetIncomingRequestsUseCase,
    private val acceptFriendRequestUseCase: AcceptFriendRequestUseCase,
    private val getMyUserIdUseCase: GetMyUserIdUseCase,
    private val snackbarController: SnackbarController,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {
    private val tag = this::class.java.simpleName

    private val currentUserId: Long? = savedStateHandle.get<Long>("userId")
    private val myUserId = MutableStateFlow<Long?>(null)
    private val _isMyFriendList = MutableStateFlow<Boolean>(false)
    val isMyFriendList = _isMyFriendList.asStateFlow()

    private val _effect = Channel<FriendEffect>()
    val effect = _effect.receiveAsFlow()

    private var _requestersStateInitialized = MutableStateFlow(false)
    val requestersStateInitialized = _requestersStateInitialized.asStateFlow()

    // 친구 상태 변경을 위한 상태 값
    private var _requesterStatus = MutableStateFlow(mapOf<FriendID, FriendStatus>())
    val requesterStatus = _requesterStatus.asStateFlow()

    init {
        viewModelScope.launch {
            myUserId.update { getMyUserIdUseCase()?.value }

            _isMyFriendList.update { myUserId.value == currentUserId }

            if (currentUserId == null) {
                showSnackbar(FriendErrorType.UserIdNotFound.asUiText())
            }
        }
    }

    val friendsPagingData = if (currentUserId != null && currentUserId > 0) {
        getFriendsPaginationUseCase(currentUserId)
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

    val requestersPagingData = requestersStateInitialized
        .flatMapLatest { initialized ->
            if (initialized) {
                getIncomingRequestsUseCase()
                    .catch { e ->
                        AppLogger.e(tag, e, "Unexpected incoming-requests pagination error")
                    }
                    .map { pagingData: PagingData<IncomingRequest> ->
                        pagingData.map { incomingRequest ->
                            incomingRequest.toUiModel()
                        }
                    }
            } else {
                flowOf(PagingData.empty())
            }
        }
        .cachedIn(viewModelScope)

    /**
     * 친구 요청 수락
     */
    fun acceptFriendRequest(
        friendId: Long,
        status: FriendStatus,
    ) {
        if (myUserId.value == null || status == FriendStatus.FRIENDS) return

        viewModelScope.launch {
            _requesterStatus.update { it + (friendId to FriendStatus.FRIENDS) }

            acceptFriendRequestUseCase(
                myUserId = myUserId.value!!,
                targetUserId = friendId,
            ).onEach { result ->
                when (result) {
                    Result.Loading -> {}
                    is Result.Error -> {
                        _requesterStatus.update { it + (friendId to FriendStatus.RECEIVED) }
                        showSnackbar(result.error.asUiText())
                    }

                    is Result.Success -> {
                        _requesterStatus.update { it + (friendId to FriendStatus.FRIENDS) }
                    }
                }
            }
        }
    }

    /**
     * [otherUserId]값과 나의 사용자 ID를 비교한 후
     * 사용자 프로필 혹은 나의 프로필로 이동하는 일회성 이벤트를 보낸다.
     *
     * @param otherUserId 비교할 사용자 ID
     */
    fun navigateToUserProfileOrMyProfile(otherUserId: Long) {
        viewModelScope.launch {
            if (myUserId.value == null || myUserId.value != otherUserId) {
                _effect.send(FriendEffect.NavigateToUserProfile(otherUserId))
            } else {
                _effect.send(FriendEffect.NavigateToMyProfile)
            }
        }
    }

    private suspend fun showSnackbar(message: UiText) {
        snackbarController.sendEvent(SnackbarEvent(message = message))
    }

    override fun onCleared() {
        super.onCleared()
        _effect.close()
    }
}
