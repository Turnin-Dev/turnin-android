package com.turnin.presentation.setting.state

import com.turnin.core.presentation.common.viewmodel.BaseUiEffect
import com.turnin.core.presentation.common.viewmodel.BaseUiEvent
import com.turnin.core.presentation.common.viewmodel.BaseUiState
import com.turnin.core.presentation.ui.util.UiText
import com.turnin.domain.setting.model.SettingProfileImagePatch
import com.turnin.presentation.setting.model.UiEditableAccountInfo

/**
 * 설정 - 계정 정보 UI 계약
 */
class AccountInfoContract {
    /**
     * 계정 정보 UI 상태
     *
     * @property accountInfo 수정용 계정 정보
     * @property profileImagePatch 프로필 사진 패치 모델
     * @property isAccountInfoEdited 계정 정보 수정 여부
     * @property fullScreenLoading 전체 화면 로딩 여부
     * @property error 에러
     */
    data class UiState(
        val accountInfo: UiEditableAccountInfo? = null,
        val profileImagePatch: SettingProfileImagePatch = SettingProfileImagePatch.Unchanged,
        val isAccountInfoEdited: Boolean = false,
        val fullScreenLoading: Boolean = false,
        val error: UiText? = null,
    ) : BaseUiState

    sealed interface UiEvent : BaseUiEvent {
        /** 계정 정보 저장 이벤트  */
        data object OnSaveAccountInfo : UiEvent

        /**
         * 프로필 사진 변경 이벤트
         *
         * @property imageBytes 변경할 프로필 사진의 [ByteArray]
         */
        class OnProfileImageUpdated(
            imageBytes: ByteArray?,
        ) : UiEvent {
            val imageBytes: ByteArray? = imageBytes?.copyOf()
        }

        /** 프로필 사진 삭제 이벤트 */
        data object OnProfileImageDeleted : UiEvent

        /** 안전하게 뒤로가기 이벤트 */
        data object SafeBackPressed : UiEvent

        // 텍스트 필드 변경 이벤트
        data class OnDisplayIdChanged(val displayId: String) : UiEvent

        data class OnNameChanged(val name: String) : UiEvent

        data class OnIntroduceChanged(val introduce: String) : UiEvent
    }

    sealed interface UiEffect : BaseUiEffect {
        /** 화면 닫기 일회성 이벤트 */
        data object CloseScreen : UiEffect

        /** 취소 경고 모달 열기 */
        data object OpenSafeCancelModal : UiEffect
    }
}
