package com.turnin.core.domain.common.error

/**
 * 서버 에러 코드 값으로 [ContractErrorCode]를 찾는 조회표 (enum 이름이 곧 서버 에러 코드)
 *
 * 새 에러 코드 그룹을 추가하면 여기에 `entries.associateBy { it.name }`를 더한다.
 *
 * 누락 항목에 대해서는 테스트 코드로 확인한다. `ContractErrorCodeMapperTest.kt`
 */
private val errorCodesByName: Map<String, ContractErrorCode> =
    ContractErrorCode.Auth.entries.associateBy { it.name } +
        ContractErrorCode.PingPong.entries.associateBy { it.name }

/** 문자열 타입의 에러코드를 [ContractErrorCode]로 변환한다. */
fun String.toServerErrorCode(): ContractErrorCode =
    errorCodesByName[trim().uppercase()] ?: ContractErrorCode.Unexpected
