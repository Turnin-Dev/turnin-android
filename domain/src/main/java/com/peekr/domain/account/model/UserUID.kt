package com.peekr.domain.account.model

/**
 * 사용자의 고유 ID를 래핑하는 값 객체입니다.
 *
 * @property uid Firebase 등에서 발급된 고유 식별자
 */
data class UserUID(val uid: String)
