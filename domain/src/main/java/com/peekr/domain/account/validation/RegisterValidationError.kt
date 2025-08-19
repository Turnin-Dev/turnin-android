package com.peekr.domain.account.validation

import com.peekr.domain.shared.util.ValidationError

/** 유효성 검사 에러 */
enum class RegisterValidationError : ValidationError {
    /** 공백 비허용 */
    EMPTY_OR_BLACK,

    /** 허용된 길이 초과 */
    EXCEEDS_MAX_LENGTH,

    /** 영어/숫자/밑줄만 허용 */
    ONLY_ALPHANUMERIC_UNDERSCORE,

    /** 영어/숫자/한글만 허용 */
    ONLY_ALPHANUMERIC_HANGUL,
}
