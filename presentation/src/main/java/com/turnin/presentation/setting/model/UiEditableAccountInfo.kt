package com.turnin.presentation.setting.model

/**
 * 수정용 계정 정보
 *
 * @property displayId 사용자 표시 ID
 * @property name 사용자 명
 * @property introduce 소개글
 * @property profileImageUrl 프로필 사진 URL
 */
data class UiEditableAccountInfo(
    val displayId: String = "",
    val name: String = "",
    val introduce: String = "",
    val profileImageUrl: String? = null,
) {
    companion object {
        val sample = UiEditableAccountInfo(
            displayId = "DisplayID",
            name = "Name",
            introduce = "Introduce, Introduce, Introduce",
            profileImageUrl = null,
        )
    }
}
