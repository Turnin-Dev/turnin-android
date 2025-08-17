package com.peekr.domain.shared.util.validation

enum class ValidationError(val message: String) {
    ExceedsLength("1~30자 이내로 입력해주세요."),
    RequireEnglishNumberUnderLine("영문/숫자/밑줄만 허용됩니다."),
    RequireEnglishNumberHangeul("영문/숫자/한글만 허용됩니다."),
}
