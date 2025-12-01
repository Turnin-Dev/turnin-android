package com.peekr.core.common.util

/**
 * 민감 정보를 포함한 문자열을 마스킹한다.
 *
 * @param take 앞에서 부터 보여줄 원문 자릿 수(기본 값은 3)
 * @param minMask 가릴 문자열 자릿 수(기본 값은 6) 단, 수신 받은 문자열의 길이를 초과할 경우 초과된 만큼 `*`가 추가된다.
 */
fun String.masking(
    take: Int = 3,
    minMask: Int = 6,
): String {
    if (this.isEmpty()) return ""
    val safeTake = take.coerceIn(0, this.length - 1)
    val maskedLength = maxOf(minMask, this.length - safeTake)
    return this.take(safeTake) + "*".repeat(maskedLength)
}

/**
 * 민감 정보를 포함한 문자열을 마스킹한다.
 *
 * @param take 앞에서 부터 보여줄 원문 자릿 수(기본 값은 3)
 * @param minMask 가릴 문자열 자릿 수(기본 값은 6) 단, 수신 받은 문자열의 길이를 초과할 경우 초과된 만큼 `*`가 추가된다.
 *
 * @see masking
 */
fun Long.masking(
    take: Int = 3,
    minMask: Int = 6,
): String {
    val stringValue = this.toString()
    return stringValue.masking(take, minMask)
}
