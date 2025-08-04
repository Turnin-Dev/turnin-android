package com.peekr.presentation.shared

import kotlinx.serialization.Serializable

/** 중첩 네비게이션을 필요로 할 때 여기서 선언해 사용한다. */
sealed interface SubGraph {
    @Serializable
    data object Login : SubGraph

    @Serializable
    data object Register : SubGraph
}

sealed interface LoginGraph {
    @Serializable
    data object Default : LoginGraph
}

sealed interface RegisterGraph {
    @Serializable
    data object Name : RegisterGraph
}

/** 별도의 화면을 정의할 때 여기서 선언해 사용한다. */
sealed interface Screens {
    @Serializable
    data object TempMain
}
