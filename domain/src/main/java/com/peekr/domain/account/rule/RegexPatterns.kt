package com.peekr.domain.account.rule

object RegexPatterns {
    /** 사용자 표시 ID 규칙: 영어/숫자/밑줄만 허용 */
    val displayId = Regex("^[a-zA-Z0-9_]+$")

    /** 사용자 이름 규칙: 영어/숫자/한글만 허용 */
    val name = Regex("^[a-zA-Z0-9가-힣]+$")
}
