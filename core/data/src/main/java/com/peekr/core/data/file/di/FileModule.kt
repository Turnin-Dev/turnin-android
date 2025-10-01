package com.peekr.core.data.file.di

import com.peekr.core.data.file.network.FileApi
import com.peekr.core.data.network.DefaultOkHttpClient
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
import okhttp3.OkHttpClient
import retrofit2.Retrofit

@Module
@InstallIn(SingletonComponent::class)
class FileModule {
    @Singleton
    @Provides
    fun provideFileApi(
        retrofit: Retrofit.Builder,
        @DefaultOkHttpClient okHttpClient: OkHttpClient,
    ): FileApi =
        retrofit.client(okHttpClient).build().create(FileApi::class.java)
}
