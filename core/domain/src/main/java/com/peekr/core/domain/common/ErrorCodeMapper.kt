package com.peekr.core.domain.common

/** 문자열 타입의 에러코드를 [ServerErrorCode]로 변환한다. */
fun String.toErrorCode(): ServerErrorCode = when (this.trim().uppercase()) {
    "A002" -> ServerErrorCode.Auth.A002
    else -> ServerErrorCode.Unexpected
}
