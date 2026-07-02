package com.turnin.presentation.friend.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.TerminalSeparatorType
import androidx.paging.cachedIn
import androidx.paging.filter
import androidx.paging.insertHeaderItem
import androidx.paging.map
import com.turnin.core.common.logger.AppLogger
import com.turnin.core.domain.common.Result
import com.turnin.core.domain.friend.model.FriendInfo
import com.turnin.core.domain.friend.model.FriendStatus
import com.turnin.core.presentation.common.navigation.args.UserProfileArgs
import com.turnin.core.presentation.common.snackbar.SnackbarController
import com.turnin.core.presentation.common.snackbar.SnackbarEvent
import com.turnin.core.presentation.ui.util.UiText
import com.turnin.domain.friend.error.FriendErrorType
import com.turnin.domain.friend.usecase.FriendUseCases
import com.turnin.presentation.friend.error.asUiText
import com.turnin.presentation.friend.model.UiRequester
import com.turnin.presentation.friend.model.toUiFriendInfo
import com.turnin.presentation.friend.model.toUiModel
import com.turnin.presentation.friend.state.FriendEffect
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

private typealias UserID = Long

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class FriendListViewModel @Inject constructor(
    private val usecases: FriendUseCases,
    private val snackbarController: SnackbarController,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {
    private val tag = this::class.java.simpleName

    private val currentUserId: Long? = savedStateHandle.get<Long>("userId")
    private val myUserId = MutableStateFlow<Long?>(null)

    private val _isMyFriendList = MutableStateFlow(false)
    val isMyFriendList = _isMyFriendList.asStateFlow()

    private val _effect = Channel<FriendEffect>()
    val effect = _effect.receiveAsFlow()

    // 친구 요청 목록 페이징 데이터 초기화 여부
    private var isInitRequestersPagingData = MutableStateFlow(false)

    // 친구 상태 변경을 위한 상태 값
    private var _requesterStatus = MutableStateFlow(mapOf<UserID, FriendStatus>())
    val requesterStatus = _requesterStatus.asStateFlow()

    // 수락된 요청자 목록
    private val cachedAcceptedRequesters = MutableStateFlow<Set<UiRequester>>(emptySet())

    init {
        viewModelScope.launch {
            // 나의 사용자 ID를 로드하고 나의 친구 목록인지 판단
            val localMyUserId = usecases.getMyUserId()
            if (localMyUserId == null) {
                showSnackbar(FriendErrorType.MyUserIdNotFound.asUiText())
                return@launch
            }
            myUserId.update { localMyUserId.value }
            _isMyFriendList.update { myUserId.value == currentUserId }

            // 인자로 넘어온 현재 사용자 ID가 null이면 스낵바 에러 표시
            if (currentUserId == null) {
                showSnackbar(FriendErrorType.UserIdNotFound.asUiText())
            }
        }
    }

    // 친구 목록 베이스 페이징 데이터
    private val friendsBasePagingData = if (currentUserId != null && currentUserId > 0) {
        usecases.getFriends(currentUserId)
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

    /** 친구 목록 페이징 데이터 */
    val friendsPagingData =
        combine(friendsBasePagingData, cachedAcceptedRequesters) { pagingData, acceptedRequester ->
            val transformPaging = pagingData.filter { pd -> pd.id !in acceptedRequester.map { it.id } }

            acceptedRequester.fold(transformPaging) { acc, requester ->
                acc.insertHeaderItem(
                    terminalSeparatorType = TerminalSeparatorType.SOURCE_COMPLETE,
                    item = requester.toUiFriendInfo(),
                )
            }
        }
            .cachedIn(viewModelScope)

    /** 친구 요청 목록 페이징 데이터 */
    val requestersPagingData = isInitRequestersPagingData
        .filter { it }
        .flatMapLatest {
            usecases.getIncomingRequests()
                .catch { e ->
                    AppLogger.e(tag, e, "Unexpected incoming-requests pagination error")
                    emit(PagingData.empty())
                }
                .map { pagingData ->
                    pagingData.map { incomingRequest ->
                        incomingRequest.toUiModel()
                    }
                }
        }
        .cachedIn(viewModelScope)

    /**
     * 친구 요청 페이징 데이터 초기 로드 트리거
     */
    fun initRequestersPagingData() {
        if (!isInitRequestersPagingData.value) {
            isInitRequestersPagingData.update { true }
        }
    }

    /**
     * 친구 목록 캐시 초기화
     */
    fun resetFriendsCache() {
        cachedAcceptedRequesters.update { emptySet() }
    }

    /**
     * 친구 요청 목록 캐시 초기화
     */
    fun resetRequestersCache() {
        _requesterStatus.update { emptyMap() }
    }

    /**
     * 친구 요청 수락
     *
     * @param requester 요청자
     * @param friendStatus 요청자의 친구 상태
     */
    fun acceptFriendRequest(
        requester: UiRequester,
        friendStatus: FriendStatus,
    ) {
        // 조건 1. 나의 사용자 ID가 null이 아니여야 한다.
        // 조건 2. 현재 친구 상태가 '친구'인 상태가 아니여야 한다.
        // 조건 3. 나의 사용자 ID와 현재 사용자 ID가 같아야 한다.

        // 1) 위 조건 중 하나라도 만족하지 않는 경우 아무 작업도 수행하지 않는다.
        if (myUserId.value == null ||
            friendStatus == FriendStatus.FRIENDS ||
            myUserId.value != currentUserId
        ) {
            return
        }

        // 2) 즉시 '친구' 상태로 업데이트 (낙관적 UI 처리)
        _requesterStatus.update { it + (requester.userId to FriendStatus.FRIENDS) }

        // 3) 친구 수락 수행
        usecases.acceptFriendRequest(
            myUserId = myUserId.value!!,
            targetUserId = requester.userId,
        ).onEach { result ->
            when (result) {
                Result.Loading -> Unit
                is Result.Error -> {
                    // 낙관적 UI 처리를 위한 에러 분기
                    if (result.error is FriendErrorType.AlreadyProceed) {
                        cachedAcceptedRequesters.update { it + requester }
                        return@onEach
                    }
                    // 에러 발생 시 친구 상태 롤백, 스낵바 에러 표시
                    _requesterStatus.update { it + (requester.userId to friendStatus) }
                    showSnackbar(result.error.asUiText())
                }

                is Result.Success -> {
                    cachedAcceptedRequesters.update { it + requester }
                }
            }
        }.launchIn(viewModelScope)
    }

    /**
     * [args]값에서 다른 사용자의 ID를 꺼내 나의 사용자 ID와 비교한 후
     * 사용자 프로필 혹은 나의 프로필로 이동하는 일회성 이벤트를 보낸다.
     *
     * @param args 사용자 프로필 네비게이션 인자 값
     */
    fun navigateToUserProfileOrMyProfile(args: UserProfileArgs) {
        viewModelScope.launch {
            val localMyUserId = myUserId.value ?: usecases.getMyUserId()?.value
            if (localMyUserId == null) {
                showSnackbar(FriendErrorType.MyUserIdNotFound.asUiText())
                return@launch
            }

            if (localMyUserId != args.userId) {
                _effect.send(FriendEffect.NavigateToUserProfile(args))
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
