package com.turnin.domain.setting.model

import com.turnin.core.domain.model.DisplayId
import com.turnin.core.domain.model.Introduce
import com.turnin.core.domain.model.Name
import com.turnin.core.domain.model.SocialLoginProvider
import com.turnin.core.domain.model.UserId

/**
 * 계정 정보
 *
 * @property userId 사용자 ID
 * @property displayId 사용자 표시 ID
 * @property name 사용자 명
 * @property profileImageUrl 프로필 사진 URL
 * @property introduce 소개글
 * @property loginProvider 로그인 타입
 */
data class AccountInfo(
    val userId: UserId,
    val displayId: DisplayId,
    val name: Name,
    val profileImageUrl: String?,
    val introduce: Introduce,
    val loginProvider: SocialLoginProvider,
)
