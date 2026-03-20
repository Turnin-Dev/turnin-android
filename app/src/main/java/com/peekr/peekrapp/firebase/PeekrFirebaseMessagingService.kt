package com.peekr.peekrapp.firebase

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import androidx.core.app.NotificationCompat
import coil.Coil
import coil.request.ImageRequest
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.peekr.core.common.coroutine.ApplicationScope
import com.peekr.core.common.coroutine.IO
import com.peekr.core.common.fcm.FcmDataKey
import com.peekr.core.common.logger.AppLogger
import com.peekr.core.domain.auth.repository.AuthRepository
import com.peekr.core.domain.common.Result
import com.peekr.core.domain.model.NotificationType
import com.peekr.domain.notification.repository.NotificationRepository
import com.peekr.peekrapp.MainActivity
import com.peekr.peekrapp.R
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@AndroidEntryPoint
class PeekrFirebaseMessagingService : FirebaseMessagingService() {
    private val tag = this::class.java.simpleName

    @IO
    @Inject
    lateinit var ioDispatcher: CoroutineDispatcher

    @ApplicationScope
    @Inject
    lateinit var applicationScope: CoroutineScope

    @Inject
    lateinit var notificationRepository: NotificationRepository

    @Inject
    lateinit var authRepository: AuthRepository

    /**
     * FCM 토큰이 새로 발급되거나 갱신될 때 호출된다.
     * 서버에 새 토큰을 등록한다.
     */
    override fun onNewToken(token: String) {
        super.onNewToken(token)
        // 로그인 상태일 때만 서버에 토큰 등록
        applicationScope.launch {
            withContext(ioDispatcher) {
                if (authRepository.isLoggedIn()) {
                    registerTokenToServer(token)
                }
            }
        }
    }

    /**
     * FCM 메시지 수신 시 호출된다.
     * 앱이 포그라운드 상태일 때만 호출된다.
     * 백그라운드에서는 OS가 자동으로 알림을 표시한다.
     */
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        // 포그라운드 상태에서 직접 알림 빌드
        remoteMessage.notification?.let { notification ->
            showNotification(
                title = notification.title ?: "",
                body = notification.body ?: "",
                imageUrl = notification.imageUrl?.toString(),
                data = remoteMessage.data,
            )
        }
    }

    // 알림 표시
    private fun showNotification(
        title: String,
        body: String,
        imageUrl: String?,
        data: Map<String, String>,
    ) {
        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager

        // 알림 유형에 따른 채널 설정
        val notiType = data[FcmDataKey.NOTI_TYPE]
            ?.let { runCatching { NotificationType.valueOf(it) }.getOrNull() }

        val channelId = PeekrNotificationChannel.getChannelId(notiType)
        val channelName = when (channelId) {
            PeekrNotificationChannel.HIGH_ID -> getString(R.string.notification_channel_high_name)
            else -> getString(R.string.notification_channel_normal_name)
        }
        val importance = when (channelId) {
            PeekrNotificationChannel.HIGH_ID -> NotificationManager.IMPORTANCE_HIGH
            else -> NotificationManager.IMPORTANCE_DEFAULT
        }
        notificationManager.createNotificationChannel(NotificationChannel(channelId, channelName, importance))

        // 딥링크 인텐트 생성
        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            createDeepLinkIntent(data),
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
        )

        // 알림 빌드
        val priority = when (channelId) {
            PeekrNotificationChannel.HIGH_ID -> NotificationCompat.PRIORITY_HIGH
            else -> NotificationCompat.PRIORITY_DEFAULT
        }
        val notificationBuilder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(title)
            .setContentText(body)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .setPriority(priority)

        // 알림 수행
        notificationManager.notify(
            System.currentTimeMillis().toInt(),
            notificationBuilder.build(),
        )

        // TODO: 초기 버전에선 알림에 이미지가 포함되지 않음.
        //  추후 이미지 포함 시 백그라운드 지원 방식(data-only message)과 함께 구현 필요.
        // 프로필 사진 여부에 따라 largeIcon 설정 후 알림 표시
//        if (imageUrl != null) {
//            applicationScope.launch {
//                val bitmap = loadBitmapFromUrl(imageUrl)
//                if (bitmap != null) {
//                    notificationBuilder.setLargeIcon(bitmap)
//                }
//                notificationManager.notify(
//                    System.currentTimeMillis().toInt(),
//                    notificationBuilder.build(),
//                )
//            }
//        } else {
//            notificationManager.notify(
//                System.currentTimeMillis().toInt(),
//                notificationBuilder.build(),
//            )
//        }
    }

    // noti_type, ref_type, ref_id 기반으로 딥링크 인텐트 생성
    private fun createDeepLinkIntent(data: Map<String, String>): Intent {
        val notiType = data[FcmDataKey.NOTI_TYPE]
            ?.let { runCatching { NotificationType.valueOf(it) }.getOrNull() }
        val refId = data[FcmDataKey.REF_ID]?.toLongOrNull()

        return when {
            notiType == NotificationType.FRIEND_REQUEST ||
                notiType == NotificationType.FRIEND_ACCEPT -> {
                // 프로필 화면으로 이동
                Intent(this, MainActivity::class.java).apply {
                    putExtra("screen", "profile")
                    putExtra("userId", refId)
                }
            }

            notiType == NotificationType.NEW_KEYWORD -> {
                // 키워드 상세 화면으로 이동
                Intent(this, MainActivity::class.java).apply {
                    putExtra("screen", "keyword_detail")
                    putExtra("postId", refId)
                }
            }

            notiType?.isBroadcast == true -> {
                // 알림 목록 화면으로 이동
                Intent(this, MainActivity::class.java).apply {
                    putExtra("screen", "notifications")
                }
            }

            else -> {
                // 알 수 없는 타입 → 홈 화면으로 이동
                Intent(this, MainActivity::class.java)
            }
        }
    }

    // URL로 비트맵 생성해서 반환
    private suspend fun loadBitmapFromUrl(url: String): Bitmap? =
        withContext(ioDispatcher) {
            runCatching {
                val request = ImageRequest.Builder(applicationContext)
                    .data(url)
                    .allowHardware(false)
                    .build()
                (Coil.imageLoader(applicationContext).execute(request).drawable as? BitmapDrawable)?.bitmap
            }.getOrNull()
        }

    // FCM 토큰 등록
    private suspend fun registerTokenToServer(token: String) {
        when (val result = notificationRepository.registerFcmToken(token)) {
            is Result.Loading -> Unit
            is Result.Success -> AppLogger.d(tag, "FCM 토큰 등록 성공")
            is Result.Error -> AppLogger.e(tag, "FCM 토큰 등록 실패: ${result.message}")
        }
    }
}
