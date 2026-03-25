package com.peekr.data.notification.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import com.peekr.core.common.coroutine.IO
import com.peekr.core.data.paging.PeekrCursorPagingSource
import com.peekr.core.data.source.network.error.toCommonErrorType
import com.peekr.core.data.source.network.util.NetworkResult
import com.peekr.core.domain.common.Result
import com.peekr.core.domain.common.coroutine.safeResultFlow
import com.peekr.core.domain.model.NotificationId
import com.peekr.data.notification.datasource.NotificationNetworkDataSource
import com.peekr.data.notification.dto.NotificationResponse
import com.peekr.domain.notification.error.NotificationErrorType
import com.peekr.domain.notification.model.Notification
import com.peekr.domain.notification.repository.NotificationRepository
import javax.inject.Inject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class NotificationRepositoryImpl @Inject constructor(
    private val notificationNetworkDataSource: NotificationNetworkDataSource,
    @param:IO private val ioDispatcher: CoroutineDispatcher,
) : NotificationRepository {
    override suspend fun registerFcmToken(token: String): Result<Unit, NotificationErrorType> =
        withContext(ioDispatcher) {
            when (val result = notificationNetworkDataSource.registerFcmToken(token)) {
                is NetworkResult.Success -> Result.Success(Unit)
                is NetworkResult.Error -> Result.Error(
                    error = NotificationErrorType.CommonError(result.error.toCommonErrorType()),
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

    override fun markAsRead(notificationId: NotificationId): Flow<Result<Unit, NotificationErrorType>> =
        safeResultFlow<Unit, NotificationErrorType>(ioDispatcher, { NotificationErrorType.Unexpected(it) }) {
            emit(Result.Loading)
            when (val result = notificationNetworkDataSource.markAsRead(notificationId.value)) {
                is NetworkResult.Success -> emit(Result.Success(Unit))
                is NetworkResult.Error -> {
                    val error = NotificationErrorType.CommonError(result.error.toCommonErrorType())
                    emit(Result.Error(error = error, message = result.message))
                }
            }
        }
}
