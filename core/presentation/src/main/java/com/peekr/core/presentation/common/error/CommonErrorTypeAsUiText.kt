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
}
