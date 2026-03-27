package com.peekr.core.data.source.local.datastore

/** DataStore 키 값 집합 */
sealed class DataStoreKey(val name: String) {
    /** 인증 관련 키 값 집합 */
    data object Auth {
        /**
         * `Key`: 액세스 토큰
         *
         * `Value`: String 타입의 값
         */
        data object AccessToken : DataStoreKey("access_token")

        /**
         * `Key`: 리프레쉬 토큰
         *
         * `Value`: String 타입의 값
         */
        data object RefreshToken : DataStoreKey("refresh_token")
    }

    /** 사용자 관련 키 값 집합 */
    data object User {
        /**
         * `Key`: 사용자 ID
         *
         * `Value`: Long 타입의 값
         */
        data object UserId : DataStoreKey("user_id")

        /**
         * `Key`: 로그인 플랫폼
         *
         * `Value`: String 타입의 값
         */
        data object LoginProvider : DataStoreKey("login_provider")
    }

    data object Setting {
        /**
         * `Key`: 푸시 알림 활성화 여부
         *
         * `Value`: Boolean 타입의 값
         */
        data object PushNotification : DataStoreKey("push_notification")

        /**
         * `Key`: 테마 모드
         *
         * `Value`: String 타입의 값
         */
        data object ThemeMode : DataStoreKey("theme_mode")

        /**
         * `Key`: 알림 동기화 상태
         *
         * `Value`: String 타입의 값
         */
        data object NotificationSyncState : DataStoreKey("notification_sync_state")
    }
}
