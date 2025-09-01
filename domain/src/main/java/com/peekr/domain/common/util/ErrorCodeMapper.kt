package com.peekr.domain.common.util

/** 문자열 타입의 에러코드를 [ErrorCode]로 변환한다. */
fun String.toErrorCode(): ErrorCode = when (this.trim().uppercase()) {
    "A002" -> ErrorCode.Auth.A002
    else -> ErrorCode.Unexpected
}
