package com.peekr.presentation.discover.model

/**
 * 히스토리 사용자 UI 모델
 *
 * 탐색 화면에 있는 히스토리 바에서 사용되고 사용자 정보 일부가 담겨있다.
 *
 * @property userId 사용자 ID
 * @property userName 사용자 명
 * @property profileImageUrl 프로필 사진 URL
 */
data class UiHistoryUser(
    val userId: Long,
    val userName: String,
    val profileImageUrl: String?,
) {
    companion object {
        val samples = List(10) {
            UiHistoryUser(it + 1L, "username$it", null)
        }
    }
}

fun UiDiscoverContext.extractHistoryUser(): UiHistoryUser =
    UiHistoryUser(
        userId = userId,
        userName = userName,
        profileImageUrl = profileImageUrl,
    )
