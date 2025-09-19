package com.peekr.domain.common.model

import com.peekr.domain.assertThrows
import org.junit.Assert.assertEquals
import org.junit.Test

class NameTest {
    @Test
    fun `이름 VO 객체 생성 성공 테스트`() {
        val name = Name(VALID_NAME)
        assertEquals(name.value, VALID_NAME)
    }

    @Test
    fun `이름 VO 객체 생성 실패 테스트 - 잘못된 형식`() {
        assertThrows<NameException.InvalidFormat> {
            Name(INVALID_FORMAT_NAME)
        }
    }

    @Test
    fun `이름 VO 객체 생성 실패 테스트 - 길이 제약 위반`() {
        assertThrows<NameException.TooShortOrLong> {
            Name(TooLongName)
        }
    }

    @Test
    fun `이름 VO 객체 생성 실패 테스트 - 빈 문자열`() {
        assertThrows<NameException.Empty> {
            Name(EMPTY_NAME)
        }
    }

    companion object {
        private const val VALID_NAME = "hong길동"
        private const val INVALID_FORMAT_NAME = "hi-hi@"
        private val TooLongName = "a".repeat(Name.MAX_LENGTH + 1)
        private const val EMPTY_NAME = ""
    }
}
