package com.peekr.presentation.common.util.error

import com.peekr.domain.common.util.CommonValidationError
import com.peekr.presentation.R
import com.peekr.presentation.common.util.UiText
import com.peekr.presentation.common.util.UiText.StringResource

fun CommonValidationError.asUiText(): UiText =
    when (this) {
        CommonValidationError.EMPTY_OR_BLANK -> {
            StringResource(R.string.register_screen_validation_empty_or_blank)
        }

        CommonValidationError.EXCEEDS_MAX_LENGTH_30 -> {
            StringResource(R.string.register_screen_validation_exceeds_max_length_30)
        }

        CommonValidationError.EXCEEDS_MAX_LENGTH_200 -> {
            StringResource(R.string.register_screen_validation_exceeds_max_length_200)
        }

        CommonValidationError.ONLY_ALPHANUMERIC_UNDERSCORE -> {
            StringResource(R.string.register_screen_validation_only_al_nu_und)
        }

        CommonValidationError.ONLY_ALPHANUMERIC_HANGUL -> {
            StringResource(R.string.register_screen_validation_only_al_nu_han)
        }
    }
