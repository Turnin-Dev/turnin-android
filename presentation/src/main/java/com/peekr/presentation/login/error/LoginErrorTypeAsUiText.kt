package com.peekr.presentation.login.error

import com.peekr.core.presentation.R
import com.peekr.core.presentation.common.error.asUiText
import com.peekr.core.presentation.ui.util.UiText
import com.peekr.core.presentation.ui.util.UiText.StringResource
import com.peekr.domain.login.error.LoginErrorType

fun LoginErrorType.asUiText(): UiText = when (this) {
    LoginErrorType.IdTokenParsing -> StringResource(R.string.auth_error_id_token_parsing)
    LoginErrorType.Cancellation -> StringResource(R.string.auth_error_cancellation)
    LoginErrorType.TokenTypeInvalid -> StringResource(R.string.auth_error_token_type_invalid)
    LoginErrorType.UserNotFound -> StringResource(R.string.auth_error_user_not_found)
    LoginErrorType.DeleteAccountFailed -> StringResource(R.string.auth_error_delete_account_failed)
    LoginErrorType.KakaoSignInError -> StringResource(R.string.auth_error_kakao_sign_in_error)
    LoginErrorType.KakaoSignOutError -> StringResource(R.string.auth_error_kakao_sign_out_error)
    LoginErrorType.KakaoDeleteAccountError -> StringResource(R.string.auth_error_kakao_delete_account_error)
    LoginErrorType.SaveTokenFailed -> StringResource(R.string.auth_error_save_token_failed)
    LoginErrorType.LoginFailed -> StringResource(R.string.auth_error_login_failed)
    is LoginErrorType.Unexpected -> StringResource(R.string.auth_error_unexpected)
    is LoginErrorType.CommonError -> this.error.asUiText()
}
