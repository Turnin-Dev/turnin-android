package com.peekr.core.data.source.network.datasource

import com.peekr.core.data.source.network.api.NotificationApi
import com.peekr.core.data.source.network.dto.notification.request.DeactivateFcmTokenRequest
import com.peekr.core.data.source.network.dto.notification.request.RegisterFcmTokenRequest
import com.peekr.core.data.source.network.dto.notification.response.FcmTokenResponse
import com.peekr.core.data.source.network.dto.notification.response.NotificationCursorPageResponse
import com.peekr.core.data.source.network.util.NetworkResult
import com.peekr.core.data.source.network.util.networkCall
import com.peekr.core.data.source.network.util.networkCallWithoutResponse
import javax.inject.Inject

class NotificationNetworkDataSourceImpl @Inject constructor(
    private val notificationApi: NotificationApi,
) : NotificationNetworkDataSource {
    override suspend fun registerFcmToken(token: String): NetworkResult<FcmTokenResponse> =
        networkCall { notificationApi.registerFcmToken(RegisterFcmTokenRequest(token)) }

    override suspend fun deactivateToken(token: String): NetworkResult<Unit> =
        networkCallWithoutResponse {
            notificationApi.deactivateToken(DeactivateFcmTokenRequest(token))
        }

    override suspend fun getNotifications(
        cursor: Long?,
        size: Int,
    ): NetworkResult<NotificationCursorPageResponse> =
        networkCall { notificationApi.getNotifications(cursor, size) }

    override suspend fun markAsRead(notificationId: Long): NetworkResult<Unit> =
        networkCallWithoutResponse { notificationApi.markAsRead(notificationId) }
}
