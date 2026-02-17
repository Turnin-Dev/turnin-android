package com.peekr.core.presentation.common.navigation

import com.peekr.core.presentation.ui.model.UiSocialLoginProvider
import kotlinx.serialization.Serializable

/** 모든 중첩 그래프 */
sealed interface SubGraph {
    /** 바텀 네비게이션 */
    sealed interface BottomNav : SubGraph {
        /** 진입점 */
        @Serializable
        data object Root : BottomNav

        /** 홈 탭 */
        @Serializable
        data object Home : BottomNav

        /** 탐색 탭 */
        @Serializable
        data object Discover : BottomNav

        /** 내 프로필 탭 */
        @Serializable
        data object Profile : BottomNav
    }

    /** 로그인 그래프 */
    sealed interface Login : SubGraph {
        @Serializable
        data object Root : Login

        @Serializable
        data object Main : Login
    }

    /** 회원가입 그래프 */
    sealed interface Register : SubGraph {
        @Serializable
        data class Root(
            val provider: UiSocialLoginProvider,
            val providerId: String,
        ) : SubGraph

        @Serializable
        data object DisplayId : Register

        @Serializable
        data object Name : Register

        @Serializable
        data object Profile : Register

        @Serializable
        data object CropProfileImage : Register
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
        ) : Report

        /** 신고/차단 선택 */
        @Serializable
        data object SelectReportBlock : Report

        /** 신고 사유 선택 */
        @Serializable
        data object SelectReportReason : Report

        /** 신고 사유 입력 */
        @Serializable
        data object InputReportReason : Report

        /** 신고 결과 */
        @Serializable
        data object ReportResult : Report
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
        ) : BlockModal

        /** 차단 사유 선택 */
        @Serializable
        data object SelectBlockModalReason : BlockModal

        /** 차단 사유 입력 */
        @Serializable
        data object InputBlockModalReason : BlockModal

        /** 차단 결과 */
        @Serializable
        data object BlockModalResult : BlockModal
    }
}

// ------------------------------ Screens (별도 화면 or 딥링크 지원 화면) ------------------------------

/** 별도의 화면을 정의할 때 여기서 선언해 사용한다. */
sealed interface Screens {
    @Serializable
    data object TempMain : Screens

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
    ) : Screens

    /**
     * 키워드 수정 화면
     *
     * @property userKeywordId 사용자 키워드 ID
     */
    @Serializable
    data class KeywordEdit(
        val userKeywordId: Long?,
    ) : Screens

    /**
     * 친구 목록 화면
     *
     * @property userId 사용자 ID
     */
    @Serializable
    data class FriendList(
        val userId: Long,
    ) : Screens

    /**
     * 사용자 프로필 화면
     *
     * @property userId 사용자 ID
     */
    @Serializable
    data class UserProfile(
        val userId: Long,
    ) : Screens

    /**
     * 나의 프로필 화면 (Screen 버전)
     */
    @Serializable
    data object MyProfile : Screens

    /**
     * 차단 목록 화면
     */
    @Serializable
    data object BlockList : Screens
}
