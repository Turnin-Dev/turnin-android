package com.peekr.core.presentation.error

import com.peekr.core.domain.util.ErrorType
import com.peekr.core.presentation.R
import com.peekr.core.presentation.util.UiText

fun ErrorType.asUiText(): UiText = when (this) {
    // ------------------------------ Auth ------------------------------
    ErrorType.Auth.IdTokenParsing -> UiText.StringResource(R.string.error_auth_id_token_parsing)
    ErrorType.Auth.Cancellation -> UiText.StringResource(R.string.error_auth_cancellation)
    ErrorType.Auth.TokenTypeInvalid -> UiText.StringResource(R.string.error_auth_token_type_invalid)
    ErrorType.Auth.UserNotFound -> UiText.StringResource(R.string.error_auth_user_not_found)
    ErrorType.Auth.DeleteAccountFailed -> UiText.StringResource(R.string.error_auth_delete_account_failed)
    ErrorType.Auth.KakaoSignInError -> UiText.StringResource(R.string.error_auth_kakao_sign_in_error)
    ErrorType.Auth.KakaoSignOutError -> UiText.StringResource(R.string.error_auth_kakao_sign_out_error)
    ErrorType.Auth.KakaoDeleteAccountError -> UiText.StringResource(R.string.error_auth_kakao_delete_account_error)
    ErrorType.Auth.SaveTokenFailed -> UiText.StringResource(R.string.error_auth_save_token_failed)
    ErrorType.Auth.LoginFailed -> UiText.StringResource(R.string.error_auth_login_failed)
    // ------------------------------ Exception ------------------------------
    ErrorType.Exception.Json -> UiText.StringResource(R.string.error_exception_json)
    ErrorType.Exception.TimeOut -> UiText.StringResource(R.string.error_exception_timeout)
    ErrorType.Exception.IO -> UiText.StringResource(R.string.error_exception_io)
    // ------------------------------ Network ------------------------------
    ErrorType.Network.Unauthorized -> UiText.StringResource(R.string.error_network_unauthorized)
    ErrorType.Network.ClientError -> UiText.StringResource(R.string.error_network_client)
    ErrorType.Network.ServerError -> UiText.StringResource(R.string.error_network_server)
    ErrorType.Network.ConnectionFailed -> UiText.StringResource(R.string.error_network_connection_failed)
    ErrorType.Network.InvalidFileType -> UiText.StringResource(R.string.error_network_invalid_file_type)
    // ------------------------------ Unexpected ------------------------------
    is ErrorType.Unexpected -> {
        this.cause?.message?.let { message ->
            UiText.DynamicString(message)
        } ?: UiText.StringResource(R.string.error_unexpected)
    }
}
