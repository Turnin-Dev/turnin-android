package com.peekr.core.presentation.error

import com.peekr.core.domain.common.CommonErrorType
import com.peekr.core.presentation.R
import com.peekr.core.presentation.util.UiText
import com.peekr.core.presentation.util.UiText.StringResource

fun CommonErrorType.asUiText(): UiText = when (this) {
    CommonErrorType.Exception.Json -> StringResource(R.string.common_error_exception_json)
    CommonErrorType.Exception.TimeOut -> StringResource(R.string.common_error_exception_timeout)
    CommonErrorType.Exception.IO -> StringResource(R.string.common_error_exception_io)
    CommonErrorType.Network.Unauthorized -> StringResource(R.string.common_error_network_unauthorized)
    CommonErrorType.Network.ClientError -> StringResource(R.string.common_error_network_client)
    CommonErrorType.Network.ServerError -> StringResource(R.string.common_error_network_server)
    CommonErrorType.Network.ConnectionFailed -> StringResource(R.string.common_error_network_connection_failed)
    CommonErrorType.Network.InvalidFileType -> StringResource(R.string.common_error_network_invalid_file_type)
    CommonErrorType.Network.UploadFileFailed -> StringResource(R.string.common_error_network_upload_file_failed)
    is CommonErrorType.Unexpected -> StringResource(R.string.common_error_unexpected)
}
