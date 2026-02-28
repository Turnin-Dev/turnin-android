package com.peekr.core.presentation.common.error

import com.peekr.core.domain.common.error.CommonErrorType
import com.peekr.core.presentation.R
import com.peekr.core.presentation.ui.util.UiText
import com.peekr.core.presentation.ui.util.UiText.StringResource

fun CommonErrorType.asUiText(): UiText = when (this) {
    // ------------------------------ Exception ------------------------------
    CommonErrorType.Exception.Json -> StringResource(R.string.common_error_exception_json)
    CommonErrorType.Exception.TimeOut -> StringResource(R.string.common_error_exception_timeout)
    CommonErrorType.Exception.IO -> StringResource(R.string.common_error_exception_io)
    // ------------------------------ Unexpected ------------------------------
    is CommonErrorType.Unexpected -> StringResource(R.string.common_error_unexpected)
    // ------------------------------ Local ------------------------------
    CommonErrorType.Local.WritingDataFailed -> StringResource(R.string.common_error_local_writing_data_failed)
    CommonErrorType.Local.Empty -> StringResource(R.string.common_error_local_empty)
    CommonErrorType.Local.UserIdNotFound -> StringResource(R.string.common_error_local_user_id_not_found)
    // ------------------------------ Network ------------------------------
    CommonErrorType.Network.ConnectionFailed -> StringResource(R.string.common_error_network_connection_failed)
    CommonErrorType.Network.InvalidFileType -> StringResource(R.string.common_error_network_invalid_file_type)
    CommonErrorType.Network.UploadFileFailed -> StringResource(R.string.common_error_network_upload_file_failed)
    CommonErrorType.Network.BadRequest -> StringResource(R.string.common_error_network_bad_request)
    CommonErrorType.Network.Unauthorized -> StringResource(R.string.common_error_network_unauthorized)
    CommonErrorType.Network.Forbidden -> StringResource(R.string.common_error_network_forbidden)
    CommonErrorType.Network.NotFound -> StringResource(R.string.common_error_network_not_found)
    CommonErrorType.Network.Conflict -> StringResource(R.string.common_error_network_conflict)
    CommonErrorType.Network.RequestTimeout -> StringResource(R.string.common_error_network_request_time_out)
    is CommonErrorType.Network.ClientError -> StringResource(R.string.common_error_network_client, this.status)
    CommonErrorType.Network.BadGateway -> StringResource(R.string.common_error_network_bad_gateway)
    CommonErrorType.Network.ServiceUnavailable -> StringResource(R.string.common_error_network_service_unavailable)
    CommonErrorType.Network.InternalServerError -> StringResource(R.string.common_error_network_internal_server_error)
    CommonErrorType.Network.GatewayTimeout -> StringResource(R.string.common_error_network_gateway_timeout)
    is CommonErrorType.Network.ServerError -> StringResource(R.string.common_error_network_server, this.status)
    // ------------------------------ SocialAuth ------------------------------
    CommonErrorType.SocialAuth.IdTokenParsing -> StringResource(R.string.common_error_social_auth_id_token_parsing)
    CommonErrorType.SocialAuth.Cancellation -> StringResource(R.string.common_error_social_auth_cancellation)
    CommonErrorType.SocialAuth.TokenTypeInvalid -> StringResource(R.string.common_error_social_auth_token_type_invalid)
    CommonErrorType.SocialAuth.UserNotFound -> StringResource(R.string.common_error_social_auth_user_not_found)
    CommonErrorType.SocialAuth.DeleteAccountFailed -> StringResource(R.string.common_error_social_auth_delete_account_failed)
    CommonErrorType.SocialAuth.KakaoSignInError -> StringResource(R.string.common_error_social_auth_kakao_sign_in_error)
    CommonErrorType.SocialAuth.KakaoSignOutError -> StringResource(R.string.common_error_social_auth_kakao_sign_out_error)
    CommonErrorType.SocialAuth.KakaoDeleteAccountError -> StringResource(R.string.common_error_social_auth_kakao_delete_account_error)
    CommonErrorType.SocialAuth.SaveTokenFailed -> StringResource(R.string.common_error_social_auth_save_token_failed)
    CommonErrorType.SocialAuth.SocialAuthFailed -> StringResource(R.string.common_error_social_auth_failed)
    is CommonErrorType.SocialAuth.Unexpected -> StringResource(R.string.common_error_social_auth_unexpected)
}
