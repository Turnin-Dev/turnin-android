package com.turnin.core.presentation.common.navigation

import com.turnin.core.presentation.ui.model.UiSocialLoginProvider
import kotlinx.serialization.Serializable

sealed interface Route {
    val analyticsName: String
}

/** 모든 중첩 그래프 */
sealed interface SubGraph : Route {
    /** 바텀 네비게이션 */
    sealed interface BottomNav : SubGraph {
        /** 진입점 */
        @Serializable
        data object Root : BottomNav {
            override val analyticsName: String = "bottom_nav_root"
        }

        /** 홈 탭 */
        @Serializable
        data object Home : BottomNav {
            override val analyticsName: String = "bottom_nav_home"
        }

        /** 탐색 탭 */
        @Serializable
        data object Discover : BottomNav {
            override val analyticsName: String = "bottom_nav_discover"
        }

        /** 내 프로필 탭 */
        @Serializable
        data object Profile : BottomNav {
            override val analyticsName: String = "bottom_nav_profile"
        }
    }

    /** 로그인 그래프 */
    sealed interface Login : SubGraph {
        @Serializable
        data object Root : Login {
            override val analyticsName: String = "login_root"
        }

        @Serializable
        data object Main : Login {
            override val analyticsName: String = "login_main"
        }
    }

    /** 회원가입 그래프 */
    sealed interface Register : SubGraph {
        @Serializable
        data class Root(
            val provider: UiSocialLoginProvider,
            val providerId: String,
        ) : SubGraph {
            override val analyticsName: String = "register_root"
        }

        @Serializable
        data object TermsAgreement : Register {
            override val analyticsName: String = "register_terms_agreement"
        }

        @Serializable
        data object DisplayId : Register {
            override val analyticsName: String = "register_display_id"
        }

        @Serializable
        data object Name : Register {
            override val analyticsName: String = "register_name"
        }

        @Serializable
        data object Profile : Register {
            override val analyticsName: String = "register_profile"
        }

        @Serializable
        data object CropProfileImage : Register {
            override val analyticsName: String = "register_crop_profile_image"
        }
    }

    /**
     * 신고 그래프
     *
     * 기본적으로 신고 그래프는 차단 그래프까지 이어진다.
     *
     * [Root.onlyReport] 인자에 따라 차단 그래프까지 수행할 수 있는 지에 대한 여부를 선택할 수 있다.
     */
    sealed interface Report : SubGraph {
        /**
         * 신고 그래프 진입점
         *
         * @property userId 사용자 ID
         * @property userKeywordId 사용자 키워드 ID
         * @property onlyReport 신고만 수행할 지에 대한 여부 (기본적으로 신고 기능은 차단 기능까지 이어진다.)
         */
        @Serializable
        data class Root(
            val userId: Long?,
            val userKeywordId: Long?,
            val onlyReport: Boolean,
        ) : Report {
            override val analyticsName: String = "report_root"
        }

        /** 신고/차단 선택 */
        @Serializable
        data object SelectReportBlock : Report {
            override val analyticsName: String = "report_select_report_block"
        }

        /** 신고 사유 선택 */
        @Serializable
        data object SelectReportReason : Report {
            override val analyticsName: String = "report_select_reason"
        }

        /** 신고 사유 입력 */
        @Serializable
        data object InputReportReason : Report {
            override val analyticsName: String = "report_input_reason"
        }

        /** 신고 결과 */
        @Serializable
        data object ReportResult : Report {
            override val analyticsName: String = "report_result"
        }
    }

    /** 차단 모달 그래프 */
    sealed interface BlockModal : SubGraph {
        /**
         * 차단 모달 그래프 진입점
         *
         * @param userId 차단할 사용자 ID
         */
        @Serializable
        data class Root(
            val userId: Long?,
        ) : BlockModal {
            override val analyticsName: String = "block_root"
        }

        /** 차단 사유 선택 */
        @Serializable
        data object SelectBlockModalReason : BlockModal {
            override val analyticsName: String = "block_select_reason"
        }

        /** 차단 사유 입력 */
        @Serializable
        data object InputBlockModalReason : BlockModal {
            override val analyticsName: String = "block_input_reason"
        }

        /** 차단 결과 */
        @Serializable
        data object BlockModalResult : BlockModal {
            override val analyticsName: String = "block_result"
        }
    }

    /** 설정 화면 그래프 */
    sealed interface Setting : SubGraph {
        /** 설정 화면 그래프 진입점 */
        @Serializable
        data object Root : Setting {
            override val analyticsName: String = "setting_root"
        }

        /** 설정 메인 화면 */
        @Serializable
        data object Main : Setting {
            override val analyticsName: String = "setting_main"
        }

        /** 계정 정보 화면 */
        @Serializable
        data class AccountInfo(
            val displayId: String?,
            val name: String?,
            val introduce: String?,
            val profileImageUrl: String?,
        ) : Setting {
            override val analyticsName: String = "setting_account_info"
        }

        /**
         * 프로필 사진 편집 화면
         *
         * @property uri 사진 URI
         */
        @Serializable
        data class CropProfileImage(
            val uri: String,
        ) : Setting {
            override val analyticsName: String = "setting_crop_profile_image"
        }

        /** 버전 정보 화면 */
        @Serializable
        data object VersionInfo : Setting {
            override val analyticsName: String = "setting_version_info"
        }

        /** 문의 화면 */
        @Serializable
        data class Qna(
            val qnaUrl: String,
        ) : Setting {
            override val analyticsName: String = "setting_qna"
        }

        /** 알림 설정 화면 */
        @Serializable
        data object NotificationSetting : Setting {
            override val analyticsName: String = "setting_notification"
        }
    }
}

// ------------------------------ Screens (별도 화면 or 딥링크 지원 화면) ------------------------------

/** 별도의 화면을 정의할 때 여기서 선언해 사용한다. */
sealed interface Screens : Route {
    /**
     * 키워드 상세 화면
     *
     * @property userKeywordId 사용자 키워드 ID
     * @property userId 사용자 ID
     */
    @Serializable
    data class KeywordDetail(
        val userKeywordId: Long,
        val userId: Long,
    ) : Screens {
        override val analyticsName: String = "keyword_detail"
    }

    /**
     * 키워드 수정 화면
     *
     * @property userKeywordId 사용자 키워드 ID
     */
    @Serializable
    data class KeywordEdit(
        val userKeywordId: Long?,
    ) : Screens {
        override val analyticsName: String = "keyword_edit"
    }

    /**
     * 친구 목록 화면
     *
     * @property userId 사용자 ID
     */
    @Serializable
    data class FriendList(
        val userId: Long,
    ) : Screens {
        override val analyticsName: String = "friend_list"
    }

    /**
     * 사용자 프로필 화면
     *
     * @property userId 사용자 ID
     * @property userName 사용자 명
     * @property displayId 사용자 표시 ID
     * @property profileImageUrl 프로필 사진 URL
     * @property blockId 차단 ID
     * @property forceRefresh 강제 새로고침 여부
     */
    @Serializable
    data class UserProfile(
        val userId: Long,
        val userName: String?,
        val displayId: String?,
        val profileImageUrl: String?,
        val blockId: Long?,
        val forceRefresh: Boolean = false,
    ) : Screens {
        override val analyticsName: String = "user_profile"
    }

    /**
     * 나의 프로필 화면 (Screen 버전)
     */
    @Serializable
    data object MyProfile : Screens {
        override val analyticsName: String = "my_profile"
    }

    /**
     * 차단 목록 화면
     */
    @Serializable
    data object BlockList : Screens {
        override val analyticsName: String = "block_list"
    }

    /**
     * 알림 목록 화면
     */
    @Serializable
    data object Notifications : Screens {
        override val analyticsName: String = "notification_list"
    }

    @Serializable
    data object TermsOfService : Screens {
        override val analyticsName: String = "terms_of_service"
    }

    @Serializable
    data object PrivacyPolicy : Screens {
        override val analyticsName: String = "privacy_policy"
    }
}
