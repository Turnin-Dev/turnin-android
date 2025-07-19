package com.peekr.domain.shared.dataStore

/** DataStore 키 값 집합 */
sealed class DataStoreKey(val name: String) {
    object AccessToken : DataStoreKey("access_token")

    object RefreshToken : DataStoreKey("refresh_token")
}
