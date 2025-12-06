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

        /** 프로필 탭 */
        sealed interface Profile : BottomNav {
            /** 진입점 */
            @Serializable
            data object Root : Profile

            /** 나의 프로필 */
            @Serializable
            data object Me : Profile

            /** 사용자 프로필 */
            @Serializable
            data class User(
                val userId: Long,
            ) : Profile
        }
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

    sealed interface Report : SubGraph {
        @Serializable
        data class Root(
            val userId: Long,
        ) : Report

        @Serializable
        data object SelectReportBlock : Report

        @Serializable
        data object SelectReportReason : Report

        @Serializable
        data object InputReportReason : Report

        @Serializable
        data object ReportResult : Report
    }
}

// ------------------------------ Screens (별도 화면 or 딥링크 지원 화면) ------------------------------

/** 별도의 화면을 정의할 때 여기서 선언해 사용한다. */
sealed interface Screens {
    @Serializable
    data object TempMain : Screens

    @Serializable
    data class KeywordDetail(
        val userKeywordId: Long,
        val userId: Long,
        val keyword: String,
    ) : Screens
}
