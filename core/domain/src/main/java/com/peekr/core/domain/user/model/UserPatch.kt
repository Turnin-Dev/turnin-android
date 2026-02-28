package com.peekr.core.domain.user.model

import com.peekr.core.domain.model.DisplayId
import com.peekr.core.domain.model.Introduce
import com.peekr.core.domain.model.Name

/** 프로필 사진 패치 모델 */
sealed interface ProfileImagePatch {
    /** 변경하지 않은 경우 */
    data object Unchanged : ProfileImagePatch

    /** 삭제하는 경우 */
    data object Remove : ProfileImagePatch

    /** 업데이트한 경우 */
    data class Update(val url: String) : ProfileImagePatch
}

/**
 * 사용자 수정 요청
 *
 * @property name 사용자 이름
 * @property displayId 사용자 표시 ID
 * @property oldProfileImageUrl 기존 사용자 프로필 사진 url
 * @property profileImagePatch 프로필 사진 패치
 * @property introduce 사용자 소개 글
 */
data class UserPatch(
    val name: Name,
    val displayId: DisplayId,
    val oldProfileImageUrl: String?,
    val profileImagePatch: ProfileImagePatch,
    val introduce: Introduce,
)
