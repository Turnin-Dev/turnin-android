package com.peekr.presentation.register.state

import com.peekr.presentation.shared.util.UiText

data class RegisterState(
    val displayId: String = "",
    val name: String = "",
    val profileImageUrl: String = "",
    val loading: Boolean = false,
    val error: UiText? = null,
)
