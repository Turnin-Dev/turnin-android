package com.peekr.data.shared.dataStore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.peekr.data.BuildConfig
import com.peekr.data.shared.util.crypto.CryptoManager
import com.peekr.domain.shared.dataStore.DataStoreManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = BuildConfig.PEEKR_DATA_STORE)

@Module
@InstallIn(SingletonComponent::class)
class DataStoreModule {
    @Singleton
    @Provides
    fun provideDataStoreManager(
        @ApplicationContext context: Context,
        cryptoManager: CryptoManager,
    ): DataStoreManager = DataStoreManagerImpl(context.dataStore, cryptoManager)
}
