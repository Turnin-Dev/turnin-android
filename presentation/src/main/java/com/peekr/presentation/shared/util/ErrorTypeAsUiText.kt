package com.peekr.presentation.shared.util

import com.peekr.domain.shared.util.ErrorType
import com.peekr.presentation.R
import com.peekr.presentation.shared.util.UiText.DynamicString
import com.peekr.presentation.shared.util.UiText.StringResource

fun ErrorType.asUiText(): UiText = when (this) {
    // ------------------------------ Auth ------------------------------
    ErrorType.Auth.IdTokenParsing -> StringResource(R.string.error_auth_id_token_parsing)
    ErrorType.Auth.Cancellation -> StringResource(R.string.error_auth_cancellation)
    ErrorType.Auth.TokenTypeInvalid -> StringResource(R.string.error_auth_token_type_invalid)
    ErrorType.Auth.UserNotFound -> StringResource(R.string.error_auth_user_not_found)
    ErrorType.Auth.DeleteAccountFailed -> StringResource(R.string.error_auth_delete_account_failed)
    ErrorType.Auth.KakaoSignInError -> StringResource(R.string.error_auth_kakao_sign_in_error)
    ErrorType.Auth.KakaoSignOutError -> StringResource(R.string.error_auth_kakao_sign_out_error)
    ErrorType.Auth.KakaoDeleteAccountError -> StringResource(R.string.error_auth_kakao_delete_account_error)
    ErrorType.Auth.SaveTokenFailed -> StringResource(R.string.error_auth_save_token_failed)
    ErrorType.Auth.LoginFailed -> StringResource(R.string.error_auth_login_failed)
    // ------------------------------ Exception ------------------------------
    ErrorType.Exception.Json -> StringResource(R.string.error_exception_json)
    ErrorType.Exception.TimeOut -> StringResource(R.string.error_exception_timeout)
    ErrorType.Exception.IO -> StringResource(R.string.error_exception_io)
    // ------------------------------ Network ------------------------------
    ErrorType.Network.Unauthorized -> StringResource(R.string.error_network_unauthorized)
    ErrorType.Network.ClientError -> StringResource(R.string.error_network_client)
    ErrorType.Network.ServerError -> StringResource(R.string.error_network_server)
    // ------------------------------ Unexpected ------------------------------
    is ErrorType.Unexpected -> {
        this.cause?.message?.let { message ->
            DynamicString(message)
        } ?: StringResource(R.string.error_unexpected)
    }
}
