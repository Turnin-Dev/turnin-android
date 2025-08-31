package com.peekr.presentation.shared

import com.peekr.presentation.login.model.UiSocialLoginProvider
import kotlinx.serialization.Serializable

// ------------------------------ Sub Graph (중첩 네비게이션 라우트) ------------------------------

/** 중첩 네비게이션을 필요로 할 때 여기서 선언해 사용한다. */
sealed interface SubGraph {
    /** 로그인 네비게이션 라우트 ([LoginGraph]) */
    @Serializable
    data object Login : SubGraph

    /** 회원가입 네비게이션 라우트 ([RegisterGraph]) */
    @Serializable
    data class Register(
        val provider: UiSocialLoginProvider,
        val providerId: String,
    ) : SubGraph

    /** 바텀 네비게이션 진입점 라우트 */
    data object BottomNav : SubGraph

    /** 바텀 네비게이션(홈) 라우트 ([HomeGraph]) */
    @Serializable
    data object Home : SubGraph

    /** 바텀 네비게이션(탐색) 라우트 ([DiscoverGraph]) */
    @Serializable
    data object Discover : SubGraph

    /** 바텀 네비게이션(프로필) 라우트 ([ProfileGraph]) */
    @Serializable
    data object Profile : SubGraph
}

// ------------------------------ Graph (중첩 네비게이션 내부) ------------------------------

/** 로그인 네비게이션 */
sealed interface LoginGraph {
    @Serializable
    data object Main : LoginGraph
}

/** 회원가입 네비게이션 */
sealed interface RegisterGraph {
    @Serializable
    data object DisplayId : RegisterGraph

    @Serializable
    data object Name : RegisterGraph

    @Serializable
    data object Profile : RegisterGraph

    @Serializable
    data object CropProfileImage : RegisterGraph
}

/** 바텀 네비게이션(홈) */
sealed interface HomeGraph {
    @Serializable
    data object Main : HomeGraph
}

/** 바텀 네비게이션(탐색) */
sealed interface DiscoverGraph {
    @Serializable
    data object Main : DiscoverGraph
}

/** 탐색 네비게이션(프로필) */
sealed interface ProfileGraph {
    @Serializable
    data object Main : ProfileGraph
}

// ------------------------------ Screens (별도 화면) ------------------------------

/** 별도의 화면을 정의할 때 여기서 선언해 사용한다. */
sealed interface Screens {
    @Serializable
    data object TempMain : Screens
}
