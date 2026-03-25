package com.peekr.data.notification.datasource

import com.peekr.core.data.source.network.util.NetworkResult
import com.peekr.core.data.source.network.util.networkCall
import com.peekr.core.data.source.network.util.networkCallWithoutResponse
import com.peekr.data.notification.api.NotificationApi
import com.peekr.data.notification.dto.FcmTokenResponse
import com.peekr.data.notification.dto.NotificationCursorPageResponse
import com.peekr.data.notification.dto.RegisterFcmTokenRequest
import javax.inject.Inject

class NotificationNetworkDataSourceImpl @Inject constructor(
    private val notificationApi: NotificationApi,
) : NotificationNetworkDataSource {
    override suspend fun registerFcmToken(token: String): NetworkResult<FcmTokenResponse> =
        networkCall { notificationApi.registerFcmToken(RegisterFcmTokenRequest(token)) }

    override suspend fun getNotifications(
        cursor: Long?,
        size: Int,
    ): NetworkResult<NotificationCursorPageResponse> =
        networkCall { notificationApi.getNotifications(cursor, size) }

    override suspend fun markAsRead(notificationId: Long): NetworkResult<Unit> =
        networkCallWithoutResponse { notificationApi.markAsRead(notificationId) }
}
