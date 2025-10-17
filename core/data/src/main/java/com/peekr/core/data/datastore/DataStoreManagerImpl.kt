package com.peekr.core.data.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import com.peekr.core.common.logger.AppLogger
import com.peekr.core.data.crypto.CryptoException
import com.peekr.core.data.crypto.CryptoManager
import java.io.IOException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map

class DataStoreManagerImpl(
    private val dataStore: DataStore<Preferences>,
    private val cryptoManager: CryptoManager,
) : DataStoreManager {
    private val tag = this::class.java.simpleName

    // ------------------------------ 일반 저장 & 읽기 메서드 ------------------------------
    override suspend fun saveStringData(key: DataStoreKey, value: String) {
        dataStoreTryCatch {
            val pKey = stringPreferencesKey(key.name)
            dataStore.edit { preferences -> preferences[pKey] = value }
        }
    }

    override suspend fun saveBooleanData(key: DataStoreKey, value: Boolean) {
        dataStoreTryCatch {
            val pKey = booleanPreferencesKey(key.name)
            dataStore.edit { preferences -> preferences[pKey] = value }
        }
    }

    override suspend fun saveLongData(key: DataStoreKey, value: Long) {
        dataStoreTryCatch {
            val pKey = longPreferencesKey(key.name)
            dataStore.edit { preferences -> preferences[pKey] = value }
        }
    }

    override fun getStringData(key: DataStoreKey): Flow<String?> =
        dataStore.data
            .catch { exception ->
                if (exception is IOException) {
                    emit(emptyPreferences())
                } else {
                    throw exception
                }
            }.map { preferences ->
                val pKey = stringPreferencesKey(key.name)
                preferences[pKey]
            }

    override fun getBooleanData(key: DataStoreKey): Flow<Boolean?> = dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }.map { preferences ->
            val pKey = booleanPreferencesKey(key.name)
            preferences[pKey]
        }

    override fun getLongData(key: DataStoreKey): Flow<Long?> = dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }.map { preferences ->
            val pKey = longPreferencesKey(key.name)
            preferences[pKey]
        }

    // ------------------------------ 암호화 저장 & 읽기 메서드 ------------------------------
    override suspend fun saveEncryptedStringData(key: DataStoreKey, value: String) {
        dataStoreTryCatch {
            val pKey = stringPreferencesKey(key.name)
            val encryptedValue = cryptoManager.encryptString(value)
            dataStore.edit { preferences -> preferences[pKey] = encryptedValue }
        }
    }

    override fun getEncryptedStringData(key: DataStoreKey): Flow<String?> = dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }.map { preferences ->
            val pKey = stringPreferencesKey(key.name)
            val encryptedValue = preferences[pKey]
            encryptedValue?.let {
                try {
                    cryptoManager.decryptString(encryptedValue)
                } catch (e: CryptoException) {
                    AppLogger.e(tag, e, "DataStoreManager에서 복호화 과정 실패")
                    throw DecryptException("DataStoreManager에서 복호화 과정 실패", e)
                } catch (e: Exception) {
                    AppLogger.e(tag, e, "DataStoreManager에서 복호화 과정 실패(정의된 이 외의 예외 발생)")
                    null
                }
            }
        }

    // ------------------------------ 삭제 메서드 ------------------------------
    override suspend fun deleteStringData(key: DataStoreKey) {
        dataStoreTryCatch {
            val pKey = stringPreferencesKey(key.name)
            dataStore.edit { preferences ->
                preferences.remove(pKey)
            }
        }
    }

    override suspend fun deleteBooleanData(key: DataStoreKey) {
        dataStoreTryCatch {
            val pKey = booleanPreferencesKey(key.name)
            dataStore.edit { preferences ->
                preferences.remove(pKey)
            }
        }
    }

    override suspend fun deleteLongData(key: DataStoreKey) {
        dataStoreTryCatch {
            val pKey = longPreferencesKey(key.name)
            dataStore.edit { preferences ->
                preferences.remove(pKey)
            }
        }
    }

    override suspend fun clearAll() {
        dataStoreTryCatch {
            dataStore.edit { preferences -> preferences.clear() }
        }
    }

    // ------------------------------ 공통 메서드 ------------------------------

    /**
     * DataStore 로직에서 공통적인 예외를 잡아내는 데 사용하는 try-catch 템플릿
     *
     * @param block DataStore 관련 로직을 수행
     */
    private inline fun dataStoreTryCatch(block: () -> Unit) {
        try {
            block()
        } catch (e: IOException) {
            throw WritingDataException("[데이터를 디스크에 쓰는 과정에서 오류가 발생했습니다.]: ${e.message}", e)
        }
    }
}
