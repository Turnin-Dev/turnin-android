package com.peekr.data.shared.dataStore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import com.peekr.domain.shared.dataStore.DataStoreKey
import com.peekr.domain.shared.dataStore.DataStoreManager
import com.peekr.domain.shared.dataStore.WritingDataException
import java.io.IOException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map

class DataStoreManagerImpl(private val dataStore: DataStore<Preferences>) : DataStoreManager {
    override suspend fun saveStringData(key: DataStoreKey, value: String) {
        dataStoreTryCatch {
            val preferenceKey = stringPreferencesKey(key.name)
            dataStore.edit { preferences -> preferences[preferenceKey] = value }
        }
    }

    override suspend fun saveBooleanData(key: DataStoreKey, value: Boolean) {
        dataStoreTryCatch {
            val pKey = booleanPreferencesKey(key.name)
            dataStore.edit { preferences -> preferences[pKey] = value }
        }
    }

    override fun getStringData(key: DataStoreKey): Flow<String?> {
        val pKey = stringPreferencesKey(key.name)
        return dataStore.data
            .catch { exception ->
                if (exception is IOException) {
                    emit(emptyPreferences())
                } else {
                    throw exception
                }
            }.map { preferences -> preferences[pKey] }
    }

    override fun getBooleanData(key: DataStoreKey): Flow<Boolean?> {
        val pKey = booleanPreferencesKey(key.name)
        return dataStore.data
            .catch { exception ->
                if (exception is IOException) {
                    emit(emptyPreferences())
                } else {
                    throw exception
                }
            }.map { preferences -> preferences[pKey] }
    }

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
            val pKey = stringPreferencesKey(key.name)
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
}

/**
 * DataStore 로직에서 공통적인 예외를 잡아내는 데 사용하는 try-catch 템플릿
 *
 * @param block DataStore 관련 로직을 수행
 */
private inline fun dataStoreTryCatch(block: () -> Unit) {
    try {
        block()
    } catch (e: IOException) {
        throw WritingDataException("[데이터를 디스크에 쓰는 과정에서 오류가 발생했습니다.]: ${e.message}")
    }
}
