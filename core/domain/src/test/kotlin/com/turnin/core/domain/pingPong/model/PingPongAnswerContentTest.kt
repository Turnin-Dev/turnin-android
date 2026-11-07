package com.turnin.core.domain.pingPong.model

import com.turnin.core.domain.assertThrows
import com.turnin.core.domain.common.validation.CommonValidationException
import org.junit.Assert.assertEquals
import org.junit.Test

class PingPongAnswerContentTest {
    @Test
    fun `유효한 값이면 그대로 생성된다`() {
        // Given
        val value = "오늘은 산책했어"

        // When
        val content = PingPongAnswerContent(value)

        // Then
        assertEquals(value, content.value)
    }

    @Test
    fun `빈 문자열이면 Empty 예외가 발생한다`() {
        // Given
        val value = ""

        // When & Then
        assertThrows<CommonValidationException.Empty> {
            PingPongAnswerContent(value)
        }
    }

    @Test
    fun `공백만 있으면 Empty 예외가 발생한다`() {
        // Given
        val value = "   "

        // When & Then
        assertThrows<CommonValidationException.Empty> {
            PingPongAnswerContent(value)
        }
    }

    @Test
    fun `최대 글자 수와 같으면 생성된다`() {
        // Given
        val value = "a".repeat(PingPongAnswerContent.MAX_LENGTH)

        // When
        val content = PingPongAnswerContent(value)

        // Then
        assertEquals(value, content.value)
    }

    @Test
    fun `최대 글자 수를 초과하면 TooShortOrLong 예외가 발생한다`() {
        // Given
        val value = "a".repeat(PingPongAnswerContent.MAX_LENGTH + 1)

        // When & Then
        assertThrows<CommonValidationException.TooShortOrLong> {
            PingPongAnswerContent(value)
        }
    }
}
