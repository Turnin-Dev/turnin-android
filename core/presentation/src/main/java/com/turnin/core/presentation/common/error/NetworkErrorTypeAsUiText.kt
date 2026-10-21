package com.turnin.core.presentation.common.error

import com.turnin.core.domain.common.error.CommonErrorType
import com.turnin.core.presentation.ui.util.UiText
import com.turnin.shared.domain.core.error.NetworkErrorType

/**
 * 공유 모듈(`:shared`)의 네트워크 에러를 UI 문구로 변환한다.
 *
 * [NetworkErrorType]은 [CommonErrorType]의 네트워크·예외 항목과 구성이 같으므로, 변환 후 기존 문구([CommonErrorType.asUiText])를 사용한다.
 */
fun NetworkErrorType.asUiText(): UiText = toCommonErrorType().asUiText()

private fun NetworkErrorType.toCommonErrorType(): CommonErrorType = when (this) {
    // ------------------------------ Exception ------------------------------
    NetworkErrorType.Exception.Json -> CommonErrorType.Exception.Json
    NetworkErrorType.Exception.TimeOut -> CommonErrorType.Exception.TimeOut
    NetworkErrorType.Exception.IO -> CommonErrorType.Exception.IO
    // ------------------------------ Network ------------------------------
    NetworkErrorType.Network.BadRequest -> CommonErrorType.Network.BadRequest
    NetworkErrorType.Network.Unauthorized -> CommonErrorType.Network.Unauthorized
    NetworkErrorType.Network.Forbidden -> CommonErrorType.Network.Forbidden
    NetworkErrorType.Network.NotFound -> CommonErrorType.Network.NotFound
    NetworkErrorType.Network.Conflict -> CommonErrorType.Network.Conflict
    NetworkErrorType.Network.RequestTimeout -> CommonErrorType.Network.RequestTimeout
    NetworkErrorType.Network.InternalServerError -> CommonErrorType.Network.InternalServerError
    NetworkErrorType.Network.BadGateway -> CommonErrorType.Network.BadGateway
    NetworkErrorType.Network.ServiceUnavailable -> CommonErrorType.Network.ServiceUnavailable
    NetworkErrorType.Network.GatewayTimeout -> CommonErrorType.Network.GatewayTimeout
    is NetworkErrorType.Network.ClientError -> CommonErrorType.Network.ClientError(status)
    is NetworkErrorType.Network.ServerError -> CommonErrorType.Network.ServerError(status)
    NetworkErrorType.Network.ConnectionFailed -> CommonErrorType.Network.ConnectionFailed
    // ------------------------------ Unexpected ------------------------------
    is NetworkErrorType.Unexpected -> CommonErrorType.Unexpected(cause)
}
