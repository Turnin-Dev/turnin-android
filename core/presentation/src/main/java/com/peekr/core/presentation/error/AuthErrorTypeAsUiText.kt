package com.peekr.core.presentation.error

import com.peekr.core.domain.auth.error.AuthErrorType
import com.peekr.core.presentation.R
import com.peekr.core.presentation.util.UiText
import com.peekr.core.presentation.util.UiText.StringResource

fun AuthErrorType.asUiText(): UiText = when (this) {
    AuthErrorType.IdTokenParsing -> StringResource(R.string.auth_error_id_token_parsing)
    AuthErrorType.Cancellation -> StringResource(R.string.auth_error_cancellation)
    AuthErrorType.TokenTypeInvalid -> StringResource(R.string.auth_error_token_type_invalid)
    AuthErrorType.UserNotFound -> StringResource(R.string.auth_error_user_not_found)
    AuthErrorType.DeleteAccountFailed -> StringResource(R.string.auth_error_delete_account_failed)
    AuthErrorType.KakaoSignInError -> StringResource(R.string.auth_error_kakao_sign_in_error)
    AuthErrorType.KakaoSignOutError -> StringResource(R.string.auth_error_kakao_sign_out_error)
    AuthErrorType.KakaoDeleteAccountError -> StringResource(R.string.auth_error_kakao_delete_account_error)
    AuthErrorType.SaveTokenFailed -> StringResource(R.string.auth_error_save_token_failed)
    AuthErrorType.LoginFailed -> StringResource(R.string.auth_error_login_failed)
    is AuthErrorType.Unexpected -> StringResource(R.string.auth_error_unexpected)
    is AuthErrorType.CommonError -> this.error.asUiText()
}
