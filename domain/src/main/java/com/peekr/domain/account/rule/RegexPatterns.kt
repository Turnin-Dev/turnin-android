package com.peekr.domain.account.rule

object RegexPatterns {
    val displayId = Regex("^[a-zA-Z0-9_]+$")
    val username = Regex("^[a-zA-Z0-9가-힣]+$")
}
