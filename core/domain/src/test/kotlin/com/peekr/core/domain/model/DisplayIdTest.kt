package com.peekr.core.domain.model

import com.peekr.core.domain.assertThrows
import com.peekr.core.domain.user.model.DisplayId
import com.peekr.core.domain.validation.CommonValidationException
import org.junit.Assert
import org.junit.Test

class DisplayIdTest {
    @Test
    fun `사용자 표시 ID VO 객체 생성 성공 테스트`() {
        val displayId = DisplayId(VALID_DISPLAY_ID)
        Assert.assertEquals(displayId.value, VALID_DISPLAY_ID)
    }

    @Test
    fun `사용자 표시 ID VO 객체 생성 실패 테스트 - 잘못된 형식`() {
        assertThrows<CommonValidationException.InvalidFormat> {
            DisplayId(INVALID_FORMAT_DISPLAY_ID)
        }
    }

    @Test
    fun `사용자 표시 ID VO 객체 생성 실패 테스트 - 길이 제약 위반`() {
        assertThrows<CommonValidationException.TooShortOrLong> {
            DisplayId(TooLongDisplayId)
        }
    }

    @Test
    fun `사용자 표시 ID VO 객체 생성 실패 테스트 - 빈 문자열`() {
        assertThrows<CommonValidationException.Empty> {
            DisplayId(EMPTY_DISPLAY_ID)
        }
    }

    companion object {
        private const val VALID_DISPLAY_ID = "hi_hi"
        private const val INVALID_FORMAT_DISPLAY_ID = "hi-hi@"
        private val TooLongDisplayId = "a".repeat(DisplayId.MAX_LENGTH + 1)
        private const val EMPTY_DISPLAY_ID = ""
    }
}
