package com.peekr.presentation.notification.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.map
import com.peekr.core.common.logger.AppLogger
import com.peekr.core.domain.common.Result
import com.peekr.core.domain.notification.model.Notification
import com.peekr.domain.notification.usecase.NotificationUseCases
import com.peekr.presentation.notification.model.toUiModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
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

    /**
     * 알림 항목 클릭 시 알림 읽음 처리와 딥링크 이동을 수행한다.
     *
     * @param notificationId 알림 ID
     * @param deepLink 딥링크 URI
     */
    fun onNotificationClick(
        notificationId: Long,
        deepLink: String,
    ) {
        markAsRead(notificationId)
        _navigateToNotificationDetail.trySend(deepLink)
    }

    /**
     * 알림 읽음 처리 (낙관적 UI 처리)
     *
     * @param notificationId 알림 ID
     */
    private fun markAsRead(notificationId: Long) {
        // 낙관적 업데이트: API 결과와 무관하게 즉시 UI 반영, 실패 시 무시
        readNotificationIds.update { it + notificationId }

        viewModelScope.launch {
            val result = usecases.markAsRead(notificationId)
            if (result is Result.Error) {
                AppLogger.e(tag, "알림 읽음 처리 실패: ${result.error}")
            }
        }
    }
}
