package com.peekr.core.data.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.peekr.core.common.crypto.CryptoManager
import com.peekr.core.common.crypto.DecryptException
import com.peekr.core.data.source.local.datastore.DataStoreKey
import com.peekr.core.data.source.local.datastore.DataStoreManager
import com.peekr.core.data.source.local.datastore.DataStoreManagerImpl
import com.peekr.core.data.source.local.error.WritingDataException
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import okio.IOException
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder

@OptIn(ExperimentalCoroutinesApi::class)
class DataStoreManagerImplTest {
    @get:Rule
    val temporaryFolder: TemporaryFolder = TemporaryFolder
        .builder()
        .assureDeletion()
        .build()

    private lateinit var dataStoreManager: DataStoreManager
    private lateinit var dataStoreFactory: TestDataStoreFactory
    private lateinit var dataStore: DataStore<Preferences>
    private val cryptoManager: CryptoManager = mockk()

    @Before
    fun setUp() {
        dataStoreFactory = TestDataStoreFactory(temporaryFolder)
        dataStore = dataStoreFactory.create()
        dataStoreManager = DataStoreManagerImpl(dataStore, cryptoManager)
    }

    @Test
    fun `String 타입의 값 읽기 및 저장 성공 테스트`() = runTest {
        // given
        val expectedKey = DataStoreKey.Auth.AccessToken
        val expectedValue = "valid value"

        // when
        dataStoreManager.saveStringData(expectedKey, expectedValue)
        val actualValue = dataStoreManager.getStringData(expectedKey).first()

        // then
        assertNotNull(actualValue)
        assertEquals(actualValue, expectedValue)
    }

    @Test
    fun `Boolean 타입의 값 읽기 및 저장 성공 테스트`() = runTest {
        // given
        val expectedKey = DataStoreKey.Auth.AccessToken
        val expectedValue = true

        // when
        dataStoreManager.saveBooleanData(expectedKey, expectedValue)
        val actualValue = dataStoreManager.getBooleanData(expectedKey).first()

        // then
        assertNotNull(actualValue)
        assertEquals(actualValue, expectedValue)
    }

    @Test
    fun `String 타입의 값 읽기 및 저장 (암호화, 복호화) 성공 테스트`() = runTest {
        // given
        val expectedKey = DataStoreKey.Auth.AccessToken
        val expectedValue = "valid value"
        val encryptedValue = "encrypted valid value"
        coEvery { cryptoManager.encryptString(any()) } returns encryptedValue
        coEvery { cryptoManager.decryptString(any()) } returns expectedValue

        // when
        dataStoreManager.saveEncryptedStringData(expectedKey, expectedValue)
        val actualValue = dataStoreManager.getEncryptedStringData(expectedKey).first()

        // then
        assertNotNull(actualValue)
        assertEquals(expectedValue, actualValue)
    }

    @Test
    fun `String 타입의 데이터 삭제 후 조회 시 null을 반환한다`() = runTest {
        // given
        val expectedKey = DataStoreKey.Auth.AccessToken
        val expectedValue = "valid value"

        // when
        dataStoreManager.saveStringData(expectedKey, expectedValue)
        dataStoreManager.deleteStringData(expectedKey)
        val actualValue = dataStoreManager.getStringData(expectedKey).first()

        // then
        assertNull(actualValue)
    }

    @Test
    fun `Boolean 타입의 데이터 삭제 후 조회 시 null을 반환한다`() = runTest {
        // given
        val expectedKey = DataStoreKey.Auth.AccessToken
        val expectedValue = false

        // when
        dataStoreManager.saveBooleanData(expectedKey, expectedValue)
        dataStoreManager.deleteBooleanData(expectedKey)
        val actualValue = dataStoreManager.getBooleanData(expectedKey).first()

        // then
        assertNull(actualValue)
    }

    @Test
    fun `IOException 예외 발생 시 WritingDataException로 매핑되어 예외가 발생한다`() = runTest {
        // given
        val expectedKey = DataStoreKey.Auth.AccessToken
        val expectedValue = "valid value"
        coEvery { cryptoManager.encryptString(any()) } throws IOException()

        // when
        val exception = runCatching {
            dataStoreManager.saveEncryptedStringData(expectedKey, expectedValue)
        }.exceptionOrNull()

        // then
        assertTrue(exception is WritingDataException)
    }

    @Test
    fun `암호화된 데이터를 가져올 때 - 복호화 실패 시 DecryptException 예외가 발생한다`() = runTest {
        // given
        val expectedKey = DataStoreKey.Auth.AccessToken
        val expectedValue = "valid value"
        val encryptedValue = "encrypted valid value"
        coEvery { cryptoManager.encryptString(any()) } returns encryptedValue
        // 예외 발생 시에는 Data 계층의 예외 발생
        coEvery { cryptoManager.decryptString(any()) } throws DecryptException()

        // when
        dataStoreManager.saveEncryptedStringData(expectedKey, expectedValue)
        val exception = runCatching {
            dataStoreManager.getEncryptedStringData(expectedKey).first()
        }.exceptionOrNull()

        // then
        // 예외 발생 후에는 Domain 계층의 예외
        assertTrue(exception is com.peekr.core.data.source.local.error.DecryptException)
    }

    @Test
    fun `암호화된 데이터를 가져올 때 - DecryptException를 제외한 예외가 발생한 경우 null을 반환한다`() = runTest {
        // given
        val expectedKey = DataStoreKey.Auth.AccessToken
        val expectedValue = "valid value"
        val encryptedValue = "encrypted valid value"
        coEvery { cryptoManager.encryptString(any()) } returns encryptedValue
        // 예외 발생 시에는 Data 계층의 예외 발생
        coEvery { cryptoManager.decryptString(any()) } throws IllegalStateException()

        // when
        dataStoreManager.saveEncryptedStringData(expectedKey, expectedValue)
        val result = dataStoreManager.getEncryptedStringData(expectedKey).first()

        // then
        assertNull(result)
    }

    @Test
    fun `데이터(String 타입)가 없거나 존재하지 않은 키를 조회한 경우 null을 반환한다`() = runTest {
        // when
        val result = dataStoreManager.getStringData(DataStoreKey.Auth.AccessToken).first()

        // when
        assertNull(result)
    }

    @Test
    fun `데이터(Boolean 타입)가 없거나 존재하지 않은 키를 조회한 경우 null을 반환한다`() = runTest {
        // when
        val result = dataStoreManager.getBooleanData(DataStoreKey.Auth.AccessToken).first()

        // when
        assertNull(result)
    }
}
