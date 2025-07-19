package com.peekr.data.shared.dataStore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import com.peekr.domain.shared.dataStore.DataStoreKey
import com.peekr.domain.shared.dataStore.DataStoreManager
import java.io.IOException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map

class DataStoreManagerImpl(private val dataStore: DataStore<Preferences>) : DataStoreManager {
    override suspend fun saveStringData(key: DataStoreKey, value: String) {
        val preferenceKey = stringPreferencesKey(key.name)
        dataStore.edit { preferences -> preferences[preferenceKey] = value }
    }

    override suspend fun saveBooleanData(key: DataStoreKey, value: Boolean) {
        val pKey = booleanPreferencesKey(key.name)
        dataStore.edit { preferences -> preferences[pKey] = value }
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
        val pKey = stringPreferencesKey(key.name)
        dataStore.edit { preferences ->
            preferences.remove(pKey)
        }
    }

    override suspend fun deleteBooleanData(key: DataStoreKey) {
        val pKey = booleanPreferencesKey(key.name)
        dataStore.edit { preferences ->
            preferences.remove(pKey)
        }
    }

    override suspend fun clearAll() {
        dataStore.edit { preferences -> preferences.clear() }
    }
}
