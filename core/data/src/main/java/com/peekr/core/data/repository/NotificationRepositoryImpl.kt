package com.peekr.core.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import com.google.firebase.messaging.FirebaseMessaging
import com.peekr.core.common.coroutine.IO
import com.peekr.core.common.fcm.FcmTopic
import com.peekr.core.common.logger.AppLogger
import com.peekr.core.data.paging.PeekrCursorPagingSource
import com.peekr.core.data.source.local.datastore.DataStoreKey
import com.peekr.core.data.source.local.datastore.DataStoreManager
import com.peekr.core.data.source.network.datasource.NotificationNetworkDataSource
import com.peekr.core.data.source.network.dto.notification.response.NotificationResponse
import com.peekr.core.data.source.network.error.toCommonErrorType
import com.peekr.core.data.source.network.util.NetworkResult
import com.peekr.core.domain.common.Result
import com.peekr.core.domain.common.coroutine.runCatchingSafe
import com.peekr.core.domain.common.error.CommonErrorType
import com.peekr.core.domain.model.NotificationId
import com.peekr.core.domain.notification.model.Notification
import com.peekr.core.domain.notification.model.NotificationPagingTokens
import com.peekr.core.domain.notification.repository.NotificationRepository
import com.peekr.core.domain.setting.model.NotificationSyncState
import javax.inject.Inject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class NotificationRepositoryImpl @Inject constructor(
    private val notificationNetworkDataSource: NotificationNetworkDataSource,
    private val dataStoreManager: DataStoreManager,
    @param:IO private val ioDispatcher: CoroutineDispatcher,
) : NotificationRepository {
    private val tag = this::class.java.simpleName

    override suspend fun getFcmToken(): String? =
        runCatchingSafe {
            FirebaseMessaging.getInstance().token.await()
        }
            .onFailure { e -> AppLogger.e(tag, "FCM 토큰 발급 실패: ${e.message}") }
            .getOrNull()

    override suspend fun unsubscribeFromTopic() {
        runCatchingSafe {
            FirebaseMessaging.getInstance()
                .unsubscribeFromTopic(FcmTopic.ALL)
                .await()
        }.onFailure { e ->
            AppLogger.e(tag, "FCM 토픽 구독 해제 실패: ${e.message}")
        }
    }

    override suspend fun subscribeToTopic() {
        runCatchingSafe {
            FirebaseMessaging.getInstance()
                .subscribeToTopic(FcmTopic.ALL)
                .await()
        }
            .onFailure { e ->
                AppLogger.e(tag, "FCM 토픽 구독 실패: ${e.message}")
            }
    }

    override suspend fun registerFcmToken(token: String): Result<Unit, CommonErrorType> =
        withContext(ioDispatcher) {
            when (val result = notificationNetworkDataSource.registerFcmToken(token)) {
                is NetworkResult.Success -> Result.Success(Unit)
                is NetworkResult.Error -> Result.Error(
                    error = result.error.toCommonErrorType(),
                    message = result.message,
                )
            }
        }

    override suspend fun deactivateFcmToken(token: String): Result<Unit, CommonErrorType> =
        withContext(ioDispatcher) {
            when (val result = notificationNetworkDataSource.deactivateToken(token)) {
                is NetworkResult.Success -> Result.Success(Unit)
                is NetworkResult.Error -> Result.Error(
                    error = result.error.toCommonErrorType(),
                    message = result.message,
                )
            }
        }

    override fun getNotifications(): Flow<PagingData<Notification>> {
        val pageSize = NotificationPagingTokens.PAGE_SIZE
        val prefetchDistance = NotificationPagingTokens.PREFETCH_DISTANCE

        return Pager(
            config = PagingConfig(
                pageSize = pageSize,
                prefetchDistance = prefetchDistance,
                initialLoadSize = pageSize + prefetchDistance,
            ),
            pagingSourceFactory = {
                PeekrCursorPagingSource<Long, NotificationResponse>(
                    apiCall = { nextCursor ->
                        notificationNetworkDataSource.getNotifications(nextCursor, pageSize)
                    },
                )
            },
        )
            .flow
            .map { pagingData ->
                pagingData.map(NotificationResponse::toDomainModel)
            }
    }

    override suspend fun markAsRead(notificationId: NotificationId): Result<Unit, CommonErrorType> =
        withContext(ioDispatcher) {
            when (val result = notificationNetworkDataSource.markAsRead(notificationId.value)) {
                is NetworkResult.Success -> Result.Success(Unit)
                is NetworkResult.Error -> {
                    val error = result.error.toCommonErrorType()
                    Result.Error(error = error, message = result.message)
                }
            }
        }

    override suspend fun getNotificationSyncState(): NotificationSyncState? =
        dataStoreManager.getStringData(DataStoreKey.Setting.NotificationSyncState)
            .first()
            ?.let { runCatching { NotificationSyncState.valueOf(it) }.getOrNull() }

    override suspend fun setNotificationSyncState(state: NotificationSyncState) {
        dataStoreManager.saveStringData(
            key = DataStoreKey.Setting.NotificationSyncState,
            value = state.name,
        )
    }
}
