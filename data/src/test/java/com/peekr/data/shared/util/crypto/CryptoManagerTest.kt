package com.peekr.data.shared.util.crypto

import android.os.Build
import java.util.Base64
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertTrue
import org.junit.BeforeClass
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(RobolectricTestRunner::class)
@Config(
    manifest = Config.NONE,
    sdk = [Build.VERSION_CODES.VANILLA_ICE_CREAM],
)
class CryptoManagerTest {
    private val testDispatcher = UnconfinedTestDispatcher()
    private val cryptoManager = CryptoManager(testDispatcher)

    @Test
    fun `암호화 후 복호화 성공 테스트`() = runTest(testDispatcher) {
        val encryptedText = cryptoManager.encryptString(PLAIN_TEXT)
        val decryptedText = cryptoManager.decryptString(encryptedText)

        assertNotEquals(encryptedText, PLAIN_TEXT)
        assertEquals(decryptedText, PLAIN_TEXT)
    }

    @Test
    fun `평문 텍스트를 암호화하고 복호화할 수 있다`() = runTest {
        // given
        val plainText = "Hello, World! 안녕하세요 🌍"

        // when
        val encryptedText = cryptoManager.encryptString(plainText)
        val decryptedText = cryptoManager.decryptString(encryptedText)

        // then
        assertEquals(plainText, decryptedText)
        assertNotEquals(plainText, encryptedText)

        // Base64 인코딩 검증
        val decodedBytes = Base64.getDecoder().decode(encryptedText)
        assert(decodedBytes.size > plainText.toByteArray().size) // IV + 암호문 + 태그로 더 큼
    }

    @Test
    fun `빈 문자열도 암호화와 복호화가 가능하다`() = runTest {
        // given
        val plainText = ""

        // when
        val encryptedText = cryptoManager.encryptString(plainText)
        val decryptedText = cryptoManager.decryptString(encryptedText)

        // then
        assertEquals(plainText, decryptedText)
    }

    @Test
    fun `긴 텍스트도 암호화와 복호화가 가능하다`() = runTest {
        // given
        val plainText = "A".repeat(10000) // 10KB 텍스트

        // when
        val encryptedText = cryptoManager.encryptString(plainText)
        val decryptedText = cryptoManager.decryptString(encryptedText)

        // then
        assertEquals(plainText, decryptedText)
    }

    @Test
    fun `같은 평문을 여러 번 암호화하면 매번 다른 결과가 나온다`() = runTest {
        // given
        val plainText = "test message"

        // when
        val encrypted1 = cryptoManager.encryptString(plainText)
        val encrypted2 = cryptoManager.encryptString(plainText)
        val encrypted3 = cryptoManager.encryptString(plainText)

        // then - 매번 다른 IV를 사용하므로 결과가 달라야 함
        assertNotEquals(encrypted1, encrypted2)
        assertNotEquals(encrypted2, encrypted3)
        assertNotEquals(encrypted1, encrypted3)

        // 하지만 모두 같은 평문으로 복호화됨
        assertEquals(plainText, cryptoManager.decryptString(encrypted1))
        assertEquals(plainText, cryptoManager.decryptString(encrypted2))
        assertEquals(plainText, cryptoManager.decryptString(encrypted3))
    }

    @Test
    fun `특수문자와 이모지가 포함된 텍스트도 정상 처리된다`() = runTest {
        // given
        val plainText = "Hello! 안녕하세요 🌍 #@$%^&*()_+ 中文 العربية"

        // when
        val encryptedText = cryptoManager.encryptString(plainText)
        val decryptedText = cryptoManager.decryptString(encryptedText)

        // then
        assertEquals(plainText, decryptedText)
    }

    @Test
    fun `잘못된 Base64 문자열로 복호화 시 예외가 발생한다`() = runTest {
        // given
        val invalidBase64 = "invalid-base64-string!!!"

        // when
        val exception = runCatching {
            cryptoManager.decryptString(invalidBase64)
        }.exceptionOrNull()

        // then
        assertTrue(exception is DecryptException)
    }

    @Test
    fun `손상된 암호문으로 복호화 시 예외가 발생한다`() = runTest {
        // given
        val plainText = "test message"
        val encryptedText = cryptoManager.encryptString(plainText)

        // Base64 디코딩 후 데이터 손상 시뮬레이션
        val encryptedBytes = Base64.getDecoder().decode(encryptedText)
        encryptedBytes[encryptedBytes.size - 1] = (encryptedBytes[encryptedBytes.size - 1] + 1).toByte()
        val corruptedEncryptedText = Base64.getEncoder().encodeToString(encryptedBytes)

        // when
        val exception = runCatching {
            cryptoManager.decryptString(corruptedEncryptedText)
        }.exceptionOrNull()

        // then
        assertTrue(exception is DecryptException)
    }

    @Test
    fun `암호화된 데이터의 구조가 올바른지 검증한다`() = runTest {
        // given
        val plainText = "test message"

        // when
        val encryptedText = cryptoManager.encryptString(plainText)
        val encryptedBytes = Base64.getDecoder().decode(encryptedText)

        // then
        // IV(12) + 암호문 + 태그(16) = 최소 28바이트 + 평문 길이
        assert(encryptedBytes.size >= 28 + plainText.toByteArray().size)

        // IV 부분 추출
        val iv = encryptedBytes.copyOfRange(0, 12)
        assert(iv.size == 12)

        // 나머지는 암호문 + 태그
        val cipherWithTag = encryptedBytes.copyOfRange(12, encryptedBytes.size)
        assert(cipherWithTag.size >= 16) // 최소 태그 크기
    }

    @Test
    fun `동일한 키로 여러 번 암호화 복호화해도 일관성이 유지된다`() = runTest {
        // given
        val messages = listOf(
            "first message",
            "second message",
            "third message",
        )

        // when - 각각 암호화
        val encryptedMessages = messages.map { cryptoManager.encryptString(it) }

        // then - 각각 복호화하여 원본과 일치하는지 확인
        encryptedMessages.forEachIndexed { index, encrypted ->
            val decrypted = cryptoManager.decryptString(encrypted)
            assertEquals(messages[index], decrypted)
        }
    }

    @Test
    fun `Crypto 객체의 키 재사용이 올바르게 동작하는지 확인`() = runTest {
        // given
        val plainText1 = "first encryption"
        val plainText2 = "second encryption"

        // when - 첫 번째 암호화 (키 생성됨)
        val encrypted1 = cryptoManager.encryptString(plainText1)
        val decrypted1 = cryptoManager.decryptString(encrypted1)

        // 두 번째 암호화 (같은 키 재사용)
        val encrypted2 = cryptoManager.encryptString(plainText2)
        val decrypted2 = cryptoManager.decryptString(encrypted2)

        // then
        assertEquals(plainText1, decrypted1)
        assertEquals(plainText2, decrypted2)

        // 서로 다른 암호문이지만 같은 키로 복호화 가능
        assertNotEquals(encrypted1, encrypted2)
    }

    companion object {
        private const val PLAIN_TEXT = "plain_text"

        @JvmStatic
        @BeforeClass
        fun beforeClass() {
            FakeAndroidKeyStore.setup
        }
    }
}
