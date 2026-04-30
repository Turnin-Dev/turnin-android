package com.turnin.core.data.source.network.error

import com.turnin.core.domain.common.BaseError

sealed interface NetworkErrorType : BaseError {
    /** Network 에러 타입 */
    sealed interface Network : NetworkErrorType {
        data class HttpError(
            val status: Int,
        ) : Network

        data object ConnectionFailed : Network

        data object InvalidFileType : Network

        data object UploadFileFailed : Network
    }

    /** Exception 에러 타입 */
    enum class Exception : NetworkErrorType {
        IO,
        TimeOut,
        JsonData,
        JsonEncoding,
        MalformedJson,
    }

    data class Unexpected(val cause: Throwable?) : NetworkErrorType
}
