package com.peekr.core.domain.common

/** 문자열 타입의 에러코드를 [ContractErrorCode]로 변환한다. */
fun String.toServerErrorCode(): ContractErrorCode = when (this.trim().uppercase()) {
    "A002" -> ContractErrorCode.Auth.A002
    else -> ContractErrorCode.Unexpected
}
