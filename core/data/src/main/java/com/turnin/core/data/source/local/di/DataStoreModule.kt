package com.turnin.core.data.source.local.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.turnin.core.data.BuildConfig
import com.turnin.core.data.crypto.CryptoManager
import com.turnin.core.data.source.local.datastore.DataStoreManager
import com.turnin.core.data.source.local.datastore.DataStoreManagerImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = BuildConfig.TURNIN_DATA_STORE)

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
