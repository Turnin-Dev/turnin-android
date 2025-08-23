package com.peekr.domain.shared.util

/** 유효성 검사 에러 */
enum class CommonValidationError : ValidationError {
    /** 공백 비허용 */
    EMPTY_OR_BLANK,

    /** 허용된 길이 초과 (1~30) */
    EXCEEDS_MAX_LENGTH_30,

    /** 허용된 길이 초과 (1~200) */
    EXCEEDS_MAX_LENGTH_200,

    /** 영어/숫자/밑줄만 허용 */
    ONLY_ALPHANUMERIC_UNDERSCORE,

    /** 영어/숫자/한글만 허용 */
    ONLY_ALPHANUMERIC_HANGUL,
}
