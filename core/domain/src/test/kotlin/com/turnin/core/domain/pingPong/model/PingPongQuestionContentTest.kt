package com.turnin.core.domain.pingPong.model

import com.turnin.core.domain.assertThrows
import com.turnin.core.domain.common.validation.CommonValidationException
import org.junit.Assert.assertEquals
import org.junit.Test

class PingPongQuestionContentTest {
    @Test
    fun `복원 - 물음표로 끝나지 않는 값은 그대로 유지된다`() {
        // Given
        val stored = "형식 규칙 도입 전 질문"

        // When
        val content = PingPongQuestionContent(stored)

        // Then
        assertEquals(stored, content.value)
    }

    @Test
    fun `복원 - 빈 문자열이면 Empty 예외가 발생한다`() {
        // Given
        val stored = ""

        // When & Then
        assertThrows<CommonValidationException.Empty> {
            PingPongQuestionContent(stored)
        }
    }

    @Test
    fun `복원 - 공백만 있으면 Empty 예외가 발생한다`() {
        // Given
        val stored = "   "

        // When & Then
        assertThrows<CommonValidationException.Empty> {
            PingPongQuestionContent(stored)
        }
    }

    @Test
    fun `복원 - 최대 글자 수와 같으면 생성된다`() {
        // Given
        val stored = "a".repeat(PingPongQuestionContent.MAX_LENGTH)

        // When
        val content = PingPongQuestionContent(stored)

        // Then
        assertEquals(stored, content.value)
    }

    @Test
    fun `복원 - 최대 글자 수를 초과하면 TooShortOrLong 예외가 발생한다`() {
        // Given
        val stored = "a".repeat(PingPongQuestionContent.MAX_LENGTH + 1)

        // When & Then
        assertThrows<CommonValidationException.TooShortOrLong> {
            PingPongQuestionContent(stored)
        }
    }

    @Test
    fun `생성 - 물음표로 끝나지 않으면 물음표를 하나 붙인다`() {
        // Given
        val input = "오늘 뭐 했어"

        // When
        val content = PingPongQuestionContent.create(input)

        // Then
        assertEquals("오늘 뭐 했어?", content.value)
    }

    @Test
    fun `생성 - 물음표로 끝나면 그대로 유지한다`() {
        // Given
        val input = "오늘 뭐 했어?"

        // When
        val content = PingPongQuestionContent.create(input)

        // Then
        assertEquals("오늘 뭐 했어?", content.value)
    }

    @Test
    fun `생성 - 앞뒤 공백을 제거한다`() {
        // Given
        val input = "  오늘 뭐 했어?  "

        // When
        val content = PingPongQuestionContent.create(input)

        // Then
        assertEquals("오늘 뭐 했어?", content.value)
    }

    @Test
    fun `생성 - 물음표 두 개는 본문이 있는 것으로 보고 그대로 유지한다`() {
        // Given
        val input = "??"

        // When
        val content = PingPongQuestionContent.create(input)

        // Then
        assertEquals("??", content.value)
    }

    @Test
    fun `생성 - 물음표 하나만 있으면 Empty 예외가 발생한다`() {
        // Given
        val input = "?"

        // When & Then
        assertThrows<CommonValidationException.Empty> {
            PingPongQuestionContent.create(input)
        }
    }

    @Test
    fun `생성 - 공백과 물음표 하나만 있으면 Empty 예외가 발생한다`() {
        // Given
        val input = "  ?  "

        // When & Then
        assertThrows<CommonValidationException.Empty> {
            PingPongQuestionContent.create(input)
        }
    }

    @Test
    fun `생성 - 공백만 있으면 Empty 예외가 발생한다`() {
        // Given
        val input = "   "

        // When & Then
        assertThrows<CommonValidationException.Empty> {
            PingPongQuestionContent.create(input)
        }
    }

    @Test
    fun `생성 - 붙인 물음표를 포함해 최대 글자 수와 같으면 생성된다`() {
        // Given
        val input = "a".repeat(PingPongQuestionContent.MAX_LENGTH - 1)

        // When
        val content = PingPongQuestionContent.create(input)

        // Then
        assertEquals(PingPongQuestionContent.MAX_LENGTH, content.value.length)
    }

    @Test
    fun `생성 - 붙인 물음표를 포함해 최대 글자 수를 초과하면 TooShortOrLong 예외가 발생한다`() {
        // Given
        val input = "a".repeat(PingPongQuestionContent.MAX_LENGTH)

        // When & Then
        assertThrows<CommonValidationException.TooShortOrLong> {
            PingPongQuestionContent.create(input)
        }
    }
}
