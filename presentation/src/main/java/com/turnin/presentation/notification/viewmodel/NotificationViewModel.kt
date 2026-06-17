package com.turnin.presentation.notification.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.map
import com.turnin.core.common.logger.AppLogger
import com.turnin.core.domain.common.Result
import com.turnin.core.domain.notification.model.Notification
import com.turnin.domain.notification.usecase.NotificationUseCases
import com.turnin.presentation.notification.error.asUiText
import com.turnin.presentation.notification.model.toUiModel
import com.turnin.presentation.notification.state.AnnouncementState
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class NotificationViewModel @Inject constructor(
    private val usecases: NotificationUseCases,
) : ViewModel() {
    private val tag = this::class.java.simpleName

    private val _navigateToNotificationDetail = Channel<String>(Channel.CONFLATED)
    val navigateToNotificationDetail = _navigateToNotificationDetail.receiveAsFlow()

    private val readNotificationIds = MutableStateFlow<Set<Long>>(emptySet())

    private val basePagingData = usecases.getNotifications()
        .catch { e ->
            AppLogger.e(tag, e, "Unexpected notification pagination error")
            emit(PagingData.empty())
        }
        .map { pagingData: PagingData<Notification> ->
            pagingData.map { notification ->
                notification.toUiModel()
            }
        }
        .cachedIn(viewModelScope)

    val notificationsPagingData =
        combine(basePagingData, readNotificationIds) { pagingData, readNotificationIds ->
            pagingData.map {
                val isRead = if (it.id in readNotificationIds) true else it.isRead
                it.copy(isRead = isRead)
            }
        }
            .cachedIn(viewModelScope)

    private val _announcementUiState = MutableStateFlow(AnnouncementState())
    val announcementUiState = _announcementUiState.asStateFlow()

    init {
        getAnnouncements()
    }

    // ------------------------------ 개인 알림 ------------------------------

    /**
     * 알림 항목 클릭 시 알림 읽음 처리와 딥링크 이동을 수행한다.
     *
     * @param notificationId 알림 ID
     * @param deepLink 딥링크 URI
     * @param currentIsRead 현재 읽음 여부 상태
     */
    fun onNotificationClick(
        notificationId: Long,
        deepLink: String,
        currentIsRead: Boolean,
    ) {
        markAsRead(notificationId, currentIsRead)
        _navigateToNotificationDetail.trySend(deepLink)
    }

    /**
     * 알림 읽음 처리 (낙관적 UI 처리)
     *
     * @param notificationId 알림 ID
     * @param currentIsRead 현재 읽음 여부 상태
     */
    private fun markAsRead(
        notificationId: Long,
        currentIsRead: Boolean,
    ) {
        // 낙관적 업데이트: API 결과와 무관하게 즉시 UI 반영, 실패 시 무시
        readNotificationIds.update { it + notificationId }

        if (!currentIsRead) {
            viewModelScope.launch {
                val result = usecases.markAsRead(notificationId)
                if (result is Result.Error) {
                    AppLogger.e(tag, "알림 읽음 처리 실패(notificationId: $notificationId): ${result.error}")
                }
            }
        }
    }

    // ------------------------------ 공지 알림 ------------------------------

    /**
     * 공지 목록 조회
     */
    fun getAnnouncements() {
        usecases.getAnnouncements()
            .onEach { result ->
                when (result) {
                    Result.Loading -> {
                        _announcementUiState.update {
                            it.copy(loading = true)
                        }
                    }

                    is Result.Error -> {
                        _announcementUiState.update {
                            it.copy(loading = false, error = result.error.asUiText())
                        }
                    }

                    is Result.Success -> {
                        _announcementUiState.update {
                            it.copy(
                                loading = false,
                                error = null,
                                announcements = result.data.map { ann -> ann.toUiModel() },
                            )
                        }
                    }
                }
            }
            .launchIn(viewModelScope)
    }

    /**
     * 공지 알림 읽음 처리 (낙관적 UI 처리)
     *
     * @param announcementId 공지 ID
     * @param currentIsRead 현재 읽음 여부 상태
     */
    fun markAnnouncementAsRead(
        announcementId: Long,
        currentIsRead: Boolean,
    ) {
        // 낙관적 업데이트: API 결과와 무관하게 즉시 UI 반영, 실패 시 무시
        _announcementUiState.update { state ->
            val announcements = state.announcements.map { ann ->
                val isRead = if (ann.id == announcementId) true else ann.isRead
                ann.copy(isRead = isRead)
            }
            state.copy(announcements = announcements)
        }

        if (!currentIsRead) {
            viewModelScope.launch {
                val result = usecases.markAnnouncementAsRead(announcementId)
                if (result is Result.Error) {
                    AppLogger.e(tag, "공지 알림 읽음 처리 실패(announcementId: $announcementId): ${result.error}")
                }
            }
        }
    }
}
