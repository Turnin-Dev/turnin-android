package com.turnin.core.domain.model

import com.turnin.core.domain.common.validation.CommonValidationException
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class IntroduceTest {
    @Test
    fun `소개 글 유효성 검사 성공 테스트`() {
        val introduce = Introduce(VALID_INTRODUCE)
        assertEquals(introduce.value, VALID_INTRODUCE)
    }

    @Test
    fun `소개 글 유효성 검사 실패 테스트 - 길이 제약 위반`() {
        assertFailsWith<CommonValidationException.TooShortOrLong> {
            Introduce(TooLongIntroduce)
        }
    }

    companion object {
        private const val VALID_INTRODUCE = "hello world!"
        private val TooLongIntroduce = "a".repeat(Introduce.MAX_LENGTH + 1)
    }
}
