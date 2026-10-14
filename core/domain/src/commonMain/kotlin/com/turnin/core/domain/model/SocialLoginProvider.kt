package com.turnin.core.domain.model

/**
 * 소셜로그인 플랫폼
 *
 * (서버에서도 Enum 타입을 사용하고 모두 대문자 형식을 사용하기 때문에 통일 시켜야 한다.)
 */
enum class SocialLoginProvider {
    GOOGLE,
    KAKAO,
    ;

    companion object {
        fun getType(provider: String): SocialLoginProvider =
            when (provider) {
                GOOGLE.name -> GOOGLE
                KAKAO.name -> KAKAO
                else -> throw IllegalArgumentException("Unknown provider: $provider")
            }
    }
}
