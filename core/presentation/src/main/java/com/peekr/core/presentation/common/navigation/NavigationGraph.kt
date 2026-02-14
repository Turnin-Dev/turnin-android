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

    /** 신고 그래프 */
    sealed interface Report : SubGraph {
        /** 신고 그래프 진입점 */
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

    /** 차단 그래프 */
    sealed interface Block : SubGraph {
        /** 차단 그래프 진입점 */
        @Serializable
        data class Root(
            val userId: Long?,
            val userKeywordId: Long?,
        ) : Block

        /** 차단 사유 선택 */
        @Serializable
        data object SelectBlockReason : Block

        /** 차단 사유 입력 */
        @Serializable
        data object InputBlockReason : Block

        /** 차단 결과 */
        @Serializable
        data object BlockResult : Block
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
     * 사용자 프로필
     *
     * @property userId 사용자 ID
     */
    @Serializable
    data class UserProfile(
        val userId: Long,
    ) : Screens

    @Serializable
    data object MyProfile : Screens
}
