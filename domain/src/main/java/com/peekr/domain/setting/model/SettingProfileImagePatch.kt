package com.peekr.domain.setting.model

import com.peekr.core.domain.file.model.ImageFileDetail

/** 설정 기능용 프로필 사진 패치 모델 */
sealed interface SettingProfileImagePatch {
    /** 변경하지 않은 경우 */
    data object Unchanged : SettingProfileImagePatch

    /** 삭제하는 경우 */
    data object Remove : SettingProfileImagePatch

    /** 업데이트한 경우 */
    data class Update(val imageFileDetail: ImageFileDetail) : SettingProfileImagePatch
}
