package com.peekr.presentation.shared.util.error

import com.peekr.domain.account.validation.RegisterValidationError
import com.peekr.presentation.R
import com.peekr.presentation.shared.util.UiText
import com.peekr.presentation.shared.util.UiText.StringResource

fun RegisterValidationError.asUiText(): UiText = when (this) {
    RegisterValidationError.EMPTY_OR_BLACK -> {
        StringResource(R.string.register_screen_validation_empty_or_black)
    }

    RegisterValidationError.EXCEEDS_MAX_LENGTH -> {
        StringResource(R.string.register_screen_validation_exceeds_max_length)
    }

    RegisterValidationError.ONLY_ALPHANUMERIC_UNDERSCORE -> {
        StringResource(R.string.register_screen_validation_only_al_nu_und)
    }

    RegisterValidationError.ONLY_ALPHANUMERIC_HANGUL -> {
        StringResource(R.string.register_screen_validation_only_al_nu_han)
    }
}
