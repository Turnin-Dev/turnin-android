package com.peekr.presentation.shared

import kotlinx.serialization.Serializable

sealed interface Screens {
    @Serializable
    data object Login : Screens

    @Serializable
    data object TempMain : Screens
}
