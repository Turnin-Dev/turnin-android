package com.peekr.core.data.source.local.datastore

/** DataStore 키 값 집합 */
sealed class DataStoreKey(val name: String) {
    /** 인증 관련 키 값 집합 */
    object Auth {
        /**
         * `Key`: 액세스 토큰
         *
         * `Value`: String 타입의 값
         */
        object AccessToken : DataStoreKey("access_token")

        /**
         * `Key`: 리프레쉬 토큰
         *
         * `Value`: String 타입의 값
         */
        object RefreshToken : DataStoreKey("refresh_token")
    }

    /** 사용자 관련 키 값 집합 */
    object User {
        /**
         * `Key`: 사용자 ID
         *
         * `Value`: Long 타입의 값
         */
        object UserId : DataStoreKey("user_id")
    }
}
