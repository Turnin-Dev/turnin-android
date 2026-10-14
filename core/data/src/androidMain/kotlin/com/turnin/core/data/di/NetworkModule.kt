package com.turnin.core.data.di

import android.content.Context
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import com.turnin.core.data.BuildConfig
import com.turnin.core.data.source.local.datastore.DataStoreManager
import com.turnin.core.data.source.network.api.RefreshTokenApi
import com.turnin.core.data.source.network.retrofit.HttpCacheInterceptor
import com.turnin.core.data.source.network.retrofit.TokenAuthenticator
import com.turnin.core.data.source.network.retrofit.TokenInterceptor
import com.turnin.core.domain.eventBus.AuthEventBus
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import java.util.concurrent.TimeUnit
import javax.inject.Qualifier
import javax.inject.Singleton
import okhttp3.Cache
import okhttp3.OkHttpClient
import okhttp3.internal.platform.Platform
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

private val maskingLogger = HttpLoggingInterceptor.Logger { message ->
    val masked = message
        .replace(
            Regex("""("(?:access_?token|refresh_?token)"\s*:\s*")([^"]+)(")""", RegexOption.IGNORE_CASE),
            "$1********$3",
        )

    Platform.get().log(masked)
}

@Module
@InstallIn(SingletonComponent::class)
class NetworkModule {
    @Singleton
    @Provides
    fun provideCache(
        @ApplicationContext context: Context,
    ): Cache =
        Cache(context.cacheDir, 10 * 1024 * 1024L)

    // ------------------------------ Serialization ------------------------------
    @Singleton
    @Provides
    fun provideMoshi(): Moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()

    // ------------------------------ OkHttpClient ------------------------------
    @DefaultOkHttpClient
    @Singleton
    @Provides
    fun provideDefaultOkHttpClient(
        httpLoggingInterceptor: HttpLoggingInterceptor,
        cache: Cache,
        cacheInterceptor: HttpCacheInterceptor,
    ): OkHttpClient = OkHttpClient
        .Builder()
        .cache(cache)
        .addInterceptor(httpLoggingInterceptor)
        .addNetworkInterceptor(cacheInterceptor)
        .commonTimeout()
        .build()

    @TokenOkHttpClient
    @Singleton
    @Provides
    fun provideTokenOkHttpClient(
        tokenAuthenticator: TokenAuthenticator,
        tokenInterceptor: TokenInterceptor,
        httpLoggingInterceptor: HttpLoggingInterceptor,
        cache: Cache,
        cacheInterceptor: HttpCacheInterceptor,
    ): OkHttpClient = OkHttpClient
        .Builder()
        .cache(cache)
        .authenticator(tokenAuthenticator)
        .addInterceptor(httpLoggingInterceptor)
        .addInterceptor(tokenInterceptor)
        .addNetworkInterceptor(cacheInterceptor)
        .commonTimeout()
        .build()

    @FileOkHttpClient
    @Singleton
    @Provides
    fun provideFileOkHttpClient(
        @TokenOkHttpClient okHttpClient: OkHttpClient,
    ): OkHttpClient = okHttpClient.newBuilder()
        .fileTimeout()
        .build()

    @FileUploadOkHttpClient
    @Singleton
    @Provides
    fun provideFileUploadOkHttpClient(
        @DefaultOkHttpClient okHttpClient: OkHttpClient,
        httpLoggingInterceptor: HttpLoggingInterceptor,
    ): OkHttpClient = okHttpClient.newBuilder()
        .apply { interceptors().remove(httpLoggingInterceptor) }
        .fileTimeout()
        .build()

    // ------------------------------ Interceptor ------------------------------

    @Singleton
    @Provides
    fun provideHttpLoggingInterceptor(): HttpLoggingInterceptor =
        HttpLoggingInterceptor(maskingLogger).apply {
            if (BuildConfig.DEBUG) {
                setLevel(HttpLoggingInterceptor.Level.BODY)
            } else {
                setLevel(HttpLoggingInterceptor.Level.NONE)
            }
            redactHeader("Authorization")
            redactHeader("Cookie")
        }

    @Singleton
    @Provides
    fun provideTokenInterceptor(dataStoreManager: DataStoreManager): TokenInterceptor =
        TokenInterceptor(dataStoreManager)

    @Singleton
    @Provides
    fun provideHttpCacheInterceptor(): HttpCacheInterceptor =
        HttpCacheInterceptor()

    // ------------------------------ Retrofit ------------------------------
    @Singleton
    @Provides
    fun provideRetrofitBuilder(
        moshi: Moshi,
    ): Retrofit.Builder = Retrofit
        .Builder()
        .addConverterFactory(MoshiConverterFactory.create(moshi).withNullSerialization())
        .baseUrl(BuildConfig.TURNIN_SERVER_URL)

    @Singleton
    @Provides
    fun providerRefreshTokenApi(
        retrofit: Retrofit.Builder,
        @DefaultOkHttpClient okHttpClient: OkHttpClient,
    ): RefreshTokenApi = retrofit
        .client(okHttpClient)
        .build()
        .create(RefreshTokenApi::class.java)

    @Singleton
    @Provides
    fun provideTokenAuthenticator(
        dataStoreManager: DataStoreManager,
        refreshTokenApi: RefreshTokenApi,
        authEventBus: AuthEventBus,
    ): TokenAuthenticator =
        TokenAuthenticator(dataStoreManager, refreshTokenApi, authEventBus)
}

// ------------------------------ Qualifier ------------------------------
@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class DefaultOkHttpClient

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class TokenOkHttpClient

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class FileOkHttpClient

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class FileUploadOkHttpClient

// ------------------------------ Utils ------------------------------
private fun OkHttpClient.Builder.commonTimeout(): OkHttpClient.Builder =
    this
        .connectTimeout(5, TimeUnit.SECONDS)
        .readTimeout(10, TimeUnit.SECONDS)
        .writeTimeout(10, TimeUnit.SECONDS)
        .callTimeout(15, TimeUnit.SECONDS)

private fun OkHttpClient.Builder.fileTimeout(): OkHttpClient.Builder =
    this
        .connectTimeout(10, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .callTimeout(60, TimeUnit.SECONDS)
